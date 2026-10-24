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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ReceiptLong
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
    modifier: Modifier = Modifier
) {
    var showRevenuesAlso by rememberSaveable { mutableStateOf(true) }
    val expandedState = remember {
        mutableStateMapOf<String, Boolean>().apply {
            put("EXP_CH2", true)
            put("EXP_CH6", true)
            put("REV_CH1", true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("chapter_deep_dive_section"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section filter chips (Expenditures + Revenues overview)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "تفاصيل الأبواب والمواد المحاسبية",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            FilterChip(
                selected = showRevenuesAlso,
                onClick = { showRevenuesAlso = !showRevenuesAlso },
                label = {
                    Text(
                        text = if (showRevenuesAlso) "إخفاء باب الإيرادات" else "عرض باب الإيرادات (القسم 1)",
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                modifier = Modifier.testTag("toggle_revenues_visibility_chip")
            )
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
                        text = "القسم الأول: إيرادات التسيير (يتضمن الضخ الآلي لإعانة الإطعام والمنح 111/01)",
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
                        onToggleExpand = {
                            expandedState[chapterWithArticles.chapter.id] = !isExpanded
                        },
                        onRegisterExpenseForArticle = onRegisterExpenseForArticle
                    )
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = EmeraldStable.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, EmeraldStable.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "القسم الثاني: نفقات التسيير - الفرع الأول (أبواب التسيير العام والإطعام 1..7)",
                style = MaterialTheme.typography.labelLarge,
                color = EmeraldStable,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }

        expenditureChapters.forEach { chapterWithArticles ->
            val isExpanded = expandedState[chapterWithArticles.chapter.id] ?: false
            ExecutiveChapterCard(
                chapterWithArticles = chapterWithArticles,
                isExpanded = isExpanded,
                onToggleExpand = {
                    expandedState[chapterWithArticles.chapter.id] = !isExpanded
                },
                onRegisterExpenseForArticle = onRegisterExpenseForArticle
            )
        }
    }
}

@Composable
private fun ExecutiveChapterCard(
    chapterWithArticles: ChapterWithArticles,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
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
            // Chapter Header (Clickable to expand/collapse)
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
                                    text = "0${chapter.chapterNumber}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = statusColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "الباب ${chapter.chapterNumber}: ${chapter.name}",
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
                                    "إيراد معتمد"
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

                // Financial Metrics Row
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

            // Expandable Constituent Articles List
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    chapterWithArticles.articles.forEach { article ->
                        ArticleDeepDiveRow(
                            article = article,
                            isRevenue = isRevenue,
                            onQuickSpend = {
                                onRegisterExpenseForArticle(article, "الباب ${chapter.chapterNumber}: ${chapter.name}")
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

@Composable
private fun ArticleDeepDiveRow(
    article: ArticleEntity,
    isRevenue: Boolean,
    onQuickSpend: () -> Unit
) {
    val statusColor = if (isRevenue) Color(0xFF0EA5E9) else healthColor(article.healthStatus)
    val animatedBar by animateFloatAsState(
        targetValue = (article.consumptionRate.toFloat() / 100f).coerceIn(0f, 1f),
        animationSpec = tween(500),
        label = "articleBar_${article.id}"
    )

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = if (article.isAutoCatering) AmberElevated.copy(alpha = 0.6f)
            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("article_row_${article.id}")
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
                            text = "المادة 0${article.articleNumber}: ${article.name}",
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
                                    text = "مخصص تلقائي بموجب المنشور الوزاري 13/2026",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = AmberElevated
                                )
                            }
                        }
                    }
                }

                if (!isRevenue) {
                    TextButton(
                        onClick = onQuickSpend,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("spend_from_article_${article.id}")
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

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "المعتمد: ${formatDzdCurrency(article.allocatedAmount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (isRevenue) "المحصل: ${formatDzdCurrency(article.spentAmount)}"
                    else "المصروف: ${formatDzdCurrency(article.spentAmount)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = statusColor
                )
                Text(
                    text = "المتبقي: ${formatDzdCurrency(article.remainingCredit)}",
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
        }
    }
}

/**
 * Tab 2: جدول حجز الاعتمادات (Allocations Matrix)
 * Direct inline tap-to-edit matrix for customizing authorized spending limits per article.
 */
@Composable
fun AllocationsMatrixSection(
    expenditureChapters: List<ChapterWithArticles>,
    revenueChapters: List<ChapterWithArticles>,
    onUpdateAllocation: (articleId: String, articleName: String, newAmount: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var editingArticleId by rememberSaveable { mutableStateOf<String?>(null) }
    var editingAmountText by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("allocations_matrix_section"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "جدول حجز الاعتمادات: اضغط على أي مادة لتعديل الاعتماد المرخص به مباشرة مع معاينة فورية للرصيد المتبقي.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        (expenditureChapters + revenueChapters).forEach { chapterWithArticles ->
            val chapter = chapterWithArticles.chapter
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("matrix_chapter_${chapter.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
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
                        Text(
                            text = "الباب ${chapter.chapterNumber}: ${chapter.name}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
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
                            isEditing = isEditing,
                            editingText = if (isEditing) editingAmountText else "",
                            onStartEdit = {
                                editingArticleId = article.id
                                editingAmountText = article.allocatedAmount.toLong().toString()
                            },
                            onEditTextChange = { editingAmountText = it },
                            onSaveEdit = {
                                val parsed = editingAmountText.replace(",", "").toDoubleOrNull()
                                if (parsed != null && parsed >= 0.0) {
                                    onUpdateAllocation(article.id, article.name, parsed)
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

@Composable
private fun AllocationMatrixItemRow(
    article: ArticleEntity,
    isEditing: Boolean,
    editingText: String,
    onStartEdit: () -> Unit,
    onEditTextChange: (String) -> Unit,
    onSaveEdit: () -> Unit,
    onCancelEdit: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isEditing) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (isEditing) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isEditing) { onStartEdit() }
            .testTag("matrix_article_row_${article.id}")
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
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = article.name,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "المصروف الفعلي: ${formatDzdCurrency(article.spentAmount)} • المتبقي: ${formatDzdCurrency(article.remainingCredit)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (!isEditing) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formatDzdWhole(article.allocatedAmount),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldStable
                        )
                        IconButton(
                            onClick = onStartEdit,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("edit_allocation_btn_${article.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "تعديل الاعتماد",
                                tint = MaterialTheme.colorScheme.primary,
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
                        value = editingText,
                        onValueChange = onEditTextChange,
                        label = { Text("الاعتماد المرخص الجديد (د.ج)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("allocation_input_${article.id}")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = {
                                    val current = editingText.toDoubleOrNull() ?: article.allocatedAmount
                                    onEditTextChange((current + 50_000).toLong().toString())
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("+50,000", style = MaterialTheme.typography.labelMedium)
                            }
                            OutlinedButton(
                                onClick = {
                                    val current = editingText.toDoubleOrNull() ?: article.allocatedAmount
                                    onEditTextChange((current - 50_000).coerceAtLeast(0.0).toLong().toString())
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("-50,000", style = MaterialTheme.typography.labelMedium)
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = onCancelEdit,
                                modifier = Modifier.testTag("cancel_allocation_btn_${article.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "إلغاء",
                                    tint = CrimsonCritical
                                )
                            }
                            Button(
                                onClick = onSaveEdit,
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldStable),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("save_allocation_btn_${article.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("حفظ الاعتماد")
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tab 3: سجل الفواتير والمصروفات (Transactions Ledger)
 * Full audit trail of committed expenses with instant search and credit rollback/refund.
 */
@Composable
fun TransactionsLedgerSection(
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
            placeholder = { Text("بحث برقم الحوالة، الممون، المادة المحاسبية، أو البيان...") },
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
                        text = "لا توجد فواتير أو حوالات مطابقة",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "يمكنك تسجيل نفقة جديدة وسيتم خصمها آلياً من اعتماد المادة المختارة.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = onOpenNewExpenseModal) {
                        Text("+ تسجيل حوالة دفع جديدة")
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
                        text = formatLedgerDate(transaction.timestamp),
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
