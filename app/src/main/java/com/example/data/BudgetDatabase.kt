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
    version = 2,
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
         * Official Algerian School Operating Budget Nomenclature (Circular No. 29 dated Feb 15, 2018
         * amended by Ministerial Circular No. 13 dated Feb 15, 2026).
         */
        fun buildOfficialChapters(): List<ChapterEntity> = listOf(
            // =========================================================================
            // العنوان الأول: الإيرادات (الأبواب 01 إلى 12 حسب المدونة الرسمية 29/2018)
            // =========================================================================
            ChapterEntity(
                id = "REV_CH01",
                sectionType = SectionType.REVENUE.name,
                branchNumber = 1,
                branchTitle = "العنوان الأول: الإيرادات - الفرع الأول: إيرادات التسيير",
                chapterNumber = 1,
                officialCode = "الباب 01",
                name = "إعانات التسيير الممنوحة من طرف الدولة",
                description = "إعانة التغذية والمنح الوطنية (المنشور 13/2026)، إعانة المصالح المشتركة، وتعويض الكهرباء والغاز للجنوب"
            ),
            ChapterEntity(
                id = "REV_CH02",
                sectionType = SectionType.REVENUE.name,
                branchNumber = 1,
                branchTitle = "العنوان الأول: الإيرادات - الفرع الأول: إيرادات التسيير",
                chapterNumber = 2,
                officialCode = "الباب 02",
                name = "الإعانات الممنوحة من الجماعات المحلية والمؤسسات والهيئات العمومية",
                description = "إعانات الولاية والبلدية والهيئات العمومية المخصصة للمؤسسة"
            ),
            ChapterEntity(
                id = "REV_CH03",
                sectionType = SectionType.REVENUE.name,
                branchNumber = 1,
                branchTitle = "العنوان الأول: الإيرادات - الفرع الأول: إيرادات التسيير",
                chapterNumber = 3,
                officialCode = "الباب 03",
                name = "الهبات والوصايا",
                description = "الهبات والوصايا والتبرعات المرخص بها قانوناً"
            ),
            ChapterEntity(
                id = "REV_CH04",
                sectionType = SectionType.REVENUE.name,
                branchNumber = 1,
                branchTitle = "العنوان الأول: الإيرادات - الفرع الأول: إيرادات التسيير",
                chapterNumber = 4,
                officialCode = "الباب 04",
                name = "إيرادات مرتبطة بنشاط المؤسسة",
                description = "عائدات الإيجار والأعباء الملحقة للسكنات الوظيفية، مستحقات المبيت، بيع البقايا، الإسقاط، ومبيعات الكتاب المدرسي"
            ),
            ChapterEntity(
                id = "REV_CH05",
                sectionType = SectionType.REVENUE.name,
                branchNumber = 1,
                branchTitle = "العنوان الأول: الإيرادات - الفرع الأول: إيرادات التسيير",
                chapterNumber = 5,
                officialCode = "الباب 05",
                name = "إيرادات مختلفة",
                description = "تحصيل الدفع غير المستحق، تعويضات التأمين، والإيرادات الاستثنائية"
            ),
            ChapterEntity(
                id = "REV_CH06",
                sectionType = SectionType.REVENUE.name,
                branchNumber = 1,
                branchTitle = "العنوان الأول: الإيرادات - الفرع الأول: إيرادات التسيير",
                chapterNumber = 6,
                officialCode = "الباب 06",
                name = "إيرادات ناتجة عن تسديد حقوق التسجيل",
                description = "حقوق التسجيل والتراسل المدرسي للتلاميذ"
            ),
            ChapterEntity(
                id = "REV_CH07",
                sectionType = SectionType.REVENUE.name,
                branchNumber = 1,
                branchTitle = "العنوان الأول: الإيرادات - الفرع الأول: إيرادات التسيير",
                chapterNumber = 7,
                officialCode = "الباب 07",
                name = "إيرادات الإطعام",
                description = "مساهمات عائلات التلاميذ غير الممنوحين، إطعام المستخدمين في الطاولة المشتركة، والمدعوين"
            ),
            ChapterEntity(
                id = "REV_CH08",
                sectionType = SectionType.REVENUE.name,
                branchNumber = 2,
                branchTitle = "العنوان الأول: الإيرادات - الفرع الثاني: أقسام الرياضة والدراسة",
                chapterNumber = 8,
                officialCode = "الباب 08",
                name = "إيرادات تسيير أقسام الرياضة والدراسة",
                description = "الاعتمادات المخصصة لتسيير أقسام الرياضة والدراسة"
            ),
            ChapterEntity(
                id = "REV_CH09",
                sectionType = SectionType.REVENUE.name,
                branchNumber = 3,
                branchTitle = "العنوان الأول: الإيرادات - الفرع الثالث: إيرادات ذات طابع خاص",
                chapterNumber = 9,
                officialCode = "الباب 09",
                name = "إعانات المنحة المدرسية الخاصة والتضامن",
                description = "إعانات المنحة المدرسية الخاصة، وحدات الكشف والمتابعة UDS، ومجانية الكتاب المدرسي"
            ),

            // =========================================================================
            // العنوان الثاني: النفقات - الفرع الأول: نفقات تسيير المصالح (الأبواب 01 إلى 15)
            // =========================================================================
            ChapterEntity(
                id = "EXP_CH01",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "العنوان الثاني: النفقات - الفرع الأول: نفقات تسيير المصالح",
                chapterNumber = 1,
                officialCode = "الباب 01",
                name = "تسديد المصاريف",
                description = "مصاريف المهام والتنقلات للموظفين والأعوان ومصاريف الاستقبالات الرسمية"
            ),
            ChapterEntity(
                id = Circular13Constants.EXP_CHAPTER_2_BASE_ID,
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "العنوان الثاني: النفقات - الفرع الأول: نفقات تسيير المصالح (شرط ≥ 35%)",
                chapterNumber = 2,
                officialCode = "الباب 02",
                name = "التكاليف الملحقة",
                description = "الماء، الكهرباء، الغاز، الوقود، البريد والمواصلات، الإعلان والنشر (يخضع لشرط ≥ 35% من نفقات التسيير)"
            ),
            ChapterEntity(
                id = "EXP_CH03",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "العنوان الثاني: النفقات - الفرع الأول: نفقات تسيير المصالح",
                chapterNumber = 3,
                officialCode = "الباب 03",
                name = "اللوازم",
                description = "أدوات ولوازم المكتب والمطبوعات الإدارية، البياضة، مواد التنظيف والتطهير، والمواد الصيدلانية"
            ),
            ChapterEntity(
                id = "EXP_CH04",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "العنوان الثاني: النفقات - الفرع الأول: نفقات تسيير المصالح",
                chapterNumber = 4,
                officialCode = "الباب 04",
                name = "الأدوات والأثاث",
                description = "تجديد وصيانة الأدوات والأثاث المدرسي والمكتبي، عتاد المطعم، أجهزة الإعلام الآلي والنسخ، ووسائل الوقاية والأمن"
            ),
            ChapterEntity(
                id = "EXP_CH05",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "العنوان الثاني: النفقات - الفرع الأول: نفقات تسيير المصالح",
                chapterNumber = 5,
                officialCode = "الباب 05",
                name = "اللوازم البيداغوجية",
                description = "الوسائل التعليمية ومواد المخابر والورشات، الطباشير والأقلام، ورق ولوازم السحب للفروض والاختبارات، والوثائق المدرسية"
            ),
            ChapterEntity(
                id = Circular13Constants.EXP_CHAPTER_6_BASE_ID,
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "العنوان الثاني: النفقات - الفرع الأول: نفقات تسيير المصالح (المنشور 13/2026)",
                chapterNumber = 6,
                officialCode = "الباب 06",
                name = "نفقات الإطعام",
                description = "إطعام التلاميذ الداخليين ونصف الداخليين (داخلي 33,696 د.ج / نصف داخلي 13,248 د.ج)، إطعام المستخدمين، والمدعوين"
            ),
            ChapterEntity(
                id = "EXP_CH07",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "العنوان الثاني: النفقات - الفرع الأول: نفقات تسيير المصالح",
                chapterNumber = 7,
                officialCode = "الباب 07",
                name = "الألبسة",
                description = "اقتناء ألبسة العمل والوقاية للعمال المهنيين وأعوان الأمن والوقاية والمخابر"
            ),
            ChapterEntity(
                id = "EXP_CH08",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "العنوان الثاني: النفقات - الفرع الأول: نفقات تسيير المصالح",
                chapterNumber = 8,
                officialCode = "الباب 08",
                name = "التأمين",
                description = "تأمين التلاميذ في الخرجات البيداغوجية وتأمين ممتلكات وهياكل المؤسسة"
            ),
            ChapterEntity(
                id = "EXP_CH09",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "العنوان الثاني: النفقات - الفرع الأول: نفقات تسيير المصالح",
                chapterNumber = 9,
                officialCode = "الباب 09",
                name = "نفقات التكوين",
                description = "نفقات العمليات التكوينية والملتقيات التربوية والأيام الدراسية"
            ),
            ChapterEntity(
                id = "EXP_CH10",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "العنوان الثاني: النفقات - الفرع الأول: نفقات تسيير المصالح",
                chapterNumber = 10,
                officialCode = "الباب 10",
                name = "حظيرة السيارات",
                description = "الوقود والزيوت، صيانة وتصليح السيارات وقطع الغيار، العجلات المطاطية، التأمين، القسيمة والمراقبة التقنية"
            ),
            ChapterEntity(
                id = "EXP_CH11",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "العنوان الثاني: النفقات - الفرع الأول: نفقات تسيير المصالح",
                chapterNumber = 11,
                officialCode = "الباب 11",
                name = "نفقات تسيير وحدات الكشف والمتابعة (UDS)",
                description = "تغطية نفقات تسيير وصيانة وحدة الكشف والمتابعة الصحية المدرسية"
            ),
            ChapterEntity(
                id = "EXP_CH12",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "العنوان الثاني: النفقات - الفرع الأول: نفقات تسيير المصالح",
                chapterNumber = 12,
                officialCode = "الباب 12",
                name = "المكتبة المدرسية",
                description = "شراء كتب ومجلات تربوية وعلمية والاشتراك في المجلات والدوريات البيداغوجية والنشرة الرسمية"
            ),
            ChapterEntity(
                id = "EXP_CH13",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "العنوان الثاني: النفقات - الفرع الأول: نفقات تسيير المصالح",
                chapterNumber = 13,
                officialCode = "الباب 13",
                name = "النفقات المتعلقة بالنشاط الثقافي والرياضي",
                description = "النشاطات الثقافية والعلمية وجوائز التلاميذ المتفوقين، والمنافسات واللوازم الرياضية"
            ),
            ChapterEntity(
                id = "EXP_CH14",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "العنوان الثاني: النفقات - الفرع الأول: نفقات تسيير المصالح",
                chapterNumber = 14,
                officialCode = "الباب 14",
                name = "صيانة وتصليح المباني البيداغوجية والإدارية والهياكل الملحقة",
                description = "صيانة وتصليح المباني، اقتناء مواد البناء والترصيص والخردوات والطلاء والتدفئة، صيانة المساحات الخضراء ونقل الفضلات"
            ),
            ChapterEntity(
                id = "EXP_CH15",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "العنوان الثاني: النفقات - الفرع الأول: نفقات تسيير المصالح",
                chapterNumber = 15,
                officialCode = "الباب 15",
                name = "المصاريف القضائية والتعويضات",
                description = "المصاريف الناتجة عن الأحكام القضائية والتعويضات والرسوم التنفيذية"
            ),
            ChapterEntity(
                id = "EXP_CH16",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 2,
                branchTitle = "العنوان الثاني: النفقات - الفرع الثاني: أقسام الرياضة والدراسة",
                chapterNumber = 16,
                officialCode = "الباب 16",
                name = "نفقات تسيير أقسام الرياضة والدراسة",
                description = "نفقات الإطعام واللوازم البيداغوجية والرياضية الخاصة بأقسام الرياضة والدراسة"
            ),
            ChapterEntity(
                id = "EXP_CH17",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 3,
                branchTitle = "العنوان الثاني: النفقات - الفرع الثالث: نفقات ذات طابع خاص",
                chapterNumber = 17,
                officialCode = "الباب 17",
                name = "نفقات المنحة المدرسية الخاصة والكتاب المدرسي والامتحانات",
                description = "صرف المنحة المدرسية الخاصة، مستحقات الكتاب المدرسي، وتسيير مراكز الامتحانات الرسمية"
            )
        )

        /**
         * Builds the complete official list of Articles for any given `fiscalYear` with `spentAmount = 0.0`
         * so new fiscal years start completely clean while having all official chapters and articles ready.
         */
        fun buildCleanArticlesForFiscalYear(
            fiscalYear: Int,
            cateringSubsidy: Double
        ): List<ArticleEntity> {
            fun art(
                baseId: String,
                chapterId: String,
                section: SectionType,
                chNum: Int,
                artNum: Int,
                name: String,
                desc: String,
                allocated: Double,
                spent: Double = 0.0,
                isCatering: Boolean = false
            ): ArticleEntity {
                val chPrefix = chNum.toString().padStart(2, '0')
                val artPrefix = artNum.toString().padStart(2, '0')
                return ArticleEntity(
                    id = Circular13Constants.articleRecordId(fiscalYear, baseId),
                    baseArticleId = baseId,
                    fiscalYear = fiscalYear,
                    chapterId = chapterId,
                    sectionType = section.name,
                    chapterNumber = chNum,
                    articleNumber = artNum,
                    officialCode = "ب$chPrefix / م$artPrefix",
                    name = name,
                    description = desc,
                    allocatedAmount = allocated,
                    spentAmount = spent,
                    isAutoCatering = isCatering
                )
            }

            return listOf(
                // ================= العنوان الأول: الإيرادات =================
                // الباب 01: إعانات التسيير الممنوحة من طرف الدولة
                art(
                    baseId = Circular13Constants.REV_CATERING_BASE_ID,
                    chapterId = "REV_CH01",
                    section = SectionType.REVENUE,
                    chNum = 1,
                    artNum = 1,
                    name = "إعانة التسيير للتغذية والمنح الوطنية (المنشور 13/2026)",
                    desc = "تُحسب وتُضخ آلياً: (داخلي × 33,696.00 د.ج) + (نصف داخلي × 13,248.00 د.ج)",
                    allocated = cateringSubsidy,
                    spent = cateringSubsidy,
                    isCatering = true
                ),
                art(
                    baseId = "REV_01_02",
                    chapterId = "REV_CH01",
                    section = SectionType.REVENUE,
                    chNum = 1,
                    artNum = 2,
                    name = "إعانات التسيير للمصالح المشتركة",
                    desc = "حصة الدولة المخصصة لتغطية نفقات تسيير المصالح المشتركة للمؤسسة",
                    allocated = 3_850_000.0,
                    spent = 3_850_000.0
                ),
                art(
                    baseId = "REV_01_03",
                    chapterId = "REV_CH01",
                    section = SectionType.REVENUE,
                    chNum = 1,
                    artNum = 3,
                    name = "الإعانة المخصصة لتعويض الاستهلاك المنزلي للكهرباء والغاز (للجنوب)",
                    desc = "تعويض مصاريف الاستهلاك المنزلي للطاقة لفائدة موظفي ولايات الجنوب",
                    allocated = 0.0
                ),
                art(
                    baseId = "REV_01_04",
                    chapterId = "REV_CH01",
                    section = SectionType.REVENUE,
                    chNum = 1,
                    artNum = 4,
                    name = "إعانات التسيير الإضافية المختلفة",
                    desc = "اعتمادات التسيير الإضافية أو الاستثنائية الممنوحة من الوصاية",
                    allocated = 150_000.0,
                    spent = 150_000.0
                ),

                // الباب 02: الإعانات الممنوحة من الجماعات المحلية والمؤسسات والهيئات العمومية
                art(
                    baseId = "REV_02_01",
                    chapterId = "REV_CH02",
                    section = SectionType.REVENUE,
                    chNum = 2,
                    artNum = 1,
                    name = "إعانات الجماعات المحلية والهيئات العمومية",
                    desc = "المساهمات المالية الممنوحة من الولاية أو البلدية",
                    allocated = 100_000.0,
                    spent = 100_000.0
                ),

                // الباب 03: الهبات والوصايا
                art(
                    baseId = "REV_03_01",
                    chapterId = "REV_CH03",
                    section = SectionType.REVENUE,
                    chNum = 3,
                    artNum = 1,
                    name = "الهبات والوصايا",
                    desc = "الهبات والتبرعات المرخص بقبولها قانوناً",
                    allocated = 0.0
                ),

                // الباب 04: إيرادات مرتبطة بنشاط المؤسسة
                art(
                    baseId = "REV_04_01",
                    chapterId = "REV_CH04",
                    section = SectionType.REVENUE,
                    chNum = 4,
                    artNum = 1,
                    name = "عائدات الإيجار والأعباء الملحقة (السكنات الوظيفية: كهرباء، غاز، ماء)",
                    desc = "اقتطاعات الكراء ومستحقات استهلاك الماء والكهرباء والغاز للسكنات الوظيفية",
                    allocated = 165_000.0
                ),
                art(
                    baseId = "REV_04_02",
                    chapterId = "REV_CH04",
                    section = SectionType.REVENUE,
                    chNum = 4,
                    artNum = 2,
                    name = "مستحقات المبيت",
                    desc = "إيرادات الإيواء والمبيت المرخص به بالمؤسسة",
                    allocated = 0.0
                ),
                art(
                    baseId = "REV_04_03",
                    chapterId = "REV_CH04",
                    section = SectionType.REVENUE,
                    chNum = 4,
                    artNum = 3,
                    name = "بيع الأشياء المصنوعة وبقايا الخبز والخردوات",
                    desc = "عائدات بيع بقايا الخبز والفضلات ومنتجات الورشات والحدائق",
                    allocated = 25_000.0
                ),
                art(
                    baseId = "REV_04_04",
                    chapterId = "REV_CH04",
                    section = SectionType.REVENUE,
                    chNum = 4,
                    artNum = 4,
                    name = "إيرادات عمليات الإسقاط",
                    desc = "محصول بيع العتاد والأثاث المسقط من الجرد العام",
                    allocated = 0.0
                ),
                art(
                    baseId = "REV_04_05",
                    chapterId = "REV_CH04",
                    section = SectionType.REVENUE,
                    chNum = 4,
                    artNum = 5,
                    name = "مبيعات الكتاب المدرسي والوثائق شبه المدرسية (دفتر المراسلة)",
                    desc = "إيرادات بيع الكتب المدرسية ودفاتر المراسلة للتلاميذ",
                    allocated = 95_000.0
                ),

                // الباب 05: إيرادات مختلفة
                art(
                    baseId = "REV_05_01",
                    chapterId = "REV_CH05",
                    section = SectionType.REVENUE,
                    chNum = 5,
                    artNum = 1,
                    name = "تحصيل الدفع غير المستحق وتعويضات التأمين والإيرادات الطارئة",
                    desc = "استرجاع المبالغ المدفوعة بغير حق والتعويضات المختلفة",
                    allocated = 0.0
                ),

                // الباب 06: إيرادات ناتجة عن تسديد حقوق التسجيل
                art(
                    baseId = "REV_06_01",
                    chapterId = "REV_CH06",
                    section = SectionType.REVENUE,
                    chNum = 6,
                    artNum = 1,
                    name = "إيرادات ناتجة عن تسديد حقوق التسجيل والتراسل",
                    desc = "حقوق التسجيل المدرسي ومساهمات التراسل",
                    allocated = 45_000.0
                ),

                // الباب 07: إيرادات الإطعام
                art(
                    baseId = "REV_07_01",
                    chapterId = "REV_CH07",
                    section = SectionType.REVENUE,
                    chNum = 7,
                    artNum = 1,
                    name = "مساهمات عائلات التلاميذ غير الممنوحين في التغذية (داخلي / نصف داخلي)",
                    desc = "مستحقات النظام الداخلي ونصف الداخلي على عاتق الأولياء",
                    allocated = 360_000.0
                ),
                art(
                    baseId = "REV_07_02",
                    chapterId = "REV_CH07",
                    section = SectionType.REVENUE,
                    chNum = 7,
                    artNum = 2,
                    name = "الإيرادات الناتجة عن إطعام مستخدمي قطاع التربية (الطاولة المشتركة)",
                    desc = "مساهمات الموظفين والأعوان والملتقيات التكوينية في الطاولة المشتركة",
                    allocated = 120_000.0
                ),
                art(
                    baseId = "REV_07_03",
                    chapterId = "REV_CH07",
                    section = SectionType.REVENUE,
                    chNum = 7,
                    artNum = 3,
                    name = "إيرادات ناتجة عن إطعام المدعوين وآخرين",
                    desc = "مستحقات إطعام الضيوف والمدعوين المرخص لهم",
                    allocated = 0.0
                ),

                // الباب 08: إيرادات تسيير أقسام الرياضة والدراسة
                art(
                    baseId = "REV_08_01",
                    chapterId = "REV_CH08",
                    section = SectionType.REVENUE,
                    chNum = 8,
                    artNum = 1,
                    name = "إعانات تسيير أقسام الرياضة والدراسة",
                    desc = "مخصصات الدولة لتسيير أقسام الرياضة والدراسة (إن وجدت)",
                    allocated = 0.0
                ),

                // الباب 09: إعانات ذات طابع خاص
                art(
                    baseId = "REV_09_01",
                    chapterId = "REV_CH09",
                    section = SectionType.REVENUE,
                    chNum = 9,
                    artNum = 1,
                    name = "إعانات المنحة المدرسية الخاصة ووحدة الكشف والمتابعة والكتاب المدرسي",
                    desc = "الاعتمادات الخاصة بالمنحة المدرسية ووحدة الكشف UDS ومجانية الكتاب",
                    allocated = 250_000.0
                ),

                // ================= العنوان الثاني: النفقات (الأبواب 01 إلى 17) =================
                // الباب 01: تسديد المصاريف
                art(
                    baseId = "EXP_01_01",
                    chapterId = "EXP_CH01",
                    section = SectionType.EXPENDITURE,
                    chNum = 1,
                    artNum = 1,
                    name = "مصاريف المهام والتنقلات",
                    desc = "تعويضات المهام والتنقلات الرسمية للمسير المالي والموظفين والأعوان",
                    allocated = 90_000.0
                ),
                art(
                    baseId = "EXP_01_02",
                    chapterId = "EXP_CH01",
                    section = SectionType.EXPENDITURE,
                    chNum = 1,
                    artNum = 2,
                    name = "مصاريف الاستقبالات الرسمية",
                    desc = "نفقات الاستقبالات الرسمية والاجتماعات البيداغوجية والإدارية",
                    allocated = 35_000.0
                ),

                // الباب 02: التكاليف الملحقة (شرط ≥ 35% من نفقات التسيير العام - المنشور 13/2026)
                art(
                    baseId = "EXP_02_01",
                    chapterId = Circular13Constants.EXP_CHAPTER_2_BASE_ID,
                    section = SectionType.EXPENDITURE,
                    chNum = 2,
                    artNum = 1,
                    name = "الماء، الكهرباء، الغاز، الوقود والطاقة الشمسية",
                    desc = "تسديد فواتير سونلغاز (كهرباء وغاز التدفئة) وفواتير المياه (ADE / سيال)",
                    allocated = 1_120_000.0
                ),
                art(
                    baseId = "EXP_02_02",
                    chapterId = Circular13Constants.EXP_CHAPTER_2_BASE_ID,
                    section = SectionType.EXPENDITURE,
                    chNum = 2,
                    artNum = 2,
                    name = "مصاريف البريد والمواصلات (الهاتف، الفاكس، الأنترنت، الطوابع)",
                    desc = "أتاوى الهاتف الثابت واشتراكات الأنترنت (اتصالات الجزائر) والطوابع البريدية",
                    allocated = 145_000.0
                ),
                art(
                    baseId = "EXP_02_03",
                    chapterId = Circular13Constants.EXP_CHAPTER_2_BASE_ID,
                    section = SectionType.EXPENDITURE,
                    chNum = 2,
                    artNum = 3,
                    name = "مصاريف الإعلان في الصحف والنشر",
                    desc = "تكاليف نشر الإعلانات عن الاستشارات والصفقات في الجرائد الوطنية",
                    allocated = 35_000.0
                ),
                art(
                    baseId = "EXP_02_04",
                    chapterId = Circular13Constants.EXP_CHAPTER_2_BASE_ID,
                    section = SectionType.EXPENDITURE,
                    chNum = 2,
                    artNum = 4,
                    name = "تعويض الاستهلاك المنزلي للكهرباء والغاز (للجنوب)",
                    desc = "خاص بمؤسسات ولايات الجنوب",
                    allocated = 0.0
                ),
                art(
                    baseId = "EXP_02_05",
                    chapterId = Circular13Constants.EXP_CHAPTER_2_BASE_ID,
                    section = SectionType.EXPENDITURE,
                    chNum = 2,
                    artNum = 5,
                    name = "اقتناء وتركيب لواحق الهاتف والربط بشبكة الأنترنت",
                    desc = "لوازم الشبكة الهاتفية الداخلية وأجهزة التوجيه والربط بالأنترنت",
                    allocated = 30_000.0
                ),

                // الباب 03: اللوازم
                art(
                    baseId = "EXP_03_01",
                    chapterId = "EXP_CH03",
                    section = SectionType.EXPENDITURE,
                    chNum = 3,
                    artNum = 1,
                    name = "أدوات ولوازم المكتب والمطبوعات والسجلات الإدارية والمحاسبية",
                    desc = "سجلات المحاسبة العمومية، دفاتر الوصولات، الحوالات، والأدوات المكتبية",
                    allocated = 210_000.0
                ),
                art(
                    baseId = "EXP_03_02",
                    chapterId = "EXP_CH03",
                    section = SectionType.EXPENDITURE,
                    chNum = 3,
                    artNum = 2,
                    name = "البياضة",
                    desc = "اقتناء وتجديد الأفرشة والأغطية والمآزر ومستلزمات المرقد والمطعم",
                    allocated = 85_000.0
                ),
                art(
                    baseId = "EXP_03_03",
                    chapterId = "EXP_CH03",
                    section = SectionType.EXPENDITURE,
                    chNum = 3,
                    artNum = 3,
                    name = "مواد التنظيف والتطهير",
                    desc = "مواد التنظيف والتعقيم اليومي للحجرات والمراقد والمطعم والمرافق الصحية",
                    allocated = 240_000.0
                ),
                art(
                    baseId = "EXP_03_04",
                    chapterId = "EXP_CH03",
                    section = SectionType.EXPENDITURE,
                    chNum = 3,
                    artNum = 4,
                    name = "المواد الصيدلانية",
                    desc = "الأدوية البسيطة والضمادات ومستلزمات الإسعاف الأولي بالمؤسسة",
                    allocated = 40_000.0
                ),

                // الباب 04: الأدوات والأثاث
                art(
                    baseId = "EXP_04_01",
                    chapterId = "EXP_CH04",
                    section = SectionType.EXPENDITURE,
                    chNum = 4,
                    artNum = 1,
                    name = "تجديد وصيانة الأدوات والأثاث المكتبي والمدرسي",
                    desc = "اقتناء وتصليح الطاولات والكراسي والخزائن والأثاث الإداري",
                    allocated = 160_000.0
                ),
                art(
                    baseId = "EXP_04_02",
                    chapterId = "EXP_CH04",
                    section = SectionType.EXPENDITURE,
                    chNum = 4,
                    artNum = 2,
                    name = "تجديد وصيانة عتاد وأدوات المطعم والمطبخ والنادي",
                    desc = "اقتناء الأواني المهنية، صيانة غرف التبريد، الطاهيات والأفران",
                    allocated = 220_000.0
                ),
                art(
                    baseId = "EXP_04_03",
                    chapterId = "EXP_CH04",
                    section = SectionType.EXPENDITURE,
                    chNum = 4,
                    artNum = 3,
                    name = "اقتناء وصيانة أجهزة الإعلام الآلي والنسخ والتصوير ولواحقها",
                    desc = "صيانة الحواسيب، الطابعات، آلات السحب الرقمية، وقطع الغيار",
                    allocated = 190_000.0
                ),
                art(
                    baseId = "EXP_04_04",
                    chapterId = "EXP_CH04",
                    section = SectionType.EXPENDITURE,
                    chNum = 4,
                    artNum = 4,
                    name = "اقتناء وصيانة وسائل الوقاية والأمن (قارورات الإطفاء)",
                    desc = "مراقبة وتعبئة واقتناء مطفآت الحريق ومعدات الأمن الداخلي",
                    allocated = 65_000.0
                ),

                // الباب 05: اللوازم البيداغوجية
                art(
                    baseId = "EXP_05_01",
                    chapterId = "EXP_CH05",
                    section = SectionType.EXPENDITURE,
                    chNum = 5,
                    artNum = 1,
                    name = "الوسائل التعليمية ولوازم التجارب ومواد المخابر والورشات",
                    desc = "المحاليل الكيميائية، الزجاجيات، ومستلزمات الأعمال التطبيقية للتلاميذ",
                    allocated = 230_000.0
                ),
                art(
                    baseId = "EXP_05_02",
                    chapterId = "EXP_CH05",
                    section = SectionType.EXPENDITURE,
                    chNum = 5,
                    artNum = 2,
                    name = "لوازم الطباشير، الأقلام، السبورات ووسائل الإيضاح",
                    desc = "أقلام اللباد للسبورات البيضاء، الطباشير، الطلاسات، والخرائط التعليمية",
                    allocated = 95_000.0
                ),
                art(
                    baseId = "EXP_05_03",
                    chapterId = "EXP_CH05",
                    section = SectionType.EXPENDITURE,
                    chNum = 5,
                    artNum = 3,
                    name = "ورق ولوازم السحب والنسخ للفروض والاختبارات",
                    desc = "رزم الورق A4 وأحبار الناسخات لطبع مواضيع الفروض والاختبارات الفصلية",
                    allocated = 280_000.0
                ),
                art(
                    baseId = "EXP_05_04",
                    chapterId = "EXP_CH05",
                    section = SectionType.EXPENDITURE,
                    chNum = 5,
                    artNum = 4,
                    name = "الدفاتر المدرسية والوثائق شبه المدرسية (دفتر المراسلة)",
                    desc = "طبع واقتناء دفاتر المراسلة، دفاتر النصوص، وأوراق الإجابة للامتحانات",
                    allocated = 95_000.0
                ),

                // الباب 06: نفقات الإطعام (المنشور 13/2026)
                art(
                    baseId = Circular13Constants.EXP_CATERING_BASE_ID,
                    chapterId = Circular13Constants.EXP_CHAPTER_6_BASE_ID,
                    section = SectionType.EXPENDITURE,
                    chNum = 6,
                    artNum = 1,
                    name = "إطعام التلاميذ الداخليين ونصف الداخليين (المنشور 13/2026)",
                    desc = "مخصص التغذية المدرسية المحسوب آلياً: (داخلي × 33,696 د.ج) + (نصف داخلي × 13,248 د.ج)",
                    allocated = cateringSubsidy,
                    spent = 0.0,
                    isCatering = true
                ),
                art(
                    baseId = "EXP_06_02",
                    chapterId = Circular13Constants.EXP_CHAPTER_6_BASE_ID,
                    section = SectionType.EXPENDITURE,
                    chNum = 6,
                    artNum = 2,
                    name = "إطعام المستخدمين (الطاولة المشتركة) والملتقيات التكوينية",
                    desc = "نفقات تغذية الموظفين والأعوان المسجلين في الطاولة المشتركة",
                    allocated = 120_000.0
                ),
                art(
                    baseId = "EXP_06_03",
                    chapterId = Circular13Constants.EXP_CHAPTER_6_BASE_ID,
                    section = SectionType.EXPENDITURE,
                    chNum = 6,
                    artNum = 3,
                    name = "إطعام المدعوين وآخرين",
                    desc = "نفقات إطعام الضيوف وأعضاء لجان الامتحانات الرسمية المسخرين",
                    allocated = 0.0
                ),

                // الباب 07: الألبسة
                art(
                    baseId = "EXP_07_01",
                    chapterId = "EXP_CH07",
                    section = SectionType.EXPENDITURE,
                    chNum = 7,
                    artNum = 1,
                    name = "ألبسة العمل والوقاية للعمال المهنيين وأعوان الأمن والمخابر",
                    desc = "اقتناء البذلات المهنية والأحذية الواقية والقفازات للعمال المهنيين",
                    allocated = 85_000.0
                ),

                // الباب 08: التأمين
                art(
                    baseId = "EXP_08_01",
                    chapterId = "EXP_CH08",
                    section = SectionType.EXPENDITURE,
                    chNum = 8,
                    artNum = 1,
                    name = "تأمين التلاميذ في الخرجات البيداغوجية وتأمين ممتلكات المؤسسة",
                    desc = "عقود التأمين والمسؤولية المدنية وتأمين الهياكل والتجهيزات",
                    allocated = 65_000.0
                ),

                // الباب 09: نفقات التكوين
                art(
                    baseId = "EXP_09_01",
                    chapterId = "EXP_CH09",
                    section = SectionType.EXPENDITURE,
                    chNum = 9,
                    artNum = 1,
                    name = "نفقات التكوين والملتقيات التربوية",
                    desc = "نفقات تنظيم العمليات التكوينية والأيام الدراسية وتحسين المستوى",
                    allocated = 40_000.0
                ),

                // الباب 10: حظيرة السيارات
                art(
                    baseId = "EXP_10_01",
                    chapterId = "EXP_CH10",
                    section = SectionType.EXPENDITURE,
                    chNum = 10,
                    artNum = 1,
                    name = "اقتناء وتجديد السيارات",
                    desc = "اقتناء سيارة مصلحة جديدة بعد إسقاط السيارة القديمة بترخيص",
                    allocated = 0.0
                ),
                art(
                    baseId = "EXP_10_02",
                    chapterId = "EXP_CH10",
                    section = SectionType.EXPENDITURE,
                    chNum = 10,
                    artNum = 2,
                    name = "الوقود والزيوت",
                    desc = "وصولات البنزين والمازوت وزيوت المحرك لسيارة المؤسسة",
                    allocated = 95_000.0
                ),
                art(
                    baseId = "EXP_10_03",
                    chapterId = "EXP_CH10",
                    section = SectionType.EXPENDITURE,
                    chNum = 10,
                    artNum = 3,
                    name = "صيانة وتصليح السيارات وشراء قطع الغيار",
                    desc = "إصلاح الأعطاب الميكانيكية والكهربائية واقتناء قطع الغيار والبطارية",
                    allocated = 65_000.0
                ),
                art(
                    baseId = "EXP_10_04",
                    chapterId = "EXP_CH10",
                    section = SectionType.EXPENDITURE,
                    chNum = 10,
                    artNum = 4,
                    name = "اقتناء العجلات المطاطية",
                    desc = "تجديد الإطارات المطاطية لسيارة المؤسسة",
                    allocated = 35_000.0
                ),
                art(
                    baseId = "EXP_10_05",
                    chapterId = "EXP_CH10",
                    section = SectionType.EXPENDITURE,
                    chNum = 10,
                    artNum = 5,
                    name = "مصاريف الترقيم، التأمين، قسيمة السيارات والمراقبة التقنية",
                    desc = "عقد تأمين السيارة، قسيمة السيارات السنوية، ومحضر المراقبة التقنية",
                    allocated = 45_000.0
                ),

                // الباب 11: نفقات تسيير وحدات الكشف والمتابعة (UDS)
                art(
                    baseId = "EXP_11_01",
                    chapterId = "EXP_CH11",
                    section = SectionType.EXPENDITURE,
                    chNum = 11,
                    artNum = 1,
                    name = "نفقات تسيير وحدات الكشف والمتابعة (UDS)",
                    desc = "لوازم ومستلزمات تسيير وصيانة وحدة الكشف والمتابعة الصحية المدرسية",
                    allocated = 50_000.0
                ),

                // الباب 12: المكتبة المدرسية
                art(
                    baseId = "EXP_12_01",
                    chapterId = "EXP_CH12",
                    section = SectionType.EXPENDITURE,
                    chNum = 12,
                    artNum = 1,
                    name = "شراء كتب ومجلات تربوية",
                    desc = "اقتناء الكتب، المراجع العلمية، القواميس، والقصص لإثراء المكتبة المدرسية",
                    allocated = 90_000.0
                ),
                art(
                    baseId = "EXP_12_02",
                    chapterId = "EXP_CH12",
                    section = SectionType.EXPENDITURE,
                    chNum = 12,
                    artNum = 2,
                    name = "الاشتراك في المجلات والدوريات البيداغوجية",
                    desc = "الاشتراك السنوي في النشرة الرسمية لوزارة التربية والدوريات البيداغوجية",
                    allocated = 25_000.0
                ),

                // الباب 13: النفقات المتعلقة بالنشاط الثقافي والرياضي
                art(
                    baseId = "EXP_13_01",
                    chapterId = "EXP_CH13",
                    section = SectionType.EXPENDITURE,
                    chNum = 13,
                    artNum = 1,
                    name = "النفقات المتعلقة بالنشاط الثقافي والجوائز التشجيعية",
                    desc = "تمويل النوادي الثقافية والعلمية وجوائز تكريم التلاميذ المتفوقين",
                    allocated = 110_000.0
                ),
                art(
                    baseId = "EXP_13_02",
                    chapterId = "EXP_CH13",
                    section = SectionType.EXPENDITURE,
                    chNum = 13,
                    artNum = 2,
                    name = "النفقات المتعلقة بالنشاط الرياضي",
                    desc = "اقتناء الكرات والشباك والألبسة الرياضية لتنشيط الرابطة الرياضية المدرسية",
                    allocated = 75_000.0
                ),

                // الباب 14: صيانة وتصليح المباني البيداغوجية والإدارية والهياكل الملحقة
                art(
                    baseId = "EXP_14_01",
                    chapterId = "EXP_CH14",
                    section = SectionType.EXPENDITURE,
                    chNum = 14,
                    artNum = 1,
                    name = "صيانة وتصليح المباني البيداغوجية والإدارية والهياكل الملحقة",
                    desc = "أشغال الترميم والصيانة للحجرات، المخابر، الإدارة، المطعم، والمراقد",
                    allocated = 280_000.0
                ),
                art(
                    baseId = "EXP_14_02",
                    chapterId = "EXP_CH14",
                    section = SectionType.EXPENDITURE,
                    chNum = 14,
                    artNum = 2,
                    name = "اقتناء مواد البناء، الترصيص، الخردوات، التدفئة، التكييف وأدوات الصيانة الصغيرة",
                    desc = "شراء الطلاء، الأسمنت، الحنفيات، المصابيح، ولوازم الكهرباء والتدفئة للصيانة الذاتية",
                    allocated = 185_000.0
                ),
                art(
                    baseId = "EXP_14_03",
                    chapterId = "EXP_CH14",
                    section = SectionType.EXPENDITURE,
                    chNum = 14,
                    artNum = 3,
                    name = "صيانة المساحات الخضراء والمسالك",
                    desc = "أدوات البستنة، الغرس، وتنقية قنوات صرف مياه الأمطار",
                    allocated = 45_000.0
                ),
                art(
                    baseId = "EXP_14_04",
                    chapterId = "EXP_CH14",
                    section = SectionType.EXPENDITURE,
                    chNum = 14,
                    artNum = 4,
                    name = "مصاريف النقل (نقل الخردوات والفضلات)",
                    desc = "تكاليف شحن ونقل الردوم والنفايات الصلبة خارج المؤسسة",
                    allocated = 25_000.0
                ),

                // الباب 15: المصاريف القضائية والتعويضات
                art(
                    baseId = "EXP_15_01",
                    chapterId = "EXP_CH15",
                    section = SectionType.EXPENDITURE,
                    chNum = 15,
                    artNum = 1,
                    name = "المصاريف القضائية والتعويضات",
                    desc = "تسديد المصاريف القضائية وأتعاب المحضرين والتعويضات القانونية",
                    allocated = 0.0
                ),

                // الباب 16: الفرع الثاني - نفقات تسيير أقسام الرياضة والدراسة
                art(
                    baseId = "EXP_16_01",
                    chapterId = "EXP_CH16",
                    section = SectionType.EXPENDITURE,
                    chNum = 16,
                    artNum = 1,
                    name = "نفقات تسيير أقسام الرياضة والدراسة",
                    desc = "التغذية المكملة واللوازم البيداغوجية والرياضية لأقسام الرياضة والدراسة",
                    allocated = 0.0
                ),

                // الباب 17: الفرع الثالث - نفقات ذات طابع خاص
                art(
                    baseId = "EXP_17_01",
                    chapterId = "EXP_CH17",
                    section = SectionType.EXPENDITURE,
                    chNum = 17,
                    artNum = 1,
                    name = "نفقات المنحة المدرسية الخاصة والكتاب المدرسي والامتحانات الرسمية",
                    desc = "صرف المنحة الخاصة للتلاميذ المعوزين وتسديد فواتير الديوان الوطني للمطبوعات المدرسية",
                    allocated = 250_000.0
                )
            )
        }

        /**
         * Seeds the database with:
         * 1) Official Circular 29/2018 & Circular 13/2026 chapters & articles.
         * 2) Historical Fiscal Year 2025 (أرشيف السنة المالية 2025) with its own saved transactions.
         * 3) Active Fiscal Year 2026 (السنة المالية الجارية 2026) with its own independent transactions.
         */
        suspend fun seedDatabaseIfEmpty(dao: BudgetDao) {
            if (dao.getChaptersCount() > 0) return

            dao.insertChapters(buildOfficialChapters())

            val now = System.currentTimeMillis()
            val dayMs = 86_400_000L

            // -----------------------------------------------------------------
            // 1. Seed Historical Fiscal Year 2025 (محفوظة في الأرشيف مع نفقاتها)
            // -----------------------------------------------------------------
            val subsidy2025 = Circular13Constants.calculateCateringSubsidy(
                internalCount = 38,
                semiInternalCount = 290
            )
            val config2025 = InstitutionConfigEntity(
                fiscalYear = 2025,
                institutionName = "ثانوية الشهيد العربي بن مهيدي - النموذجية",
                wilayaName = "مديرية التربية لولاية الجزائر وسط",
                regime = "داخلي ونصف داخلي",
                internalScholarshipCount = 38,
                semiInternalScholarshipCount = 290,
                isActiveYear = false,
                updatedAt = now - (dayMs * 180)
            )
            dao.upsertInstitutionConfig(config2025)

            val articles2025 = buildCleanArticlesForFiscalYear(2025, subsidy2025).map { art ->
                when (art.baseArticleId) {
                    Circular13Constants.EXP_CATERING_BASE_ID -> art.copy(spentAmount = 4_820_000.0)
                    "EXP_02_01" -> art.copy(allocatedAmount = 1_450_000.0, spentAmount = 1_390_000.0)
                    "EXP_03_03" -> art.copy(spentAmount = 225_000.0)
                    "EXP_05_03" -> art.copy(spentAmount = 265_000.0)
                    else -> art
                }
            }
            dao.insertArticles(articles2025)

            val txs2025 = listOf(
                TransactionEntity(
                    fiscalYear = 2025,
                    articleId = Circular13Constants.articleRecordId(2025, Circular13Constants.EXP_CATERING_BASE_ID),
                    chapterId = Circular13Constants.EXP_CHAPTER_6_BASE_ID,
                    chapterName = "الباب 06: نفقات الإطعام",
                    articleCode = "ب06 / م01",
                    articleName = "إطعام التلاميذ الداخليين ونصف الداخليين (المنشور 13/2026)",
                    voucherNumber = "MD-2025/184",
                    supplierName = "الديوان الوطني للحبوب والتموين الغذائي",
                    description = "تسوية الحوصلة السنوية لتموين المطعم المدرسي لسنة 2025",
                    amount = 4_820_000.0,
                    timestamp = now - (dayMs * 200)
                ),
                TransactionEntity(
                    fiscalYear = 2025,
                    articleId = Circular13Constants.articleRecordId(2025, "EXP_02_01"),
                    chapterId = Circular13Constants.EXP_CHAPTER_2_BASE_ID,
                    chapterName = "الباب 02: التكاليف الملحقة",
                    articleCode = "ب02 / م01",
                    articleName = "الماء، الكهرباء، الغاز، الوقود والطاقة الشمسية",
                    voucherNumber = "MD-2025/162",
                    supplierName = "سونلغاز + سيال",
                    description = "تسديد فواتير استهلاك الكهرباء والغاز والماء لسنة 2025",
                    amount = 1_390_000.0,
                    timestamp = now - (dayMs * 215)
                ),
                TransactionEntity(
                    fiscalYear = 2025,
                    articleId = Circular13Constants.articleRecordId(2025, "EXP_05_03"),
                    chapterId = "EXP_CH05",
                    chapterName = "الباب 05: اللوازم البيداغوجية",
                    articleCode = "ب05 / م03",
                    articleName = "ورق ولوازم السحب والنسخ للفروض والاختبارات",
                    voucherNumber = "MD-2025/140",
                    supplierName = "المؤسسة الوطنية للوراقة",
                    description = "اقتناء الورق والأحبار لاختبارات الفصول الثلاثة 2025",
                    amount = 265_000.0,
                    timestamp = now - (dayMs * 240)
                ),
                TransactionEntity(
                    fiscalYear = 2025,
                    articleId = Circular13Constants.articleRecordId(2025, "EXP_03_03"),
                    chapterId = "EXP_CH03",
                    chapterName = "الباب 03: اللوازم",
                    articleCode = "ب03 / م03",
                    articleName = "مواد التنظيف والتطهير",
                    voucherNumber = "MD-2025/112",
                    supplierName = "مؤسسة التوزيع لمواد النظافة",
                    description = "اقتناء مواد التنظيف والتطهير السنوية لسنة 2025",
                    amount = 225_000.0,
                    timestamp = now - (dayMs * 260)
                )
            )
            txs2025.forEach { dao.insertTransaction(it) }

            // -----------------------------------------------------------------
            // 2. Seed Active Fiscal Year 2026 (السنة المالية الجارية 2026)
            // -----------------------------------------------------------------
            val initialInternal2026 = 42
            val initialSemiInternal2026 = 310
            val subsidy2026 = Circular13Constants.calculateCateringSubsidy(
                internalCount = initialInternal2026,
                semiInternalCount = initialSemiInternal2026
            )

            val config2026 = InstitutionConfigEntity(
                fiscalYear = 2026,
                institutionName = "ثانوية الشهيد العربي بن مهيدي - النموذجية",
                wilayaName = "مديرية التربية لولاية الجزائر وسط",
                regime = "داخلي ونصف داخلي",
                internalScholarshipCount = initialInternal2026,
                semiInternalScholarshipCount = initialSemiInternal2026,
                isActiveYear = true,
                updatedAt = now
            )
            dao.upsertInstitutionConfig(config2026)

            val articles2026 = buildCleanArticlesForFiscalYear(2026, subsidy2026).map { art ->
                when (art.baseArticleId) {
                    Circular13Constants.EXP_CATERING_BASE_ID -> art.copy(spentAmount = 2_450_000.0)
                    "EXP_02_01" -> art.copy(spentAmount = 540_000.0)
                    "EXP_02_02" -> art.copy(spentAmount = 82_000.0)
                    "EXP_03_01" -> art.copy(spentAmount = 125_000.0)
                    "EXP_03_03" -> art.copy(spentAmount = 148_000.0)
                    "EXP_05_03" -> art.copy(spentAmount = 195_000.0)
                    "EXP_14_01" -> art.copy(spentAmount = 175_000.0)
                    else -> art
                }
            }
            dao.insertArticles(articles2026)

            val txs2026 = listOf(
                TransactionEntity(
                    fiscalYear = 2026,
                    articleId = Circular13Constants.articleRecordId(2026, Circular13Constants.EXP_CATERING_BASE_ID),
                    chapterId = Circular13Constants.EXP_CHAPTER_6_BASE_ID,
                    chapterName = "الباب 06: نفقات الإطعام",
                    articleCode = "ب06 / م01",
                    articleName = "إطعام التلاميذ الداخليين ونصف الداخليين (المنشور 13/2026)",
                    voucherNumber = "MD-2026/048",
                    supplierName = "الديوان الوطني للحبوب والتموين الغذائي",
                    description = "تسديد فاتورة المواد الغذائية العامة واللحوم والخضر للمطعم المدرسي",
                    amount = 2_450_000.0,
                    timestamp = now - (dayMs * 2)
                ),
                TransactionEntity(
                    fiscalYear = 2026,
                    articleId = Circular13Constants.articleRecordId(2026, "EXP_02_01"),
                    chapterId = Circular13Constants.EXP_CHAPTER_2_BASE_ID,
                    chapterName = "الباب 02: التكاليف الملحقة",
                    articleCode = "ب02 / م01",
                    articleName = "الماء، الكهرباء، الغاز، الوقود والطاقة الشمسية",
                    voucherNumber = "MD-2026/039",
                    supplierName = "سونلغاز - مديرية التوزيع الجزائر الوسطى",
                    description = "تسديد فاتورة استهلاك الكهرباء والغاز الطبيعي والمياه",
                    amount = 540_000.0,
                    timestamp = now - (dayMs * 7)
                ),
                TransactionEntity(
                    fiscalYear = 2026,
                    articleId = Circular13Constants.articleRecordId(2026, "EXP_05_03"),
                    chapterId = "EXP_CH05",
                    chapterName = "الباب 05: اللوازم البيداغوجية",
                    articleCode = "ب05 / م03",
                    articleName = "ورق ولوازم السحب والنسخ للفروض والاختبارات",
                    voucherNumber = "MD-2026/031",
                    supplierName = "الشركة الوطنية للوراقة والطباعة المكتبية",
                    description = "اقتناء رزم الورق A4 وأحبار الناسخات الرقمية للفروض والاختبارات",
                    amount = 195_000.0,
                    timestamp = now - (dayMs * 12)
                ),
                TransactionEntity(
                    fiscalYear = 2026,
                    articleId = Circular13Constants.articleRecordId(2026, "EXP_14_01"),
                    chapterId = "EXP_CH14",
                    chapterName = "الباب 14: صيانة وتصليح المباني البيداغوجية والإدارية والهياكل الملحقة",
                    articleCode = "ب14 / م01",
                    articleName = "صيانة وتصليح المباني البيداغوجية والإدارية والهياكل الملحقة",
                    voucherNumber = "MD-2026/024",
                    supplierName = "مقاولة الأشغال والصيانة المدرسية",
                    description = "صيانة شبكة التدفئة المركزية وترميم مساكة الجناح البيداغوجي",
                    amount = 175_000.0,
                    timestamp = now - (dayMs * 16)
                ),
                TransactionEntity(
                    fiscalYear = 2026,
                    articleId = Circular13Constants.articleRecordId(2026, "EXP_03_03"),
                    chapterId = "EXP_CH03",
                    chapterName = "الباب 03: اللوازم",
                    articleCode = "ب03 / م03",
                    articleName = "مواد التنظيف والتطهير",
                    voucherNumber = "MD-2026/019",
                    supplierName = "مؤسسة التوريد لمواد النظافة والتعقيم",
                    description = "اقتناء مواد التنظيف والتطهير للحجرات والمطعم والمرافق الصحية",
                    amount = 148_000.0,
                    timestamp = now - (dayMs * 21)
                ),
                TransactionEntity(
                    fiscalYear = 2026,
                    articleId = Circular13Constants.articleRecordId(2026, "EXP_03_01"),
                    chapterId = "EXP_CH03",
                    chapterName = "الباب 03: اللوازم",
                    articleCode = "ب03 / م01",
                    articleName = "أدوات ولوازم المكتب والمطبوعات والسجلات الإدارية والمحاسبية",
                    voucherNumber = "MD-2026/014",
                    supplierName = "مطبعة السجلات الإدارية والمحاسبية",
                    description = "اقتناء السجلات المحاسبية ودفاتر الوصولات والحوالات ولوازم المكتب",
                    amount = 125_000.0,
                    timestamp = now - (dayMs * 25)
                ),
                TransactionEntity(
                    fiscalYear = 2026,
                    articleId = Circular13Constants.articleRecordId(2026, "EXP_02_02"),
                    chapterId = Circular13Constants.EXP_CHAPTER_2_BASE_ID,
                    chapterName = "الباب 02: التكاليف الملحقة",
                    articleCode = "ب02 / م02",
                    articleName = "مصاريف البريد والمواصلات (الهاتف، الفاكس، الأنترنت، الطوابع)",
                    voucherNumber = "MD-2026/008",
                    supplierName = "اتصالات الجزائر",
                    description = "تسديد اشتراكات الألياف البصرية والهاتف الثابت للمؤسسة",
                    amount = 82_000.0,
                    timestamp = now - (dayMs * 29)
                )
            )
            txs2026.forEach { dao.insertTransaction(it) }
        }
    }
}
