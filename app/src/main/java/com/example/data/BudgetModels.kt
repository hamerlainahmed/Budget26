package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.math.max

/**
 * Circular No. 13 dated Feb 15, 2026 (المنشور الوزاري رقم 13 المؤرخ في 15 فيفري 2026)
 * Official annual scholarship & catering subsidy rates in Algerian Dinars (DZD).
 */
object Circular13Constants {
    const val INTERNAL_STUDENT_RATE_DZD = 33_696.00
    const val SEMI_INTERNAL_STUDENT_RATE_DZD = 13_248.00
    const val CHAPTER_2_MIN_OPERATIONAL_RATIO = 0.35 // 35% minimum for utilities & annexes
    const val DEFAULT_FISCAL_YEAR = 2026

    const val REV_CATERING_ARTICLE_ID = "R-1-1"
    const val EXP_CATERING_ARTICLE_ID = "E-6-1"
    const val EXP_CHAPTER_2_ID = "EXP_CH2"
    const val EXP_CHAPTER_6_ID = "EXP_CH6"

    fun calculateCateringSubsidy(internalCount: Int, semiInternalCount: Int): Double {
        val safeInternal = max(0, internalCount)
        val safeSemi = max(0, semiInternalCount)
        return (safeInternal * INTERNAL_STUDENT_RATE_DZD) +
            (safeSemi * SEMI_INTERNAL_STUDENT_RATE_DZD)
    }
}

enum class SectionType {
    REVENUE,
    EXPENDITURE
}

enum class ConsumptionHealth {
    STABLE,   // < 70% (Emerald Green)
    ELEVATED, // 70% - 90% (Amber/Orange)
    CRITICAL, // > 90% (Crimson Red)
    OVER_BUDGET // > 100%
}

@Entity(tableName = "institution_config")
data class InstitutionConfigEntity(
    @PrimaryKey val id: Int = 1,
    val institutionName: String,
    val wilayaName: String,
    val regime: String, // داخلي | نصف داخلي | خارجي
    val fiscalYear: Int = 2026,
    val internalScholarshipCount: Int,
    val semiInternalScholarshipCount: Int,
    val updatedAt: Long = System.currentTimeMillis()
) {
    val totalCateringSubsidy: Double
        get() = Circular13Constants.calculateCateringSubsidy(
            internalScholarshipCount,
            semiInternalScholarshipCount
        )
}

@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey val id: String,
    val sectionType: String, // "REVENUE" or "EXPENDITURE"
    val titleHeader: String, // e.g., "القسم الثاني: النفقات - الفرع الأول"
    val chapterNumber: Int,
    val name: String,
    val description: String
)

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val id: String,
    val chapterId: String,
    val sectionType: String, // "REVENUE" or "EXPENDITURE"
    val articleNumber: Int,
    val officialCode: String, // e.g., "212/01"
    val name: String,
    val description: String,
    val allocatedAmount: Double,
    val spentAmount: Double = 0.0,
    val isAutoCatering: Boolean = false
) {
    val remainingCredit: Double
        get() = allocatedAmount - spentAmount

    val consumptionRate: Double
        get() = if (allocatedAmount <= 0.0) {
            if (spentAmount > 0.0) 100.0 else 0.0
        } else {
            (spentAmount / allocatedAmount) * 100.0
        }

    val healthStatus: ConsumptionHealth
        get() = when {
            consumptionRate > 100.0 -> ConsumptionHealth.OVER_BUDGET
            consumptionRate >= 90.0 -> ConsumptionHealth.CRITICAL
            consumptionRate >= 70.0 -> ConsumptionHealth.ELEVATED
            else -> ConsumptionHealth.STABLE
        }
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val articleId: String,
    val chapterId: String,
    val chapterName: String,
    val articleCode: String,
    val articleName: String,
    val voucherNumber: String,
    val supplierName: String,
    val description: String,
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis()
)

data class ChapterWithArticles(
    val chapter: ChapterEntity,
    val articles: List<ArticleEntity>
) {
    val totalAllocated: Double
        get() = articles.sumOf { it.allocatedAmount }

    val totalSpent: Double
        get() = articles.sumOf { it.spentAmount }

    val remainingCredit: Double
        get() = totalAllocated - totalSpent

    val consumptionRate: Double
        get() = if (totalAllocated <= 0.0) 0.0 else (totalSpent / totalAllocated) * 100.0

    val healthStatus: ConsumptionHealth
        get() = when {
            consumptionRate > 100.0 -> ConsumptionHealth.OVER_BUDGET
            consumptionRate >= 90.0 -> ConsumptionHealth.CRITICAL
            consumptionRate >= 70.0 -> ConsumptionHealth.ELEVATED
            else -> ConsumptionHealth.STABLE
        }
}

data class Chapter2AuditCheck(
    val chapter2Allocated: Double,
    val totalOperationalAllocatedExcludingCatering: Double,
    val currentPercentage: Double,
    val requiredMinimumAmount: Double,
    val deficitAmount: Double,
    val isCompliant: Boolean
)
