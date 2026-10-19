package com.example.data.model

data class Exercise(
    val id: String,
    val nameTr: String,
    val nameEnAlias: String = "",
    val equipmentTr: String, // Barbell, Dumbbell, Cable, Makine, Vücut Ağırlığı
    val difficultyTr: String, // Başlangıç, Orta, İleri
    val primaryMuscle: MuscleGroup,
    val subregionTr: String, // e.g. "Üst Göğüs", "Orta Göğüs", "Alt Göğüs"
    val secondaryMuscles: List<MuscleGroup> = emptyList(),
    val instructionsTr: List<String>,
    val breathingTipTr: String,
    val commonMistakesTr: List<String>,
    val defaultSets: Int = 4,
    val defaultReps: String = "8-12",
    val defaultRestSeconds: Int = 90
)

object ExerciseDatabase {
    val sampleExercises = listOf(
        // --- ÜST GÖĞÜS (Upper Chest) ---
        Exercise(
            id = "incline_dumbbell_press",
            nameTr = "Incline Dumbbell Press",
            nameEnAlias = "Incline Dumbbell Bench Press",
            equipmentTr = "Dumbbell",
            difficultyTr = "Orta",
            primaryMuscle = MuscleGroup.CHEST,
            subregionTr = "Üst Göğüs",
            instructionsTr = listOf(
                "Sehpayı yaklaşık 30-45 derece eğime ayarlayın.",
                "Dumbbell'ları göğüs hizasında tutarak sehpaya yaslanın.",
                "Dumbbell'ları tepe noktada birbirine çarpmadan yukarı itin.",
                "Kontrollü bir hızda üst göğüs kaslarının gerildiğini hissederek indirin."
            ),
            breathingTipTr = "Aşağı inişte derin nefes al, yukarı itişin sonunda nefes ver.",
            commonMistakesTr = listOf("Sehpayı çok dik ayarlayarak ağırlığı ön omuza bindirmek"),
            defaultSets = 4,
            defaultReps = "8-12",
            defaultRestSeconds = 75
        ),
        Exercise(
            id = "incline_barbell_press",
            nameTr = "Incline Barbell Press",
            nameEnAlias = "Incline Barbell Bench Press",
            equipmentTr = "Barbell",
            difficultyTr = "Orta",
            primaryMuscle = MuscleGroup.CHEST,
            subregionTr = "Üst Göğüs",
            instructionsTr = listOf(
                "30-45 derecelik açılı sehpaya uzanın ve ayaklarınızı yere sabitleyin.",
                "Barı omuz hizasından biraz geniş tutarak klavikula kemiği hizasına yavaşça indirin.",
                "Üst göğüs liflerini sıkıştırarak barı düz yukarı itin."
            ),
            breathingTipTr = "İndirirken nefes al, yukarı iterken patlayıcı şekilde ver.",
            commonMistakesTr = listOf("Barı göğse çarptırmak", "Dirsekleri gövdeden 90 derece açmak"),
            defaultSets = 4,
            defaultReps = "8-10",
            defaultRestSeconds = 90
        ),
        Exercise(
            id = "incline_cable_fly",
            nameTr = "Low-to-High Cable Fly",
            nameEnAlias = "Incline Cable Fly",
            equipmentTr = "Cable",
            difficultyTr = "Başlangıç",
            primaryMuscle = MuscleGroup.CHEST,
            subregionTr = "Üst Göğüs",
            instructionsTr = listOf(
                "Makaraları en alt seviyeye ayarlayın.",
                "Hafifçe öne adım atıp kollarınızı dirseklerden hafif bükün.",
                "Ellerinizi göz hizasında üst göğsünüzü sıkarak birleştirin.",
                "Yavaş ve kontrollü şekilde gerginliği bırakmadan geri açın."
            ),
            breathingTipTr = "Kolları yukarı birleştirirken nefes ver, açarken nefes al.",
            commonMistakesTr = listOf("Momentum kullanıp sırtı sallamak"),
            defaultSets = 3,
            defaultReps = "12-15",
            defaultRestSeconds = 60
        ),

        // --- ORTA GÖĞÜS (Middle Chest) ---
        Exercise(
            id = "bench_press",
            nameTr = "Bench Press",
            nameEnAlias = "Barbell Flat Bench Press",
            equipmentTr = "Barbell",
            difficultyTr = "Orta",
            primaryMuscle = MuscleGroup.CHEST,
            subregionTr = "Orta Göğüs",
            instructionsTr = listOf(
                "Sırtını bench'e tam olarak yasla ve kürek kemiklerini birbirine yaklaştır.",
                "Barı omuz genişliğinden biraz daha geniş bir açıklıkla kavra.",
                "Barı kontrollü şekilde göğsünün orta çizgisine doğru indir.",
                "Nefes vererek barı başlangıç pozisyonuna patlayıcı bir güçle it."
            ),
            breathingTipTr = "Barı göğse indirirken derin nefes al, yukarı iterken nefes ver.",
            commonMistakesTr = listOf(
                "Bileklerin geriye bükülmesi",
                "Kalçanın bench'ten havaya kalkması"
            ),
            defaultSets = 4,
            defaultReps = "8-10",
            defaultRestSeconds = 90
        ),
        Exercise(
            id = "flat_dumbbell_fly",
            nameTr = "Dumbbell Fly",
            nameEnAlias = "Flat Dumbbell Fly",
            equipmentTr = "Dumbbell",
            difficultyTr = "Başlangıç",
            primaryMuscle = MuscleGroup.CHEST,
            subregionTr = "Orta Göğüs",
            instructionsTr = listOf(
                "Düz sehpaya uzanın, dumbbell'ları göğsünüzün üzerinde tutun.",
                "Dirsekleri hafif bükülü tutarak kollarınızı yana doğru genişçe açın.",
                "Göğsün tam gerildiğini hissedip tepe noktada kasları sıkın."
            ),
            breathingTipTr = "Açılırken nefes al, kapatırken nefes ver.",
            commonMistakesTr = listOf("Dirsekleri kitlemek veya aşırı büküp presse dönüştürmek"),
            defaultSets = 3,
            defaultReps = "10-12",
            defaultRestSeconds = 60
        ),
        Exercise(
            id = "cable_fly",
            nameTr = "Cable Fly (Orta)",
            nameEnAlias = "Standing Cable Chest Fly",
            equipmentTr = "Cable",
            difficultyTr = "Başlangıç",
            primaryMuscle = MuscleGroup.CHEST,
            subregionTr = "Orta Göğüs",
            instructionsTr = listOf(
                "Kabloları göğüs hizasında konumlandırın.",
                "Ellerinizi göğüs önünde birleştirip 1 saniye tepe sıkışmayı koruyun.",
                "Yavaş ve kontrollü açın."
            ),
            breathingTipTr = "Kapatırken nefes ver, açarken al.",
            commonMistakesTr = listOf("Gövdeyi sallamak"),
            defaultSets = 3,
            defaultReps = "12-15",
            defaultRestSeconds = 60
        ),

        // --- ALT GÖĞÜS (Lower Chest) ---
        Exercise(
            id = "dips_chest",
            nameTr = "Chest Dips",
            nameEnAlias = "Parallel Bar Chest Dips",
            equipmentTr = "Vücut Ağırlığı",
            difficultyTr = "İleri",
            primaryMuscle = MuscleGroup.CHEST,
            subregionTr = "Alt Göğüs",
            instructionsTr = listOf(
                "Paralel barlara tutunun, gövdenizi 30 derece öne eğin.",
                "Dirsekleri yana açarak alt göğüs gerilene kadar alçalın.",
                "Göğsü sıkarak gövdenizi yukarı itin."
            ),
            breathingTipTr = "Alçalırken nefes al, kalkarken ver.",
            commonMistakesTr = listOf("Dik durarak ağırlığı sadece tricepse bindirmek"),
            defaultSets = 4,
            defaultReps = "8-12",
            defaultRestSeconds = 90
        ),
        Exercise(
            id = "decline_dumbbell_press",
            nameTr = "Decline Dumbbell Press",
            nameEnAlias = "Decline Dumbbell Bench Press",
            equipmentTr = "Dumbbell",
            difficultyTr = "Orta",
            primaryMuscle = MuscleGroup.CHEST,
            subregionTr = "Alt Göğüs",
            instructionsTr = listOf(
                "Ters eğimli sehpaya bacaklarınızı kilitleyerek yatın.",
                "Dumbbell'ları alt göğüs çizgisine kontrollü indirip itin."
            ),
            breathingTipTr = "İnerken nefes al, iterken nefes ver.",
            commonMistakesTr = listOf("Dumbbell'ları çok hızlı bırakmak"),
            defaultSets = 3,
            defaultReps = "10-12",
            defaultRestSeconds = 75
        ),
        Exercise(
            id = "high_to_low_cable_fly",
            nameTr = "High-to-Low Cable Fly",
            nameEnAlias = "Decline Cable Crossover",
            equipmentTr = "Cable",
            difficultyTr = "Başlangıç",
            primaryMuscle = MuscleGroup.CHEST,
            subregionTr = "Alt Göğüs",
            instructionsTr = listOf(
                "Makaraları en üst seviyeye alın.",
                "Kolları aşağı ve öne doğru çaprazlayarak alt göğsü sıkın."
            ),
            breathingTipTr = "İterken nefes ver, açarken al.",
            commonMistakesTr = listOf("Aşırı ağırlık takıp omuzdan çekmek"),
            defaultSets = 3,
            defaultReps = "12-15",
            defaultRestSeconds = 60
        ),

        // --- OMUZ (Shoulders) ---
        Exercise(
            id = "overhead_shoulder_press",
            nameTr = "Overhead Press",
            nameEnAlias = "Military Press",
            equipmentTr = "Barbell",
            difficultyTr = "İleri",
            primaryMuscle = MuscleGroup.SHOULDERS,
            subregionTr = "Ön Omuz",
            instructionsTr = listOf(
                "Ayaklar omuz genişliğinde açık, bar klavikula üzerinde.",
                "Barı başınızın üzerinden tavana doğru dik itin.",
                "Tepe noktada gövdeyi kitleyin."
            ),
            breathingTipTr = "Başlangıçta nefes al, iterken ver.",
            commonMistakesTr = listOf("Belden aşırı kavis yapmak"),
            defaultSets = 4,
            defaultReps = "8-10",
            defaultRestSeconds = 90
        ),
        Exercise(
            id = "dumbbell_front_raise",
            nameTr = "Dumbbell Front Raise",
            nameEnAlias = "Alternating Front Raise",
            equipmentTr = "Dumbbell",
            difficultyTr = "Başlangıç",
            primaryMuscle = MuscleGroup.SHOULDERS,
            subregionTr = "Ön Omuz",
            instructionsTr = listOf(
                "Dumbbell'ları uyluk önünde tutun.",
                "Sırayla omuz hizasına kadar kontrollü kaldırın."
            ),
            breathingTipTr = "Kaldırırken ver, indirirken al.",
            commonMistakesTr = listOf("Gövdeyi sallamak"),
            defaultSets = 3,
            defaultReps = "12",
            defaultRestSeconds = 60
        ),
        Exercise(
            id = "lateral_raise",
            nameTr = "Lateral Raise",
            nameEnAlias = "Side Lateral Dumbbell Raise",
            equipmentTr = "Dumbbell",
            difficultyTr = "Orta",
            primaryMuscle = MuscleGroup.SHOULDERS,
            subregionTr = "Yan Omuz",
            instructionsTr = listOf(
                "Dumbbell'ları gövde yanında tutun, dirsekleri hafif bükün.",
                "Kolları yanlara doğru omuz hizasına kadar yükseltin.",
                "Serçe parmağı hafif yukarı bakacak şekilde tepe noktada sıkın."
            ),
            breathingTipTr = "Yükseltirken nefes ver, indirirken al.",
            commonMistakesTr = listOf("Aşırı ağır kilo kullanıp trapezle çekmek"),
            defaultSets = 4,
            defaultReps = "12-15",
            defaultRestSeconds = 60
        ),
        Exercise(
            id = "cable_lateral_raise",
            nameTr = "Cable Lateral Raise",
            nameEnAlias = "Single Arm Cable Lateral",
            equipmentTr = "Cable",
            difficultyTr = "Orta",
            primaryMuscle = MuscleGroup.SHOULDERS,
            subregionTr = "Yan Omuz",
            instructionsTr = listOf(
                "Kabloyu bacak arkasından veya önünden kavrayın.",
                "Sürekli gerginliği koruyarak omuz hizasına kaldırın."
            ),
            breathingTipTr = "Kaldırırken nefes ver, kontrollü indir.",
            commonMistakesTr = listOf("Hızlı bırakmak"),
            defaultSets = 3,
            defaultReps = "12-15",
            defaultRestSeconds = 60
        ),
        Exercise(
            id = "face_pull",
            nameTr = "Face Pull",
            nameEnAlias = "Rope Face Pull",
            equipmentTr = "Cable",
            difficultyTr = "Başlangıç",
            primaryMuscle = MuscleGroup.SHOULDERS,
            subregionTr = "Arka Omuz",
            instructionsTr = listOf(
                "Halatı göz hizasına çekin, dirsekleri dışarı ve yukarı açın.",
                "Arka omuz ve dış rotatörleri sıkıştırarak 1 saniye bekleyin."
            ),
            breathingTipTr = "Çekerken ver, uzatırken al.",
            commonMistakesTr = listOf("Dirsekleri aşağı düşürmek"),
            defaultSets = 4,
            defaultReps = "12-15",
            defaultRestSeconds = 60
        ),
        Exercise(
            id = "reverse_pec_deck",
            nameTr = "Reverse Pec Deck",
            nameEnAlias = "Rear Delt Fly Machine",
            equipmentTr = "Makine",
            difficultyTr = "Başlangıç",
            primaryMuscle = MuscleGroup.SHOULDERS,
            subregionTr = "Arka Omuz",
            instructionsTr = listOf(
                "Gövdenizi makineye yaslayın.",
                "Kolları geriye doğru açıp arka omuzları sıkıştırın."
            ),
            breathingTipTr = "Açarken nefes ver.",
            commonMistakesTr = listOf("Kürek kemiklerini aşırı sıkıp sırta devretmek"),
            defaultSets = 3,
            defaultReps = "12-15",
            defaultRestSeconds = 60
        ),

        // --- SIRT (Back) ---
        Exercise(
            id = "lat_pulldown",
            nameTr = "Lat Pulldown",
            nameEnAlias = "Wide-Grip Lat Pulldown",
            equipmentTr = "Kablo / Makine",
            difficultyTr = "Başlangıç",
            primaryMuscle = MuscleGroup.BACK,
            subregionTr = "Kanat (Lat)",
            instructionsTr = listOf(
                "Barı omuzlardan geniş kavrayın.",
                "Üst göğse doğru çekip kanat kaslarını sıkın."
            ),
            breathingTipTr = "Çekerken nefes ver.",
            commonMistakesTr = listOf("Geriye aşırı yatmak"),
            defaultSets = 4,
            defaultReps = "10-12",
            defaultRestSeconds = 75
        ),
        Exercise(
            id = "pull_up",
            nameTr = "Barfiks (Pull-up)",
            nameEnAlias = "Wide Grip Pull-up",
            equipmentTr = "Vücut Ağırlığı",
            difficultyTr = "İleri",
            primaryMuscle = MuscleGroup.BACK,
            subregionTr = "Kanat (Lat)",
            instructionsTr = listOf(
                "Geniş tutuşla bara asılın.",
                "Göğsü bara doğru yükseltin."
            ),
            breathingTipTr = "Çekilirken nefes ver.",
            commonMistakesTr = listOf("Bacakları savurmak"),
            defaultSets = 4,
            defaultReps = "6-10",
            defaultRestSeconds = 90
        ),
        Exercise(
            id = "barbell_shrug",
            nameTr = "Barbell Shrug",
            nameEnAlias = "Trapezius Shrug",
            equipmentTr = "Barbell",
            difficultyTr = "Başlangıç",
            primaryMuscle = MuscleGroup.BACK,
            subregionTr = "Trapez",
            instructionsTr = listOf(
                "Barı tutun, omuzları kulaklara doğru yukarı çekin."
            ),
            breathingTipTr = "Kaldırırken nefes ver.",
            commonMistakesTr = listOf("Omuzları dairesel çevirmek"),
            defaultSets = 4,
            defaultReps = "12-15",
            defaultRestSeconds = 60
        ),
        Exercise(
            id = "seated_cable_row",
            nameTr = "Seated Row",
            nameEnAlias = "Seated Cable Row",
            equipmentTr = "Cable",
            difficultyTr = "Orta",
            primaryMuscle = MuscleGroup.BACK,
            subregionTr = "Romboid / Orta Sırt",
            instructionsTr = listOf(
                "Tutamacı karnınıza doğru çekip kürek kemiklerini birbirine yapıştırın."
            ),
            breathingTipTr = "Çekerken nefes ver.",
            commonMistakesTr = listOf("Belden sallanmak"),
            defaultSets = 4,
            defaultReps = "10-12",
            defaultRestSeconds = 75
        ),

        // --- KOL (Arms: Biceps & Triceps) ---
        Exercise(
            id = "barbell_bicep_curl",
            nameTr = "Barbell Curl",
            nameEnAlias = "Standing Bicep Barbell Curl",
            equipmentTr = "Barbell",
            difficultyTr = "Başlangıç",
            primaryMuscle = MuscleGroup.BICEPS,
            subregionTr = "Biceps (Ön Kol)",
            instructionsTr = listOf(
                "Dirsekleri gövdeye sabitleyip barı omuzlara doğru bükün."
            ),
            breathingTipTr = "Kaldırırken ver, indirirken al.",
            commonMistakesTr = listOf("Dirsekleri öne kaçırmak"),
            defaultSets = 3,
            defaultReps = "10-12",
            defaultRestSeconds = 60
        ),
        Exercise(
            id = "incline_dumbbell_curl",
            nameTr = "Incline Dumbbell Curl",
            nameEnAlias = "Seated Incline Bicep Curl",
            equipmentTr = "Dumbbell",
            difficultyTr = "Orta",
            primaryMuscle = MuscleGroup.BICEPS,
            subregionTr = "Biceps (Ön Kol)",
            instructionsTr = listOf(
                "Eğimli sehpada kolları tam gerip bicepsi esneterek kaldırın."
            ),
            breathingTipTr = "Bükerken nefes ver.",
            commonMistakesTr = listOf("Ağırlığı savurmak"),
            defaultSets = 3,
            defaultReps = "10-12",
            defaultRestSeconds = 60
        ),
        Exercise(
            id = "triceps_pushdown",
            nameTr = "Triceps Pushdown",
            nameEnAlias = "Cable Rope Triceps Pushdown",
            equipmentTr = "Cable",
            difficultyTr = "Başlangıç",
            primaryMuscle = MuscleGroup.BICEPS,
            subregionTr = "Triceps (Arka Kol)",
            instructionsTr = listOf(
                "Halatı aşağı doğru tam kilitlenene kadar itin."
            ),
            breathingTipTr = "İterken nefes ver.",
            commonMistakesTr = listOf("Dirsekleri yana açmak"),
            defaultSets = 3,
            defaultReps = "12-15",
            defaultRestSeconds = 60
        ),

        // --- KARIN (Abs) ---
        Exercise(
            id = "cable_crunch",
            nameTr = "Cable Crunch",
            nameEnAlias = "Kneeling Cable Crunch",
            equipmentTr = "Cable",
            difficultyTr = "Orta",
            primaryMuscle = MuscleGroup.ABS,
            subregionTr = "Üst Karın",
            instructionsTr = listOf(
                "Diz çökün, halatı baş yanında tutup göğsü leğen kemiğine doğru bükün."
            ),
            breathingTipTr = "Kıvrılırken nefes ver.",
            commonMistakesTr = listOf("Kalçadan bükülmek"),
            defaultSets = 3,
            defaultReps = "12-15",
            defaultRestSeconds = 60
        ),
        Exercise(
            id = "hanging_leg_raise",
            nameTr = "Hanging Leg Raise",
            nameEnAlias = "Bar Hanging Leg Raise",
            equipmentTr = "Vücut Ağırlığı",
            difficultyTr = "İleri",
            primaryMuscle = MuscleGroup.ABS,
            subregionTr = "Alt Karın",
            instructionsTr = listOf(
                "Bara asılın, leğen kemiğini yukarı yuvarlayarak bacakları kaldırın."
            ),
            breathingTipTr = "Kaldırırken nefes ver.",
            commonMistakesTr = listOf("Sallanarak momentum almak"),
            defaultSets = 3,
            defaultReps = "10-12",
            defaultRestSeconds = 60
        ),

        // --- BACAK (Legs) ---
        Exercise(
            id = "barbell_squat",
            nameTr = "Barbell Squat",
            nameEnAlias = "Back Squat",
            equipmentTr = "Barbell",
            difficultyTr = "İleri",
            primaryMuscle = MuscleGroup.QUADS,
            subregionTr = "Ön Bacak (Quadriceps)",
            instructionsTr = listOf(
                "Barı üst trapezlere yerleştirin, kalçayı geriye verip paralel derinliğe inin."
            ),
            breathingTipTr = "İnerken nefes al, kalkarken ver.",
            commonMistakesTr = listOf("Dizleri içeri bükmek"),
            defaultSets = 4,
            defaultReps = "6-8",
            defaultRestSeconds = 120
        ),
        Exercise(
            id = "leg_press",
            nameTr = "Leg Press",
            nameEnAlias = "45 Degree Leg Press",
            equipmentTr = "Makine",
            difficultyTr = "Orta",
            primaryMuscle = MuscleGroup.QUADS,
            subregionTr = "Ön Bacak (Quadriceps)",
            instructionsTr = listOf(
                "Platformu kontrollü indirin, dizleri kitlemeden yukarı itin."
            ),
            breathingTipTr = "İnerken al, iterken ver.",
            commonMistakesTr = listOf("Dizleri tam kilitlemek"),
            defaultSets = 4,
            defaultReps = "10-12",
            defaultRestSeconds = 90
        ),
        Exercise(
            id = "romanian_deadlift",
            nameTr = "Romanian Deadlift",
            nameEnAlias = "RDL",
            equipmentTr = "Barbell",
            difficultyTr = "Orta",
            primaryMuscle = MuscleGroup.QUADS,
            subregionTr = "Arka Bacak (Hamstrings)",
            instructionsTr = listOf(
                "Dizler hafif bükük, kalçayı geriye iterek barı kaval kemiği boyunca indirin."
            ),
            breathingTipTr = "İnerken nefes al, doğrulurken ver.",
            commonMistakesTr = listOf("Sırtı kamburlaştırmak"),
            defaultSets = 4,
            defaultReps = "8-10",
            defaultRestSeconds = 90
        ),
        Exercise(
            id = "standing_calf_raise",
            nameTr = "Standing Calf Raise",
            nameEnAlias = "Calf Machine Raise",
            equipmentTr = "Makine",
            difficultyTr = "Başlangıç",
            primaryMuscle = MuscleGroup.QUADS,
            subregionTr = "Kalf",
            instructionsTr = listOf(
                "Parmak uçlarında yükselin, tepe noktada kalf kasını 2 saniye sıkın."
            ),
            breathingTipTr = "Yükselirken nefes ver.",
            commonMistakesTr = listOf("Yaylanarak hızlı yapmak"),
            defaultSets = 4,
            defaultReps = "15-20",
            defaultRestSeconds = 45
        )
    )

    fun getExercisesForSubregion(subregion: String): List<Exercise> {
        return sampleExercises.filter { it.subregionTr.equals(subregion, ignoreCase = true) }
    }

    fun getExercisesForMuscle(muscle: MuscleGroup): List<Exercise> {
        return sampleExercises.filter { it.primaryMuscle == muscle }
    }
}
