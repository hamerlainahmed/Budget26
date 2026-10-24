package com.example.ui

import androidx.compose.ui.graphics.Color
import com.example.data.ConsumptionHealth
import com.example.ui.theme.AmberElevated
import com.example.ui.theme.CrimsonCritical
import com.example.ui.theme.EmeraldStable
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val dzdSymbols = DecimalFormatSymbols(Locale.US).apply {
    groupingSeparator = ','
    decimalSeparator = '.'
}

private val dzdFormatter = DecimalFormat("#,##0.00", dzdSymbols)
private val dzdCompactFormatter = DecimalFormat("#,##0", dzdSymbols)
private val percentFormatter = DecimalFormat("0.0", dzdSymbols)

fun formatDzdCurrency(amount: Double, includeSymbol: Boolean = true): String {
    val formatted = dzdFormatter.format(amount)
    return if (includeSymbol) "$formatted د.ج" else formatted
}

fun formatDzdWhole(amount: Double, includeSymbol: Boolean = true): String {
    val formatted = dzdCompactFormatter.format(amount)
    return if (includeSymbol) "$formatted د.ج" else formatted
}

fun formatPercentage(rate: Double): String {
    return "${percentFormatter.format(rate)}%"
}

fun formatLedgerDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.US)
    return sdf.format(Date(timestamp))
}

fun healthColor(health: ConsumptionHealth): Color {
    return when (health) {
        ConsumptionHealth.STABLE -> EmeraldStable
        ConsumptionHealth.ELEVATED -> AmberElevated
        ConsumptionHealth.CRITICAL, ConsumptionHealth.OVER_BUDGET -> CrimsonCritical
    }
}

fun healthStatusLabelAr(health: ConsumptionHealth): String {
    return when (health) {
        ConsumptionHealth.STABLE -> "مستقر (<70%)"
        ConsumptionHealth.ELEVATED -> "قيد الاستهلاك المرتفع"
        ConsumptionHealth.CRITICAL -> "حرج / اقتراب الاستنفاد"
        ConsumptionHealth.OVER_BUDGET -> "تجاوز الاعتماد (>100%)"
    }
}
