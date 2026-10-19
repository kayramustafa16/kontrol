package com.example.data.model

enum class MuscleGroup(
    val id: String,
    val displayNameTr: String,
    val latinName: String,
    val subMusclesTr: List<String>,
    val descriptionTr: String
) {
    CHEST(
        id = "chest",
        displayNameTr = "Göğüs",
        latinName = "Pectoralis Major & Minor",
        subMusclesTr = listOf("Üst Göğüs", "Orta Göğüs", "Alt Göğüs"),
        descriptionTr = "Pectoralis major, göğüs bölgesinin en büyük kasıdır. Kolu öne, içe ve aşağı doğru hareket ettirir. Üst, orta ve alt olmak üzere üç ana bölgeden oluşur."
    ),
    BACK(
        id = "back",
        displayNameTr = "Sırt",
        latinName = "Latissimus Dorsi & Trapezius",
        subMusclesTr = listOf("Kanat (Lat)", "Trapez", "Romboid / Orta Sırt"),
        descriptionTr = "Sırt kasları, vücudun duruşunu ve çekiş gücünü sağlar. Omuz ve omurgayı stabilize ederek geniş ve güçlü bir V form oluşturur."
    ),
    SHOULDERS(
        id = "shoulders",
        displayNameTr = "Omuz",
        latinName = "Deltoideus (Ön, Yan, Arka)",
        subMusclesTr = listOf("Ön Omuz", "Yan Omuz", "Arka Omuz"),
        descriptionTr = "Deltoid kası omzun yuvarlak hatlarını oluşturur. Kolun her yöne kaldırılmasını ve omuz ekleminin rotasyonunu kontrol eder."
    ),
    BICEPS(
        id = "biceps",
        displayNameTr = "Kol",
        latinName = "Biceps & Triceps Brachii",
        subMusclesTr = listOf("Biceps (Ön Kol)", "Triceps (Arka Kol)", "Önkol (Bilek)"),
        descriptionTr = "Kol kasları dirsek fleksiyonu (biceps) ve ekstansiyonunu (triceps) yönetir. Çekiş ve itiş hareketlerinin kilit uygulayıcılarıdır."
    ),
    TRICEPS(
        id = "triceps",
        displayNameTr = "Arka Kol",
        latinName = "Triceps Brachii",
        subMusclesTr = listOf("Uzun Baş", "Lateral Baş", "Medial Baş"),
        descriptionTr = "Triceps brachii kol hacminin yaklaşık %60'ını oluşturur. Dirseğin tam açılmasından ve itiş hareketlerinden sorumludur."
    ),
    ABS(
        id = "abs",
        displayNameTr = "Karın",
        latinName = "Rectus Abdominis & Obliques",
        subMusclesTr = listOf("Üst Karın", "Alt Karın", "Yan Karın (Oblique)"),
        descriptionTr = "Karın kasları omurga stabilitesini korur, gövdeyi öne büker ve tüm temel hareketlerde kuvvet transferini sağlar."
    ),
    QUADS(
        id = "quads",
        displayNameTr = "Bacak",
        latinName = "Quadriceps & Hamstrings",
        subMusclesTr = listOf("Ön Bacak (Quadriceps)", "Arka Bacak (Hamstrings)", "Kalf"),
        descriptionTr = "Vücudun en büyük kas grubudur. Diz ve kalça eklemlerini koordine ederek koşu, sıçrama ve ağırlık kaldırmada temel motor güçtür."
    ),
    HAMSTRINGS(
        id = "hamstrings",
        displayNameTr = "Arka Bacak",
        latinName = "Biceps Femoris & Semitendinosus",
        subMusclesTr = listOf("Biceps Femoris", "Semitendinosus"),
        descriptionTr = "Diz eklemini büker ve kalçayı geriye doğru iter. Patlayıcı bacak kuvveti ve sakatlık önlemede kritik role sahiptir."
    ),
    GLUTES(
        id = "glutes",
        displayNameTr = "Kalça",
        latinName = "Gluteus Maximus & Medius",
        subMusclesTr = listOf("Gluteus Maximus", "Gluteus Medius"),
        descriptionTr = "Vücudun en büyük tekil kasıdır. Kalça ekstansiyonunu ve dik duruşu sağlar; deadlift ve sprint hareketlerinde temel kuvvettir."
    ),
    CALVES(
        id = "calves",
        displayNameTr = "Kalf",
        latinName = "Gastrocnemius & Soleus",
        subMusclesTr = listOf("Gastrocnemius", "Soleus"),
        descriptionTr = "Ayak bileği plantar fleksiyonunu sağlar. Yürüme, koşma ve sıçramalarda yerle temasın ilk tepki kuvvetini yönetir."
    );

    companion object {
        fun fromId(id: String): MuscleGroup {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: CHEST
        }
    }
}
