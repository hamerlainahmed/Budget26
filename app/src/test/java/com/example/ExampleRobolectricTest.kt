package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.BudgetDatabase
import com.example.data.Circular13Constants
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
    fun `seed database and verify Circular 13_2026 catering auto injection and rollback`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, BudgetDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val dao = db.budgetDao()

        BudgetDatabase.seedDatabaseIfEmpty(dao)

        // Apply new pupil counts: 50 internal, 200 semi-internal
        val expectedSubsidy = (50 * 33_696.00) + (200 * 13_248.00)
        val returnedSubsidy = dao.applyCateringBudgetTransaction(
            institutionName = "ثانوية العقيد لطفي",
            wilayaName = "مديرية التربية لولاية وهران",
            regime = "داخلي ونصف داخلي",
            fiscalYear = 2026,
            internalStudents = 50,
            semiInternalStudents = 200
        )
        assertEquals(expectedSubsidy, returnedSubsidy, 0.01)

        // Verify injection into Revenue R-1-1 and Expenditure E-6-1
        val revArticle = dao.getArticleById(Circular13Constants.REV_CATERING_ARTICLE_ID)
        val expArticle = dao.getArticleById(Circular13Constants.EXP_CATERING_ARTICLE_ID)
        assertNotNull(revArticle)
        assertNotNull(expArticle)
        assertEquals(expectedSubsidy, revArticle!!.allocatedAmount, 0.01)
        assertEquals(expectedSubsidy, expArticle!!.allocatedAmount, 0.01)

        // Commit expense and verify spentAmount increases, then rollback and verify refund
        val spentBefore = expArticle.spentAmount
        val txId = dao.commitExpenseTransaction(
            articleId = Circular13Constants.EXP_CATERING_ARTICLE_ID,
            chapterName = "الباب 6",
            voucherNumber = "MD-2026/999",
            supplierName = "ممون تجريبي",
            description = "فاتورة تموين غذائي",
            amount = 150_000.0
        )
        val afterSpend = dao.getArticleById(Circular13Constants.EXP_CATERING_ARTICLE_ID)!!
        assertEquals(spentBefore + 150_000.0, afterSpend.spentAmount, 0.01)

        dao.rollbackExpenseTransaction(txId)
        val afterRollback = dao.getArticleById(Circular13Constants.EXP_CATERING_ARTICLE_ID)!!
        assertEquals(spentBefore, afterRollback.spentAmount, 0.01)

        db.close()
    }
}
