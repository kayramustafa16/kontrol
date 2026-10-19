# Anabolik Atlas — Complete Android Fitness App

A high-performance, native Android fitness application with 11 cohesive screens, built in 100% grammatically correct Turkish. Anabolik Atlas pairs a modular 3D human anatomy viewport with offline-first Room local persistence, active workout tracking with volume calculation, interactive nutrition planning, lifestyle tracking, and comprehensive progress analytics.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> The following user-confirmed choices are integrated into this architecture:
> - **Tripo 3D FBX Integration Pipeline**: Support for user-provided FBX anatomy model exported from Tripo 3D. Includes an automated conversion workflow (`scripts/convert_fbx_to_glb.py`) preserving node hierarchy, vertex materials, and segment names, alongside an in-app 3D Model Import tool (`Model İçe Aktar`) in Settings.
> - **Mesh-to-Muscle Logical Mapping**: Clean decoupled mapping engine (`muscle_mesh_mapping.json`) mapping segmented mesh IDs (e.g. `Pectoralis_L`, `Pectoralis_R`) to logical muscle groups (`chest`, `back`, `shoulders`, `biceps`, `triceps`, `abs`, `quadriceps`, `hamstrings`, `glutes`, `calves`). Tapping either left or right pectoral segment highlights both chest heads synchronously.
> - **Modular 3D Anatomy Viewport**: Implemented as a dedicated, replaceable Compose component with multi-angle view toggles (Ön / Arka / Yan), orbital rotation controls, zoom handling, and fallback placeholder state with clear asset import instructions until the actual model is imported.
> - **Starter Workout Split**: Configured with "Push Day" (İtiş Günü: Göğüs • Omuz • Triceps) active by default on the Ana Sayfa dashboard.
> - **Dynamic Goal Calculations**: Nutrition and hydration targets are calculated dynamically based on user biometrics (weight, height, goal) with full customizability in the Profile screen.
> - **Zero Cloud / 100% Local**: No backend accounts, no paywalls, and no fake subscriptions. Full local persistence using Android Room and DataStore.

---

## 1. Overview & Core Concept

**Anabolik Atlas** is a native dark-mode fitness companion engineered for athletes and gym-goers who want complete ownership of their training, anatomy understanding, and nutrition.

- **Primary Persona**: Strength trainees, bodybuilders, and fitness enthusiasts seeking precise muscle targeting, real workout logging, and macro tracking without cloud lock-in.
- **Key Value**: A visual-first workflow that connects human anatomy directly to exercises, programs, live set logging, and progression metrics in an intuitive Turkish UI.

---

## 2. User Experience & Visual Design

### 2.1 Visual Identity & Dark Aesthetic

The design directly recreates the 11-screen reference guide:
- **Backgrounds**: Deep obsidian (`#0D0F14`) and charcoal card surfaces (`#161922`, `#1F2430`).
- **Accent Palette**: Electric Cyan (`#00E5FF`), Vivid Blue (`#0D8BF0`), Crimson Accent (`#FF3B30` for primary CTA buttons and muscle highlights), and Amber/Orange (`#FF9500`).
- **Typography**: Clean, bold sans-serif hierarchy using Material 3 Typography tokens, strictly scaled in `sp`.
- **Card Architecture**: Rounded 16-24dp corners, subtle hairline borders (`#2A2F3D`), and restrained glow effects on interactive states.

### 2.2 Screen Hierarchy & User Flows

