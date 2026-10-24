package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ArticleEntity
import com.example.ui.components.AllocationsMatrixSection
import com.example.ui.components.Chapter2AuditBanner
import com.example.ui.components.ChapterDeepDiveSection
import com.example.ui.components.ExecutiveHeaderCard
import com.example.ui.components.ExpenseRegistrationModal
import com.example.ui.components.FiscalYearOnboardingModal
import com.example.ui.components.KpiBentoGrid
import com.example.ui.components.OverBudgetAlertDialog
import com.example.ui.components.SegmentedBudgetTabsBar
import com.example.ui.components.TransactionsLedgerSection
import com.example.ui.theme.EmeraldStable

const val ROUTE_SCHOOL_BUDGET_DASHBOARD = "school_budget_dashboard_2026"

@Composable
fun SchoolBudgetDashboardScreen(
    viewModel: BudgetViewModel,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val overBudgetWarning by viewModel.overBudgetWarning.collectAsStateWithLifecycle()

    var showFiscalYearWizard by rememberSaveable { mutableStateOf(false) }
    var showExpenseModal by rememberSaveable { mutableStateOf(false) }
    var preselectedExpenseArticle by remember { mutableStateOf<ArticleEntity?>(null) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("school_budget_scaffold"),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    preselectedExpenseArticle = null
                    showExpenseModal = true
                },
                containerColor = EmeraldStable,
                contentColor = Color(0xFF042F2E),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "نفقة جديدة"
                    )
                },
                text = {
                    Text(
                        text = "حوالة دفع جديدة",
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                modifier = Modifier.testTag("fab_new_expense")
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 760.dp)
                    .testTag("dashboard_lazy_column"),
                contentPadding = PaddingValues(
                    start = 14.dp,
                    end = 14.dp,
                    top = 12.dp,
                    bottom = 92.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Top Executive Header
                item(key = "executive_header") {
                    ExecutiveHeaderCard(
                        config = uiState.config,
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = onToggleTheme,
                        onOpenFiscalYearSetup = { showFiscalYearWizard = true },
                        onQuickExpense = {
                            preselectedExpenseArticle = null
                            showExpenseModal = true
                        }
                    )
                }

                // Optional Action Status Feedback Banner
                if (uiState.statusBannerMessage != null) {
                    item(key = "status_feedback_banner") {
                        AnimatedVisibility(
                            visible = uiState.statusBannerMessage != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = EmeraldStable.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, EmeraldStable),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("status_feedback_banner")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = EmeraldStable,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = uiState.statusBannerMessage.orEmpty(),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.clearBannerMessage() },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "إغلاق الإشعار",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. KPI Overview Cards (Modern Bento Grid)
                item(key = "kpi_bento_grid") {
                    KpiBentoGrid(
                        totalAuthorized = uiState.totalAuthorizedBudget,
                        totalCommitted = uiState.totalCommittedExpenses,
                        netRemaining = uiState.netRemainingBalance,
                        cateringSubsidy = uiState.cateringSubsidyAmount,
                        internalStudents = uiState.config.internalScholarshipCount,
                        semiInternalStudents = uiState.config.semiInternalScholarshipCount,
                        globalConsumptionRate = uiState.globalConsumptionRate,
                        globalHealthStatus = uiState.globalHealthStatus
                    )
                }

                // 3. Regulatory Audit Warning (شرط الباب 2 - المنشور 13/2026)
                item(key = "chapter2_audit_banner") {
                    Chapter2AuditBanner(
                        audit = uiState.chapter2Audit,
                        onAutoFixCompliance = { viewModel.autoFixChapter2Compliance() },
                        onGoToAllocationsTab = {
                            viewModel.selectTab(BudgetTab.ALLOCATIONS_MATRIX)
                        }
                    )
                }

                // 4. Segmented Tabbed Navigation Bar
                item(key = "segmented_tabs_bar") {
                    SegmentedBudgetTabsBar(
                        selectedTab = uiState.selectedTab,
                        transactionsCount = uiState.transactions.size,
                        onSelectTab = { viewModel.selectTab(it) }
                    )
                }

                // 5. Active Tab Content
                item(key = "active_tab_content_${uiState.selectedTab.name}") {
                    when (uiState.selectedTab) {
                        BudgetTab.CHAPTERS_DEEP_DIVE -> {
                            ChapterDeepDiveSection(
                                expenditureChapters = uiState.expenditureChapters,
                                revenueChapters = uiState.revenueChapters,
                                onRegisterExpenseForArticle = { article, _ ->
                                    preselectedExpenseArticle = article
                                    showExpenseModal = true
                                }
                            )
                        }

                        BudgetTab.ALLOCATIONS_MATRIX -> {
                            AllocationsMatrixSection(
                                expenditureChapters = uiState.expenditureChapters,
                                revenueChapters = uiState.revenueChapters,
                                onUpdateAllocation = { articleId, articleName, newAmount ->
                                    viewModel.updateArticleAllocation(
                                        articleId = articleId,
                                        articleName = articleName,
                                        newAmount = newAmount
                                    )
                                }
                            )
                        }

                        BudgetTab.TRANSACTIONS_LEDGER -> {
                            TransactionsLedgerSection(
                                transactions = uiState.transactions,
                                searchQuery = uiState.searchQuery,
                                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                                onDeleteAndRefundTransaction = { tx ->
                                    viewModel.deleteTransaction(tx)
                                },
                                onOpenNewExpenseModal = {
                                    preselectedExpenseArticle = null
                                    showExpenseModal = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Fiscal Year Onboarding Wizard Modal
    if (showFiscalYearWizard) {
        FiscalYearOnboardingModal(
            initialConfig = uiState.config,
            onDismiss = { showFiscalYearWizard = false },
            onSaveAndInjectSubsidy = { name, wilaya, regime, year, internalCount, semiCount ->
                viewModel.applyCateringBudget(
                    institutionName = name,
                    wilayaName = wilaya,
                    regime = regime,
                    fiscalYear = year,
                    internalCount = internalCount,
                    semiInternalCount = semiCount
                )
            }
        )
    }

    // Expense Registration Modal
    if (showExpenseModal) {
        ExpenseRegistrationModal(
            expenditureChapters = uiState.expenditureChapters,
            preselectedArticle = preselectedExpenseArticle,
            onDismiss = { showExpenseModal = false },
            onSubmitExpense = { article, chapterName, voucher, supplier, desc, amount ->
                viewModel.addExpense(
                    article = article,
                    chapterName = chapterName,
                    voucherNumber = voucher,
                    supplierName = supplier,
                    description = desc,
                    amount = amount,
                    forceCommit = false,
                    onCommitted = { showExpenseModal = false }
                )
            }
        )
    }

    // Over-Budget (> 100%) Confirmation Alert Dialog
    if (overBudgetWarning != null) {
        OverBudgetAlertDialog(
            warning = overBudgetWarning!!,
            onConfirmOverride = {
                viewModel.confirmOverBudgetExpense(
                    onCommitted = { showExpenseModal = false }
                )
            },
            onDismiss = { viewModel.dismissOverBudgetWarning() }
        )
    }
}
