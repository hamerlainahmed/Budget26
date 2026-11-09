package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ArticleEntity
import com.example.data.BudgetRepository
import com.example.data.Chapter2AuditCheck
import com.example.data.ChapterWithArticles
import com.example.data.Circular13Constants
import com.example.data.ConsumptionHealth
import com.example.data.InstitutionConfigEntity
import com.example.data.TransactionEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.max

enum class BudgetTab(val index: Int, val titleAr: String) {
    CHAPTERS_DEEP_DIVE(0, "الأبواب والمواد"),
    ALLOCATIONS_MATRIX(1, "حجز الاعتمادات"),
    TRANSACTIONS_LEDGER(2, "سجل سندات الطلب")
}

data class PendingExpenseOverBudgetWarning(
    val article: ArticleEntity,
    val chapterName: String,
    val orderNumber: String,
    val supplierName: String,
    val description: String,
    val requestedAmount: Double,
    val remainingBeforeExpense: Double,
    val projectedConsumptionRate: Double
)

data class BudgetUiState(
    val isLoading: Boolean = true,
    val config: InstitutionConfigEntity = InstitutionConfigEntity(
        fiscalYear = 2026,
        institutionName = "ثانوية الشهيد العربي بن مهيدي - النموذجية",
        wilayaName = "مديرية التربية لولاية الجزائر وسط",
        regime = "داخلي ونصف داخلي",
        internalScholarshipCount = 42,
        semiInternalScholarshipCount = 310,
        isActiveYear = true
    ),
    val savedFiscalYears: List<InstitutionConfigEntity> = emptyList(),
    val expenditureChapters: List<ChapterWithArticles> = emptyList(),
    val transactions: List<TransactionEntity> = emptyList(),
    val totalAuthorizedBudget: Double = 0.0,
    val totalCommittedExpenses: Double = 0.0,
    val netRemainingBalance: Double = 0.0,
    val globalConsumptionRate: Double = 0.0,
    val globalHealthStatus: ConsumptionHealth = ConsumptionHealth.STABLE,
    val cateringSubsidyAmount: Double = 0.0,
    val chapter2Audit: Chapter2AuditCheck = Chapter2AuditCheck(
        chapter2Allocated = 0.0,
        totalOperationalAllocatedExcludingCatering = 0.0,
        currentPercentage = 0.0,
        requiredMinimumAmount = 0.0,
        deficitAmount = 0.0,
        isCompliant = true
    ),
    val selectedTab: BudgetTab = BudgetTab.CHAPTERS_DEEP_DIVE,
    val searchQuery: String = "",
    val statusBannerMessage: String? = null
)

