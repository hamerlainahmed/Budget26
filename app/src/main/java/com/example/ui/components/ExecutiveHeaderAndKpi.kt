package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.example.ui.theme.EmeraldStable

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExecutiveHeaderCard(
    config: InstitutionConfigEntity,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
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
            containerColor = Color(0xFF0D1F38)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.ic_hero_budget_illustration),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xCC0A1628),
                                Color(0xE60E223B)
                            )
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
                            color = EmeraldStable.copy(alpha = 0.18f),
                            border = BorderStroke(1.dp, EmeraldStable.copy(alpha = 0.45f)),
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
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.testTag("header_institution_name")
                            )
                            Text(
                                text = "${config.wilayaName} • المنشور 29/2018 والمنشور 13/2026",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFCBD5E1),
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
                            tint = Color(0xFFFDE68A)
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
                        accentColor = Color(0xFF38BDF8)
                    )
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
                            contentColor = Color(0xFF042F2E)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_expense_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+ نفقة سريعة",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenFiscalYearSetup,
                        border = BorderStroke(1.dp, Color(0xFF64748B)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
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
        color = accentColor.copy(alpha = 0.16f),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.45f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun KpiBentoGrid(
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
        // Row 1: Total Budget Authorized & Total Committed Expenses
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KpiStatCard(
                title = "مجموع الاعتمادات",
                subtitle = "إجمالي الميزانية المرخصة",
                amountText = formatDzdWhole(totalAuthorized),
                accentColor = MaterialTheme.colorScheme.primary,
                icon = Icons.Default.AccountBalance,
                modifier = Modifier
                    .weight(1f)
                    .testTag("kpi_total_authorized_card")
            )
            KpiStatCard(
                title = "مجموع النفقات",
                subtitle = "النفقات الملتزم بها فعلياً",
                amountText = formatDzdWhole(totalCommitted),
                accentColor = AmberElevated,
                icon = Icons.Default.TrendingUp,
                modifier = Modifier
                    .weight(1f)
                    .testTag("kpi_total_spent_card")
            )
        }

        // Row 2: Net Remaining Balance & Catering 2026 Subsidy Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KpiStatCard(
                title = "الرصيد المتبقي العام",
                subtitle = "الاعتمادات المتاحة للصرف",
                amountText = formatDzdWhole(netRemaining),
                accentColor = if (netRemaining >= 0) EmeraldStable else CrimsonCritical,
                icon = Icons.Default.Savings,
                modifier = Modifier
                    .weight(1f)
                    .testTag("kpi_net_remaining_card")
            )
            KpiStatCard(
                title = "معتمد الإطعام 2026",
                subtitle = "داخلي ($internalStudents) + ن.داخلي ($semiInternalStudents)",
                amountText = formatDzdWhole(cateringSubsidy),
                accentColor = Color(0xFF0EA5E9),
                icon = Icons.Default.Restaurant,
                modifier = Modifier
                    .weight(1f)
                    .testTag("kpi_catering_subsidy_card")
            )
        }

        // Row 3: Global Animated Consumption Progress Card
        GlobalConsumptionCard(
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
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
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
                    // Circular Gauge
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
                            text = "مؤشر استهلاك الميزانية العام",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "مخصص المنشور 13/2026 مدرج آلياً في الإيرادات (111/01) والنفقات (216/01)",
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
                text = "تفصيل الإطعام: داخلي (${formatDzdWhole(internalTotal)}) + نصف داخلي (${formatDzdWhole(semiTotal)}) = ${formatDzdCurrency(cateringSubsidy)}",
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
                            text = "تنبيه رقابي إلزامي - شرط الباب 2 (المنشور 13/2026)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "اعتماد الباب الثاني (التكاليف الملحقة: ماء، غاز، كهرباء) يمثل حالياً ${formatPercentage(audit.currentPercentage)} فقط من نفقات التسيير العام (${formatDzdWhole(audit.chapter2Allocated)} من أصل ${formatDzdWhole(audit.totalOperationalAllocatedExcludingCatering)}). يشترط المنشور الوزاري 13/2026 ألا يقل عن 35% (الحد الأدنى المطلوب: ${formatDzdWhole(audit.requiredMinimumAmount)}).",
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
                            text = "مطابقة شرط الباب 2 (المنشور 13/2026): ${formatPercentage(audit.currentPercentage)} من نفقات التسيير (≥ 35%)",
                            style = MaterialTheme.typography.labelLarge,
                            color = EmeraldStable
                        )
                    }
                }
            }
        }
    }
}
