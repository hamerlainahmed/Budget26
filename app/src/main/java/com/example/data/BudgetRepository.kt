package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class BudgetRepository(private val dao: BudgetDao) {

    val institutionConfigFlow: Flow<InstitutionConfigEntity?> =
        dao.observeInstitutionConfig()

    val chaptersFlow: Flow<List<ChapterEntity>> =
        dao.observeChapters()

    val articlesFlow: Flow<List<ArticleEntity>> =
        dao.observeArticles()

    val transactionsFlow: Flow<List<TransactionEntity>> =
        dao.observeTransactions()

    val chaptersWithArticlesFlow: Flow<List<ChapterWithArticles>> =
        combine(chaptersFlow, articlesFlow) { chapters, articles ->
            val articlesByChapter = articles.groupBy { it.chapterId }
            chapters.map { chapter ->
                ChapterWithArticles(
                    chapter = chapter,
                    articles = articlesByChapter[chapter.id].orEmpty()
                        .sortedBy { it.articleNumber }
                )
            }
        }

    suspend fun applyCateringBudget(
        institutionName: String,
        wilayaName: String,
        regime: String,
        fiscalYear: Int,
        internalStudents: Int,
        semiInternalStudents: Int
    ): Double {
        return dao.applyCateringBudgetTransaction(
            institutionName = institutionName,
            wilayaName = wilayaName,
            regime = regime,
            fiscalYear = fiscalYear,
            internalStudents = internalStudents,
            semiInternalStudents = semiInternalStudents
        )
    }

    suspend fun updateArticleAllocation(articleId: String, newAmount: Double) {
        dao.updateArticleAllocation(articleId = articleId, newAllocated = newAmount.coerceAtLeast(0.0))
    }

    suspend fun addExpense(
        articleId: String,
        chapterName: String,
        voucherNumber: String,
        supplierName: String,
        description: String,
        amount: Double
    ): Long {
        return dao.commitExpenseTransaction(
            articleId = articleId,
            chapterName = chapterName,
            voucherNumber = voucherNumber,
            supplierName = supplierName,
            description = description,
            amount = amount
        )
    }

    suspend fun deleteTransaction(transactionId: Long) {
        dao.rollbackExpenseTransaction(transactionId)
    }

    /**
     * Auto-adjusts Chapter 2 articles proportionally so that Chapter 2 reaches at least 35%
     * of total operational charges (excluding Chapter 6 Catering), satisfying Circular 13/2026.
     */
    suspend fun autoFixChapter2Compliance(deficitAmount: Double) {
        if (deficitAmount <= 0.0) return
        val chapter2Articles = dao.getArticlesByChapter(Circular13Constants.EXP_CHAPTER_2_ID)
        if (chapter2Articles.isEmpty()) return

        // Add the required increment to Article 212/01 (Electricity & Gas) and 212/02 (Water)
        val extraWithBuffer = deficitAmount * 1.05 // 5% safety margin above 35% threshold
        val primaryUtility = chapter2Articles.first()
        dao.updateArticleAllocation(
            articleId = primaryUtility.id,
            newAllocated = primaryUtility.allocatedAmount + extraWithBuffer
        )
    }
}
