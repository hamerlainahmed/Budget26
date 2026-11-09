package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ArticleEntity
import com.example.data.ChapterWithArticles
import com.example.data.Circular13Constants
import com.example.data.InstitutionConfigEntity
import com.example.data.TransactionEntity
import com.example.ui.PendingExpenseOverBudgetWarning
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
import com.example.ui.theme.SkyInstitutional

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiscalYearOnboardingModal(
    initialConfig: InstitutionConfigEntity,
    savedFiscalYears: List<InstitutionConfigEntity>,
    onSwitchToSavedYear: (Int) -> Unit,
    onDismiss: () -> Unit,
    onSaveAndInjectSubsidy: (
        institutionName: String,
        wilayaName: String,
        regime: String,
        fiscalYear: Int,
        internalCount: Int,
        semiInternalCount: Int,
        clearExpensesForTargetYear: Boolean,
        startWithZeroCustomAllocations: Boolean
    ) -> Unit
) {
    BackHandler(onBack = onDismiss)

    var institutionName by rememberSaveable { mutableStateOf(initialConfig.institutionName) }
    var wilayaName by rememberSaveable { mutableStateOf(initialConfig.wilayaName) }
    var selectedRegime by rememberSaveable { mutableStateOf(initialConfig.regime) }
    var fiscalYearText by rememberSaveable { mutableStateOf(initialConfig.fiscalYear.toString()) }
    var internalCountText by rememberSaveable {
        mutableStateOf(initialConfig.internalScholarshipCount.toString())
    }
    var semiInternalCountText by rememberSaveable {
        mutableStateOf(initialConfig.semiInternalScholarshipCount.toString())
    }
    var clearExpensesForTargetYear by rememberSaveable { mutableStateOf(true) }
    var startWithZeroCustomAllocations by rememberSaveable { mutableStateOf(false) }

    val regimes = listOf("داخلي ونصف داخلي", "نصف داخلي", "داخلي", "خارجي")

    val enteredYear = fiscalYearText.toIntOrNull() ?: initialConfig.fiscalYear

    val internalCount = internalCountText.toIntOrNull()?.coerceAtLeast(0) ?: 0
    val semiInternalCount = semiInternalCountText.toIntOrNull()?.coerceAtLeast(0) ?: 0
    val internalAmount = internalCount * Circular13Constants.INTERNAL_STUDENT_RATE_DZD
    val semiInternalAmount = semiInternalCount * Circular13Constants.SEMI_INTERNAL_STUDENT_RATE_DZD
    val totalCalculatedCatering = Circular13Constants.calculateCateringSubsidy(
        internalCount = internalCount,
        semiInternalCount = semiInternalCount
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .testTag("fiscal_year_onboarding_modal"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = null,
                            tint = EmeraldStable,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "إعداد وفتح السنة المالية للمؤسسة",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "تحديد المعطيات وحجز اعتماد الإطعام وفق المنشور الوزاري 13/2026",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                HorizontalDivider()

                OutlinedTextField(
                    value = institutionName,
                    onValueChange = { institutionName = it },
                    label = { Text("اسم المؤسسة التربوية (ثانوية / متوسطة)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_institution_name")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = wilayaName,
                        onValueChange = { wilayaName = it },
                        label = { Text("مديرية التربية لولاية") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1.4f)
                            .testTag("input_wilaya_name")
                    )

                    OutlinedTextField(
                        value = fiscalYearText,
                        onValueChange = { fiscalYearText = it.filter { ch -> ch.isDigit() }.take(4) },
                        label = { Text("السنة المالية") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_fiscal_year")
                    )
                }

                Text(
                    text = "النظام الداخلي للمؤسسة:",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    regimes.forEach { regimeOption ->
                        FilterChip(
                            selected = selectedRegime == regimeOption,
                            onClick = {
                                selectedRegime = regimeOption
                                if (regimeOption == "خارجي") {
                                    internalCountText = "0"
                                    semiInternalCountText = "0"
                                } else if (regimeOption == "نصف داخلي") {
                                    internalCountText = "0"
                                }
                            },
                            label = { Text(regimeOption) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = internalCountText,
                        onValueChange = { internalCountText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("عدد الداخليين الممنوحين") },
                        supportingText = { Text("33,696.00 د.ج / تلميذ") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_internal_students")
                    )

                    OutlinedTextField(
                        value = semiInternalCountText,
                        onValueChange = { semiInternalCountText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("عدد نصف الداخليين الممنوحين") },
                        supportingText = { Text("13,248.00 د.ج / تلميذ") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_semi_internal_students")
                    )
                }

                // Live Preview Card of Circular 13/2026 Automated Calculation
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = EmeraldStable.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, EmeraldStable.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("catering_live_preview_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = EmeraldStable,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "المعاينة الفورية لمحرك الإطعام (المنشور الوزاري 13/2026)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldStable
                            )
                        }

                        Text(
                            text = "• حصة النظام الداخلي ($internalCount × 33,696.00 د.ج): ${formatDzdCurrency(internalAmount)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "• حصة النظام نصف الداخلي ($semiInternalCount × 13,248.00 د.ج): ${formatDzdCurrency(semiInternalAmount)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        HorizontalDivider(color = EmeraldStable.copy(alpha = 0.3f))

                        Text(
                            text = "إجمالي اعتماد الإطعام: ${formatDzdCurrency(totalCalculatedCatering)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldStable,
                            modifier = Modifier.testTag("preview_total_catering_amount")
                        )

                        Text(
                            text = "سيتم حجز هذا الاعتماد آلياً في مدونة ميزانية نفقات السنة المالية $enteredYear:\nالنفقات: الفرع الأول -> الباب 06 -> المادة 01 (نفقات الإطعام ب06/م01)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("إلغاء")
                    }

                    Button(
                        onClick = {
                            onSaveAndInjectSubsidy(
                                institutionName,
                                wilayaName,
                                selectedRegime,
                                enteredYear,
                                internalCount,
                                semiInternalCount,
                                clearExpensesForTargetYear,
                                startWithZeroCustomAllocations
                            )
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldStable,
                            contentColor = Color(0xFF022C22)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.6f)
                            .testTag("confirm_fiscal_year_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "فتح سنة $enteredYear وحجز الاعتماد",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

/**
 * Modal dialog for the Authorizing Officer (الآمر بالصرف) to create an expenditure order (سند الطلب - Bon de Commande).
 */
@Composable
fun ExpenseRegistrationModal(
    expenditureChapters: List<ChapterWithArticles>,
    preselectedArticle: ArticleEntity?,
    onDismiss: () -> Unit,
    onSubmitExpense: (
        article: ArticleEntity,
        chapterName: String,
        orderNumber: String,
        supplierName: String,
        description: String,
        amount: Double
    ) -> Unit
) {
    BackHandler(onBack = onDismiss)

    val allExpenditureArticles = remember(expenditureChapters) {
        expenditureChapters.flatMap { ch ->
            ch.articles.map { article ->
                article to "${ch.chapter.officialCode}: ${ch.chapter.name}"
            }
        }
    }

    val activeFiscalYear = allExpenditureArticles.firstOrNull()?.first?.fiscalYear ?: 2026

    var searchFilter by rememberSaveable { mutableStateOf("") }
    var selectedArticleId by rememberSaveable {
        mutableStateOf(
            preselectedArticle?.id ?: allExpenditureArticles.firstOrNull()?.first?.id.orEmpty()
        )
    }
    var orderNumber by rememberSaveable {
        mutableStateOf("BC-$activeFiscalYear/${(105..980).random()}")
    }
    var supplierName by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var amountText by rememberSaveable { mutableStateOf("") }
    var validationError by rememberSaveable { mutableStateOf<String?>(null) }

    val selectedPair = allExpenditureArticles.firstOrNull { it.first.id == selectedArticleId }
        ?: allExpenditureArticles.firstOrNull()

    val filteredArticles = remember(allExpenditureArticles, searchFilter) {
        if (searchFilter.isBlank()) {
            allExpenditureArticles
        } else {
            val q = searchFilter.trim()
            allExpenditureArticles.filter { (article, chapterName) ->
                article.name.contains(q, ignoreCase = true) ||
                    article.officialCode.contains(q, ignoreCase = true) ||
                    chapterName.contains(q, ignoreCase = true)
            }
        }
    }

    val enteredAmount = amountText.replace(",", "").toDoubleOrNull() ?: 0.0
    val selectedArticle = selectedPair?.first
    val currentRemaining = selectedArticle?.remainingCredit ?: 0.0
    val remainingAfterExpense = currentRemaining - enteredAmount
    val isInsufficientCredit = enteredAmount > currentRemaining && enteredAmount > 0.0
    val projectedSpent = (selectedArticle?.spentAmount ?: 0.0) + enteredAmount
    val projectedRate = if ((selectedArticle?.allocatedAmount ?: 0.0) <= 0.0) {
        if (projectedSpent > 0.0) 100.0 else 0.0
    } else {
        (projectedSpent / selectedArticle!!.allocatedAmount) * 100.0
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 14.dp)
                .testTag("expense_registration_modal"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = EmeraldStable,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "تحرير سند طلب جديد (التزام بنفقة - سنة $activeFiscalYear)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "يقوم الآمر بالصرف بالالتزام بسند الطلب بعد التحقق من كفاية الاعتماد",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                HorizontalDivider()

                OutlinedTextField(
                    value = searchFilter,
                    onValueChange = { searchFilter = it },
                    placeholder = { Text("ابحث عن المادة أو الباب (مثال: ب06/م01 أو ب02/م01 أو الإطعام)...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("expense_article_search_input")
                )

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp, max = 185.dp)
                ) {
                    LazyColumn(
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filteredArticles, key = { it.first.id }) { (article, chapterTitle) ->
                            val isSelected = article.id == selectedArticle?.id
                            val artColor = healthColor(article.healthStatus)

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surface
                                },
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedArticleId = article.id }
                                    .testTag("select_expense_article_${article.baseArticleId}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "[${article.officialCode}] ${article.name}",
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = chapterTitle,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "الرصيد: ${formatDzdWhole(article.remainingCredit)}",
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                            color = if (article.remainingCredit > 0) EmeraldStable else CrimsonCritical
                                        )
                                        Text(
                                            text = "استهلاك ${formatPercentage(article.consumptionRate)}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = artColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Live Balance Verification Card for the Authorizing Officer (الآمر بالصرف)
                if (selectedArticle != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (!isInsufficientCredit) {
                            EmeraldStable.copy(alpha = 0.12f)
                        } else {
                            CrimsonCritical.copy(alpha = 0.15f)
                        },
                        border = BorderStroke(
                            1.dp,
                            if (!isInsufficientCredit) EmeraldStable.copy(alpha = 0.4f)
                            else CrimsonCritical
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "المادة: [${selectedArticle.officialCode}] ${selectedArticle.name}",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = if (!isInsufficientCredit) "اعتماد كافٍ ومتاح ✓" else "اعتماد غير كافٍ ⚠",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (!isInsufficientCredit) EmeraldStable else CrimsonCritical
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "الاعتماد المفتوح: ${formatDzdWhole(selectedArticle.allocatedAmount)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "المصروف السابق: ${formatDzdWhole(selectedArticle.spentAmount)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "الرصيد المتاح: ${formatDzdCurrency(currentRemaining)}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (currentRemaining > 0) EmeraldStable else CrimsonCritical
                                )
                                if (enteredAmount > 0.0) {
                                    Text(
                                        text = "المتبقي بعد السند: ${formatDzdCurrency(remainingAfterExpense)}",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (remainingAfterExpense >= 0) EmeraldStable else CrimsonCritical
                                    )
                                }
                            }

                            if (isInsufficientCredit) {
                                val deficit = enteredAmount - currentRemaining
                                Text(
                                    text = "تنبيه للآمر بالصرف: مبلغ سند الطلب يتجاوز الرصيد المتاح بمقدار (${formatDzdCurrency(deficit)}). نسبة الاستهلاك المتوقعة: ${formatPercentage(projectedRate)}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = CrimsonCritical
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = orderNumber,
                        onValueChange = { orderNumber = it },
                        label = { Text("رقم سند الطلب (N° Bon de commande)") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("expense_voucher_input")
                    )

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = {
                            amountText = it
                            validationError = null
                        },
                        label = { Text("مبلغ سند الطلب (د.ج)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("expense_amount_input")
                    )
                }

                OutlinedTextField(
                    value = supplierName,
                    onValueChange = { supplierName = it },
                    label = { Text("المتعامل المتعاقد / الممون (الجهة المستفيدة)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("expense_supplier_input")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        validationError = null
                    },
                    label = { Text("محتوى سند الطلب وبيان النفقة") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("expense_description_input")
                )

                if (validationError != null) {
                    Text(
                        text = validationError!!,
                        style = MaterialTheme.typography.labelLarge,
                        color = CrimsonCritical
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("إلغاء")
                    }

                    Button(
                        onClick = {
                            val pair = selectedPair
                            if (pair == null) {
                                validationError = "يرجى اختيار المادة المحاسبية"
                                return@Button
                            }
                            if (enteredAmount <= 0.0) {
                                validationError = "يرجى إدخال مبلغ صحيح أكبر من الصفر"
                                return@Button
                            }
                            val finalDesc = description.trim().ifEmpty {
                                "سند طلب على المادة ${pair.first.name}"
                            }
                            onSubmitExpense(
                                pair.first,
                                pair.second,
                                orderNumber,
                                supplierName,
                                finalDesc,
                                enteredAmount
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isInsufficientCredit) AmberElevated else EmeraldStable,
                            contentColor = Color(0xFF022C22)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("submit_expense_button")
                    ) {
                        Text(
                            text = if (isInsufficientCredit) "إصدار سند الطلب (تجاوز الاعتماد)" else "تأكيد وإصدار سند الطلب",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

/**
 * بطاقة الالتزام (Fiche d'Engagement par article)
 * تعرض جميع الالتزامات (سندات الطلب) التي تمت على هذه المادة مرتبة ترتيباً زمنياً
 * في جدول يوضح رقم سند الطلب، محتواه، مبلغ الطلب، والرصيد المتبقي.
 */
@Composable
fun CommitmentCardModal(
    article: ArticleEntity,
    chapterOfficialName: String,
    institutionName: String,
    fiscalYear: Int,
    transactions: List<TransactionEntity>,
    onDismiss: () -> Unit,
    onAddNewOrder: () -> Unit
) {
    BackHandler(onBack = onDismiss)

    // Chronological sorting: from oldest to newest
    val sortedTransactions = remember(transactions) {
        transactions.sortedWith(compareBy({ it.timestamp }, { it.id }))
    }

    val statusColor = healthColor(article.healthStatus)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .padding(vertical = 14.dp)
                .testTag("commitment_card_modal_${article.baseArticleId}"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header of Fiche d'engagement
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SkyInstitutional.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, SkyInstitutional.copy(alpha = 0.45f)),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Assignment,
                                    contentDescription = "بطاقة الالتزام",
                                    tint = SkyInstitutional,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "بطاقة الالتزام (Fiche d'Engagement)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = EmeraldStable.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, EmeraldStable.copy(alpha = 0.35f))
                                ) {
                                    Text(
                                        text = "سنة $fiscalYear",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = EmeraldStable,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "$institutionName • $chapterOfficialName",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                // Article Identifier Banner
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = statusColor.copy(alpha = 0.18f),
                                border = BorderStroke(1.dp, statusColor.copy(alpha = 0.45f))
                            ) {
                                Text(
                                    text = article.officialCode,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = statusColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = article.name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (article.description.isNotBlank()) {
                            Text(
                                text = article.description,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 4 Financial Summary KPI Boxes for this Article
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SummaryMiniBox(
                        title = "الاعتماد المفتوح",
                        amount = formatDzdWhole(article.allocatedAmount),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryMiniBox(
                        title = "مجموع الالتزامات",
                        amount = formatDzdWhole(article.spentAmount),
                        color = AmberElevated,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryMiniBox(
                        title = "الرصيد المتاح",
                        amount = formatDzdWhole(article.remainingCredit),
                        color = if (article.remainingCredit >= 0) EmeraldStable else CrimsonCritical,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryMiniBox(
                        title = "نسبة الاستهلاك",
                        amount = formatPercentage(article.consumptionRate),
                        color = statusColor,
                        modifier = Modifier.weight(0.9f)
                    )
                }

                // Table Title and Count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "جدول سندات الطلب والالتزامات المسجلة (${sortedTransactions.size} سند):",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Chronological Table Content
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .heightIn(min = 160.dp, max = 340.dp)
                ) {
                    if (sortedTransactions.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(44.dp)
                            )
                            Text(
                                text = "لا توجد التزامات أو سندات طلب محررة على هذه المادة بعد",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "الاعتماد المالي المفتوح (${formatDzdCurrency(article.allocatedAmount)}) متاح بالكامل للصرف",
                                style = MaterialTheme.typography.bodySmall,
                                color = EmeraldStable
                            )
                        }
                    } else {
                        // Calculate cumulative balances progressive ledger
                        var progressiveSpent = 0.0
                        val ledgerRows = sortedTransactions.mapIndexed { index, tx ->
                            progressiveSpent += tx.amount
                            val soldeApres = article.allocatedAmount - progressiveSpent
                            CommitmentRowData(
                                index = index + 1,
                                transaction = tx,
                                remainingAfter = soldeApres
                            )
                        }

                        LazyColumn(
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(ledgerRows, key = { it.transaction.id }) { row ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(
                                                            text = "#${row.index}",
                                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.width(6.dp))

                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = EmeraldStable.copy(alpha = 0.15f),
                                                    border = BorderStroke(1.dp, EmeraldStable.copy(alpha = 0.4f))
                                                ) {
                                                    Text(
                                                        text = row.transaction.orderNumber,
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                        color = EmeraldStable,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }

                                                Spacer(modifier = Modifier.width(8.dp))

                                                Text(
                                                    text = formatLedgerDate(row.transaction.timestamp),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            Text(
                                                text = formatDzdCurrency(row.transaction.amount),
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        Text(
                                            text = "البيان: ${row.transaction.description}",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (row.transaction.supplierName.isNotBlank()) {
                                                Text(
                                                    text = "المتعامل: ${row.transaction.supplierName}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            } else {
                                                Spacer(modifier = Modifier.width(1.dp))
                                            }

                                            Text(
                                                text = "الرصيد بعد الالتزام: ${formatDzdCurrency(row.remainingAfter)}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = if (row.remainingAfter >= 0) EmeraldStable else CrimsonCritical
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Modal Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("إغلاق البطاقة")
                    }

                    Button(
                        onClick = {
                            onDismiss()
                            onAddNewOrder()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldStable,
                            contentColor = Color(0xFF022C22)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.4f)
                            .testTag("commitment_card_add_order_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تحرير سند طلب جديد",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

private data class CommitmentRowData(
    val index: Int,
    val transaction: TransactionEntity,
    val remainingAfter: Double
)

@Composable
private fun SummaryMiniBox(
    title: String,
    amount: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.09f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
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

@Composable
fun OverBudgetAlertDialog(
    warning: PendingExpenseOverBudgetWarning,
    onConfirmOverride: () -> Unit,
    onDismiss: () -> Unit
) {
    val deficit = warning.requestedAmount - warning.remainingBeforeExpense

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = "تحذير تجاوز الاعتماد",
                tint = CrimsonCritical,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "تنبيه رقابي للآمر بالصرف: عدم كفاية الاعتماد المالي",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = CrimsonCritical
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "سند الطلب المطلوب على المادة [${warning.article.officialCode}] (${warning.article.name}) يتجاوز الرصيد المتاح في الميزانية:",
                    style = MaterialTheme.typography.bodyMedium
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, CrimsonCritical.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "• الاعتماد المفتوح في المادة: ${formatDzdCurrency(warning.article.allocatedAmount)}",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = "• المصروف السابق: ${formatDzdCurrency(warning.article.spentAmount)}",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = "• الرصيد المتاح للصرف: ${formatDzdCurrency(warning.remainingBeforeExpense)}",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = AmberElevated
                        )
                        Text(
                            text = "• مبلغ سند الطلب المطلوب: ${formatDzdCurrency(warning.requestedAmount)}",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = CrimsonCritical
                        )
                        Text(
                            text = "• العجز المالي الناتج: ${formatDzdCurrency(deficit)}",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = CrimsonCritical
                        )
                        Text(
                            text = "• نسبة استهلاك الاعتماد المتوقعة: ${formatPercentage(warning.projectedConsumptionRate)}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = CrimsonCritical
                        )
                    }
                }

                Text(
                    text = "وفقاً لأحكام المحاسبة العمومية للمؤسسات التعليمية، لا يجوز للآمر بالصرف تحرير سند طلب يتجاوز الاعتماد المفتوح إلا بعد ترخيص رسمي بتحويل اعتماد (Virement de crédit).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmOverride,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CrimsonCritical,
                    contentColor = Color.White
                ),
                modifier = Modifier.testTag("confirm_over_budget_button")
            ) {
                Text("تأكيد الصرف استثنائياً")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_over_budget_button")
            ) {
                Text("إلغاء ومراجعة الاعتماد")
            }
        },
        modifier = Modifier.testTag("over_budget_alert_dialog")
    )
}
