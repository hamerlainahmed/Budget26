package com.example

import com.example.data.ArticleEntity
import com.example.data.Circular13Constants
import com.example.data.ConsumptionHealth
import com.example.data.SectionType
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun circular13CateringFormula_calculatesExactSubsidy() {
        val internalCount = 42
        val semiInternalCount = 310
        val expected = (42 * 33_696.00) + (310 * 13_248.00)
        val actual = Circular13Constants.calculateCateringSubsidy(internalCount, semiInternalCount)
        assertEquals(expected, actual, 0.001)
    }

    @Test
    fun articleHealthMetrics_classifiesConsumptionRatesProperly() {
        val stableArticle = ArticleEntity(
            id = "E-1-1",
            chapterId = "EXP_CH1",
            sectionType = SectionType.EXPENDITURE.name,
            articleNumber = 1,
            officialCode = "211/01",
            name = "وسائل تعليمية",
            description = "",
            allocatedAmount = 100_000.0,
            spentAmount = 65_000.0
        )
        assertEquals(ConsumptionHealth.STABLE, stableArticle.healthStatus)
        assertEquals(35_000.0, stableArticle.remainingCredit, 0.01)

        val elevatedArticle = stableArticle.copy(spentAmount = 80_000.0)
        assertEquals(ConsumptionHealth.ELEVATED, elevatedArticle.healthStatus)

        val criticalArticle = stableArticle.copy(spentAmount = 95_000.0)
        assertEquals(ConsumptionHealth.CRITICAL, criticalArticle.healthStatus)

        val overBudgetArticle = stableArticle.copy(spentAmount = 105_000.0)
        assertEquals(ConsumptionHealth.OVER_BUDGET, overBudgetArticle.healthStatus)
    }
}
