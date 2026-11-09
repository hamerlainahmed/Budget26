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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.ui.theme.DarkTextPrimary
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
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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
                        .clickable { onSelectTab(tab) }
                        .testTag("tab_button_${tab.name}")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(
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

/**
 * Tab 1: مدونة الأبواب والمواد (نفقات فقط حسب مدونة 2024 الرسمية)
 */
@Composable
fun ChapterDeepDiveSection(
    expenditureChapters: List<ChapterWithArticles>,
    onRegisterExpenseForArticle: (ArticleEntity, String) -> Unit,
    onViewCommitmentCard: (ArticleEntity, String) -> Unit,
    onUpdateExpenditureAllocation: (articleRecordId: String, articleName: String, newAmount: Double) -> Unit,
    onGoToCustomizeTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    var editingArticleId by rememberSaveable { mutableStateOf<String?>(null) }
    var editingAllocatedText by rememberSaveable { mutableStateOf("") }

    val expandedState = remember {
        mutableStateMapOf<String, Boolean>().apply {
            put(Circular13Constants.EXP_CHAPTER_2_BASE_ID, true)
            put(Circular13Constants.EXP_CHAPTER_6_BASE_ID, true)
            put("EXP_CH01", true)
            put("EXP_CH03", true)
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
                    text = "مدونة أبواب ومواد النفقات",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "متابعة استهلاك الاعتماد واقتطاع النفقات لكل مادة لحظة بلحظة",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

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
                Text("حجز الاعتمادات", style = MaterialTheme.typography.labelMedium)
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
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
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
                    onToggleExpand = {
                        expandedState[chapterWithArticles.chapter.id] = !isExpanded
                    },
                    onStartInlineEdit = { article ->
                        editingArticleId = article.id
                        editingAllocatedText = article.allocatedAmount.toLong().toString()
                    },
                    onAllocatedTextChange = { editingAllocatedText = it },
                    onSaveInlineEdit = { article ->
                        val parsed = editingAllocatedText.replace(",", "").toDoubleOrNull()
                        if (parsed != null && parsed >= 0.0) {
                            onUpdateExpenditureAllocation(article.id, article.name, parsed)
                            editingArticleId = null
                        }
                    },
                    onCancelInlineEdit = { editingArticleId = null },
                    onRegisterExpenseForArticle = onRegisterExpenseForArticle,
                    onViewCommitmentCard = onViewCommitmentCard
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
    onToggleExpand: () -> Unit,
    onStartInlineEdit: (ArticleEntity) -> Unit,
    onAllocatedTextChange: (String) -> Unit,
    onSaveInlineEdit: (ArticleEntity) -> Unit,
    onCancelInlineEdit: () -> Unit,
    onRegisterExpenseForArticle: (ArticleEntity, String) -> Unit,
    onViewCommitmentCard: (ArticleEntity, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val chapter = chapterWithArticles.chapter
    val statusColor = healthColor(chapterWithArticles.healthStatus)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("executive_chapter_card_${chapter.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (isExpanded) statusColor.copy(alpha = 0.45f)
            else MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Chapter Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = statusColor.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.45f)),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = chapter.chapterNumber.toString().padStart(2, '0'),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = statusColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${chapter.officialCode}: ${chapter.name}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Text(
                            text = "${chapterWithArticles.articles.size} مواد • الاعتماد: ${formatDzdWhole(chapterWithArticles.totalAllocated)} • المصروف: ${formatDzdWhole(chapterWithArticles.totalSpent)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = statusColor.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = formatPercentage(chapterWithArticles.consumptionRate),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(onClick = onToggleExpand, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "طي" else "توسيع",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Expanded Chapter Content
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // 3 KPI summary metrics for Chapter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MiniKpiBox(
                            label = "الاعتماد المفتوح",
                            amount = formatDzdWhole(chapterWithArticles.totalAllocated),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        MiniKpiBox(
                            label = "النفقات المسددة",
                            amount = formatDzdWhole(chapterWithArticles.totalSpent),
                            color = AmberElevated,
                            modifier = Modifier.weight(1f)
                        )
                        MiniKpiBox(
                            label = "الرصيد المتبقي",
                            amount = formatDzdWhole(chapterWithArticles.remainingCredit),
                            color = if (chapterWithArticles.remainingCredit >= 0) EmeraldStable else CrimsonCritical,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Chapter Consumption Progress Bar
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "نسبة استهلاك اعتماد الباب:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${formatPercentage(chapterWithArticles.consumptionRate)} (${healthStatusLabelAr(chapterWithArticles.healthStatus)})",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = statusColor
                            )
                        }

                        LinearProgressIndicator(
                            progress = { (chapterWithArticles.consumptionRate.toFloat() / 100f).coerceIn(0f, 1f) },
                            color = statusColor,
                            trackColor = statusColor.copy(alpha = 0.15f),
                            strokeCap = StrokeCap.Round,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(50))
                        )
                    }

                    // Articles list inside this Chapter
                    chapterWithArticles.articles.forEach { article ->
                        val isEditing = editingArticleId == article.id
                        ArticleRowCard(
                            article = article,
                            chapterOfficialName = "${chapter.officialCode}: ${chapter.name}",
                            isEditing = isEditing,
                            editingAllocatedText = editingAllocatedText,
                            onStartCustomize = { onStartInlineEdit(article) },
                            onAllocatedTextChange = onAllocatedTextChange,
                            onSaveCustomize = { onSaveInlineEdit(article) },
                            onCancelCustomize = onCancelInlineEdit,
                            onRegisterExpense = {
                                onRegisterExpenseForArticle(article, "${chapter.officialCode}: ${chapter.name}")
                            },
                            onViewCommitmentCard = {
                                onViewCommitmentCard(article, "${chapter.officialCode}: ${chapter.name}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniKpiBox(
    label: String,
    amount: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = amount,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ArticleRowCard(
    article: ArticleEntity,
    chapterOfficialName: String,
    isEditing: Boolean,
    editingAllocatedText: String,
    onStartCustomize: () -> Unit,
    onAllocatedTextChange: (String) -> Unit,
    onSaveCustomize: () -> Unit,
    onCancelCustomize: () -> Unit,
    onRegisterExpense: () -> Unit,
    onViewCommitmentCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = healthColor(article.healthStatus)
    val animatedBar by animateFloatAsState(
        targetValue = (article.consumptionRate.toFloat() / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 650),
        label = "articleBar"
    )

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(
            1.dp,
            if (isEditing) statusColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = statusColor.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, statusColor.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = article.officialCode,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = statusColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = article.name,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (article.description.isNotBlank()) {
                        Text(
                            text = article.description,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onViewCommitmentCard,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("article_commitment_card_btn_${article.baseArticleId}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = "بطاقة الالتزام",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    IconButton(
                        onClick = onStartCustomize,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("inline_edit_alloc_btn_${article.baseArticleId}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "حجز / تعديل الاعتماد",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onRegisterExpense,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("article_quick_expense_btn_${article.baseArticleId}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "تحرير سند طلب للمادة",
                            tint = EmeraldStable,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Article metrics row: Allocated, Spent, Remaining
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الاعتماد: ${formatDzdWhole(article.allocatedAmount)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "المصروف: ${formatDzdWhole(article.spentAmount)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmberElevated
                )
                Text(
                    text = "المتبقي: ${formatDzdWhole(article.remainingCredit)}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
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

            // Quick bottom row: Direct link to بطاقة الالتزام
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onViewCommitmentCard,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("article_view_fiche_btn_${article.baseArticleId}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "الإطلاع على بطاقة الالتزام",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (article.isAutoCatering) {
                    Text(
                        text = "مخصص آلي (المنشور 13/2026)",
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldStable
                    )
                }
            }

            // Inline Editor for Customizing Expenditure Allocation directly
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
                        label = { Text("الاعتماد المفتوح المحجوز للمادة (د.ج)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("deep_dive_alloc_input_${article.baseArticleId}")
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = {
                                val current = editingAllocatedText.toDoubleOrNull() ?: article.allocatedAmount
                                onAllocatedTextChange((current + 50_000).toLong().toString())
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("+50,000", style = MaterialTheme.typography.labelSmall)
                        }

                        OutlinedButton(
                            onClick = {
                                val current = editingAllocatedText.toDoubleOrNull() ?: article.allocatedAmount
                                onAllocatedTextChange((current + 100_000).toLong().toString())
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("+100,000", style = MaterialTheme.typography.labelSmall)
                        }

                        OutlinedButton(
                            onClick = {
                                onAllocatedTextChange("0")
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("تصفير (0)", style = MaterialTheme.typography.labelSmall)
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
                            Text("حفظ الاعتماد")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tab 2: حجز الاعتمادات (Credit Allocation Studio for the Authorizing Officer)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AllocationsMatrixSection(
    fiscalYear: Int,
    expenditureChapters: List<ChapterWithArticles>,
    onUpdateAllocation: (articleRecordId: String, articleName: String, newAmount: Double) -> Unit,
    onZeroOutCustomExpenditures: () -> Unit,
    modifier: Modifier = Modifier
) {
    var filterQuery by rememberSaveable { mutableStateOf("") }
    var editingArticleId by rememberSaveable { mutableStateOf<String?>(null) }
    var editingAmountText by rememberSaveable { mutableStateOf("") }

    val totalExpendituresAllocated = remember(expenditureChapters) {
        expenditureChapters.sumOf { it.totalAllocated }
    }

    val chaptersToShow = remember(filterQuery, expenditureChapters) {
        if (filterQuery.isBlank()) {
            expenditureChapters
        } else {
            val q = filterQuery.trim()
            expenditureChapters.mapNotNull { ch ->
                val matchingArticles = ch.articles.filter { art ->
                    art.name.contains(q, ignoreCase = true) ||
                        art.officialCode.contains(q, ignoreCase = true) ||
                        art.description.contains(q, ignoreCase = true)
                }
                if (matchingArticles.isNotEmpty() || ch.chapter.name.contains(q, ignoreCase = true)) {
                    ch.copy(articles = if (matchingArticles.isNotEmpty()) matchingArticles else ch.articles)
                } else null
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("allocations_matrix_section"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Studio Executive Summary Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "جدول حجز اعتمادات نفقات السنة المالية $fiscalYear",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "يحجز الآمر بالصرف الاعتمادات المفتوحة لكل مادة لضبط ومتابعة سقف الصرف",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedButton(
                        onClick = onZeroOutCustomExpenditures,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, AmberElevated),
                        modifier = Modifier.testTag("zero_out_expenditures_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = null,
                            tint = AmberElevated,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "تصفير الاعتمادات",
                            style = MaterialTheme.typography.labelMedium,
                            color = AmberElevated
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "مجموع الاعتمادات المحجوزة في الميزانية:",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = formatDzdCurrency(totalExpendituresAllocated),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("matrix_total_allocated_text")
                        )
                    }
                }
            }
        }

        // Live Search Input inside Allocations Matrix
        OutlinedTextField(
            value = filterQuery,
            onValueChange = { filterQuery = it },
            placeholder = { Text("ابحث عن أي مادة أو باب لحجز الاعتماد (مثال: ب02/م01 أو التدفئة)...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("matrix_search_input")
        )

        // Render Chapters & Articles for Allocation
        chaptersToShow.forEach { chapterWithArticles ->
            val chapter = chapterWithArticles.chapter

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("matrix_chapter_block_${chapter.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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
                            text = "${chapter.officialCode}: ${chapter.name}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldStable,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "المجموع: ${formatDzdWhole(chapterWithArticles.totalAllocated)}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    chapterWithArticles.articles.forEach { article ->
                        val isEditing = editingArticleId == article.id

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = BorderStroke(
                                1.dp,
                                if (isEditing) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "[${article.officialCode}] ${article.name}",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (article.isAutoCatering) {
                                            Text(
                                                text = "مخصص آلي (المنشور 13/2026)",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = EmeraldStable
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = formatDzdCurrency(article.allocatedAmount),
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        IconButton(
                                            onClick = {
                                                if (isEditing) {
                                                    editingArticleId = null
                                                } else {
                                                    editingArticleId = article.id
                                                    editingAmountText = article.allocatedAmount.toLong().toString()
                                                }
                                            },
                                            modifier = Modifier
                                                .size(32.dp)
                                                .testTag("matrix_edit_btn_${article.baseArticleId}")
                                        ) {
                                            Icon(
                                                imageVector = if (isEditing) Icons.Default.Close else Icons.Default.Edit,
                                                contentDescription = "حجز الاعتماد",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                AnimatedVisibility(visible = isEditing) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = editingAmountText,
                                            onValueChange = { input ->
                                                editingAmountText = input.filter { it.isDigit() || it == '.' }
                                            },
                                            label = { Text("قيمة الاعتماد المفتوح (د.ج)") },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("matrix_alloc_input_${article.baseArticleId}")
                                        )

                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            listOf(50_000, 100_000, 250_000, 500_000).forEach { bump ->
                                                OutlinedButton(
                                                    onClick = {
                                                        val current = editingAmountText.toDoubleOrNull() ?: article.allocatedAmount
                                                        editingAmountText = (current + bump).toLong().toString()
                                                    },
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    Text("+$bump", style = MaterialTheme.typography.labelSmall)
                                                }
                                            }

                                            OutlinedButton(
                                                onClick = { editingAmountText = "0" },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text("تصفير (0)", style = MaterialTheme.typography.labelSmall)
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            TextButton(onClick = { editingArticleId = null }) {
                                                Text("إلغاء", color = CrimsonCritical)
                                            }

                                            Spacer(modifier = Modifier.width(6.dp))

                                            Button(
                                                onClick = {
                                                    val parsed = editingAmountText.replace(",", "").toDoubleOrNull()
                                                    if (parsed != null && parsed >= 0.0) {
                                                        onUpdateAllocation(article.id, article.name, parsed)
                                                        editingArticleId = null
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldStable),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.testTag("matrix_save_btn_${article.baseArticleId}")
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
            }
        }
    }
}

/**
 * Tab 3: سجل سندات الطلب والالتزامات (Bons de Commande & Commitments Ledger)
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
    val totalSpentLedger = remember(transactions) {
        transactions.sumOf { it.amount }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("transactions_ledger_section"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "سجل سندات الطلب والالتزامات للسنة المالية $fiscalYear",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "إجمالي الالتزامات في السجل: ${formatDzdCurrency(totalSpentLedger)} (${transactions.size} سند طلب)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onOpenNewExpenseModal,
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldStable,
                    contentColor = Color(0xFF022C22)
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag("ledger_new_expense_btn")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("سند طلب جديد", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("ابحث برقم سند الطلب، اسم الممون، بيان النفقة أو المادة...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ledger_search_input")
        )

        if (transactions.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "لا توجد سندات طلب مسجلة في هذه السنة المالية حتى الآن",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "اضغط على «سند طلب جديد» أو اختر مادة من التبويب الأول لتحرير سند طلب واقتطاعه من الاعتماد المفتوح",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            transactions.forEach { tx ->
                LedgerItemCard(
                    transaction = tx,
                    onDeleteAndRefund = { onDeleteAndRefundTransaction(tx) }
                )
            }
        }
    }
}

@Composable
private fun LedgerItemCard(
    transaction: TransactionEntity,
    onDeleteAndRefund: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ledger_item_${transaction.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldStable.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, EmeraldStable.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = transaction.orderNumber,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldStable,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = formatLedgerDate(transaction.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatDzdCurrency(transaction.amount),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = onDeleteAndRefund,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("delete_transaction_btn_${transaction.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "إلغاء واسترجاع الاعتماد",
                            tint = CrimsonCritical,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Text(
                text = transaction.description,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "المادة: [${transaction.articleCode}] ${transaction.articleName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (transaction.supplierName.isNotBlank()) {
                    Text(
                        text = "الممون: ${transaction.supplierName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
