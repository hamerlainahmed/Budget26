package com.example.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class BudgetRepository(private val dao: BudgetDao) {

    val allFiscalYearsFlow: Flow<List<InstitutionConfigEntity>> =
        dao.observeAllFiscalYears()

    val activeConfigFlow: Flow<InstitutionConfigEntity?> =
        dao.observeActiveConfig()

    private val activeYearNumberFlow: Flow<Int> =
        activeConfigFlow.map { it?.fiscalYear ?: Circular13Constants.DEFAULT_FISCAL_YEAR }

    val chaptersFlow: Flow<List<ChapterEntity>> =
        dao.observeChapters()

    val activeYearArticlesFlow: Flow<List<ArticleEntity>> =
        activeYearNumberFlow.flatMapLatest { year ->
            dao.observeArticlesByYear(year)
        }

    val activeYearTransactionsFlow: Flow<List<TransactionEntity>> =
        activeYearNumberFlow.flatMapLatest { year ->
            dao.observeTransactionsByYear(year)
        }

    val chaptersWithArticlesFlow: Flow<List<ChapterWithArticles>> =
        combine(chaptersFlow, activeYearArticlesFlow) { chapters, articles ->
            val articlesByChapter = articles.groupBy { it.chapterId }
            chapters.map { chapter ->
                ChapterWithArticles(
                    chapter = chapter,
                    articles = articlesByChapter[chapter.id].orEmpty()
                        .sortedBy { it.articleNumber }
                )
            }
        }

    suspend fun switchFiscalYear(fiscalYear: Int) {
        dao.switchActiveFiscalYear(fiscalYear)
    }

    suspend fun applyCateringBudget(
        institutionName: String,
        wilayaName: String,
        regime: String,
        fiscalYear: Int,
        internalStudents: Int,
        semiInternalStudents: Int,
        clearExpensesForTargetYear: Boolean
    ): Double {
        return dao.applyCateringBudgetTransaction(
            institutionName = institutionName,
            wilayaName = wilayaName,
            regime = regime,
            fiscalYear = fiscalYear,
            internalStudents = internalStudents,
            semiInternalStudents = semiInternalStudents,
            clearExpensesForTargetYear = clearExpensesForTargetYear,
            templateArticlesProvider = { year, subsidy ->
                BudgetDatabase.buildCleanArticlesForFiscalYear(year, subsidy)
            }
        )
    }

    suspend fun updateArticleAllocation(articleRecordId: String, newAmount: Double) {
        dao.updateArticleAllocation(
            articleRecordId = articleRecordId,
            newAllocated = newAmount.coerceAtLeast(0.0)
        )
    }

    suspend fun addExpense(
        articleRecordId: String,
        chapterName: String,
        voucherNumber: String,
        supplierName: String,
        description: String,
        amount: Double
    ): Long {
        return dao.commitExpenseTransaction(
            articleRecordId = articleRecordId,
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
     * Auto-adjusts Chapter 2 (الباب 02: التكاليف الملحقة) for the active fiscal year
     * so that it reaches >= 35% of total operational charges (Branch 1 excluding Chapter 6 Catering).
     */
    suspend fun autoFixChapter2Compliance(fiscalYear: Int, deficitAmount: Double) {
        if (deficitAmount <= 0.0) return
        val chapter2Articles = dao.getArticlesByYearAndChapter(
            fiscalYear = fiscalYear,
            chapterId = Circular13Constants.EXP_CHAPTER_2_BASE_ID
        )
        if (chapter2Articles.isEmpty()) return

        val extraWithBuffer = deficitAmount * 1.05
        val primaryUtility = chapter2Articles.first()
        dao.updateArticleAllocation(
            articleRecordId = primaryUtility.id,
            newAllocated = primaryUtility.allocatedAmount + extraWithBuffer
        )
    }
}
