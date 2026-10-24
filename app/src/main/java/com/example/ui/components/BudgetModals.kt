package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ArticleEntity
import com.example.data.ChapterWithArticles
import com.example.data.Circular13Constants
import com.example.data.InstitutionConfigEntity
import com.example.ui.PendingExpenseOverBudgetWarning
import com.example.ui.formatDzdCurrency
import com.example.ui.formatDzdWhole
import com.example.ui.formatPercentage
import com.example.ui.healthColor
import com.example.ui.theme.AmberElevated
import com.example.ui.theme.CrimsonCritical
import com.example.ui.theme.EmeraldStable

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiscalYearOnboardingModal(
    initialConfig: InstitutionConfigEntity,
    onDismiss: () -> Unit,
    onSaveAndInjectSubsidy: (
        institutionName: String,
        wilayaName: String,
        regime: String,
        fiscalYear: Int,
        internalCount: Int,
        semiInternalCount: Int
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

    val regimes = listOf("داخلي ونصف داخلي", "نصف داخلي", "داخلي", "خارجي")

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
                                text = "فتح وإعداد السنة المالية 2026",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "محرك احتساب إعانة الإطعام والمنح (المنشور 13/2026)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق"
                        )
                    }
                }

                HorizontalDivider()

                OutlinedTextField(
                    value = institutionName,
                    onValueChange = { institutionName = it },
                    label = { Text("تسمية المؤسسة التربوية (متوسطة / ثانوية)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_institution_name")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = wilayaName,
                        onValueChange = { wilayaName = it },
                        label = { Text("مديرية التربية / الولاية") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1.4f)
                            .testTag("input_wilaya_name")
                    )

                    OutlinedTextField(
                        value = fiscalYearText,
                        onValueChange = { fiscalYearText = it },
                        label = { Text("السنة المالية") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(0.8f)
                            .testTag("input_fiscal_year")
                    )
                }

                Text(
                    text = "نظام المؤسسة التربوية:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
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
                            text = "• حصة النظام الداخلي ($internalCount × 33,696.00): ${formatDzdCurrency(internalAmount)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "• حصة النظام نصف الداخلي ($semiInternalCount × 13,248.00): ${formatDzdCurrency(semiInternalAmount)}",
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
                            text = "سيتم ضخ هذا المبلغ آلياً في:\n1) الإيرادات: القسم 1 -> الباب 1 -> المادة 1 (إعانة التغذية والمنح 111/01)\n2) النفقات: القسم 2 -> الفرع 1 -> الباب 6 -> المادة 1 (إطعام التلاميذ الممنوحين 216/01)",
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
                                fiscalYearText.toIntOrNull() ?: 2026,
                                internalCount,
                                semiInternalCount
                            )
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldStable,
                            contentColor = Color(0xFF042F2E)
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
                            text = "اعتماد وضخ المخصصات",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseRegistrationModal(
    expenditureChapters: List<ChapterWithArticles>,
    preselectedArticle: ArticleEntity?,
    onDismiss: () -> Unit,
    onSubmitExpense: (
        article: ArticleEntity,
        chapterName: String,
        voucherNumber: String,
        supplierName: String,
        description: String,
        amount: Double
    ) -> Unit
) {
    BackHandler(onBack = onDismiss)

    val allExpenditureArticles = remember(expenditureChapters) {
        expenditureChapters.flatMap { ch ->
            ch.articles.map { article ->
                article to "الباب ${ch.chapter.chapterNumber}: ${ch.chapter.name}"
            }
        }
    }

    var searchFilter by rememberSaveable { mutableStateOf("") }
    var selectedArticleId by rememberSaveable {
        mutableStateOf(
            preselectedArticle?.id ?: allExpenditureArticles.firstOrNull()?.first?.id.orEmpty()
        )
    }
    var voucherNumber by rememberSaveable {
        mutableStateOf("MD-2026/${(105..980).random()}")
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
    val remainingAfterExpense = (selectedArticle?.remainingCredit ?: 0.0) - enteredAmount

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
                                text = "تسجيل التزام ونفقة جديدة (حوالة دفع)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "اختر المادة المحاسبية وتحقق من الرصيد المتاح قبل الصرف",
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

                // Searchable Dropdown / List for Articles with real-time Remaining Credit
                OutlinedTextField(
                    value = searchFilter,
                    onValueChange = { searchFilter = it },
                    placeholder = { Text("ابحث عن المادة المحاسبية أو رمز الباب (مثال: 216/01 أو سونلغاز)...") },
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
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
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
                                    .testTag("select_expense_article_${article.id}")
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
                                            text = "المتبقي: ${formatDzdWhole(article.remainingCredit)}",
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

                // Selected Article Live Credit Status Banner
                if (selectedArticle != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (remainingAfterExpense >= 0) {
                            EmeraldStable.copy(alpha = 0.12f)
                        } else {
                            CrimsonCritical.copy(alpha = 0.15f)
                        },
                        border = BorderStroke(
                            1.dp,
                            if (remainingAfterExpense >= 0) EmeraldStable.copy(alpha = 0.4f)
                            else CrimsonCritical
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "المادة المختارة: [${selectedArticle.officialCode}] ${selectedArticle.name}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "الرصيد المتاح حالياً: ${formatDzdCurrency(selectedArticle.remainingCredit)} • بعد الصرف: ${formatDzdCurrency(remainingAfterExpense)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (remainingAfterExpense >= 0) EmeraldStable else CrimsonCritical
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
                        value = voucherNumber,
                        onValueChange = { voucherNumber = it },
                        label = { Text("رقم الحوالة / الفاتورة") },
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
                        label = { Text("المبلغ (د.ج)") },
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
                    label = { Text("الممون / الجهة المستفيدة (مثال: سونلغاز، الديوان الوطني...)") },
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
                    label = { Text("موضوع النفقة وبيان الفاتورة") },
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
                                "تسديد نفقة على المادة ${pair.first.name}"
                            }
                            onSubmitExpense(
                                pair.first,
                                pair.second,
                                voucherNumber,
                                supplierName,
                                finalDesc,
                                enteredAmount
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldStable,
                            contentColor = Color(0xFF042F2E)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("submit_expense_button")
                    ) {
                        Text(
                            text = "تأكيد وحسم النفقة",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OverBudgetAlertDialog(
    warning: PendingExpenseOverBudgetWarning,
    onConfirmOverride: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = "تحذير تجاوز الاعتماد",
                tint = CrimsonCritical,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "تنبيه رقابي: تجاوز الاعتماد المالي المرخص (> 100%)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = CrimsonCritical
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "النفقة المطلوبة على المادة [${warning.article.officialCode}] (${warning.article.name}) تتجاوز الرصيد المتاح:",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• المبلغ المطلوب صرفه: ${formatDzdCurrency(warning.requestedAmount)}",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "• الرصيد المتبقي المتاح: ${formatDzdCurrency(warning.remainingBeforeExpense)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = AmberElevated
                )
                Text(
                    text = "• نسبة الاستهلاك المتوقعة: ${formatPercentage(warning.projectedConsumptionRate)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = CrimsonCritical
                )
                Text(
                    text = "طبقاً لقواعد المحاسبة العمومية (المنشور 29/2018)، لا يجوز الالتزام بنفقة تتجاوز الاعتماد المفتوح إلا بعد ترخيص أو تحويل اعتماد.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmOverride,
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonCritical),
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
