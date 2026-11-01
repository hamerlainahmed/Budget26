package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.ArticleEntity
import com.example.data.ChapterWithArticles
import com.example.data.Circular13Constants
import com.example.data.SectionType
import com.example.data.TransactionEntity
import com.example.ui.BudgetTab
import com.example.ui.formatDzdCurrency
import com.example.ui.formatDzdWhole
import com.example.ui.formatLedgerDate
import com.example.ui.formatPercentage
import com.example.ui.healthColor
import com.example.ui.healthStatusLabelAr
import com.example.ui.theme.AmberElevated
import com.example.ui.theme.CrimsonCritical
import com.example.ui.theme.EmeraldStable
import kotlin.math.abs

@Composable
fun SegmentedBudgetTabsBar(
    selectedTab: BudgetTab,
    transactionsCount: Int,
    onSelectTab: (BudgetTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("segmented_tabs_bar"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            BudgetTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                val label = if (tab == BudgetTab.TRANSACTIONS_LEDGER) {
                    "${tab.titleAr} ($transactionsCount)"
                } else {
                    tab.titleAr
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Transparent
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectTab(tab) }
                        .testTag("tab_${tab.name.lowercase()}")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChapterDeepDiveSection(
    expenditureChapters: List<ChapterWithArticles>,
    revenueChapters: List<ChapterWithArticles>,
    onRegisterExpenseForArticle: (ArticleEntity, String) -> Unit,
    onUpdateExpenditureAllocation: (articleRecordId: String, articleName: String, newAmount: Double) -> Unit,
    onUpdateRevenueAmounts: (articleRecordId: String, articleName: String, newAllocated: Double, newCollected: Double) -> Unit,
    onGoToCustomizeTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showRevenuesAlso by rememberSaveable { mutableStateOf(true) }
    var editingArticleId by rememberSaveable { mutableStateOf<String?>(null) }
    var editingAllocatedText by rememberSaveable { mutableStateOf("") }
    var editingCollectedText by rememberSaveable { mutableStateOf("") }

    val expandedState = remember {
        mutableStateMapOf<String, Boolean>().apply {
            put(Circular13Constants.EXP_CHAPTER_2_BASE_ID, true)
            put(Circular13Constants.EXP_CHAPTER_6_BASE_ID, true)
            put("REV_CH01", true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("chapter_deep_dive_section"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "مدونة الأبواب والمواد (المنشور 29/2018)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "اضغط «تخصيص الإيراد» أو «تخصيص الاعتماد» أمام أي مادة لتعديل مبالغها مباشرة",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                FilterChip(
                    selected = showRevenuesAlso,
                    onClick = { showRevenuesAlso = !showRevenuesAlso },
                    label = {
                        Text(
                            text = if (showRevenuesAlso) "إخفاء الإيرادات" else "عرض الإيرادات",
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    modifier = Modifier.testTag("toggle_revenues_visibility_chip")
                )

                OutlinedButton(
                    onClick = onGoToCustomizeTab,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("quick_goto_customize_tab_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("جدول التخصيص", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        AnimatedVisibility(visible = showRevenuesAlso) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0EA5E9).copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Color(0xFF0EA5E9).copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "العنوان الأول: الإيرادات (الأبواب 01 - 09 • يمكنك تخصيص الإيراد المقدر والمحصّل لكل مادة)",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF0EA5E9),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }

                revenueChapters.forEach { chapterWithArticles ->
                    val isExpanded = expandedState[chapterWithArticles.chapter.id] ?: false
                    ExecutiveChapterCard(
                        chapterWithArticles = chapterWithArticles,
                        isExpanded = isExpanded,
                        editingArticleId = editingArticleId,
                        editingAllocatedText = editingAllocatedText,
                        editingCollectedText = editingCollectedText,
                        onToggleExpand = {
                            expandedState[chapterWithArticles.chapter.id] = !isExpanded
                        },
                        onStartInlineEdit = { article ->
                            editingArticleId = article.id
                            editingAllocatedText = article.allocatedAmount.toLong().toString()
                            editingCollectedText = article.spentAmount.toLong().toString()
                        },
                        onAllocatedTextChange = { editingAllocatedText = it },
                        onCollectedTextChange = { editingCollectedText = it },
                        onSaveInlineEdit = { article ->
                            val parsedAlloc = editingAllocatedText.replace(",", "").toDoubleOrNull()
                            if (parsedAlloc != null && parsedAlloc >= 0.0) {
                                val parsedColl = editingCollectedText.replace(",", "").toDoubleOrNull() ?: parsedAlloc
                                onUpdateRevenueAmounts(article.id, article.name, parsedAlloc, parsedColl.coerceAtLeast(0.0))
                                editingArticleId = null
                            }
                        },
                        onCancelInlineEdit = { editingArticleId = null },
                        onRegisterExpenseForArticle = onRegisterExpenseForArticle
                    )
                }
            }
        }

        // Group Expenditure Chapters by Branch (الفرع الأول، الفرع الثاني، الفرع الثالث)
        val expByBranch = expenditureChapters.groupBy { it.chapter.branchNumber to it.chapter.branchTitle }
        for ((branchKey, chaptersInBranch) in expByBranch) {
            val branchTitle = branchKey.second
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = EmeraldStable.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, EmeraldStable.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = branchTitle,
                    style = MaterialTheme.typography.labelLarge,
                    color = EmeraldStable,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            chaptersInBranch.forEach { chapterWithArticles ->
                val isExpanded = expandedState[chapterWithArticles.chapter.id] ?: false
                ExecutiveChapterCard(
                    chapterWithArticles = chapterWithArticles,
                    isExpanded = isExpanded,
                    editingArticleId = editingArticleId,
                    editingAllocatedText = editingAllocatedText,
                    editingCollectedText = editingCollectedText,
                    onToggleExpand = {
                        expandedState[chapterWithArticles.chapter.id] = !isExpanded
                    },
                    onStartInlineEdit = { article ->
                        editingArticleId = article.id
                        editingAllocatedText = article.allocatedAmount.toLong().toString()
                        editingCollectedText = article.spentAmount.toLong().toString()
                    },
                    onAllocatedTextChange = { editingAllocatedText = it },
                    onCollectedTextChange = { editingCollectedText = it },
                    onSaveInlineEdit = { article ->
                        val parsedAlloc = editingAllocatedText.replace(",", "").toDoubleOrNull()
                        if (parsedAlloc != null && parsedAlloc >= 0.0) {
                            onUpdateExpenditureAllocation(article.id, article.name, parsedAlloc)
                            editingArticleId = null
                        }
                    },
                    onCancelInlineEdit = { editingArticleId = null },
                    onRegisterExpenseForArticle = onRegisterExpenseForArticle
                )
            }
        }
    }
}

@Composable
private fun ExecutiveChapterCard(
    chapterWithArticles: ChapterWithArticles,
    isExpanded: Boolean,
    editingArticleId: String?,
    editingAllocatedText: String,
    editingCollectedText: String,
    onToggleExpand: () -> Unit,
    onStartInlineEdit: (ArticleEntity) -> Unit,
    onAllocatedTextChange: (String) -> Unit,
    onCollectedTextChange: (String) -> Unit,
    onSaveInlineEdit: (ArticleEntity) -> Unit,
    onCancelInlineEdit: () -> Unit,
    onRegisterExpenseForArticle: (ArticleEntity, String) -> Unit
) {
    val chapter = chapterWithArticles.chapter
    val isRevenue = chapter.sectionType == SectionType.REVENUE.name
    val statusColor = if (isRevenue) Color(0xFF0EA5E9) else healthColor(chapterWithArticles.healthStatus)
    val animatedProgress by animateFloatAsState(
        targetValue = (chapterWithArticles.consumptionRate.toFloat() / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 650),
        label = "chapterProgress_${chapter.id}"
    )
    val paddedChapterNum = chapter.chapterNumber.toString().padStart(2, '0')

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chapter_card_${chapter.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = statusColor.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, statusColor.copy(alpha = 0.4f)),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = paddedChapterNum,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = statusColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${chapter.officialCode}: ${chapter.name}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = chapter.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = statusColor.copy(alpha = 0.14f)
                        ) {
                            Text(
                                text = if (isRevenue) {
                                    "إيراد (${chapterWithArticles.articles.size} مواد)"
                                } else {
                                    "${formatPercentage(chapterWithArticles.consumptionRate)} • ${healthStatusLabelAr(chapterWithArticles.healthStatus)}"
                                },
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = statusColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "طي الباب" else "توسيع الباب",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ChapterMetricColumn(
                        label = if (isRevenue) "الإيراد المقدر" else "الاعتماد المفتوح",
                        value = formatDzdCurrency(chapterWithArticles.totalAllocated),
                        valueColor = MaterialTheme.colorScheme.onSurface
                    )
                    ChapterMetricColumn(
                        label = if (isRevenue) "المحصل فعلياً" else "النفقات المسددة",
                        value = formatDzdCurrency(chapterWithArticles.totalSpent),
                        valueColor = statusColor
                    )
                    ChapterMetricColumn(
                        label = if (isRevenue) "الباقي للتحصيل" else "الرصيد المتبقي",
                        value = formatDzdCurrency(chapterWithArticles.remainingCredit),
                        valueColor = if (chapterWithArticles.remainingCredit >= 0) EmeraldStable else CrimsonCritical
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    color = statusColor,
                    trackColor = statusColor.copy(alpha = 0.14f),
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    chapterWithArticles.articles.forEach { article ->
                        val isEditingThis = editingArticleId == article.id
                        ArticleDeepDiveRow(
                            article = article,
                            isRevenue = isRevenue,
                            isEditing = isEditingThis,
                            editingAllocatedText = if (isEditingThis) editingAllocatedText else "",
                            editingCollectedText = if (isEditingThis) editingCollectedText else "",
                            onStartCustomize = { onStartInlineEdit(article) },
                            onAllocatedTextChange = onAllocatedTextChange,
                            onCollectedTextChange = onCollectedTextChange,
                            onSaveCustomize = { onSaveInlineEdit(article) },
                            onCancelCustomize = onCancelInlineEdit,
                            onQuickSpend = {
                                onRegisterExpenseForArticle(
                                    article,
                                    "${chapter.officialCode}: ${chapter.name}"
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChapterMetricColumn(
    label: String,
    value: String,
    valueColor: Color
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = valueColor
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ArticleDeepDiveRow(
    article: ArticleEntity,
    isRevenue: Boolean,
    isEditing: Boolean,
    editingAllocatedText: String,
    editingCollectedText: String,
    onStartCustomize: () -> Unit,
    onAllocatedTextChange: (String) -> Unit,
    onCollectedTextChange: (String) -> Unit,
    onSaveCustomize: () -> Unit,
    onCancelCustomize: () -> Unit,
    onQuickSpend: () -> Unit
) {
    val statusColor = if (isRevenue) Color(0xFF0EA5E9) else healthColor(article.healthStatus)
    val animatedBar by animateFloatAsState(
        targetValue = (article.consumptionRate.toFloat() / 100f).coerceIn(0f, 1f),
        animationSpec = tween(500),
        label = "articleBar_${article.id}"
    )
    val paddedArtNum = article.articleNumber.toString().padStart(2, '0')

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (isEditing) 1.5.dp else 1.dp,
            color = when {
                isEditing -> statusColor
                article.isAutoCatering -> AmberElevated.copy(alpha = 0.6f)
                else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("article_row_${article.baseArticleId}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusColor.copy(alpha = 0.14f)
                    ) {
                        Text(
                            text = article.officialCode,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "المادة $paddedArtNum: ${article.name}",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (article.isAutoCatering) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AmberElevated,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "يحسب تلقائياً بموجب المنشور 13/2026 (قابل للتخصيص أيضاً)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AmberElevated
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons Row: Customize Revenue / Customize Allocation + Spend Expense
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        if (isEditing) onCancelCustomize() else onStartCustomize()
                    },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.55f)),
                    modifier = Modifier.testTag("customize_article_btn_${article.baseArticleId}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isRevenue) "تخصيص الإيراد" else "تخصيص الاعتماد",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = statusColor
                    )
                }

                if (!isRevenue) {
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = onQuickSpend,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("spend_from_article_${article.baseArticleId}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "صرف نفقة",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isRevenue) "المقدر: ${formatDzdCurrency(article.allocatedAmount)}"
                    else "المعتمد: ${formatDzdCurrency(article.allocatedAmount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (isRevenue) "المحصّل: ${formatDzdCurrency(article.spentAmount)}"
                    else "المصروف: ${formatDzdCurrency(article.spentAmount)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = statusColor
                )
                Text(
                    text = if (isRevenue) "باقي التحصيل: ${formatDzdCurrency(article.remainingCredit)}"
                    else "المتبقي: ${formatDzdCurrency(article.remainingCredit)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = if (article.remainingCredit >= 0) EmeraldStable else CrimsonCritical
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = { animatedBar },
                    color = statusColor,
                    trackColor = statusColor.copy(alpha = 0.14f),
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                )
                Text(
                    text = formatPercentage(article.consumptionRate),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = statusColor
                )
            }

            // Inline Expandable Editor for Customizing Revenue or Expenditure Allocation directly in Tab 1
            AnimatedVisibility(visible = isEditing) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(color = statusColor.copy(alpha = 0.3f))

                    OutlinedTextField(
                        value = editingAllocatedText,
                        onValueChange = { input ->
                            onAllocatedTextChange(input.filter { it.isDigit() || it == '.' })
                        },
                        label = {
                            Text(
                                if (isRevenue) "الإيراد المقدر / المفتوح للسنة (د.ج)"
                                else "الاعتماد المفتوح / المرخص للمادة (د.ج)"
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("deep_dive_alloc_input_${article.baseArticleId}")
                    )

                    if (isRevenue) {
                        OutlinedTextField(
                            value = editingCollectedText,
                            onValueChange = { input ->
                                onCollectedTextChange(input.filter { it.isDigit() || it == '.' })
                            },
                            label = { Text("الإيراد المحصّل فعلياً حتى الآن (د.ج)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("deep_dive_collected_input_${article.baseArticleId}")
                        )
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = {
                                val current = editingAllocatedText.toDoubleOrNull() ?: article.allocatedAmount
                                val next = (current + 50_000).toLong().toString()
                                onAllocatedTextChange(next)
                                if (isRevenue) onCollectedTextChange(next)
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("+50,000", style = MaterialTheme.typography.labelSmall)
                        }

                        OutlinedButton(
                            onClick = {
                                val current = editingAllocatedText.toDoubleOrNull() ?: article.allocatedAmount
                                val next = (current + 100_000).toLong().toString()
                                onAllocatedTextChange(next)
                                if (isRevenue) onCollectedTextChange(next)
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("+100,000", style = MaterialTheme.typography.labelSmall)
                        }

                        OutlinedButton(
                            onClick = {
                                onAllocatedTextChange("0")
                                if (isRevenue) onCollectedTextChange("0")
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("تصفير (0)", style = MaterialTheme.typography.labelSmall)
                        }

                        if (isRevenue) {
                            OutlinedButton(
                                onClick = {
                                    onCollectedTextChange(editingAllocatedText)
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("تحصيل 100% من المقدر", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onCancelCustomize) {
                            Text("إلغاء", color = CrimsonCritical)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = onSaveCustomize,
                            colors = ButtonDefaults.buttonColors(containerColor = statusColor),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("deep_dive_save_btn_${article.baseArticleId}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isRevenue) "حفظ الإيراد" else "حفظ الاعتماد")
                        }
                    }
                }
            }
        }
    }
}

private enum class MatrixSectionFilter(val labelAr: String) {
    ALL("الكل (الإيرادات + النفقات)"),
    REVENUES_ONLY("العنوان 1: الإيرادات (9 أبواب)"),
    EXPENDITURES_ONLY("العنوان 2: النفقات (17 باباً)")
}

/**
 * Tab 2: تخصيص الإيرادات واعتمادات النفقات (Budget Revenues & Allocations Customization Studio)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AllocationsMatrixSection(
    fiscalYear: Int,
    expenditureChapters: List<ChapterWithArticles>,
    revenueChapters: List<ChapterWithArticles>,
    onUpdateAllocation: (articleRecordId: String, articleName: String, newAmount: Double) -> Unit,
    onUpdateRevenueAmounts: (articleRecordId: String, articleName: String, newAllocated: Double, newCollected: Double) -> Unit,
    onZeroOutCustomRevenues: () -> Unit,
    onZeroOutCustomExpenditures: () -> Unit,
    modifier: Modifier = Modifier
) {
    var sectionFilter by rememberSaveable { mutableStateOf(MatrixSectionFilter.ALL) }
    var filterQuery by rememberSaveable { mutableStateOf("") }
    var editingArticleId by rememberSaveable { mutableStateOf<String?>(null) }
    var editingAmountText by rememberSaveable { mutableStateOf("") }
    var editingCollectedText by rememberSaveable { mutableStateOf("") }

    val totalRevenuesAllocated = remember(revenueChapters) {
        revenueChapters.sumOf { it.totalAllocated }
    }
    val totalRevenuesCollected = remember(revenueChapters) {
        revenueChapters.sumOf { it.totalSpent }
    }
    val totalExpendituresAllocated = remember(expenditureChapters) {
        expenditureChapters.sumOf { it.totalAllocated }
    }
    val budgetBalanceDiff = totalRevenuesAllocated - totalExpendituresAllocated
    val isBalanced = abs(budgetBalanceDiff) < 1.0

    val chaptersToShow = remember(sectionFilter, filterQuery, revenueChapters, expenditureChapters) {
        val baseList = when (sectionFilter) {
            MatrixSectionFilter.ALL -> revenueChapters + expenditureChapters
            MatrixSectionFilter.REVENUES_ONLY -> revenueChapters
            MatrixSectionFilter.EXPENDITURES_ONLY -> expenditureChapters
        }
        if (filterQuery.isBlank()) {
            baseList
        } else {
            val q = filterQuery.trim()
            baseList.mapNotNull { chWithArts ->
                val chapterMatches = chWithArts.chapter.name.contains(q, ignoreCase = true) ||
                    chWithArts.chapter.officialCode.contains(q, ignoreCase = true)
                if (chapterMatches) {
                    chWithArts
                } else {
                    val matchingArticles = chWithArts.articles.filter { art ->
                        art.name.contains(q, ignoreCase = true) ||
                            art.officialCode.contains(q, ignoreCase = true)
                    }
                    if (matchingArticles.isNotEmpty()) {
                        chWithArts.copy(articles = matchingArticles)
                    } else {
                        null
                    }
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("allocations_matrix_section"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Live Budget Equilibrium Card (توازن الإيرادات واعتمادات النفقات)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("budget_equilibrium_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                width = 1.dp,
                color = when {
                    isBalanced -> EmeraldStable.copy(alpha = 0.5f)
                    budgetBalanceDiff > 0 -> Color(0xFF0EA5E9).copy(alpha = 0.5f)
                    else -> CrimsonCritical.copy(alpha = 0.6f)
                }
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = Color(0xFF0EA5E9),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ميزان تخصيص الميزانية لسنة $fiscalYear (الإيرادات ⇄ النفقات)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = when {
                            isBalanced -> EmeraldStable.copy(alpha = 0.16f)
                            budgetBalanceDiff > 0 -> Color(0xFF0EA5E9).copy(alpha = 0.16f)
                            else -> CrimsonCritical.copy(alpha = 0.16f)
                        }
                    ) {
                        Text(
                            text = when {
                                isBalanced -> "ميزانية متوازنة 100%"
                                budgetBalanceDiff > 0 -> "فائض إيرادات للتوزيع"
                                else -> "الاعتمادات تفوق الإيرادات"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = when {
                                isBalanced -> EmeraldStable
                                budgetBalanceDiff > 0 -> Color(0xFF0EA5E9)
                                else -> CrimsonCritical
                            },
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "إجمالي الإيرادات (العنوان 1)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatDzdCurrency(totalRevenuesAllocated),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF0EA5E9)
                        )
                        Text(
                            text = "المحصّل: ${formatDzdWhole(totalRevenuesCollected)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column {
                        Text(
                            text = "اعتمادات النفقات (العنوان 2)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatDzdCurrency(totalExpendituresAllocated),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldStable
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (budgetBalanceDiff >= 0) "رصيد إيرادات غير موزع" else "عجز التوازن",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatDzdCurrency(abs(budgetBalanceDiff)),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = when {
                                isBalanced -> EmeraldStable
                                budgetBalanceDiff > 0 -> AmberElevated
                                else -> CrimsonCritical
                            }
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                // Quick Bulk Reset / Manual Customization Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onZeroOutCustomRevenues,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("zero_out_revenues_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = null,
                            tint = Color(0xFF0EA5E9),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "تصفير الإيرادات لتخصيصها يدوياً",
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    OutlinedButton(
                        onClick = onZeroOutCustomExpenditures,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("zero_out_expenditures_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = null,
                            tint = AmberElevated,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "تصفير اعتمادات النفقات يدوياً",
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // 2. Section Filter Chips (الكل / الإيرادات / النفقات) + Search Field
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MatrixSectionFilter.entries.forEach { filter ->
                FilterChip(
                    selected = sectionFilter == filter,
                    onClick = { sectionFilter = filter },
                    label = {
                        Text(
                            text = filter.labelAr,
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    modifier = Modifier.testTag("matrix_filter_${filter.name.lowercase()}")
                )
            }
        }

        OutlinedTextField(
            value = filterQuery,
            onValueChange = { filterQuery = it },
            placeholder = { Text("ابحث عن باب أو مادة لتخصيص إيرادها أو اعتمادها (مثال: الباب 02، الإطعام، ب04/م01)...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "بحث"
                )
            },
            trailingIcon = {
                if (filterQuery.isNotEmpty()) {
                    IconButton(onClick = { filterQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "مسح"
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("matrix_search_input")
        )

        // 3. Chapters & Articles Customization Cards
        chaptersToShow.forEach { chapterWithArticles ->
            val chapter = chapterWithArticles.chapter
            val isRevenueChapter = chapter.sectionType == SectionType.REVENUE.name
            val accentColor = if (isRevenueChapter) Color(0xFF0EA5E9) else EmeraldStable

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("matrix_chapter_${chapter.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isRevenueChapter) "العنوان الأول (إيرادات) • ${chapter.officialCode}: ${chapter.name}"
                                else "العنوان الثاني (نفقات) • ${chapter.officialCode}: ${chapter.name}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = accentColor
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = formatDzdWhole(chapterWithArticles.totalAllocated),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                    chapterWithArticles.articles.forEach { article ->
                        val isEditing = editingArticleId == article.id
                        AllocationMatrixItemRow(
                            article = article,
                            isRevenue = isRevenueChapter,
                            isEditing = isEditing,
                            editingAllocatedText = if (isEditing) editingAmountText else "",
                            editingCollectedText = if (isEditing) editingCollectedText else "",
                            unallocatedRevenueSurplus = if (budgetBalanceDiff > 0) budgetBalanceDiff else 0.0,
                            onStartEdit = {
                                editingArticleId = article.id
                                editingAmountText = article.allocatedAmount.toLong().toString()
                                editingCollectedText = article.spentAmount.toLong().toString()
                            },
                            onEditAllocatedChange = { editingAmountText = it },
                            onEditCollectedChange = { editingCollectedText = it },
                            onSaveEdit = {
                                val parsedAlloc = editingAmountText.replace(",", "").toDoubleOrNull()
                                if (parsedAlloc != null && parsedAlloc >= 0.0) {
                                    if (isRevenueChapter) {
                                        val parsedCollected =
                                            editingCollectedText.replace(",", "").toDoubleOrNull() ?: parsedAlloc
                                        onUpdateRevenueAmounts(
                                            article.id,
                                            article.name,
                                            parsedAlloc,
                                            parsedCollected.coerceAtLeast(0.0)
                                        )
                                    } else {
                                        onUpdateAllocation(article.id, article.name, parsedAlloc)
                                    }
                                    editingArticleId = null
                                }
                            },
                            onCancelEdit = { editingArticleId = null }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AllocationMatrixItemRow(
    article: ArticleEntity,
    isRevenue: Boolean,
    isEditing: Boolean,
    editingAllocatedText: String,
    editingCollectedText: String,
    unallocatedRevenueSurplus: Double,
    onStartEdit: () -> Unit,
    onEditAllocatedChange: (String) -> Unit,
    onEditCollectedChange: (String) -> Unit,
    onSaveEdit: () -> Unit,
    onCancelEdit: () -> Unit
) {
    val paddedArtNum = article.articleNumber.toString().padStart(2, '0')
    val accentColor = if (isRevenue) Color(0xFF0EA5E9) else EmeraldStable

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isEditing) {
            accentColor.copy(alpha = 0.10f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        },
        border = BorderStroke(
            width = if (isEditing) 1.5.dp else 1.dp,
            color = if (isEditing) accentColor
            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isEditing) { onStartEdit() }
            .testTag("matrix_article_row_${article.baseArticleId}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "[${article.officialCode}]",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = accentColor
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "المادة $paddedArtNum: ${article.name}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = if (isRevenue) {
                            "المحصّل الفعلي: ${formatDzdCurrency(article.spentAmount)} • الباقي للتحصيل: ${formatDzdCurrency(article.remainingCredit)}"
                        } else {
                            "المصروف الفعلي: ${formatDzdCurrency(article.spentAmount)} • المتبقي للصرف: ${formatDzdCurrency(article.remainingCredit)}"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (!isEditing) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (isRevenue) "الإيراد المقدر" else "الاعتماد المفتوح",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatDzdWhole(article.allocatedAmount),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = accentColor
                            )
                        }
                        IconButton(
                            onClick = onStartEdit,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("edit_allocation_btn_${article.baseArticleId}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = if (isRevenue) "تخصيص الإيراد" else "تعديل الاعتماد",
                                tint = accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = isEditing) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = editingAllocatedText,
                        onValueChange = { input ->
                            onEditAllocatedChange(input.filter { it.isDigit() || it == '.' })
                        },
                        label = {
                            Text(
                                if (isRevenue) "الإيراد المقدر / المفتوح للسنة (د.ج)"
                                else "الاعتماد المفتوح / المرخص للمادة (د.ج)"
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("allocation_input_${article.baseArticleId}")
                    )

                    if (isRevenue) {
                        OutlinedTextField(
                            value = editingCollectedText,
                            onValueChange = { input ->
                                onEditCollectedChange(input.filter { it.isDigit() || it == '.' })
                            },
                            label = { Text("الإيراد المحصّل فعلياً (د.ج)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("revenue_collected_input_${article.baseArticleId}")
                        )
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = {
                                val current = editingAllocatedText.toDoubleOrNull() ?: article.allocatedAmount
                                val next = (current + 50_000).toLong().toString()
                                onEditAllocatedChange(next)
                                if (isRevenue) onEditCollectedChange(next)
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("+50,000", style = MaterialTheme.typography.labelMedium)
                        }

                        OutlinedButton(
                            onClick = {
                                val current = editingAllocatedText.toDoubleOrNull() ?: article.allocatedAmount
                                val next = (current + 100_000).toLong().toString()
                                onEditAllocatedChange(next)
                                if (isRevenue) onEditCollectedChange(next)
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("+100,000", style = MaterialTheme.typography.labelMedium)
                        }

                        OutlinedButton(
                            onClick = {
                                val current = editingAllocatedText.toDoubleOrNull() ?: article.allocatedAmount
                                val next = (current - 50_000).coerceAtLeast(0.0).toLong().toString()
                                onEditAllocatedChange(next)
                                if (isRevenue) onEditCollectedChange(next)
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("-50,000", style = MaterialTheme.typography.labelMedium)
                        }

                        OutlinedButton(
                            onClick = {
                                onEditAllocatedChange("0")
                                if (isRevenue) onEditCollectedChange("0")
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("تصفير (0)", style = MaterialTheme.typography.labelMedium)
                        }

                        if (isRevenue) {
                            OutlinedButton(
                                onClick = {
                                    onEditCollectedChange(editingAllocatedText)
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("تحصيل 100% من المقدر", style = MaterialTheme.typography.labelMedium)
                            }
                        } else if (unallocatedRevenueSurplus > 0.0) {
                            OutlinedButton(
                                onClick = {
                                    val current = editingAllocatedText.toDoubleOrNull() ?: article.allocatedAmount
                                    val balancedAmount = (current + unallocatedRevenueSurplus).toLong().toString()
                                    onEditAllocatedChange(balancedAmount)
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "+ فائض الإيرادات (${formatDzdWhole(unallocatedRevenueSurplus)})",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF0EA5E9)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onCancelEdit,
                            modifier = Modifier.testTag("cancel_allocation_btn_${article.baseArticleId}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إلغاء",
                                tint = CrimsonCritical
                            )
                        }
                        Button(
                            onClick = onSaveEdit,
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("save_allocation_btn_${article.baseArticleId}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isRevenue) "حفظ الإيراد" else "حفظ الاعتماد")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tab 3: سجل الفواتير والمصروفات (Transactions Ledger)
 */
@Composable
fun TransactionsLedgerSection(
    fiscalYear: Int,
    transactions: List<TransactionEntity>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onDeleteAndRefundTransaction: (TransactionEntity) -> Unit,
    onOpenNewExpenseModal: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("transactions_ledger_section"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("بحث في سجل السنة المالية $fiscalYear برقم الحوالة، الممون، أو المادة...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "بحث"
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "مسح البحث"
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ledger_search_input")
        )

        if (transactions.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "سجل النفقات للسنة المالية $fiscalYear فارغ حالياً (0 حوالات)",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "تبدأ كل سنة مالية جديدة بسجل نفقات فارغ ومستقل، مع حفظ سجلات السنوات السابقة في الأرشيف.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = onOpenNewExpenseModal) {
                        Text("+ تسجيل حوالة دفع في سنة $fiscalYear")
                    }
                }
            }
        } else {
            transactions.forEach { tx ->
                TransactionLedgerCard(
                    transaction = tx,
                    onRollback = { onDeleteAndRefundTransaction(tx) }
                )
            }
        }
    }
}

@Composable
private fun TransactionLedgerCard(
    transaction: TransactionEntity,
    onRollback: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("transaction_card_${transaction.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AmberElevated.copy(alpha = 0.16f)
                    ) {
                        Text(
                            text = transaction.voucherNumber,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = AmberElevated,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${formatLedgerDate(transaction.timestamp)} • سنة ${transaction.fiscalYear}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "- ${formatDzdCurrency(transaction.amount)}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = CrimsonCritical
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = transaction.description,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "الممون: ${transaction.supplierName} • [${transaction.articleCode}] ${transaction.articleName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = transaction.chapterName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onRollback,
                    border = BorderStroke(1.dp, CrimsonCritical.copy(alpha = 0.55f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonCritical),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("rollback_tx_btn_${transaction.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "إلغاء واسترجاع الاعتماد",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
