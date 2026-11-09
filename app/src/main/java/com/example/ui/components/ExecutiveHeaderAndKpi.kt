package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.Chapter2AuditCheck
import com.example.data.Circular13Constants
import com.example.data.ConsumptionHealth
import com.example.data.InstitutionConfigEntity
import com.example.ui.formatDzdCurrency
import com.example.ui.formatDzdWhole
import com.example.ui.formatPercentage
import com.example.ui.healthColor
import com.example.ui.healthStatusLabelAr
import com.example.ui.theme.AmberElevated
import com.example.ui.theme.CrimsonCritical
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.EmeraldStable
import com.example.ui.theme.SkyInstitutional
import com.example.ui.theme.SlateBorderDark
import com.example.ui.theme.SlateCardDark

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExecutiveHeaderCard(
    config: InstitutionConfigEntity,
    savedFiscalYears: List<InstitutionConfigEntity>,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onSwitchFiscalYear: (Int) -> Unit,
    onOpenFiscalYearSetup: () -> Unit,
    onQuickExpense: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("executive_header_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF111C2E) else Color(0xFF0F2B48)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(
            1.dp,
            if (isDarkTheme) SlateBorderDark else Color(0x33FFFFFF)
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.ic_hero_budget_illustration),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = if (isDarkTheme) {
                                listOf(
                                    Color(0xF20F172A),
                                    Color(0xF71E293B)
                                )
                            } else {
                                listOf(
                                    Color(0xEA0F2B48),
                                    Color(0xF5163E66)
                                )
                            }
                        )
                    )
                    .padding(18.dp)
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
                            shape = RoundedCornerShape(14.dp),
                            color = EmeraldStable.copy(alpha = 0.20f),
                            border = BorderStroke(1.dp, EmeraldStable.copy(alpha = 0.50f)),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalance,
                                    contentDescription = "شعار الميزانية المدرسية",
                                    tint = EmeraldStable,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = config.institutionName,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = DarkTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.testTag("header_institution_name")
                            )
                            Text(
                                text = "${config.wilayaName} • مدونة ميزانية نفقات المؤسسات التعليمية 2024",
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("toggle_theme_button")
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "تبديل المظهر الليلي والنهاري",
                            tint = if (isDarkTheme) Color(0xFFFDE68A) else Color(0xFFFEF08A)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExecutiveBadge(
                        text = "السنة المالية ${config.fiscalYear}",
                        accentColor = EmeraldStable
                    )
                    ExecutiveBadge(
                        text = "النظام: ${config.regime}",
                        accentColor = AmberElevated
                    )
                    ExecutiveBadge(
                        text = "الممنوحون: ${config.internalScholarshipCount} داخلي | ${config.semiInternalScholarshipCount} ن.داخلي",
                        accentColor = SkyInstitutional
                    )
                }

                // Fiscal Year History Switcher Bar (سجل السنوات المالية المحفوظة)
                if (savedFiscalYears.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isDarkTheme) Color(0x991E293B) else Color(0x33FFFFFF),
                        border = BorderStroke(
                            1.dp,
                            if (isDarkTheme) SlateBorderDark else Color(0x44FFFFFF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("fiscal_year_history_bar")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "أرشيف السنوات المالية",
                                    tint = AmberElevated,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "سجل السنوات المحفوظة:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = DarkTextPrimary
                                )
                            }

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                savedFiscalYears.forEach { yearItem ->
                                    val isCurrent = yearItem.fiscalYear == config.fiscalYear
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = if (isCurrent) EmeraldStable else if (isDarkTheme) Color(0xFF27354A) else Color(0x40FFFFFF),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isCurrent) EmeraldStable else Color(0x55FFFFFF)
                                        ),
                                        modifier = Modifier
                                            .clickable { onSwitchFiscalYear(yearItem.fiscalYear) }
                                            .testTag("switch_fiscal_year_pill_${yearItem.fiscalYear}")
                                    ) {
                                        Text(
                                            text = if (isCurrent) "${yearItem.fiscalYear} (الجارية)" else yearItem.fiscalYear.toString(),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (isCurrent) Color(0xFF022C22) else DarkTextPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onQuickExpense,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldStable,
                            contentColor = Color(0xFF022C22)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("quick_expense_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تسجيل نفقة جديدة",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenFiscalYearSetup,
                        border = BorderStroke(
                            1.dp,
                            if (isDarkTheme) Color(0xFF475569) else Color(0x88FFFFFF)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = DarkTextPrimary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("open_fiscal_year_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = null,
                            tint = AmberElevated,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "فتح سنة مالية",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExecutiveBadge(
    text: String,
    accentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = accentColor.copy(alpha = 0.18f),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.50f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = DarkTextPrimary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun KpiBentoGrid(
    fiscalYear: Int,
    totalAuthorized: Double,
    totalCommitted: Double,
    netRemaining: Double,
    cateringSubsidy: Double,
    internalStudents: Int,
    semiInternalStudents: Int,
    globalConsumptionRate: Double,
    globalHealthStatus: ConsumptionHealth,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("kpi_bento_grid"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Row 1: Total Authorized Budget & Total Committed Expenses
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KpiStatCard(
                title = "مجموع الاعتمادات ($fiscalYear)",
                subtitle = "إجمالي النفقات المرخصة",
                amountText = formatDzdWhole(totalAuthorized),
                accentColor = MaterialTheme.colorScheme.primary,
                icon = Icons.Default.AccountBalance,
                modifier = Modifier
                    .weight(1f)
                    .testTag("kpi_total_authorized_card")
            )
            KpiStatCard(
                title = "مجموع النفقات ($fiscalYear)",
                subtitle = "النفقات الملتزم بها فعلياً",
                amountText = formatDzdWhole(totalCommitted),
                accentColor = AmberElevated,
                icon = Icons.Default.TrendingUp,
                modifier = Modifier
                    .weight(1f)
                    .testTag("kpi_total_spent_card")
            )
        }

        // Row 2: Net Remaining Balance & Catering Subsidy Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KpiStatCard(
                title = "الرصيد المالي المتبقي",
                subtitle = "الاعتمادات المتاحة للصرف",
                amountText = formatDzdWhole(netRemaining),
                accentColor = if (netRemaining >= 0) EmeraldStable else CrimsonCritical,
                icon = Icons.Default.Savings,
                modifier = Modifier
                    .weight(1f)
                    .testTag("kpi_net_remaining_card")
            )
            KpiStatCard(
                title = "اعتماد الإطعام (ب06/م01)",
                subtitle = "داخلي ($internalStudents) + ن.داخلي ($semiInternalStudents)",
                amountText = formatDzdWhole(cateringSubsidy),
                accentColor = SkyInstitutional,
                icon = Icons.Default.Restaurant,
                modifier = Modifier
                    .weight(1f)
                    .testTag("kpi_catering_subsidy_card")
            )
        }

        // Row 3: Global Animated Consumption Progress Card
        GlobalConsumptionCard(
            fiscalYear = fiscalYear,
            consumptionRate = globalConsumptionRate,
            healthStatus = globalHealthStatus,
            cateringSubsidy = cateringSubsidy,
            internalStudents = internalStudents,
            semiInternalStudents = semiInternalStudents
        )
    }
}

@Composable
private fun KpiStatCard(
    title: String,
    subtitle: String,
    amountText: String,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = amountText,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun GlobalConsumptionCard(
    fiscalYear: Int,
    consumptionRate: Double,
    healthStatus: ConsumptionHealth,
    cateringSubsidy: Double,
    internalStudents: Int,
    semiInternalStudents: Int
) {
    val statusColor = healthColor(healthStatus)
    val animatedProgress by animateFloatAsState(
        targetValue = (consumptionRate.toFloat() / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
        label = "globalConsumptionAnimation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("global_consumption_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(52.dp)
                    ) {
                        Canvas(modifier = Modifier.size(48.dp)) {
                            drawArc(
                                color = statusColor.copy(alpha = 0.18f),
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = statusColor,
                                startAngle = -90f,
                                sweepAngle = 360f * animatedProgress,
                                useCenter = false,
                                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        Text(
                            text = formatPercentage(consumptionRate),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = statusColor
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "نسبة استهلاك ميزانية النفقات ($fiscalYear)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "مخصص الإطعام (المنشور 13/2026) محجوز آلياً في نفقات الإطعام (ب06/م01)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = statusColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = healthStatusLabelAr(healthStatus),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                color = statusColor,
                trackColor = statusColor.copy(alpha = 0.15f),
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
            )

            Spacer(modifier = Modifier.height(8.dp))

            val internalTotal = internalStudents * Circular13Constants.INTERNAL_STUDENT_RATE_DZD
            val semiTotal = semiInternalStudents * Circular13Constants.SEMI_INTERNAL_STUDENT_RATE_DZD
            Text(
                text = "تفصيل اعتماد الإطعام: داخلي (${formatDzdWhole(internalTotal)}) + نصف داخلي (${formatDzdWhole(semiTotal)}) = ${formatDzdCurrency(cateringSubsidy)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun Chapter2AuditBanner(
    audit: Chapter2AuditCheck,
    onAutoFixCompliance: () -> Unit,
    onGoToAllocationsTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(visible = true, modifier = modifier.fillMaxWidth()) {
        if (!audit.isCompliant) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("chapter2_audit_warning_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f)
                ),
                border = BorderStroke(1.5.dp, CrimsonCritical)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = "تنبيه رقابي",
                            tint = CrimsonCritical,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تنبيه رقابي إلزامي - شرط الباب 02: التكاليف الملحقة (المنشور 13/2026)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "اعتماد الباب 02 (التكاليف الملحقة: الماء، الكهرباء، الغاز، البريد والمواصلات) يمثل حالياً ${formatPercentage(audit.currentPercentage)} فقط من نفقات تسيير المصالح (${formatDzdWhole(audit.chapter2Allocated)} من أصل ${formatDzdWhole(audit.totalOperationalAllocatedExcludingCatering)}). يشترط المنشور الوزاري 13/2026 ألا يقل عن 35% (الحد الأدنى المطلوب: ${formatDzdWhole(audit.requiredMinimumAmount)}).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = onAutoFixCompliance,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = CrimsonCritical,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("auto_fix_chapter2_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "تصحيح تلقائي إلى ≥35%",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        OutlinedButton(
                            onClick = onGoToAllocationsTab,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onErrorContainer),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("goto_allocations_from_warning_button")
                        ) {
                            Text(
                                text = "حجز الاعتمادات",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        } else {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("chapter2_audit_compliant_banner"),
                shape = RoundedCornerShape(14.dp),
                color = EmeraldStable.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, EmeraldStable.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "مستوف للشرط",
                            tint = EmeraldStable,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "مطابقة شرط الباب 02 (التكاليف الملحقة - المنشور 13/2026): ${formatPercentage(audit.currentPercentage)} من نفقات تسيير المصالح (≥ 35%)",
                            style = MaterialTheme.typography.labelLarge,
                            color = EmeraldStable
                        )
                    }
                }
            }
        }
    }
}
