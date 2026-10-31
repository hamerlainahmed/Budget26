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
    fun `seed database, verify Circular 29_2018 nomenclature, multi-year history isolation, and expense clearing`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, BudgetDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val dao = db.budgetDao()

        BudgetDatabase.seedDatabaseIfEmpty(dao)

        // 1. Verify pre-seeded history contains both 2025 and 2026
        val savedYears = dao.observeAllFiscalYears().first()
        assertEquals(2, savedYears.size)
        assertTrue(savedYears.any { it.fiscalYear == 2025 })
        assertTrue(savedYears.any { it.fiscalYear == 2026 && it.isActiveYear })

        // 2. Apply updated pupil counts on 2026 without clearing 2026 expenses
        val expectedSubsidy = (50 * 33_696.00) + (200 * 13_248.00)
        val returnedSubsidy = dao.applyCateringBudgetTransaction(
            institutionName = "ثانوية العقيد لطفي",
            wilayaName = "مديرية التربية لولاية وهران",
            regime = "داخلي ونصف داخلي",
            fiscalYear = 2026,
            internalStudents = 50,
            semiInternalStudents = 200,
            clearExpensesForTargetYear = false,
            templateArticlesProvider = BudgetDatabase::buildCleanArticlesForFiscalYear
        )
        assertEquals(expectedSubsidy, returnedSubsidy, 0.01)

        val rev2026Id = Circular13Constants.articleRecordId(2026, Circular13Constants.REV_CATERING_BASE_ID)
        val exp2026Id = Circular13Constants.articleRecordId(2026, Circular13Constants.EXP_CATERING_BASE_ID)
        val revArticle = dao.getArticleById(rev2026Id)
        val expArticle = dao.getArticleById(exp2026Id)
        assertNotNull(revArticle)
        assertNotNull(expArticle)
        assertEquals(expectedSubsidy, revArticle!!.allocatedAmount, 0.01)
        assertEquals(expectedSubsidy, expArticle!!.allocatedAmount, 0.01)

        // 3. Commit expense on 2026 and verify spentAmount increases, then rollback
        val spentBefore = expArticle.spentAmount
        val txId = dao.commitExpenseTransaction(
            articleRecordId = exp2026Id,
            chapterName = "الباب 06: نفقات الإطعام",
            voucherNumber = "MD-2026/999",
            supplierName = "ممون تجريبي",
            description = "فاتورة تموين غذائي",
            amount = 150_000.0
        )
        val afterSpend = dao.getArticleById(exp2026Id)!!
        assertEquals(spentBefore + 150_000.0, afterSpend.spentAmount, 0.01)

        dao.rollbackExpenseTransaction(txId)
        val afterRollback = dao.getArticleById(exp2026Id)!!
        assertEquals(spentBefore, afterRollback.spentAmount, 0.01)

        // 4. Open a brand-new fiscal year 2027 and verify its expenses are 0.0 while 2026 history remains intact
        dao.applyCateringBudgetTransaction(
            institutionName = "ثانوية العقيد لطفي",
            wilayaName = "مديرية التربية لولاية وهران",
            regime = "داخلي ونصف داخلي",
            fiscalYear = 2027,
            internalStudents = 60,
            semiInternalStudents = 220,
            clearExpensesForTargetYear = true,
            templateArticlesProvider = BudgetDatabase::buildCleanArticlesForFiscalYear
        )

        val exp2027Id = Circular13Constants.articleRecordId(2027, Circular13Constants.EXP_CATERING_BASE_ID)
        val exp2027 = dao.getArticleById(exp2027Id)!!
        assertEquals(0.0, exp2027.spentAmount, 0.01)
        assertEquals(0, dao.observeTransactionsByYear(2027).first().size)

        // Verify 2026 transactions and spent amounts are still preserved in history
        val preserved2026 = dao.getArticleById(exp2026Id)!!
        assertTrue(preserved2026.spentAmount > 0.0)
        assertTrue(dao.observeTransactionsByYear(2026).first().isNotEmpty())

        db.close()
    }
}