```
                        ┌───────────────────────────────┐
                        │    ANA SAYFA (Dashboard)      │
                        │ - Hero 3D Preview             │
                        │ - Push Day Quick CTA          │
                        │ - Today's Summary & Metrics   │
                        └──────────────┬────────────────┘
                                       │
        ┌──────────────┬───────────────┼───────────────┬──────────────┐
        ▼              ▼               ▼               ▼              ▼
┌──────────────┐┌──────────────┐┌──────────────┐┌──────────────┐┌──────────────┐
│  3D KAS      ││ PROGRAMLAR   ││ KEŞFET       ││ BESLENME     ││ PROFİL       │
│  MODELİ      ││ - Splitler   ││ - Arama      ││ - Kalori     ││ - Ölçüler    │
│  - Ön/Arka   ││ - 4 Program  ││ - Filtreler  ││ - Makrolar   ││ - Hedefler   │
│  - Hot-spots ││ - Program    ││ - Egzersiz   ││ - Öğünler    ││ - Model İçe  │
│  - Kas Seçimi││   Detayı     ││   Kartları   ││ - Ekle/Sil   ││   Aktar      │
└───────┬──────┘└──────┬───────┘└──────┬───────┘└──────────────┘└──────┬───────┘
        │              │               │                               │
        │              └───────┬───────┘                               ▼
        ▼                      ▼                               ┌──────────────┐
┌──────────────┐       ┌──────────────┐                        │ 3D MODEL     │
│ ANATOMİ      │       │ EGZERSİZ     │                        │ YÖNETİCİSİ   │
│ BİLGİSİ      │◄──────┤ DETAYI       │                        │ - Dosya Seç  │
│ - Fonksiyon  │       │ - Video/3D   │                        │ - Segment    │
│ - Kas Başları│       │ - Nasıl      │                        │   Eşleme     │
│ - Sakatlıklar│       │ - Antrenmana │                        │ - Önizleme   │
└──────────────┘       │   Ekle       │                        └──────────────┘
                       └───────┬──────┘
                               ▼
                       ┌──────────────┐       ┌──────────────┐
                       │ ANTRENMAN    │──────►│ GELİŞİM /    │
                       │ EKRANI       │       │ İSTATİSTİK   │
                       │ - Canlı Süre │       │ - Kilo Graf. │
                       │ - Set Tablosu│       │ - Hacim      │
                       │ - Dinlenme T.│       │ - Ölçümler   │
                       │ - Hacim Hes. │       │ - PR Kaydı   │
                       └──────────────┘       └──────────────┘
                               ▲
                               │
                       ┌───────┴──────┐
                       │ YAŞAM TAKİBİ │
                       │ - Su, Uyku,  │
                       │   Adım, Mod  │
                       └──────────────┘
```

---

## 3. Key Product Decisions & Architecture

### 3.1 Separate 3D Model Viewport & Tripo 3D FBX Pipeline
- **Dedicated Viewport Component (`AnatomyViewport3D`)**:
  - Independent bounds, configurable camera presets (Ön, Arka, Yan), 360° horizontal drag, pitch rotation, pinch zoom, and reset button.
  - Distinct development placeholder state with informative Turkish instructions ("Model Henüz Eklenmedi — Profil'den 3D Model İçe Aktarın") when no user file is loaded yet.
- **FBX Asset Conversion Workflow**:
  - `scripts/convert_fbx_to_glb.py`: Standalone CLI utility using Assimp/Trimesh/Blender to inspect node hierarchies, log sub-meshes, convert Tripo 3D `.fbx` to optimized binary `.glb`, and generate initial muscle mappings.
- **In-App Model Picker (`Model Yöneticisi`)**:
  - Accessible from Profil -> Ayarlar -> "3D Model İçe Aktar".
  - Uses `ActivityResultContracts.OpenDocument` allowing the user to select `.glb`, `.gltf`, or `.fbx` from their device storage.
  - Model file is cached in app private storage (`files/models/custom_anatomy.glb`).
- **Logical Muscle Mapping Engine (`MuscleMeshMapping`)**:
  - Decouples raw mesh IDs from domain concepts.
  - Handles bilateral groups: left and right pectoral meshes both map to `MuscleGroup.CHEST`.
  - Normalizes Tripo 3D naming patterns (`chest_l`, `pectoralis_r`, `latissimus_dorsi`, `biceps_brachii`, `rectus_abdominis`, `quadriceps_femoris`, etc.).

### 3.2 Real Active Workout Engine
- Active stopwatch and rest countdown timer.
- Dynamic set management: add/remove set, input weight (kg) and reps, checkmark set completion.
- Real-time training volume: $\text{Volume} = \sum (\text{weight} \times \text{reps})$ for all completed sets.
- Session summary dialog upon completion that writes to Room database.

