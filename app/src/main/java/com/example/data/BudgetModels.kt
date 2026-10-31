package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.math.max

/**
 * Circular No. 29 dated Feb 15, 2018 (المنشور الوزاري رقم 29 المؤرخ في 15 فيفري 2018)
 * as updated by Ministerial Circular No. 13 dated Feb 15, 2026 (المنشور الوزاري رقم 13 المؤرخ في 15 فيفري 2026).
 */
object Circular13Constants {
    const val INTERNAL_STUDENT_RATE_DZD = 33_696.00
    const val SEMI_INTERNAL_STUDENT_RATE_DZD = 13_248.00
    const val CHAPTER_2_MIN_OPERATIONAL_RATIO = 0.35 // 35% minimum for Chapter 2 (Utilities & Annexes)
    const val DEFAULT_FISCAL_YEAR = 2026

    // Official base article codes in Circular 29/2018 nomenclature
    const val REV_CATERING_BASE_ID = "REV_01_01" // العنوان 1 - الباب 01 - المادة 01: إعانة التسيير للتغذية
    const val EXP_CATERING_BASE_ID = "EXP_06_01" // العنوان 2 - الفرع 1 - الباب 06 - المادة 01: إطعام التلاميذ الداخليين ونصف الداخليين
    const val EXP_CHAPTER_2_BASE_ID = "EXP_CH02" // الباب 02: التكاليف الملحقة
    const val EXP_CHAPTER_6_BASE_ID = "EXP_CH06" // الباب 06: نفقات الإطعام

    fun articleRecordId(fiscalYear: Int, baseArticleId: String): String =
        "${fiscalYear}_$baseArticleId"

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
    STABLE,      // < 70% (Emerald Green)
    ELEVATED,    // 70% - 90% (Amber/Orange)
    CRITICAL,    // > 90% (Crimson Red)
    OVER_BUDGET  // > 100%
}

/**
 * Stores configuration per Fiscal Year (كل سنة مالية تحفظ إعداداتها الخاصة في السجل التاريخي).
 */
@Entity(tableName = "institution_configs")
data class InstitutionConfigEntity(
    @PrimaryKey val fiscalYear: Int,
    val institutionName: String,
    val wilayaName: String,
    val regime: String, // داخلي ونصف داخلي | نصف داخلي | داخلي | خارجي
    val internalScholarshipCount: Int,
    val semiInternalScholarshipCount: Int,
    val isActiveYear: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
) {
    val totalCateringSubsidy: Double
        get() = Circular13Constants.calculateCateringSubsidy(
            internalScholarshipCount,
            semiInternalScholarshipCount
        )
}

/**
 * Official nomenclature Chapters from Circular No. 29 dated Feb 15, 2018.
 */
@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey val id: String, // e.g., "REV_CH01", "EXP_CH01".."EXP_CH19"
    val sectionType: String,   // "REVENUE" or "EXPENDITURE"
    val branchNumber: Int,     // 1, 2, or 3 (الفرع الأول، الفرع الثاني، الفرع الثالث)
    val branchTitle: String,   // e.g., "الفرع الأول: نفقات تسيير المصالح"
    val chapterNumber: Int,    // Official chapter number (الباب 01 .. الباب 15)
    val officialCode: String,  // e.g., "الباب 01", "الباب 02"
    val name: String,          // Official Arabic title in Circular 29/2018
    val description: String
)

/**
 * Per-Fiscal-Year Article allocations and expenditures.
 * Each fiscal year has its own independent records so switching years preserves full history.
 */
@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val id: String,        // "${fiscalYear}_${baseArticleId}"
    val baseArticleId: String,         // e.g., "EXP_02_01"
    val fiscalYear: Int,               // e.g., 2026
    val chapterId: String,             // Foreign key to ChapterEntity.id
    val sectionType: String,           // "REVENUE" or "EXPENDITURE"
    val chapterNumber: Int,            // 1..15
    val articleNumber: Int,            // 1..8 (المادة 01، المادة 02...)
    val officialCode: String,          // Official الباب/المادة notation: e.g., "ب02 / م01"
    val name: String,                  // Official Arabic article title from Circular 29/2018
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

/**
 * Per-Fiscal-Year Transaction (حوالة دفع / فاتورة).
 * Stored with `fiscalYear` so each fiscal year preserves its own independent ledger history.
 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fiscalYear: Int,
    val articleId: String,     // "${fiscalYear}_${baseArticleId}"
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
