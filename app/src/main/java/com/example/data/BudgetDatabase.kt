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
    version = 4,
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
         * Official Educational Institutions Budget Nomenclature - 2024
         * (نموذج مدونة ميزانية المؤسسات التعليمية حسب الطبيعة الاقتصادية للنفقة - المتوسطة والثانوية)
         * Exclusively expenditures (النفقات فقط).
         */
        fun buildOfficialChapters(): List<ChapterEntity> = listOf(
            // =========================================================================
            // العنوان الثاني: النفقات - الفرع الأول: نفقات تسيير المصالح (الباب الثاني T2)
            // =========================================================================
            ChapterEntity(
                id = "EXP_CH01",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "الفرع الأول: نفقات تسيير المصالح (الباب الثاني T2)",
                chapterNumber = 1,
                officialCode = "الباب 01",
                name = "تسديد المصاريف",
                description = "مصاريف المهام والتنقلات والاستقبالات والخرجات البيداغوجية، ومصاريف الدراسة والخبرة"
            ),
            ChapterEntity(
                id = Circular13Constants.EXP_CHAPTER_2_BASE_ID,
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "الفرع الأول: نفقات تسيير المصالح (الباب الثاني T2)",
                chapterNumber = 2,
                officialCode = "الباب 02",
                name = "التكاليف الملحقة",
                description = "الماء، الكهرباء، الغاز، الوقود، الطاقة الشمسية، البريد والمواصلات، الإعلان، وتأمين المباني"
            ),
            ChapterEntity(
                id = "EXP_CH03",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "الفرع الأول: نفقات تسيير المصالح (الباب الثاني T2)",
                chapterNumber = 3,
                officialCode = "الباب 03",
                name = "اللوازم",
                description = "لوازم المكتب، مستهلكات الإعلام الآلي، النسخ والطبع، البياضة، المواد الصيدلانية، ومواد التنظيف"
            ),
            ChapterEntity(
                id = "EXP_CH04",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "الفرع الأول: نفقات تسيير المصالح (الباب الثاني T2)",
                chapterNumber = 4,
                officialCode = "الباب 04",
                name = "الأدوات والأثاث",
                description = "صيانة الأثاث المدرسي والمكتبي، أجهزة الإعلام الآلي، أدوات الوقاية، عتاد المطعم، والستائر ولوازم الداخلية"
            ),
            ChapterEntity(
                id = "EXP_CH05",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "الفرع الأول: نفقات تسيير المصالح (الباب الثاني T2)",
                chapterNumber = 5,
                officialCode = "الباب 05",
                name = "اللوازم البيداغوجية",
                description = "لوازم التجارب، مواد المخابر والكواشف، وأدوات الاستنساخ والطباشير والأقلام والطلاسات"
            ),
            ChapterEntity(
                id = Circular13Constants.EXP_CHAPTER_6_BASE_ID,
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "الفرع الأول: نفقات تسيير المصالح (الباب الثاني T2)",
                chapterNumber = 6,
                officialCode = "الباب 06",
                name = "نفقات الإطعام",
                description = "إطعام التلاميذ الداخليين ونصف الداخليين والمستخدمين والمدعوين"
            ),
            ChapterEntity(
                id = "EXP_CH07",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "الفرع الأول: نفقات تسيير المصالح (الباب الثاني T2)",
                chapterNumber = 7,
                officialCode = "الباب 07",
                name = "الألبسة",
                description = "ملابس العمل والوقاية (السائق، الحاجب، عون الاستقبال، أعوان الخدمات)"
            ),
            ChapterEntity(
                id = "EXP_CH08",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "الفرع الأول: نفقات تسيير المصالح (الباب الثاني T2)",
                chapterNumber = 8,
                officialCode = "الباب 08",
                name = "التأمين",
                description = "تأمين التلاميذ أثناء الخرجات البيداغوجية"
            ),
            ChapterEntity(
                id = "EXP_CH09",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "الفرع الأول: نفقات تسيير المصالح (الباب الثاني T2)",
                chapterNumber = 9,
                officialCode = "الباب 09",
                name = "نفقات التكوين",
                description = "التكوين والتحسين المستمر، التكوين ما قبل الترقية، نفقات الامتحانات والمسابقات، وتكوين معلمي الابتدائي"
            ),
            ChapterEntity(
                id = "EXP_CH10",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "الفرع الأول: نفقات تسيير المصالح (الباب الثاني T2)",
                chapterNumber = 10,
                officialCode = "الباب 10",
                name = "حظيرة السيارات",
                description = "اقتناء وتجديد السيارات، الوقود والزيوت، الصيانة والتصليح وقطع الغيار، العجلات، الترقيم، التأمين، القسيمة، والمراقبة التقنية"
            ),
            ChapterEntity(
                id = "EXP_CH11",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "الفرع الأول: نفقات تسيير المصالح (الباب الثاني T2)",
                chapterNumber = 11,
                officialCode = "الباب 11",
                name = "نفقات تسيير وحدات الكشف والمتابعة (UDS)",
                description = "نفقات ومستلزمات تسيير وحدة الكشف والمتابعة الصحية المدرسية"
            ),
            ChapterEntity(
                id = "EXP_CH12",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "الفرع الأول: نفقات تسيير المصالح (الباب الثاني T2)",
                chapterNumber = 12,
                officialCode = "الباب 12",
                name = "المكتبة المدرسية",
                description = "شراء كتب ومجلات تربوية، والاشتراك في المجلات والدوريات البيداغوجية"
            ),
            ChapterEntity(
                id = "EXP_CH13",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "الفرع الأول: نفقات تسيير المصالح (الباب الثاني T2)",
                chapterNumber = 13,
                officialCode = "الباب 13",
                name = "النفقات المتعلقة بالنشاط الثقافي والرياضي",
                description = "النفقات المتعلقة بالنشاط الثقافي والنفقات المتعلقة بالنشاط الرياضي"
            ),
            ChapterEntity(
                id = "EXP_CH14",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "الفرع الأول: نفقات تسيير المصالح (الباب الثاني T2)",
                chapterNumber = 14,
                officialCode = "الباب 14",
                name = "صيانة وتصليح المباني البيداغوجية والإدارية والهياكل الملحقة",
                description = "ترميم وصيانة المباني، مواد البناء والترصيص والخردوات والكهرباء والتدفئة، صيانة المساحات الخضراء، ونقل الخردوات والمهملات"
            ),
            ChapterEntity(
                id = "EXP_CH15",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 1,
                branchTitle = "الفرع الأول: نفقات تسيير المصالح (الباب الثاني T2)",
                chapterNumber = 15,
                officialCode = "الباب 15",
                name = "المصاريف القضائية والتعويضات المستحقة على عاتق الدولة",
                description = "تسديد المصاريف القضائية والتعويضات المستحقة قانوناً على عاتق الدولة"
            ),

            // =========================================================================
            // العنوان الثاني: النفقات - الفرع الثاني: نفقات تسيير أقسام الرياضة والدراسة (الباب الثاني T2)
            // =========================================================================
            ChapterEntity(
                id = "EXP_CH16",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 2,
                branchTitle = "الفرع الثاني: نفقات تسيير أقسام الرياضة والدراسة (الباب الثاني T2)",
                chapterNumber = 16,
                officialCode = "الباب 16",
                name = "نفقات تسيير أقسام الرياضة والدراسة",
                description = "مصاريف الإطعام، نقل وتأمين التلاميذ، واقتناء الأدوات واللوازم الرياضية والبيداغوجية"
            ),

            // =========================================================================
            // العنوان الثاني: النفقات - الفرع الثالث: نفقات ذات طابع خاص
            // =========================================================================
            ChapterEntity(
                id = "EXP_CH17",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 3,
                branchTitle = "الفرع الثالث: نفقات ذات طابع خاص (الباب الثاني T2)",
                chapterNumber = 17,
                officialCode = "الباب 17",
                name = "نفقات النشاط التربوي الاستثنائي",
                description = "الإطعام ومصاريف الإقامة والتمدرس للنشاط التربوي الاستثنائي"
            ),
            ChapterEntity(
                id = "EXP_CH18",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 3,
                branchTitle = "الفرع الثالث: نفقات ذات طابع خاص (الباب الرابع T4)",
                chapterNumber = 18,
                officialCode = "الباب 18",
                name = "المنحة المدرسية الخاصة لفائدة التلاميذ المعوزين",
                description = "صرف المنحة المدرسية الخاصة لفائدة التلاميذ المعوزين (5000 د.ج)"
            ),
            ChapterEntity(
                id = "EXP_CH19",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 3,
                branchTitle = "الفرع الثالث: نفقات ذات طابع خاص (الباب الرابع T4)",
                chapterNumber = 19,
                officialCode = "الباب 19",
                name = "المنح المدرسية للتجهيز",
                description = "منحة التجهيز المدرسية المخصصة لفائدة التلاميذ"
            ),
            ChapterEntity(
                id = "EXP_CH20",
                sectionType = SectionType.EXPENDITURE.name,
                branchNumber = 3,
                branchTitle = "الفرع الثالث: نفقات ذات طابع خاص (الباب الرابع T4)",
                chapterNumber = 20,
                officialCode = "الباب 20",
                name = "نفقات الكتاب المدرسي",
                description = "تسديد مستحقات الديوان الوطني للمطبوعات المدرسية من بيع الكتاب المدرسي"
            )
        )

        /**
         * Builds the complete official list of Expenditure Articles for any given `fiscalYear`
         * according to the 2024 educational budget nomenclature model.
         */
        fun buildCleanArticlesForFiscalYear(
            fiscalYear: Int,
            cateringSubsidy: Double
        ): List<ArticleEntity> {
            fun art(
                baseId: String,
                chapterId: String,
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
                    sectionType = SectionType.EXPENDITURE.name,
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
                // ================= الفرع الأول: نفقات تسيير المصالح =================
                // الباب 01: تسديد المصاريف
                art(
                    baseId = "EXP_01_01",
                    chapterId = "EXP_CH01",
                    chNum = 1,
                    artNum = 1,
                    name = "مصاريف المهام و التنقلات والاستقبالات داخل التراب الوطني والخرجات البيداغوجية وكذا خارج التراب الوطني بالنسبة للمشاركين في أولمبياد المواد التعليمية",
                    desc = "تعويضات المهام، التنقلات، الاستقبالات، الخرجات البيداغوجية، ومشاركات أولمبياد المواد التعليمية",
                    allocated = 120_000.0
                ),
                art(
                    baseId = "EXP_01_02",
                    chapterId = "EXP_CH01",
                    chNum = 1,
                    artNum = 2,
                    name = "مصاريف الدراسة، الخبرة، المعاونون والمحضرون الفعليون",
                    desc = "أتعاب الخبراء، الاستشارات الفنية، ومستحقات المعاونين والمحضرين الفعليين",
                    allocated = 45_000.0
                ),

                // الباب 02: التكاليف الملحقة (شرط ≥ 35% من نفقات تسيير المصالح)
                art(
                    baseId = "EXP_02_01",
                    chapterId = Circular13Constants.EXP_CHAPTER_2_BASE_ID,
                    chNum = 2,
                    artNum = 1,
                    name = "الماء، الكهرباء والغاز، الوقود، والطاقة الشمسية",
                    desc = "تسديد فواتير سونلغاز والمياه ووقود التدفئة والطاقة الشمسية",
                    allocated = 1_250_000.0
                ),
                art(
                    baseId = "EXP_02_02",
                    chapterId = Circular13Constants.EXP_CHAPTER_2_BASE_ID,
                    chNum = 2,
                    artNum = 2,
                    name = "مصاريف البريد والمواصلات (اتاوات الهاتف، التلكس، الانترنيت والرسوم المختلفة)",
                    desc = "اشتراكات الهاتف الثابت والأنترنت لاتصالات الجزائر والطوابع البريدية والرسوم",
                    allocated = 150_000.0
                ),
                art(
                    baseId = "EXP_02_03",
                    chapterId = Circular13Constants.EXP_CHAPTER_2_BASE_ID,
                    chNum = 2,
                    artNum = 3,
                    name = "مصاريف الإعلان في الصحف",
                    desc = "نشر الإعلانات عن الاستشارات والمناقصات في الجرائد اليومية",
                    allocated = 35_000.0
                ),
                art(
                    baseId = "EXP_02_04",
                    chapterId = Circular13Constants.EXP_CHAPTER_2_BASE_ID,
                    chNum = 2,
                    artNum = 4,
                    name = "تعويض مصاريف الاستهلاك المنزلي للكهرباء والغاز بنسبة 50% للجنوب الكبير",
                    desc = "خاص بموظفي المؤسسات التربوية بولايات الجنوب الكبير",
                    allocated = 0.0
                ),
                art(
                    baseId = "EXP_02_05",
                    chapterId = Circular13Constants.EXP_CHAPTER_2_BASE_ID,
                    chNum = 2,
                    artNum = 5,
                    name = "اقتناء وتركيب لواحق الهاتف والربط بالانترنيت",
                    desc = "أجهزة التوجيه (Modem/Router)، الأسلاك، والموزعات ولواحق الشبكة الداخلية",
                    allocated = 40_000.0
                ),
                art(
                    baseId = "EXP_02_06",
                    chapterId = Circular13Constants.EXP_CHAPTER_2_BASE_ID,
                    chNum = 2,
                    artNum = 6,
                    name = "تأمين المباني",
                    desc = "عقود التأمين الشاملة لهياكل ومباني المؤسسة التعليمية ضد الأخطار",
                    allocated = 50_000.0
                ),

                // الباب 03: اللوازم
                art(
                    baseId = "EXP_03_01",
                    chapterId = "EXP_CH03",
                    chNum = 3,
                    artNum = 1,
                    name = "اقتناء لوازم المكتب",
                    desc = "الأدوات المكتبية، الملفات، السجلات الإدارية والمحاسبية ودفاتر الوصولات",
                    allocated = 180_000.0
                ),
                art(
                    baseId = "EXP_03_02",
                    chapterId = "EXP_CH03",
                    chNum = 3,
                    artNum = 2,
                    name = "مستهلكات وبرامج الإعلام الآلي",
                    desc = "خراطيش الحبر، أحبار الطابعات، وسائط التخزين، ورخص البرمجيات",
                    allocated = 160_000.0
                ),
                art(
                    baseId = "EXP_03_03",
                    chapterId = "EXP_CH03",
                    chNum = 3,
                    artNum = 3,
                    name = "مصاريف النسخ والطبع",
                    desc = "طبع الوثائق الرسمية، الشهادات، الاستمارات، وسحب المطبوعات الإدارية",
                    allocated = 90_000.0
                ),
                art(
                    baseId = "EXP_03_04",
                    chapterId = "EXP_CH03",
                    chNum = 3,
                    artNum = 4,
                    name = "البياضة",
                    desc = "الأفرشة، الأغطية، الشراشف، المآزر، ومستلزمات المراقد والمطعم",
                    allocated = 80_000.0
                ),
                art(
                    baseId = "EXP_03_05",
                    chapterId = "EXP_CH03",
                    chNum = 3,
                    artNum = 5,
                    name = "المواد الصيدلانية ومواد أخرى",
                    desc = "الأدوية الأولية، مستلزمات الإسعاف السريع، والضمادات الطبية",
                    allocated = 45_000.0
                ),
                art(
                    baseId = "EXP_03_06",
                    chapterId = "EXP_CH03",
                    chNum = 3,
                    artNum = 6,
                    name = "مواد التنظيف",
                    desc = "مواد الغسيل والتعقيم والتطهير اليومي للحجرات والمطعم والمرافق الصحية",
                    allocated = 220_000.0
                ),

                // الباب 04: الأدوات والأثاث
                art(
                    baseId = "EXP_04_01",
                    chapterId = "EXP_CH04",
                    chNum = 4,
                    artNum = 1,
                    name = "تجديد وصيانة أدوات وأثاث المكاتب والعتاد المدرسي",
                    desc = "تصليح الطاولات والمقاعد وخزائن المكاتب والحجرات الدراسية",
                    allocated = 170_000.0
                ),
                art(
                    baseId = "EXP_04_02",
                    chapterId = "EXP_CH04",
                    chNum = 4,
                    artNum = 2,
                    name = "اقتناء وصيانة أجهزة الإعلام الآلي",
                    desc = "صيانة الحواسيب، الطابعات، شاشات العرض، وأجهزة النسخ الرقمي",
                    allocated = 180_000.0
                ),
                art(
                    baseId = "EXP_04_03",
                    chapterId = "EXP_CH04",
                    chNum = 4,
                    artNum = 3,
                    name = "اقتناء وصيانة أدوات الوقاية والأمن",
                    desc = "تعبئة ومراقبة مطفآت الحريق، أجهزة الإنذار، وأدوات الحماية والسلامة",
                    allocated = 70_000.0
                ),
                art(
                    baseId = "EXP_04_04",
                    chapterId = "EXP_CH04",
                    chNum = 4,
                    artNum = 4,
                    name = "تجديد وصيانة لوازم ومستلزمات المطعم",
                    desc = "أواني المطبخ، صيانة الأفران وغرف التبريد والطاهيات المهنية",
                    allocated = 190_000.0
                ),
                art(
                    baseId = "EXP_04_05",
                    chapterId = "EXP_CH04",
                    chNum = 4,
                    artNum = 5,
                    name = "اقتناء الستائر والأعلام ولوازم الداخلية",
                    desc = "ستائر الحجرات والإدارة، الأعلام الوطنية، ومستلزمات النظام الداخلي",
                    allocated = 60_000.0
                ),

                // الباب 05: اللوازم البيداغوجية
                art(
                    baseId = "EXP_05_01",
                    chapterId = "EXP_CH05",
                    chNum = 5,
                    artNum = 1,
                    name = "لوازم التجارب",
                    desc = "الأدوات والمعدات والمجسمات العلمية لإجراء التجارب البيداغوجية",
                    allocated = 160_000.0
                ),
                art(
                    baseId = "EXP_05_02",
                    chapterId = "EXP_CH05",
                    chNum = 5,
                    artNum = 2,
                    name = "مواد التجارب والكواشف ومواد المخابر",
                    desc = "المحاليل الكيميائية والكواشف المخبرية والزجاجيات للأعمال التطبيقية",
                    allocated = 180_000.0
                ),
                art(
                    baseId = "EXP_05_03",
                    chapterId = "EXP_CH05",
                    chNum = 5,
                    artNum = 3,
                    name = "الإستنساخ، الطباشير، الأقلام، الطلاسات وغيرها",
                    desc = "أوراق الفروض والاختبارات، أحبار السحب، أقلام اللباد للسبورات البيضاء والطباشير",
                    allocated = 320_000.0
                ),

                // الباب 06: نفقات الإطعام
                art(
                    baseId = Circular13Constants.EXP_CATERING_BASE_ID,
                    chapterId = Circular13Constants.EXP_CHAPTER_6_BASE_ID,
                    chNum = 6,
                    artNum = 1,
                    name = "نفقات الإطعام",
                    desc = "إطعام التلاميذ الداخليين ونصف الداخليين والمستخدمين في الطاولة المشتركة والمدعوين",
                    allocated = cateringSubsidy,
                    spent = 0.0,
                    isCatering = true
                ),

                // الباب 07: الألبسة
                art(
                    baseId = "EXP_07_01",
                    chapterId = "EXP_CH07",
                    chNum = 7,
                    artNum = 1,
                    name = "ملابس العمل (السائق، الحاجب، عون الإستقبال، أعوان الخدمات)",
                    desc = "البذلات المهنية، الأحذية الواقية، والقفازات للعمال المهنيين وأعوان الخدمات",
                    allocated = 85_000.0
                ),

                // الباب 08: التأمين
                art(
                    baseId = "EXP_08_01",
                    chapterId = "EXP_CH08",
                    chNum = 8,
                    artNum = 1,
                    name = "تأمين التلاميذ اثناء الخرجات البيداغوجية",
                    desc = "التأمين ضد الحوادث المدرسية والمسؤولية المدنية أثناء الرحلات والخرجات التعليمية",
                    allocated = 55_000.0
                ),

                // الباب 09: نفقات التكوين
                art(
                    baseId = "EXP_09_01",
                    chapterId = "EXP_CH09",
                    chNum = 9,
                    artNum = 1,
                    name = "نفقات التكوين والتحسين المستمر للمستوى المنظم على المستوى الولائي",
                    desc = "الأيام الدراسية والملتقيات التربوية التحسينية المنظمة ولائياً",
                    allocated = 40_000.0
                ),
                art(
                    baseId = "EXP_09_02",
                    chapterId = "EXP_CH09",
                    chNum = 9,
                    artNum = 2,
                    name = "نفقات التكوين ما قبل الترقية على المستوى الولائي",
                    desc = "دورات التكوين المتخصص للموظفين المقبلين على الترقية على مستوى الولاية",
                    allocated = 30_000.0
                ),
                art(
                    baseId = "EXP_09_03",
                    chapterId = "EXP_CH09",
                    chNum = 9,
                    artNum = 3,
                    name = "نفقات الامتحانات والمسابقات المهنية على المستوى الولائي",
                    desc = "تنظيم وتأطير الامتحانات والمسابقات المهنية الولائية",
                    allocated = 25_000.0
                ),
                art(
                    baseId = "EXP_09_04",
                    chapterId = "EXP_CH09",
                    chNum = 9,
                    artNum = 4,
                    name = "تشجيع التكوين أثناء الخدمة وعن بعد لمعلمي التعليم الابتدائي",
                    desc = "دعم عمليات التكوين المستمر والتعليم عن بعد لمعلمي وأساتذة الابتدائي",
                    allocated = 20_000.0
                ),
                art(
                    baseId = "EXP_09_05",
                    chapterId = "EXP_CH09",
                    chNum = 9,
                    artNum = 5,
                    name = "نفقات التكوين الخاصة بالتحسين المستمر للمستوى المنظم على المستوى الجهوي و/أو الوطني، التكوين ما قبل الترقية، نفقات الامتحانات",
                    desc = "تغطية نفقات المشاركة في التكوينات والامتحانات الوطنية والجهوية",
                    allocated = 30_000.0
                ),

                // الباب 10: حظيرة السيارات
                art(
                    baseId = "EXP_10_01",
                    chapterId = "EXP_CH10",
                    chNum = 10,
                    artNum = 1,
                    name = "اقتناء وتجديد السيارات",
                    desc = "اقتناء سيارة جديدة للمؤسسة بعد إسقاط السيارة القديمة بترخيص رسمي",
                    allocated = 0.0
                ),
                art(
                    baseId = "EXP_10_02",
                    chapterId = "EXP_CH10",
                    chNum = 10,
                    artNum = 2,
                    name = "الوقود والزيوت",
                    desc = "وصولات البنزين والمازوت وزيوت المحرك لسيارات المصلحة التابعة للمؤسسة",
                    allocated = 110_000.0
                ),
                art(
                    baseId = "EXP_10_03",
                    chapterId = "EXP_CH10",
                    chNum = 10,
                    artNum = 3,
                    name = "صيانة وتصليح وشحن السيارات وشراء قطع الغيار",
                    desc = "الإصلاحات الميكانيكية والكهربائية واقتناء قطع الغيار والبطاريات والشحن",
                    allocated = 70_000.0
                ),
                art(
                    baseId = "EXP_10_04",
                    chapterId = "EXP_CH10",
                    chNum = 10,
                    artNum = 4,
                    name = "اقتناء العجلات",
                    desc = "تجديد الإطارات المطاطية لسيارات المؤسسة",
                    allocated = 40_000.0
                ),
                art(
                    baseId = "EXP_10_05",
                    chapterId = "EXP_CH10",
                    chNum = 10,
                    artNum = 5,
                    name = "مصاريف الترقيم والتعريف",
                    desc = "لوحات الترقيم، بطاقات الترقيم، ورسوم التعريف الإداري",
                    allocated = 15_000.0
                ),
                art(
                    baseId = "EXP_10_06",
                    chapterId = "EXP_CH10",
                    chNum = 10,
                    artNum = 6,
                    name = "تأمين السيارات",
                    desc = "عقد التأمين السنوي للمسؤولية المدنية والتأمين الشامل لسيارة المصلحة",
                    allocated = 45_000.0
                ),
                art(
                    baseId = "EXP_10_07",
                    chapterId = "EXP_CH10",
                    chNum = 10,
                    artNum = 7,
                    name = "مصاريف اقتناء قسيمة السيارات",
                    desc = "قسيمة السيارات السنوية الصادرة عن إدارة الضرائب",
                    allocated = 18_000.0
                ),
                art(
                    baseId = "EXP_10_08",
                    chapterId = "EXP_CH10",
                    chNum = 10,
                    artNum = 8,
                    name = "مصاريف المراقبة التقنية للسيارات",
                    desc = "الفحص والمراقبة الدورية المعتمدة لسلامة سيارات المصلحة",
                    allocated = 12_000.0
                ),

                // الباب 11: نفقات تسيير وحدات الكشف والمتابعة (UDS)
                art(
                    baseId = "EXP_11_01",
                    chapterId = "EXP_CH11",
                    chNum = 11,
                    artNum = 1,
                    name = "نفقات تسيير وحدات الكشف والمتابعة (UDS)",
                    desc = "أدوات ومستلزمات تسيير وفحص التلاميذ في وحدة الكشف والمتابعة الصحية",
                    allocated = 60_000.0
                ),

                // الباب 12: المكتبة المدرسية
                art(
                    baseId = "EXP_12_01",
                    chapterId = "EXP_CH12",
                    chNum = 12,
                    artNum = 1,
                    name = "شراء كتب ومجلات تربوية",
                    desc = "اقتناء الكتب، المراجع العلمية، القواميس، والمؤلفات للمطالعة المدرسية",
                    allocated = 95_000.0
                ),
                art(
                    baseId = "EXP_12_02",
                    chapterId = "EXP_CH12",
                    chNum = 12,
                    artNum = 2,
                    name = "الإشتراك في المجلات والدوريات البيداغوجية",
                    desc = "الاشتراك السنوي في النشرة الرسمية للتربية الوطنية والدوريات المتخصصة",
                    allocated = 30_000.0
                ),

                // الباب 13: النفقات المتعلقة بالنشاط الثقافي والرياضي
                art(
                    baseId = "EXP_13_01",
                    chapterId = "EXP_CH13",
                    chNum = 13,
                    artNum = 1,
                    name = "النفقات المتعلقة بالنشاط الثقافي",
                    desc = "النوادي الثقافية والعلمية، المسابقات الفكرية، وجوائز التلاميذ المتفوقين",
                    allocated = 115_000.0
                ),
                art(
                    baseId = "EXP_13_02",
                    chapterId = "EXP_CH13",
                    chNum = 13,
                    artNum = 2,
                    name = "النفقات المتعلقة بالنشاط الرياضي",
                    desc = "الألبسة الرياضية، الكرات، الشباك، ولوازم المنافسات الرياضية المدرسية",
                    allocated = 80_000.0
                ),

                // الباب 14: صيانة وتصليح المباني البيداغوجية والإدارية والهياكل الملحقة
                art(
                    baseId = "EXP_14_01",
                    chapterId = "EXP_CH14",
                    chNum = 14,
                    artNum = 1,
                    name = "صيانة وتصليح المباني البيداغوجية والإدارية والهياكل الملحقة",
                    desc = "أشغال الترميم والتهيئة للحجرات الدراسية والمخابر والإدارة والمطعم والمراقد",
                    allocated = 300_000.0
                ),
                art(
                    baseId = "EXP_14_02",
                    chapterId = "EXP_CH14",
                    chNum = 14,
                    artNum = 2,
                    name = "اقتناء مواد: البناء، الترصيص، الخردوات، كهرباء، التدفئة، التكييف وأدوات الصيانة الصغيرة",
                    desc = "شراء الطلاء، الأسمنت، الحنفيات، مصابيح الإنارة، مفاتيح الكهرباء ولوازم التدفئة للصيانة الذاتية",
                    allocated = 210_000.0
                ),
                art(
                    baseId = "EXP_14_03",
                    chapterId = "EXP_CH14",
                    chNum = 14,
                    artNum = 3,
                    name = "صيانة المساحات الخضراء والمسالك",
                    desc = "أدوات ومستلزمات البستنة، تشذيب الأشجار وتنقية المسالك وقنوات تصريف مياه الأمطار",
                    allocated = 45_000.0
                ),
                art(
                    baseId = "EXP_14_04",
                    chapterId = "EXP_CH14",
                    chNum = 14,
                    artNum = 4,
                    name = "مصاريف نقل (الخردوات والمهملات...)",
                    desc = "أجور وشحن ونقل النفايات الصلبة والردوم خارج المؤسسة",
                    allocated = 25_000.0
                ),

                // الباب 15: المصاريف القضائية والتعويضات المستحقة على عاتق الدولة
                art(
                    baseId = "EXP_15_01",
                    chapterId = "EXP_CH15",
                    chNum = 15,
                    artNum = 1,
                    name = "المصاريف القضائية والتعويضات المستحقة على عاتق الدولة",
                    desc = "المصاريف القضائية وأتعاب المحضرين القضائيين وأحكام التعويضات القانونية",
                    allocated = 0.0
                ),

                // ================= الفرع الثاني: نفقات تسيير أقسام الرياضة والدراسة =================
                // الباب 16: نفقات تسيير أقسام الرياضة والدراسة
                art(
                    baseId = "EXP_16_01",
                    chapterId = "EXP_CH16",
                    chNum = 16,
                    artNum = 1,
                    name = "مصاريف الإطعام",
                    desc = "الوجبات الغذائية المكملة المخصصة لتلاميذ أقسام الرياضة والدراسة",
                    allocated = 0.0
                ),
                art(
                    baseId = "EXP_16_02",
                    chapterId = "EXP_CH16",
                    chNum = 16,
                    artNum = 2,
                    name = "نقل وتأمين التلاميذ",
                    desc = "نقل وتأمين رياضيي النخبة المتمدرسين في التنقلات والمنافسات الرسمية",
                    allocated = 0.0
                ),
                art(
                    baseId = "EXP_16_03",
                    chapterId = "EXP_CH16",
                    chNum = 16,
                    artNum = 3,
                    name = "اقتناء الأدوات واللوازم الرياضية والبيداغوجية",
                    desc = "التجهيزات والألبسة والأدوات الرياضية الخاصة برياضيي أقسام الرياضة ودراسة",
                    allocated = 0.0
                ),

                // ================= الفرع الثالث: نفقات ذات طابع خاص =================
                // الباب 17: نفقات النشاط التربوي الاستثنائي
                art(
                    baseId = "EXP_17_01",
                    chapterId = "EXP_CH17",
                    chNum = 17,
                    artNum = 1,
                    name = "الإطعام",
                    desc = "نفقات الإطعام في النشاطات والملتقيات والتظاهرات التربوية الاستثنائية",
                    allocated = 0.0
                ),
                art(
                    baseId = "EXP_17_02",
                    chapterId = "EXP_CH17",
                    chNum = 17,
                    artNum = 2,
                    name = "مصاريف الإقامة والتمدرس",
                    desc = "مصاريف الإيواء والإقامة والتأطير الاستثنائي للتلاميذ",
                    allocated = 0.0
                ),

                // الباب 18: المنحة المدرسية الخاصة لفائدة التلاميذ المعوزين
                art(
                    baseId = "EXP_18_01",
                    chapterId = "EXP_CH18",
                    chNum = 18,
                    artNum = 1,
                    name = "المنحة المدرسية الخاصة لفائدة التلاميذ المعوزين",
                    desc = "صرف المنحة المدرسية الخاصة (5,000 د.ج) للتلاميذ المسجلين في قوائم التضامن",
                    allocated = 250_000.0
                ),

                // الباب 19: المنح المدرسية للتجهيز
                art(
                    baseId = "EXP_19_01",
                    chapterId = "EXP_CH19",
                    chNum = 19,
                    artNum = 1,
                    name = "منحة التجهيز لفائدة التلاميذ",
                    desc = "منحة التجهيز المخصصة للتلاميذ في التعليم التقني والتجهيز الأولي",
                    allocated = 0.0
                ),

                // الباب 20: نفقات الكتاب المدرسي
                art(
                    baseId = "EXP_20_01",
                    chapterId = "EXP_CH20",
                    chNum = 20,
                    artNum = 1,
                    name = "تسديد مستحقات الديوان الوطني للمطبوعات المدرسية من بيع الكتاب المدرسي",
                    desc = "تسديد مستحقات بيع الكتب والمطبوعات المدرسية للديوان الوطني (CRDDP)",
                    allocated = 350_000.0
                )
            )
        }

        /**
         * Seeds the database with:
         * 1) Official 2024 educational expenditures nomenclature chapters & articles.
         * 2) Historical Fiscal Year 2025 archived records.
         * 3) Active Fiscal Year 2026 clean initial allocations.
         */
        suspend fun seedDatabaseIfEmpty(dao: BudgetDao) {
            if (dao.getChaptersCount() > 0) return

            dao.insertChapters(buildOfficialChapters())

            val now = System.currentTimeMillis()
            val dayMs = 86_400_000L

            // -----------------------------------------------------------------
            // 1. Seed Historical Fiscal Year 2025 (أرشيف 2025)
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
                    "EXP_03_06" -> art.copy(spentAmount = 210_000.0)
                    "EXP_05_03" -> art.copy(spentAmount = 295_000.0)
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
                    articleName = "نفقات الإطعام",
                    orderNumber = "BC-2025/184",
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
                    articleName = "الماء، الكهرباء والغاز، الوقود، والطاقة الشمسية",
                    orderNumber = "BC-2025/162",
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
                    articleName = "الإستنساخ، الطباشير، الأقلام، الطلاسات وغيرها",
                    orderNumber = "BC-2025/138",
                    supplierName = "مؤسسة الورق والطباعة الحديثة",
                    description = "اقتناء رزم ورق السحب وأقلام السبورات لفروض وامتحانات 2025",
                    amount = 295_000.0,
                    timestamp = now - (dayMs * 240)
                )
            )
            txs2025.forEach { dao.insertTransaction(it) }

            // -----------------------------------------------------------------
            // 2. Seed Active Fiscal Year 2026 (السنة المالية الجارية)
            // -----------------------------------------------------------------
            val subsidy2026 = Circular13Constants.calculateCateringSubsidy(
                internalCount = 42,
                semiInternalCount = 310
            )
            val config2026 = InstitutionConfigEntity(
                fiscalYear = 2026,
                institutionName = "ثانوية الشهيد العربي بن مهيدي - النموذجية",
                wilayaName = "مديرية التربية لولاية الجزائر وسط",
                regime = "داخلي ونصف داخلي",
                internalScholarshipCount = 42,
                semiInternalScholarshipCount = 310,
                isActiveYear = true,
                updatedAt = now
            )
            dao.upsertInstitutionConfig(config2026)

            val articles2026 = buildCleanArticlesForFiscalYear(2026, subsidy2026).map { art ->
                when (art.baseArticleId) {
                    Circular13Constants.EXP_CATERING_BASE_ID -> art.copy(spentAmount = 1_280_000.0)
                    "EXP_02_01" -> art.copy(spentAmount = 345_000.0)
                    "EXP_03_06" -> art.copy(spentAmount = 68_000.0)
                    "EXP_05_03" -> art.copy(spentAmount = 112_000.0)
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
                    articleName = "نفقات الإطعام",
                    orderNumber = "BC-2026/041",
                    supplierName = "مجمع أغروديف - تموين الحبوب ومشتقاتها",
                    description = "تموين المطعم المدرسي بالمواد الغذائية الأساسية (الثلاثي الأول)",
                    amount = 1_280_000.0,
                    timestamp = now - (dayMs * 18)
                ),
                TransactionEntity(
                    fiscalYear = 2026,
                    articleId = Circular13Constants.articleRecordId(2026, "EXP_02_01"),
                    chapterId = Circular13Constants.EXP_CHAPTER_2_BASE_ID,
                    chapterName = "الباب 02: التكاليف الملحقة",
                    articleCode = "ب02 / م01",
                    articleName = "الماء، الكهرباء والغاز، الوقود، والطاقة الشمسية",
                    orderNumber = "BC-2026/028",
                    supplierName = "سونلغاز (مديرية التوزيع الجزائر)",
                    description = "فاتورة استهلاك الغاز الطبيعي للتدفئة المركزية والكهرباء",
                    amount = 345_000.0,
                    timestamp = now - (dayMs * 25)
                ),
                TransactionEntity(
                    fiscalYear = 2026,
                    articleId = Circular13Constants.articleRecordId(2026, "EXP_05_03"),
                    chapterId = "EXP_CH05",
                    chapterName = "الباب 05: اللوازم البيداغوجية",
                    articleCode = "ب05 / م03",
                    articleName = "الإستنساخ، الطباشير، الأقلام، الطلاسات وغيرها",
                    orderNumber = "BC-2026/015",
                    supplierName = "مكتبة ومطبعة النجاح التربوية",
                    description = "شراء رزم أوراق السحب A4 وأحبار آلات النسخ لاختبارات الفصل الأول",
                    amount = 112_000.0,
                    timestamp = now - (dayMs * 35)
                ),
                TransactionEntity(
                    fiscalYear = 2026,
                    articleId = Circular13Constants.articleRecordId(2026, "EXP_03_06"),
                    chapterId = "EXP_CH03",
                    chapterName = "الباب 03: اللوازم",
                    articleCode = "ب03 / م06",
                    articleName = "مواد التنظيف",
                    orderNumber = "BC-2026/009",
                    supplierName = "مؤسسة النظافة والتطهير للوسط",
                    description = "اقتناء مواد التنظيف والمطهرات والمكانس للحجرات والمرافق",
                    amount = 68_000.0,
                    timestamp = now - (dayMs * 45)
                )
            )
            txs2026.forEach { dao.insertTransaction(it) }
        }
    }
}