### 3.3 Dynamic Nutrition Calculator & Meal Planner
- Per-100g nutrient mathematical calculation: $\text{Nutrient} = \text{Value per 100g} \times \text{Grams} / 100$.
- Meal categories: Sabah, Kuşluk, Öğle, İkindi, Antrenman Sonrası, Gece.
- Local food item database with search, custom food entry, and meal logging.

### 3.4 Progress Charts & Body Measurements
- Canvas-based custom smooth bezier weight and volume line charts with date-range filters (`1H`, `1A`, `3A`, `1Y`, `Tümü`).
- Real-time delta calculations (+/- change since last measurement).
- Empty states with Turkish instructional prompts when no logs exist yet.

---

## 4. Technical Architecture & Database Schema

```
┌────────────────────────────────────────────────────────┐
│                   JETPACK COMPOSE UI                   │
│  - 11 Screens (Turkish Localization & M3 Theme)       │
│  - AnatomyViewport3D & Model Import Screen             │
│  - Custom Canvas Charts & BackHandler Navigation       │
└───────────────────────────▲────────────────────────────┘
                            │ (StateFlow / Events)
┌───────────────────────────┴────────────────────────────┐
│                       VIEWMODELS                       │
│  - AtlasViewModel: Navigation, Dashboard & Workouts    │
│  - AnatomyViewModel: 3D Viewport, Muscle Selection,    │
│    Model Loading & Mesh-to-Muscle Mapping              │
│  - NutritionViewModel: Macros, Meals, Food Search      │
│  - ProgressViewModel: Measurements & Chart Telemetry   │
└───────────────────────────▲────────────────────────────┘
                            │ (Coroutines & Flows)
┌───────────────────────────┴────────────────────────────┐
│                    REPOSITORY LAYER                    │
│  - WorkoutRepository, NutritionRepository,             │
│    ProfileRepository, ProgressRepository, ModelRepo    │
└───────────────────────────▲────────────────────────────┘
                            │ (Room DAOs / Local Files)
┌───────────────────────────┴────────────────────────────┐
│                  LOCAL PERSISTENCE                     │
│  - Room Database (workouts, sets, food, progress)      │
│  - Private Storage: /files/models/custom_anatomy.glb   │
│  - Mesh Mapping Config: /files/models/mapping.json     │
└────────────────────────────────────────────────────────┘
```

---

## 5. Implementation Phases & Verification Steps

1. **Phase 1: Project Setup & Turkish String Catalog**
   - Update `metadata.json` and `strings.xml` to set "Anabolik Atlas".
   - Configure centralized Turkish strings, colors, typography, and M3 dark theme.
2. **Phase 2: 3D Model Architecture & FBX Conversion Utilities**
   - Provide `scripts/convert_fbx_to_glb.py` for Tripo 3D asset conversion.
   - Implement `MuscleMeshMapping` resolver with bilateral chest grouping.
   - Create `AnatomyViewport3D` with camera presets, orbit drag, zoom, and fallback placeholder.
   - Add in-app Model Import screen via Android Document Picker.
3. **Phase 3: Room Persistence Layer & Domain Models**
   - Entities: `UserProfileEntity`, `WorkoutSessionEntity`, `WorkoutSetEntity`, `FoodItemEntity`, `MealLogEntity`, `WeightRecordEntity`, `BodyMeasurementEntity`, `LifestyleEntity`.
   - Room Database & DAOs with reactive `Flow` queries and suspend mutation functions.
4. **Phase 4: The 11 Connected Screens**
   - Implement Screen 1 through Screen 11 with working state transitions, forms, and dialogs.
   - Implement bottom navigation with active indicators and `BackHandler`.
5. **Phase 5: Verification & Compilation**
   - Run `compile_applet` to verify compilation, fixing any syntax or type issues.
   - Verify all 15 acceptance criteria: 100% Turkish text, functional buttons, persistent storage, volume calculations, and responsive layouts.
