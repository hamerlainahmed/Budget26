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

    @Query("SELECT * FROM institution_config WHERE id = 1")
    fun observeInstitutionConfig(): Flow<InstitutionConfigEntity?>

    @Query("SELECT * FROM institution_config WHERE id = 1")
    suspend fun getInstitutionConfigOnce(): InstitutionConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertInstitutionConfig(config: InstitutionConfigEntity)

    @Query("SELECT * FROM chapters ORDER BY sectionType ASC, chapterNumber ASC")
    fun observeChapters(): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM articles ORDER BY sectionType ASC, chapterId ASC, articleNumber ASC")
    fun observeArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE id = :articleId LIMIT 1")
    suspend fun getArticleById(articleId: String): ArticleEntity?

    @Query("SELECT * FROM articles WHERE chapterId = :chapterId ORDER BY articleNumber ASC")
    suspend fun getArticlesByChapter(chapterId: String): List<ArticleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Update
    suspend fun updateArticle(article: ArticleEntity)

    @Query("UPDATE articles SET allocatedAmount = :newAllocated WHERE id = :articleId")
    suspend fun updateArticleAllocation(articleId: String, newAllocated: Double)

    @Query("UPDATE articles SET spentAmount = :newSpent WHERE id = :articleId")
    suspend fun updateArticleSpent(articleId: String, newSpent: Double)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC, id DESC")
    fun observeTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :transactionId LIMIT 1")
    suspend fun getTransactionById(transactionId: Long): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Query("DELETE FROM transactions WHERE id = :transactionId")
    suspend fun deleteTransactionById(transactionId: Long)

    @Query("SELECT COUNT(*) FROM chapters")
    suspend fun getChaptersCount(): Int

    /**
     * Automated Catering & Scholarship Budget Engine (Circular 13/2026):
     * Updates the InstitutionConfig AND automatically injects the calculated catering subsidy into:
     * 1) Revenue Title 1 -> Chapter 1 -> Article 1 (R-1-1: إعانة التغذية والمنح)
     * 2) Expenditure Title 2 -> Branch 1 -> Chapter 6 -> Article 1 (E-6-1: إطعام التلاميذ الممنوحين)
     */
    @Transaction
    suspend fun applyCateringBudgetTransaction(
        institutionName: String,
        wilayaName: String,
        regime: String,
        fiscalYear: Int,
        internalStudents: Int,
        semiInternalStudents: Int
    ): Double {
        val calculatedSubsidy = Circular13Constants.calculateCateringSubsidy(
            internalCount = internalStudents,
            semiInternalCount = semiInternalStudents
        )

        val config = InstitutionConfigEntity(
            id = 1,
            institutionName = institutionName.trim().ifEmpty { "ثانوية الشهيد محمد بوضياف" },
            wilayaName = wilayaName.trim().ifEmpty { "مديرية التربية لولاية الجزائر" },
            regime = regime,
            fiscalYear = fiscalYear,
            internalScholarshipCount = internalStudents,
            semiInternalScholarshipCount = semiInternalStudents,
            updatedAt = System.currentTimeMillis()
        )
        upsertInstitutionConfig(config)

        // Inject into Revenue Chapter 1 -> Item 1 (R-1-1)
        updateArticleAllocation(
            articleId = Circular13Constants.REV_CATERING_ARTICLE_ID,
            newAllocated = calculatedSubsidy
        )

        // Inject into Expenditure Chapter 6 -> Item 1 (E-6-1)
        updateArticleAllocation(
            articleId = Circular13Constants.EXP_CATERING_ARTICLE_ID,
            newAllocated = calculatedSubsidy
        )

        return calculatedSubsidy
    }

    /**
     * Commits an expense and updates the corresponding Article's spentAmount atomically.
     */
    @Transaction
    suspend fun commitExpenseTransaction(
        articleId: String,
        chapterName: String,
        voucherNumber: String,
        supplierName: String,
        description: String,
        amount: Double,
        timestamp: Long = System.currentTimeMillis()
    ): Long {
        val article = getArticleById(articleId)
            ?: throw IllegalArgumentException("المادة المحاسبية غير موجودة")

        val updatedSpent = article.spentAmount + amount
        updateArticleSpent(articleId = article.id, newSpent = updatedSpent)

        val tx = TransactionEntity(
            articleId = article.id,
            chapterId = article.chapterId,
            chapterName = chapterName,
            articleCode = article.officialCode,
            articleName = article.name,
            voucherNumber = voucherNumber.trim().ifEmpty { "MD-2026/${(100..999).random()}" },
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
            updateArticleSpent(articleId = article.id, newSpent = refundedSpent)
        }
        deleteTransactionById(transactionId)
    }
}