class BudgetViewModel(
    private val repository: BudgetRepository
) : ViewModel() {

    private val selectedTabFlow = MutableStateFlow(BudgetTab.CHAPTERS_DEEP_DIVE)
    private val searchQueryFlow = MutableStateFlow("")
    private val statusBannerFlow = MutableStateFlow<String?>(null)

    private val _overBudgetWarning = MutableStateFlow<PendingExpenseOverBudgetWarning?>(null)
    val overBudgetWarning: StateFlow<PendingExpenseOverBudgetWarning?> = _overBudgetWarning

    val uiState: StateFlow<BudgetUiState> = combine(
        combine(
            repository.activeConfigFlow,
            repository.allFiscalYearsFlow
        ) { active, allYears -> active to allYears },
        repository.chaptersWithArticlesFlow,
        repository.activeYearTransactionsFlow,
        selectedTabFlow,
        combine(searchQueryFlow, statusBannerFlow) { q, msg -> q to msg }
    ) { (configEntity, allYears), allChapters, transactions, selectedTab, (query, bannerMsg) ->
        val config = configEntity ?: allYears.firstOrNull() ?: InstitutionConfigEntity(
            fiscalYear = 2026,
            institutionName = "ثانوية الشهيد العربي بن مهيدي - النموذجية",
            wilayaName = "مديرية التربية لولاية الجزائر وسط",
            regime = "داخلي ونصف داخلي",
            internalScholarshipCount = 42,
            semiInternalScholarshipCount = 310,
            isActiveYear = true
        )

        val expenditureChapters = allChapters

        val totalAuthorized = expenditureChapters.sumOf { it.totalAllocated }
        val totalSpent = expenditureChapters.sumOf { it.totalSpent }
        val remaining = totalAuthorized - totalSpent
        val consumptionRate = if (totalAuthorized <= 0.0) 0.0 else (totalSpent / totalAuthorized) * 100.0
        val globalHealth = when {
            consumptionRate > 100.0 -> ConsumptionHealth.OVER_BUDGET
            consumptionRate >= 90.0 -> ConsumptionHealth.CRITICAL
            consumptionRate >= 70.0 -> ConsumptionHealth.ELEVATED
            else -> ConsumptionHealth.STABLE
        }

        val chapter2Audit = evaluateChapter2Audit(expenditureChapters)

        val filteredTransactions = if (query.isBlank()) {
            transactions
        } else {
            val q = query.trim()
            transactions.filter { tx ->
                tx.description.contains(q, ignoreCase = true) ||
                    tx.supplierName.contains(q, ignoreCase = true) ||
                    tx.orderNumber.contains(q, ignoreCase = true) ||
                    tx.articleName.contains(q, ignoreCase = true) ||
                    tx.articleCode.contains(q, ignoreCase = true) ||
                    tx.chapterName.contains(q, ignoreCase = true)
            }
        }

        BudgetUiState(
            isLoading = false,
            config = config,
            savedFiscalYears = allYears,
            expenditureChapters = expenditureChapters,
            transactions = filteredTransactions,
            totalAuthorizedBudget = totalAuthorized,
            totalCommittedExpenses = totalSpent,
            netRemainingBalance = remaining,
            globalConsumptionRate = consumptionRate,
            globalHealthStatus = globalHealth,
            cateringSubsidyAmount = config.totalCateringSubsidy,
            chapter2Audit = chapter2Audit,
            selectedTab = selectedTab,
            searchQuery = query,
            statusBannerMessage = bannerMsg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BudgetUiState()
    )

    /**
     * Evaluates condition for Chapter 2 (الباب 02: التكاليف الملحقة):
     * Must represent at least 35% of Branch 1 operational charges (excluding Chapter 6 Catering).
     */
    fun evaluateChapter2Audit(expenditureChapters: List<ChapterWithArticles>): Chapter2AuditCheck {
        val chapter2Allocated = expenditureChapters
            .firstOrNull { it.chapter.id == Circular13Constants.EXP_CHAPTER_2_BASE_ID }
            ?.totalAllocated ?: 0.0

        val operationalChaptersWithoutCatering = expenditureChapters.filter {
            it.chapter.branchNumber == 1 && it.chapter.id != Circular13Constants.EXP_CHAPTER_6_BASE_ID
        }
        val totalOperational = operationalChaptersWithoutCatering.sumOf { it.totalAllocated }
        val otherOperational = (totalOperational - chapter2Allocated).coerceAtLeast(0.0)

        val currentPercentage = if (totalOperational <= 0.0) {
            0.0
        } else {
            (chapter2Allocated / totalOperational) * 100.0
        }

        val requiredC2ToReach35Percent = (0.35 / 0.65) * otherOperational
        val currentBenchmark35 = totalOperational * Circular13Constants.CHAPTER_2_MIN_OPERATIONAL_RATIO
        val deficit = max(0.0, requiredC2ToReach35Percent - chapter2Allocated)
        val isCompliant = totalOperational <= 0.0 ||
            (chapter2Allocated / totalOperational) >= (Circular13Constants.CHAPTER_2_MIN_OPERATIONAL_RATIO - 0.0001)

        return Chapter2AuditCheck(
            chapter2Allocated = chapter2Allocated,
            totalOperationalAllocatedExcludingCatering = totalOperational,
            currentPercentage = currentPercentage,
            requiredMinimumAmount = currentBenchmark35,
            deficitAmount = deficit,
            isCompliant = isCompliant
        )
    }

    fun selectTab(tab: BudgetTab) {
        selectedTabFlow.value = tab
    }

    fun updateSearchQuery(query: String) {
        searchQueryFlow.value = query
    }

    fun clearBannerMessage() {
        statusBannerFlow.value = null
    }

    /**
     * Switches between saved fiscal years (e.g., 2025, 2026, 2027), restoring all saved allocations
     * and transaction history for that year.
     */
    fun switchFiscalYear(fiscalYear: Int) {
        viewModelScope.launch {
            repository.switchFiscalYear(fiscalYear)
            statusBannerFlow.value =
                "تم استرجاع وعرض سجل السنة المالية $fiscalYear بكامل اعتماداتها وحوالاتها المحفوظة"
        }
    }

    /**
     * Opens a new fiscal year (or updates/resets an existing one):
     * - Preserves previous fiscal years and their expenses in history.
     * - Clears/starts with 0 expenses when opening a new fiscal year (or when clearExpenses is checked).
     * - Injects Circular 13/2026 catering subsidy into Expenditure ب06/م01.
     */
    fun applyCateringBudget(
        institutionName: String,
        wilayaName: String,
        regime: String,
        fiscalYear: Int,
        internalCount: Int,
        semiInternalCount: Int,
        clearExpensesForTargetYear: Boolean = true,
        startWithZeroCustomAllocations: Boolean = false
    ) {
        viewModelScope.launch {
            val totalCatering = repository.applyCateringBudget(
                institutionName = institutionName,
                wilayaName = wilayaName,
                regime = regime,
                fiscalYear = fiscalYear,
                internalStudents = internalCount.coerceAtLeast(0),
                semiInternalStudents = semiInternalCount.coerceAtLeast(0),
                clearExpensesForTargetYear = clearExpensesForTargetYear,
                startWithZeroCustomAllocations = startWithZeroCustomAllocations
            )
            val resetNote = when {
                startWithZeroCustomAllocations -> " (مع تصفير الاعتمادات لتخصيصها يدوياً)"
                clearExpensesForTargetYear -> " (مع تصفير النفقات للسنة الجديدة وحفظ السنوات السابقة في الأرشيف)"
                else -> ""
            }
            statusBannerFlow.value =
                "تم فتح السنة المالية $fiscalYear$resetNote وحجز اعتماد الإطعام آلياً في النفقات (ب06/م01) بمبلغ ${formatDzdCurrency(totalCatering)}"
        }
    }

    /**
     * Customizes the authorized expenditure credit (حجز الاعتماد) for an individual article in the active fiscal year.
     */
    fun updateArticleAllocation(articleRecordId: String, articleName: String, newAmount: Double) {
        viewModelScope.launch {
            repository.updateArticleAllocation(articleRecordId = articleRecordId, newAmount = newAmount)
            statusBannerFlow.value =
                "تم حجز الاعتماد المفتوح للمادة ($articleName) بمبلغ ${formatDzdCurrency(newAmount)}"
        }
    }

    /**
     * Zeroes out all non-catering expenditure allocations for the active fiscal year so the user can distribute credits from scratch.
     */
    fun zeroOutCustomExpenditures() {
        val currentYear = uiState.value.config.fiscalYear
        viewModelScope.launch {
            repository.zeroOutCustomExpenditures(currentYear)
            statusBannerFlow.value =
                "تم تصفير اعتمادات النفقات غير الآلية للسنة المالية $currentYear (مع الإبقاء على إطعام الممنوحين ب06/م01) لتتمكن من حجزها يدوياً"
        }
    }

    /**
     * Registers an expense order (سند الطلب) with an automatic Over-Budget check.
     * If the credit is insufficient (amount > remainingBefore), shows a warning with the exact deficit.
     */
    fun addExpense(
        article: ArticleEntity,
        chapterName: String,
        orderNumber: String,
        supplierName: String,
        description: String,
        amount: Double,
        forceCommit: Boolean = false,
        onCommitted: () -> Unit = {}
    ) {
        if (amount <= 0.0) return

        val remainingBefore = article.remainingCredit
        val projectedSpent = article.spentAmount + amount
        val projectedRate = if (article.allocatedAmount <= 0.0) {
            100.0
        } else {
            (projectedSpent / article.allocatedAmount) * 100.0
        }

        if (!forceCommit && amount > remainingBefore) {
            _overBudgetWarning.value = PendingExpenseOverBudgetWarning(
                article = article,
                chapterName = chapterName,
                orderNumber = orderNumber,
                supplierName = supplierName,
                description = description,
                requestedAmount = amount,
                remainingBeforeExpense = remainingBefore,
                projectedConsumptionRate = projectedRate
            )
            return
        }

        _overBudgetWarning.value = null
        viewModelScope.launch {
            repository.addExpense(
                articleRecordId = article.id,
                chapterName = chapterName,
                orderNumber = orderNumber,
                supplierName = supplierName,
                description = description,
                amount = amount
            )
            statusBannerFlow.value =
                "تم تسجيل سند الطلب ($orderNumber) بمبلغ ${formatDzdCurrency(amount)} واقتطاعه من اعتماد المادة ${article.officialCode}"
            onCommitted()
        }
    }

    fun confirmOverBudgetExpense(onCommitted: () -> Unit = {}) {
        val pending = _overBudgetWarning.value ?: return
        addExpense(
            article = pending.article,
            chapterName = pending.chapterName,
            orderNumber = pending.orderNumber,
            supplierName = pending.supplierName,
            description = pending.description,
            amount = pending.requestedAmount,
            forceCommit = true,
            onCommitted = onCommitted
        )
    }

    fun dismissOverBudgetWarning() {
        _overBudgetWarning.value = null
    }

    /**
     * Deletes a transaction from the ledger and automatically refunds its amount
     * back to the article's remaining credit.
     */
    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction.id)
            statusBannerFlow.value =
                "تم إلغاء سند الطلب (${transaction.orderNumber}) واسترجاع مبلغ ${formatDzdCurrency(transaction.amount)} إلى رصيد المادة ${transaction.articleCode}"
        }
    }

    /**
     * Fixes Chapter 2 deficit to reach the 35% minimum operational ratio.
     */
    fun autoFixChapter2Compliance() {
        val audit = uiState.value.chapter2Audit
        val fiscalYear = uiState.value.config.fiscalYear
        viewModelScope.launch {
            repository.autoFixChapter2Compliance(fiscalYear, audit.deficitAmount)
            statusBannerFlow.value =
                "تمت تسوية وتكييف اعتمادات الباب 02 مع شرط المنشور 13/2026 (≥ 35% من نفقات التسيير)"
        }
    }

    class Factory(
        private val repository: BudgetRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
                return BudgetViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
