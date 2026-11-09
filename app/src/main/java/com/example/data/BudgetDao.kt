package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Query("SELECT * FROM institution_configs ORDER BY fiscalYear DESC")
    fun observeAllFiscalYears(): Flow<List<InstitutionConfigEntity>>

    @Query("SELECT * FROM institution_configs WHERE isActiveYear = 1 LIMIT 1")
    fun observeActiveConfig(): Flow<InstitutionConfigEntity?>

    @Query("SELECT * FROM institution_configs WHERE fiscalYear = :fiscalYear LIMIT 1")
    suspend fun getConfigByYear(fiscalYear: Int): InstitutionConfigEntity?

    @Query("UPDATE institution_configs SET isActiveYear = 0")
    suspend fun clearActiveFlags()

    @Query("UPDATE institution_configs SET isActiveYear = 1 WHERE fiscalYear = :fiscalYear")
    suspend fun markYearAsActive(fiscalYear: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertInstitutionConfig(config: InstitutionConfigEntity)

    @Query("SELECT * FROM chapters ORDER BY branchNumber ASC, chapterNumber ASC")
    fun observeChapters(): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM articles WHERE fiscalYear = :fiscalYear ORDER BY chapterNumber ASC, articleNumber ASC")
    fun observeArticlesByYear(fiscalYear: Int): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE fiscalYear = :fiscalYear ORDER BY chapterNumber ASC, articleNumber ASC")
    suspend fun getArticlesByYearOnce(fiscalYear: Int): List<ArticleEntity>

    @Query("SELECT * FROM articles WHERE id = :articleRecordId LIMIT 1")
    suspend fun getArticleById(articleRecordId: String): ArticleEntity?

    @Query("SELECT * FROM articles WHERE fiscalYear = :fiscalYear AND chapterId = :chapterId ORDER BY articleNumber ASC")
    suspend fun getArticlesByYearAndChapter(fiscalYear: Int, chapterId: String): List<ArticleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Update
    suspend fun updateArticle(article: ArticleEntity)

    @Query("UPDATE articles SET allocatedAmount = :newAllocated WHERE id = :articleRecordId")
    suspend fun updateArticleAllocation(articleRecordId: String, newAllocated: Double)

    @Query("UPDATE articles SET spentAmount = :newSpent WHERE id = :articleRecordId")
    suspend fun updateArticleSpent(articleRecordId: String, newSpent: Double)

    @Query("UPDATE articles SET spentAmount = 0.0 WHERE fiscalYear = :fiscalYear")
    suspend fun resetAllSpentAmountsForYear(fiscalYear: Int)

    @Query("UPDATE articles SET allocatedAmount = 0.0 WHERE fiscalYear = :fiscalYear AND isAutoCatering = 0")
    suspend fun zeroOutCustomExpendituresForYear(fiscalYear: Int)

    @Query("SELECT * FROM transactions WHERE fiscalYear = :fiscalYear ORDER BY timestamp DESC, id DESC")
    fun observeTransactionsByYear(fiscalYear: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE articleId = :articleId ORDER BY timestamp ASC, id ASC")
    fun observeTransactionsByArticle(articleId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE articleId = :articleId ORDER BY timestamp ASC, id ASC")
    suspend fun getTransactionsByArticleOnce(articleId: String): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE id = :transactionId LIMIT 1")
    suspend fun getTransactionById(transactionId: Long): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Query("DELETE FROM transactions WHERE id = :transactionId")
    suspend fun deleteTransactionById(transactionId: Long)

    @Query("DELETE FROM transactions WHERE fiscalYear = :fiscalYear")
    suspend fun deleteAllTransactionsForYear(fiscalYear: Int)

    @Query("SELECT COUNT(*) FROM chapters")
    suspend fun getChaptersCount(): Int

    @Transaction
    suspend fun switchActiveFiscalYear(fiscalYear: Int) {
        val existing = getConfigByYear(fiscalYear) ?: return
        clearActiveFlags()
        upsertInstitutionConfig(existing.copy(isActiveYear = true, updatedAt = System.currentTimeMillis()))
    }

    /**
     * Opens a new Fiscal Year (or updates/resets an existing one) while preserving all other
     * fiscal years and their expenses in the database history.
     */
    @Transaction
    suspend fun applyCateringBudgetTransaction(
        institutionName: String,
        wilayaName: String,
        regime: String,
        fiscalYear: Int,
        internalStudents: Int,
        semiInternalStudents: Int,
        clearExpensesForTargetYear: Boolean = true,
        startWithZeroCustomAllocations: Boolean = false,
        templateArticlesProvider: (Int, Double) -> List<ArticleEntity>
    ): Double {
        val calculatedSubsidy = Circular13Constants.calculateCateringSubsidy(
            internalCount = internalStudents,
            semiInternalCount = semiInternalStudents
        )

        clearActiveFlags()

        val config = InstitutionConfigEntity(
            fiscalYear = fiscalYear,
            institutionName = institutionName.trim().ifEmpty { "ثانوية الشهيد العربي بن مهيدي" },
            wilayaName = wilayaName.trim().ifEmpty { "مديرية التربية لولاية الجزائر" },
            regime = regime,
            internalScholarshipCount = internalStudents,
            semiInternalScholarshipCount = semiInternalStudents,
            isActiveYear = true,
            updatedAt = System.currentTimeMillis()
        )
        upsertInstitutionConfig(config)

        val existingArticlesForYear = getArticlesByYearOnce(fiscalYear)
        if (existingArticlesForYear.isEmpty()) {
            // Brand new fiscal year: create fresh nomenclature articles with 0 spentAmount
            val freshArticles = templateArticlesProvider(fiscalYear, calculatedSubsidy)
            insertArticles(freshArticles)
            deleteAllTransactionsForYear(fiscalYear)
        } else {
            if (clearExpensesForTargetYear) {
                deleteAllTransactionsForYear(fiscalYear)
                resetAllSpentAmountsForYear(fiscalYear)
            }
            // Inject catering subsidy into Expenditure 06/01 for this fiscal year
            val expRecordId = Circular13Constants.articleRecordId(
                fiscalYear = fiscalYear,
                baseArticleId = Circular13Constants.EXP_CATERING_BASE_ID
            )
            updateArticleAllocation(articleRecordId = expRecordId, newAllocated = calculatedSubsidy)
        }

        if (startWithZeroCustomAllocations) {
            zeroOutCustomExpendituresForYear(fiscalYear)
        }

        return calculatedSubsidy
    }

    /**
     * Commits an expenditure order (سند الطلب) to a specific article in its fiscal year and updates spentAmount atomically.
     */
    @Transaction
    suspend fun commitExpenseTransaction(
        articleRecordId: String,
        chapterName: String,
        orderNumber: String,
        supplierName: String,
        description: String,
        amount: Double,
        timestamp: Long = System.currentTimeMillis()
    ): Long {
        val article = getArticleById(articleRecordId)
            ?: throw IllegalArgumentException("المادة المحاسبية غير موجودة")

        val updatedSpent = article.spentAmount + amount
        updateArticleSpent(articleRecordId = article.id, newSpent = updatedSpent)

        val tx = TransactionEntity(
            fiscalYear = article.fiscalYear,
            articleId = article.id,
            chapterId = article.chapterId,
            chapterName = chapterName,
            articleCode = article.officialCode,
            articleName = article.name,
            orderNumber = orderNumber.trim().ifEmpty { "BC-${article.fiscalYear}/${(100..999).random()}" },
            supplierName = supplierName.trim().ifEmpty { "ممون معتمد" },
            description = description.trim(),
            amount = amount,
            timestamp = timestamp
        )
        return insertTransaction(tx)
    }

    /**
     * Deletes a transaction and automatically refunds the credit back to the constituent article.
     */
    @Transaction
    suspend fun rollbackExpenseTransaction(transactionId: Long) {
        val tx = getTransactionById(transactionId) ?: return
        val article = getArticleById(tx.articleId)
        if (article != null) {
            val refundedSpent = (article.spentAmount - tx.amount).coerceAtLeast(0.0)
            updateArticleSpent(articleRecordId = article.id, newSpent = refundedSpent)
        }
        deleteTransactionById(transactionId)
    }
}
