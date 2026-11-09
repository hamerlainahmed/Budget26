package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.BudgetDatabase
import com.example.data.Circular13Constants
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("الميزانية المدرسية 2026", appName)
    }

    @Test
    fun `seed database, verify 2024 nomenclature expenditures, credit reservation, balance checks and consumption rates`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, BudgetDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val dao = db.budgetDao()

        BudgetDatabase.seedDatabaseIfEmpty(dao)

        // 1. Verify chapters are exclusively expenditures and match the 2024 nomenclature (20 chapters)
        val chapters = dao.observeChapters().first()
        assertEquals(20, chapters.size)
        assertTrue(chapters.all { it.sectionType == "EXPENDITURE" })
        assertEquals("الباب 01", chapters[0].officialCode)
        assertEquals("تسديد المصاريف", chapters[0].name)
        assertEquals("الباب 02", chapters[1].officialCode)
        assertEquals("التكاليف الملحقة", chapters[1].name)
        assertEquals("الباب 03", chapters[2].officialCode)
        assertEquals("اللوازم", chapters[2].name)
        assertEquals("الباب 06", chapters[5].officialCode)
        assertEquals("نفقات الإطعام", chapters[5].name)

        // 2. Verify pre-seeded history contains both 2025 and 2026
        val savedYears = dao.observeAllFiscalYears().first()
        assertEquals(2, savedYears.size)
        assertTrue(savedYears.any { it.fiscalYear == 2025 })
        assertTrue(savedYears.any { it.fiscalYear == 2026 && it.isActiveYear })

        // 3. Verify Authorizing Officer credit allocation (حجز الاعتماد)
        val exp2026UtilityId = Circular13Constants.articleRecordId(2026, "EXP_02_01")
        dao.updateArticleAllocation(
            articleRecordId = exp2026UtilityId,
            newAllocated = 1_500_000.0
        )
        val updatedUtility = dao.getArticleById(exp2026UtilityId)!!
        assertEquals(1_500_000.0, updatedUtility.allocatedAmount, 0.01)

        // 4. Verify balance check and sufficient credit deduction via سند الطلب
        val availableBefore = updatedUtility.remainingCredit
        val expenseAmount = 200_000.0
        assertTrue("يجب أن يكون الاعتماد كافياً قبل الاقتطاع", availableBefore >= expenseAmount)

        val txId = dao.commitExpenseTransaction(
            articleRecordId = exp2026UtilityId,
            chapterName = "الباب 02: التكاليف الملحقة",
            orderNumber = "BC-2026/777",
            supplierName = "سونلغاز",
            description = "فاتورة استهلاك الغاز والكهرباء للثلاثي الأول",
            amount = expenseAmount
        )

        val afterExpense = dao.getArticleById(exp2026UtilityId)!!
        assertEquals(availableBefore - expenseAmount, afterExpense.remainingCredit, 0.01)
        val expectedRate = (afterExpense.spentAmount / afterExpense.allocatedAmount) * 100.0
        assertEquals(expectedRate, afterExpense.consumptionRate, 0.01)

        // Verify بطاقة الالتزام (Fiche d'Engagement) transactions for this article
        val articleTxs = dao.observeTransactionsByArticle(exp2026UtilityId).first()
        assertTrue(articleTxs.isNotEmpty())
        assertEquals("BC-2026/777", articleTxs.last().orderNumber)
        assertEquals(expenseAmount, articleTxs.last().amount, 0.01)

        // 5. Rollback transaction and verify refund
        dao.rollbackExpenseTransaction(txId)
        val afterRefund = dao.getArticleById(exp2026UtilityId)!!
        assertEquals(availableBefore, afterRefund.remainingCredit, 0.01)

        // 6. Test opening fiscal year 2027 and injecting catering subsidy
        val expectedSubsidy2027 = (50 * 33_696.00) + (200 * 13_248.00)
        val returnedSubsidy2027 = dao.applyCateringBudgetTransaction(
            institutionName = "ثانوية العقيد لطفي",
            wilayaName = "مديرية التربية لولاية وهران",
            regime = "داخلي ونصف داخلي",
            fiscalYear = 2027,
            internalStudents = 50,
            semiInternalStudents = 200,
            clearExpensesForTargetYear = true,
            startWithZeroCustomAllocations = false,
            templateArticlesProvider = BudgetDatabase::buildCleanArticlesForFiscalYear
        )
        assertEquals(expectedSubsidy2027, returnedSubsidy2027, 0.01)

        val exp2027CateringId = Circular13Constants.articleRecordId(2027, Circular13Constants.EXP_CATERING_BASE_ID)
        val cateringArticle2027 = dao.getArticleById(exp2027CateringId)!!
        assertEquals(expectedSubsidy2027, cateringArticle2027.allocatedAmount, 0.01)
        assertEquals(0.0, cateringArticle2027.spentAmount, 0.01)
        assertEquals(expectedSubsidy2027, cateringArticle2027.remainingCredit, 0.01)

        db.close()
    }
}
