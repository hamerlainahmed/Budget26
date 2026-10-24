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
import com.example.data.SectionType
import com.example.data.TransactionEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.max

enum class BudgetTab(val index: Int, val titleAr: String) {
    CHAPTERS_DEEP_DIVE(0, "تفاصيل الأبواب"),
    ALLOCATIONS_MATRIX(1, "حجز الاعتمادات"),
    TRANSACTIONS_LEDGER(2, "سجل الفواتير")
}

data class PendingExpenseOverBudgetWarning(
    val article: ArticleEntity,
    val chapterName: String,
    val voucherNumber: String,
    val supplierName: String,
    val description: String,
    val requestedAmount: Double,
    val remainingBeforeExpense: Double,
    val projectedConsumptionRate: Double
)

data class BudgetUiState(
    val isLoading: Boolean = true,
    val config: InstitutionConfigEntity = InstitutionConfigEntity(
        institutionName = "ثانوية الشهيد العربي بن مهيدي - النموذجية",
        wilayaName = "مديرية التربية لولاية الجزائر وسط",
        regime = "داخلي ونصف داخلي",
        fiscalYear = 2026,
        internalScholarshipCount = 42,
        semiInternalScholarshipCount = 310
    ),
    val revenueChapters: List<ChapterWithArticles> = emptyList(),
    val expenditureChapters: List<ChapterWithArticles> = emptyList(),
    val transactions: List<TransactionEntity> = emptyList(),
    val totalAuthorizedBudget: Double = 0.0,
    val totalCommittedExpenses: Double = 0.0,
    val netRemainingBalance: Double = 0.0,
    val globalConsumptionRate: Double = 0.0,
    val globalHealthStatus: ConsumptionHealth = ConsumptionHealth.STABLE,
    val totalRevenuesAllocated: Double = 0.0,
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
        repository.institutionConfigFlow,
        repository.chaptersWithArticlesFlow,
        repository.transactionsFlow,
        selectedTabFlow,
        combine(searchQueryFlow, statusBannerFlow) { q, msg -> q to msg }
    ) { configEntity, allChapters, transactions, selectedTab, (query, bannerMsg) ->
        val config = configEntity ?: InstitutionConfigEntity(
            institutionName = "ثانوية الشهيد العربي بن مهيدي - النموذجية",
            wilayaName = "مديرية التربية لولاية الجزائر وسط",
            regime = "داخلي ونصف داخلي",
            fiscalYear = 2026,
            internalScholarshipCount = 42,
            semiInternalScholarshipCount = 310
        )

        val revenueChapters = allChapters.filter {
            it.chapter.sectionType == SectionType.REVENUE.name
        }
        val expenditureChapters = allChapters.filter {
            it.chapter.sectionType == SectionType.EXPENDITURE.name
        }

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

        val totalRevenues = revenueChapters.sumOf { it.totalAllocated }
        val chapter2Audit = evaluateChapter2Audit(expenditureChapters)

        val filteredTransactions = if (query.isBlank()) {
            transactions
        } else {
            val q = query.trim()
            transactions.filter { tx ->
                tx.description.contains(q, ignoreCase = true) ||
                    tx.supplierName.contains(q, ignoreCase = true) ||
                    tx.voucherNumber.contains(q, ignoreCase = true) ||
                    tx.articleName.contains(q, ignoreCase = true) ||
                    tx.articleCode.contains(q, ignoreCase = true) ||
                    tx.chapterName.contains(q, ignoreCase = true)
            }
        }

        BudgetUiState(
            isLoading = false,
            config = config,
            revenueChapters = revenueChapters,
            expenditureChapters = expenditureChapters,
            transactions = filteredTransactions,
            totalAuthorizedBudget = totalAuthorized,
            totalCommittedExpenses = totalSpent,
            netRemainingBalance = remaining,
            globalConsumptionRate = consumptionRate,
            globalHealthStatus = globalHealth,
            totalRevenuesAllocated = totalRevenues,
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
     * Evaluates Circular 13/2026 condition for Chapter 2 (Utilities & Annexes: Water, Gas, Electricity):
     * Must represent at least 35% of total operational charges (excluding Chapter 6 Catering).
     */
    fun evaluateChapter2Audit(expenditureChapters: List<ChapterWithArticles>): Chapter2AuditCheck {
        val chapter2Allocated = expenditureChapters
            .firstOrNull { it.chapter.id == Circular13Constants.EXP_CHAPTER_2_ID }
            ?.totalAllocated ?: 0.0

        val operationalChaptersWithoutCatering = expenditureChapters.filter {
            it.chapter.id != Circular13Constants.EXP_CHAPTER_6_ID
        }
        val totalOperational = operationalChaptersWithoutCatering.sumOf { it.totalAllocated }
        val otherOperational = (totalOperational - chapter2Allocated).coerceAtLeast(0.0)

        val currentPercentage = if (totalOperational <= 0.0) {
            0.0
        } else {
            (chapter2Allocated / totalOperational) * 100.0
        }

        // If we increase Chapter 2 while keeping other operational chapters constant,
        // C2_new / (C2_new + otherOperational) >= 0.35  =>  C2_new >= (0.35 / 0.65) * otherOperational
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
     * Automated Catering & Scholarship Budget Engine (Circular 13/2026 - Chapter 6):
     * Saves fiscal year configuration and automatically injects the calculated subsidy into:
     * - Revenue Title 1 -> Chapter 1 -> Article 1 (R-1-1)
     * - Expenditure Title 2 -> Branch 1 -> Chapter 6 -> Article 1 (E-6-1)
     */
    fun applyCateringBudget(
        institutionName: String,
        wilayaName: String,
        regime: String,
        fiscalYear: Int,
        internalCount: Int,
        semiInternalCount: Int
    ) {
        viewModelScope.launch {
            val totalCatering = repository.applyCateringBudget(
                institutionName = institutionName,
                wilayaName = wilayaName,
                regime = regime,
                fiscalYear = fiscalYear,
                internalStudents = internalCount.coerceAtLeast(0),
                semiInternalStudents = semiInternalCount.coerceAtLeast(0)
            )
            statusBannerFlow.value =
                "تم فتح السنة المالية $fiscalYear وتحديث اعتماد الإطعام آلياً في الإيرادات (111/01) والنفقات (216/01) بمبلغ ${formatDzdCurrency(totalCatering)}"
        }
    }

    /**
     * Budget Credit Authorization (حجز الاعتمادات):
     * Updates the authorized credit for an individual article.
     */
    fun updateArticleAllocation(articleId: String, articleName: String, newAmount: Double) {
        viewModelScope.launch {
            repository.updateArticleAllocation(articleId = articleId, newAmount = newAmount)
            statusBannerFlow.value =
                "تم تحديث الاعتماد المسموح به للمادة ($articleName) إلى ${formatDzdCurrency(newAmount)}"
        }
    }

    /**
     * Registers an expense with an automatic Over-Budget check (> 100%).
     * If the expense exceeds the article's remaining credit and forceCommit is false,
     * triggers an over-budget confirmation alert before committing.
     */
    fun addExpense(
        article: ArticleEntity,
        chapterName: String,
        voucherNumber: String,
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
                voucherNumber = voucherNumber,
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
                articleId = article.id,
                chapterName = chapterName,
                voucherNumber = voucherNumber,
                supplierName = supplierName,
                description = description,
                amount = amount
            )
            statusBannerFlow.value =
                "تم تسجيل النفقة (${formatDzdCurrency(amount)}) وخصمها من المادة ${article.officialCode}"
            onCommitted()
        }
    }

    fun confirmOverBudgetExpense(onCommitted: () -> Unit = {}) {
        val pending = _overBudgetWarning.value ?: return
        addExpense(
            article = pending.article,
            chapterName = pending.chapterName,
            voucherNumber = pending.voucherNumber,
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
                "تم إلغاء الحوالة ${transaction.voucherNumber} واسترجاع ${formatDzdCurrency(transaction.amount)} إلى رصيد المادة ${transaction.articleCode}"
        }
    }

    /**
     * Automatically adjusts Chapter 2 utilities allocation to satisfy Circular 13/2026 (>= 35%).
     */
    fun autoFixChapter2Compliance() {
        val currentAudit = uiState.value.chapter2Audit
        if (currentAudit.isCompliant || currentAudit.deficitAmount <= 0.0) return
        viewModelScope.launch {
            repository.autoFixChapter2Compliance(currentAudit.deficitAmount)
            statusBannerFlow.value =
                "تم تعديل اعتماد الباب الثاني (التكاليف الملحقة) لاستيفاء النسبة القانونية (≥ 35%) وفق المنشور 13/2026"
        }
    }

    class Factory(private val repository: BudgetRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
                return BudgetViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
