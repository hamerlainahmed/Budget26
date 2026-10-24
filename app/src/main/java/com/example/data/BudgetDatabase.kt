package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        InstitutionConfigEntity::class,
        ChapterEntity::class,
        ArticleEntity::class,
        TransactionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BudgetDatabase : RoomDatabase() {

    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: BudgetDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope): BudgetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BudgetDatabase::class.java,
                    "algerian_school_budget_2026.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()

                INSTANCE = instance
                scope.launch(Dispatchers.IO) {
                    seedDatabaseIfEmpty(instance.budgetDao())
                }
                instance
            }
        }

        /**
         * Pre-populates the Algerian Public School Operating Budget Nomenclature
         * governed by Circular No. 29 (Feb 15, 2018) and Circular No. 13 (Feb 15, 2026).
         */
        suspend fun seedDatabaseIfEmpty(dao: BudgetDao) {
            if (dao.getChaptersCount() > 0) return

            val initialInternalStudents = 42
            val initialSemiInternalStudents = 310
            val cateringSubsidy = Circular13Constants.calculateCateringSubsidy(
                internalCount = initialInternalStudents,
                semiInternalCount = initialSemiInternalStudents
            ) // (42 * 33,696) + (310 * 13,248) = 1,415,232 + 4,106,880 = 5,522,112.00 DZD

            val defaultConfig = InstitutionConfigEntity(
                id = 1,
                institutionName = "ثانوية الشهيد العربي بن مهيدي - النموذجية",
                wilayaName = "مديرية التربية لولاية الجزائر وسط",
                regime = "داخلي ونصف داخلي",
                fiscalYear = 2026,
                internalScholarshipCount = initialInternalStudents,
                semiInternalScholarshipCount = initialSemiInternalStudents
            )
            dao.upsertInstitutionConfig(defaultConfig)

            val chapters = listOf(
                // Revenues (Title 1)
                ChapterEntity(
                    id = "REV_CH1",
                    sectionType = SectionType.REVENUE.name,
                    titleHeader = "القسم الأول: إيرادات التسيير - الباب الأول",
                    chapterNumber = 1,
                    name = "إعانات التسيير الممنوحة من الدولة",
                    description = "مخصصات الدولة للتغذية والمنح الوطنية والتسيير العام (المنشور 13/2026)"
                ),
                ChapterEntity(
                    id = "REV_CH2",
                    sectionType = SectionType.REVENUE.name,
                    titleHeader = "القسم الأول: إيرادات التسيير - الباب الثاني",
                    chapterNumber = 2,
                    name = "الموارد الخاصة والمداخيل المختلفة",
                    description = "مساهمات العائلات في الإطعام والمداخيل الذاتية للمؤسسة"
                ),
                // Expenditures (Title 2 -> Branch 1)
                ChapterEntity(
                    id = "EXP_CH1",
                    sectionType = SectionType.EXPENDITURE.name,
                    titleHeader = "القسم الثاني: النفقات - الفرع الأول",
                    chapterNumber = 1,
                    name = "تسديد النفقات الخاصة بالتعليم",
                    description = "الوسائل التعليمية، المخابر، الورشات، المكتبة، والطباعة البيداغوجية"
                ),
                ChapterEntity(
                    id = "EXP_CH2",
                    sectionType = SectionType.EXPENDITURE.name,
                    titleHeader = "القسم الثاني: النفقات - الفرع الأول (شرط 35%)",
                    chapterNumber = 2,
                    name = "التكاليف الملحقة (ماء، غاز، كهرباء، اتصالات)",
                    description = "يخضع وجوباً لشرط تخصيص 35% على الأقل من نفقات التسيير العام (المنشور 13/2026)"
                ),
                ChapterEntity(
                    id = "EXP_CH3",
                    sectionType = SectionType.EXPENDITURE.name,
                    titleHeader = "القسم الثاني: النفقات - الفرع الأول",
                    chapterNumber = 3,
                    name = "الصيانة والتصليحات الجارية والنظافة",
                    description = "صيانة الهياكل البيداغوجية، مواد التنظيف والتطهير، وصيانة العتاد"
                ),
                ChapterEntity(
                    id = "EXP_CH4",
                    sectionType = SectionType.EXPENDITURE.name,
                    titleHeader = "القسم الثاني: النفقات - الفرع الأول",
                    chapterNumber = 4,
                    name = "نفقات التأمين والضرائب والرسوم",
                    description = "تأمين الهياكل والحظيرة والمسؤولية المدنية للمؤسسة"
                ),
                ChapterEntity(
                    id = "EXP_CH5",
                    sectionType = SectionType.EXPENDITURE.name,
                    titleHeader = "القسم الثاني: النفقات - الفرع الأول",
                    chapterNumber = 5,
                    name = "نفقات التسيير الإداري والبيداغوجي",
                    description = "اللوازم المكتبية، السجلات المحاسبية، ومصاريف المهمات والتنقلات"
                ),
                ChapterEntity(
                    id = "EXP_CH6",
                    sectionType = SectionType.EXPENDITURE.name,
                    titleHeader = "القسم الثاني: النفقات - الفرع الأول (منشور 13/2026)",
                    chapterNumber = 6,
                    name = "نفقات الإطعام المدرسي والمنح",
                    description = "تغذية التلاميذ الممنوحين (داخلي 33,696 د.ج / نصف داخلي 13,248 د.ج) وصيانة المطعم"
                ),
                ChapterEntity(
                    id = "EXP_CH7",
                    sectionType = SectionType.EXPENDITURE.name,
                    titleHeader = "القسم الثاني: النفقات - الفرع الأول",
                    chapterNumber = 7,
                    name = "النشاط الثقافي والرياضي والوقاية الصحية",
                    description = "جوائز التلاميذ المتفوقين، الأنشطة اللاصفية، ومستلزمات وحدة الكشف والمتابعة"
                )
            )
            dao.insertChapters(chapters)

            val articles = listOf(
                // Revenues - Chapter 1
                ArticleEntity(
                    id = Circular13Constants.REV_CATERING_ARTICLE_ID,
                    chapterId = "REV_CH1",
                    sectionType = SectionType.REVENUE.name,
                    articleNumber = 1,
                    officialCode = "111/01",
                    name = "إعانة التغذية والمنح الوطنية (المنشور 13/2026)",
                    description = "محتسبة آلياً: (داخلي × 33,696 د.ج) + (نصف داخلي × 13,248 د.ج)",
                    allocatedAmount = cateringSubsidy,
                    spentAmount = cateringSubsidy, // Realized/Allocated grant
                    isAutoCatering = true
                ),
                ArticleEntity(
                    id = "R-1-2",
                    chapterId = "REV_CH1",
                    sectionType = SectionType.REVENUE.name,
                    articleNumber = 2,
                    officialCode = "111/02",
                    name = "إعانة التسيير العام الممنوحة من الدولة",
                    description = "مخصصات التسيير المالي والبيداغوجي والتكاليف الملحقة",
                    allocatedAmount = 3_950_000.0,
                    spentAmount = 3_950_000.0
                ),
                // Revenues - Chapter 2
                ArticleEntity(
                    id = "R-2-1",
                    chapterId = "REV_CH2",
                    sectionType = SectionType.REVENUE.name,
                    articleNumber = 1,
                    officialCode = "121/01",
                    name = "مساهمات العائلات في النظامين الداخلي ونصف الداخلي",
                    description = "حقوق التسجيل ومساهمة الأولياء في التغذية المدرسية",
                    allocatedAmount = 420_000.0,
                    spentAmount = 385_000.0
                ),
                ArticleEntity(
                    id = "R-2-2",
                    chapterId = "REV_CH2",
                    sectionType = SectionType.REVENUE.name,
                    articleNumber = 2,
                    officialCode = "121/02",
                    name = "المداخيل المختلفة وإيجار السكنات الوظيفية",
                    description = "اقتطاعات الإيجار والأعباء المشتركة والموارد الاستثنائية",
                    allocatedAmount = 180_000.0,
                    spentAmount = 165_000.0
                ),

                // Expenditures - Chapter 1 (Teaching Expenses)
                ArticleEntity(
                    id = "E-1-1",
                    chapterId = "EXP_CH1",
                    sectionType = SectionType.EXPENDITURE.name,
                    articleNumber = 1,
                    officialCode = "211/01",
                    name = "الوسائل التعليمية ومواد المخابر والورشات",
                    description = "المواد الكيميائية، الزجاجيات، والتجهيزات البيداغوجية للتجارب",
                    allocatedAmount = 320_000.0,
                    spentAmount = 195_000.0
                ),
                ArticleEntity(
                    id = "E-1-2",
                    chapterId = "EXP_CH1",
                    sectionType = SectionType.EXPENDITURE.name,
                    articleNumber = 2,
                    officialCode = "211/02",
                    name = "نفقات المكتبة المدرسية والوثائق البيداغوجية",
                    description = "اقتناء الكتب، المراجع العلمية، والمجلات التربوية",
                    allocatedAmount = 130_000.0,
                    spentAmount = 74_500.0
                ),
                ArticleEntity(
                    id = "E-1-3",
                    chapterId = "EXP_CH1",
                    sectionType = SectionType.EXPENDITURE.name,
                    articleNumber = 3,
                    officialCode = "211/03",
                    name = "ورق ولوازم السحب والنسخ للفروض والاختبارات",
                    description = "رزم الورق، الأحبار، ومستلزمات طبع مواضيع الامتحانات الفصلية",
                    allocatedAmount = 290_000.0,
                    spentAmount = 248_000.0
                ),

                // Expenditures - Chapter 2 (Utilities & Annexes - >= 35% of operational charges)
                ArticleEntity(
                    id = "E-2-1",
                    chapterId = "EXP_CH2",
                    sectionType = SectionType.EXPENDITURE.name,
                    articleNumber = 1,
                    officialCode = "212/01",
                    name = "استهلاك الكهرباء والغاز والتدفئة (سونلغاز)",
                    description = "فواتير الطاقة الكهربائية والغاز الطبيعي لتدفئة الحجرات والمرقد",
                    allocatedAmount = 510_000.0,
                    spentAmount = 385_000.0
                ),
                ArticleEntity(
                    id = "E-2-2",
                    chapterId = "EXP_CH2",
                    sectionType = SectionType.EXPENDITURE.name,
                    articleNumber = 2,
                    officialCode = "212/02",
                    name = "استهلاك المياه الصالحة للشرب والتطهير (ADE)",
                    description = "فواتير المياه للمطعم والمراقد والمرافق الصحية",
                    allocatedAmount = 410_000.0,
                    spentAmount = 312_000.0
                ),
                ArticleEntity(
                    id = "E-2-3",
                    chapterId = "EXP_CH2",
                    sectionType = SectionType.EXPENDITURE.name,
                    articleNumber = 3,
                    officialCode = "212/03",
                    name = "أتاوى البريد والهاتف والأنترنت (اتصالات الجزائر)",
                    description = "اشتراكات الألياف البصرية والخطوط الهاتفية الرسمية",
                    allocatedAmount = 145_000.0,
                    spentAmount = 98_400.0
                ),
                ArticleEntity(
                    id = "E-2-4",
                    chapterId = "EXP_CH2",
                    sectionType = SectionType.EXPENDITURE.name,
                    articleNumber = 4,
                    officialCode = "212/04",
                    name = "الوقود والزيوت وصيانة السيارة النفعية",
                    description = "وصولات الوقود، العجلات المطاطية، وقطع الغيار",
                    allocatedAmount = 185_000.0,
                    spentAmount = 124_000.0
                ),

                // Expenditures - Chapter 3 (Maintenance & Hygiene)
                ArticleEntity(
                    id = "E-3-1",
                    chapterId = "EXP_CH3",
                    sectionType = SectionType.EXPENDITURE.name,
                    articleNumber = 1,
                    officialCode = "213/01",
                    name = "صيانة المباني البيداغوجية والإدارية والترميمات",
                    description = "أشغال الدهن، السباكة، الكتامة، وتصليح الأبواب والنوافذ",
                    allocatedAmount = 480_000.0,
                    spentAmount = 390_000.0
                ),
                ArticleEntity(
                    id = "E-3-2",
                    chapterId = "EXP_CH3",
                    sectionType = SectionType.EXPENDITURE.name,
                    articleNumber = 2,
                    officialCode = "213/02",
                    name = "مواد التنظيف والتطهير والوقاية الصحية",
                    description = "مواد التعقيم اليومي للأقسام والمطعم والمرافق الصحية",
                    allocatedAmount = 260_000.0,
                    spentAmount = 178_000.0
                ),
                ArticleEntity(
                    id = "E-3-3",
                    chapterId = "EXP_CH3",
                    sectionType = SectionType.EXPENDITURE.name,
                    articleNumber = 3,
                    officialCode = "213/03",
                    name = "صيانة عتاد الإعلام الآلي والتجهيزات المكتبية",
                    description = "تصليح الحواسيب، الناسخات، وتجهيزات المقتصدية والأمانة",
                    allocatedAmount = 210_000.0,
                    spentAmount = 142_000.0
                ),

                // Expenditures - Chapter 4 (Insurance & Taxes)
                ArticleEntity(
                    id = "E-4-1",
                    chapterId = "EXP_CH4",
                    sectionType = SectionType.EXPENDITURE.name,
                    articleNumber = 1,
                    officialCode = "214/01",
                    name = "تأمين الهياكل والحظيرة والمسؤولية المدنية",
                    description = "عقود التأمين السنوية الشاملة (SAA / CAAR)",
                    allocatedAmount = 140_000.0,
                    spentAmount = 132_500.0
                ),

                // Expenditures - Chapter 5 (Admin & Pedagogical Management)
                ArticleEntity(
                    id = "E-5-1",
                    chapterId = "EXP_CH5",
                    sectionType = SectionType.EXPENDITURE.name,
                    articleNumber = 1,
                    officialCode = "215/01",
                    name = "اللوازم المكتبية والمطبوعات والسجلات المحاسبية",
                    description = "سجلات الصندوق، دفاتر الوصولات، الحوالات، وأدوات المكتب",
                    allocatedAmount = 240_000.0,
                    spentAmount = 164_000.0
                ),
                ArticleEntity(
                    id = "E-5-2",
                    chapterId = "EXP_CH5",
                    sectionType = SectionType.EXPENDITURE.name,
                    articleNumber = 2,
                    officialCode = "215/02",
                    name = "تعويضات التنقل والمهمات الرسمية",
                    description = "مصاريف تنقل المسير المالي والأعوان للخزينة والمديرية",
                    allocatedAmount = 110_000.0,
                    spentAmount = 62_000.0
                ),

                // Expenditures - Chapter 6 (Catering - Circular 13/2026)
                ArticleEntity(
                    id = Circular13Constants.EXP_CATERING_ARTICLE_ID,
                    chapterId = "EXP_CH6",
                    sectionType = SectionType.EXPENDITURE.name,
                    articleNumber = 1,
                    officialCode = "216/01",
                    name = "إطعام التلاميذ الممنوحين (داخلي / نصف داخلي)",
                    description = "مخصص التغذية المدرسية المحسوب آلياً بموجب المنشور الوزاري 13/2026",
                    allocatedAmount = cateringSubsidy,
                    spentAmount = 2_845_000.0,
                    isAutoCatering = true
                ),
                ArticleEntity(
                    id = "E-6-2",
                    chapterId = "EXP_CH6",
                    sectionType = SectionType.EXPENDITURE.name,
                    articleNumber = 2,
                    officialCode = "216/02",
                    name = "تجديد وصيانة عتاد المطعم والمطبخ والبياضة",
                    description = "اقتناء الأواني، صيانة غرف التبريد والأفران المهنية",
                    allocatedAmount = 420_000.0,
                    spentAmount = 295_000.0
                ),

                // Expenditures - Chapter 7 (Cultural, Sports & Health)
                ArticleEntity(
                    id = "E-7-1",
                    chapterId = "EXP_CH7",
                    sectionType = SectionType.EXPENDITURE.name,
                    articleNumber = 1,
                    officialCode = "217/01",
                    name = "النشاط الثقافي والرياضي وجوائز التلاميذ المتفوقين",
                    description = "الجوائز التشجيعية، المنافسات الرياضية، ومستلزمات وحدة الكشف الصحي",
                    allocatedAmount = 250_000.0,
                    spentAmount = 118_000.0
                )
            )
            dao.insertArticles(articles)

            val now = System.currentTimeMillis()
            val dayMs = 86_400_000L
            val initialTransactions = listOf(
                TransactionEntity(
                    articleId = Circular13Constants.EXP_CATERING_ARTICLE_ID,
                    chapterId = "EXP_CH6",
                    chapterName = "الباب 6: نفقات الإطعام المدرسي والمنح",
                    articleCode = "216/01",
                    articleName = "إطعام التلاميذ الممنوحين (داخلي / نصف داخلي)",
                    voucherNumber = "MD-2026/048",
                    supplierName = "الديوان الوطني للحبوب والتموين الغذائي",
                    description = "تسديد فاتورة المواد الغذائية العامة والبقوليات الجافة - الثلاثي الثاني",
                    amount = 1_450_000.0,
                    timestamp = now - (dayMs * 2)
                ),
                TransactionEntity(
                    articleId = Circular13Constants.EXP_CATERING_ARTICLE_ID,
                    chapterId = "EXP_CH6",
                    chapterName = "الباب 6: نفقات الإطعام المدرسي والمنح",
                    articleCode = "216/01",
                    articleName = "إطعام التلاميذ الممنوحين (داخلي / نصف داخلي)",
                    voucherNumber = "MD-2026/041",
                    supplierName = "مؤسسة اللحوم والخضر والفواكه الطازجة",
                    description = "توريد اللحوم البيضاء والحمراء والخضر الموسمية للمطعم المدرسي",
                    amount = 1_395_000.0,
                    timestamp = now - (dayMs * 6)
                ),
                TransactionEntity(
                    articleId = "E-2-1",
                    chapterId = "EXP_CH2",
                    chapterName = "الباب 2: التكاليف الملحقة (ماء، غاز، كهرباء، اتصالات)",
                    articleCode = "212/01",
                    articleName = "استهلاك الكهرباء والغاز والتدفئة (سونلغاز)",
                    voucherNumber = "MD-2026/039",
                    supplierName = "سونلغاز - مديرية التوزيع الجزائر الوسطى",
                    description = "تسديد فاتورة استهلاك الكهرباء والغاز الطبيعي للتدفئة المدرسية",
                    amount = 385_000.0,
                    timestamp = now - (dayMs * 9)
                ),
                TransactionEntity(
                    articleId = "E-3-1",
                    chapterId = "EXP_CH3",
                    chapterName = "الباب 3: الصيانة والتصليحات الجارية والنظافة",
                    articleCode = "213/01",
                    articleName = "صيانة المباني البيداغوجية والإدارية والترميمات",
                    voucherNumber = "MD-2026/034",
                    supplierName = "مقاولة الأشغال العمومية والصيانة المدرسية",
                    description = "ترميم شبكة التدفئة المركزية وصيانة مساكة الجناح البيداغوجي",
                    amount = 390_000.0,
                    timestamp = now - (dayMs * 14)
                ),
                TransactionEntity(
                    articleId = "E-2-2",
                    chapterId = "EXP_CH2",
                    chapterName = "الباب 2: التكاليف الملحقة (ماء، غاز، كهرباء، اتصالات)",
                    articleCode = "212/02",
                    articleName = "استهلاك المياه الصالحة للشرب والتطهير (ADE)",
                    voucherNumber = "MD-2026/028",
                    supplierName = "شركة المياه والتطهير للجزائر (سيال)",
                    description = "تسديد مستحقات استهلاك المياه الصالحة للشرب للمرقد والمطعم",
                    amount = 312_000.0,
                    timestamp = now - (dayMs * 18)
                ),
                TransactionEntity(
                    articleId = "E-1-3",
                    chapterId = "EXP_CH1",
                    chapterName = "الباب 1: تسديد النفقات الخاصة بالتعليم",
                    articleCode = "211/03",
                    articleName = "ورق ولوازم السحب والنسخ للفروض والاختبارات",
                    voucherNumber = "MD-2026/021",
                    supplierName = "الشركة الوطنية للوراقة والطباعة المكتبية",
                    description = "اقتناء 250 رزمة ورق A4 وأحبار الناسخات الرقمية لاختبارات الفصل",
                    amount = 248_000.0,
                    timestamp = now - (dayMs * 24)
                ),
                TransactionEntity(
                    articleId = "E-4-1",
                    chapterId = "EXP_CH4",
                    chapterName = "الباب 4: نفقات التأمين والضرائب والرسوم",
                    articleCode = "214/01",
                    articleName = "تأمين الهياكل والحظيرة والمسؤولية المدنية",
                    voucherNumber = "MD-2026/012",
                    supplierName = "الشركة الوطنية للتأمين (SAA)",
                    description = "عقد التأمين السنوي الشامل للهياكل والتجهيزات والسيارة النفعية",
                    amount = 132_500.0,
                    timestamp = now - (dayMs * 31)
                )
            )
            initialTransactions.forEach { dao.insertTransaction(it) }
        }
    }
}
