# PR 1: Project Scaffolding + Database Layer

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Scaffold the Haven Android project and implement the complete Room database layer with seed data, proving the data model compiles and passes tests.

**Architecture:** Standard Android single-module app with Kotlin + Jetpack Compose + Room + Hilt. All 10 database tables implemented as Room entities. DAOs provide Phase 1 query operations (entry creation, label lookup, date-based queries). Seed data delivered via `RoomDatabase.Callback.onCreate`. Bottom nav shell included as a minimal UI proving the app launches.

**Tech Stack:** Kotlin 2.0, Jetpack Compose (BOM 2024.12), Room 2.6, Hilt 2.51, Navigation Compose 2.8, Material3, Gradle version catalog

---

## Task 1: Gradle Project Structure

Create the Android project skeleton: version catalog, project-level build file, app-level build file, settings, gradle properties.

**Files:**
- Create: `gradle/libs.versions.toml`
- Create: `build.gradle.kts` (project-level)
- Create: `settings.gradle.kts`
- Create: `gradle.properties`
- Create: `app/build.gradle.kts`

**Step 1: Create version catalog**

```toml
# gradle/libs.versions.toml
[versions]
agp = "8.7.3"
kotlin = "2.0.21"
ksp = "2.0.21-1.0.28"
composeBom = "2024.12.01"
room = "2.6.1"
hilt = "2.51.1"
hiltNavigationCompose = "1.2.0"
navigationCompose = "2.8.5"
coreKtx = "1.15.0"
lifecycleRuntime = "2.8.7"
activityCompose = "1.9.3"
coroutines = "1.9.0"
junit = "4.13.2"
junitExt = "1.2.1"
espresso = "3.6.1"
turbine = "1.2.0"

[libraries]
androidx-core-ktx = { module = "androidx.core:core-ktx", version.ref = "coreKtx" }
androidx-lifecycle-runtime = { module = "androidx.lifecycle:lifecycle-runtime-ktx", version.ref = "lifecycleRuntime" }
androidx-lifecycle-viewmodel-compose = { module = "androidx.lifecycle:lifecycle-viewmodel-compose", version.ref = "lifecycleRuntime" }
androidx-activity-compose = { module = "androidx.activity:activity-compose", version.ref = "activityCompose" }

# Compose
compose-bom = { module = "androidx.compose:compose-bom", version.ref = "composeBom" }
compose-ui = { module = "androidx.compose.ui:ui" }
compose-ui-graphics = { module = "androidx.compose.ui:ui-graphics" }
compose-ui-tooling-preview = { module = "androidx.compose.ui:ui-tooling-preview" }
compose-material3 = { module = "androidx.compose.material3:material3" }
compose-ui-tooling = { module = "androidx.compose.ui:ui-tooling" }
compose-ui-test-manifest = { module = "androidx.compose.ui:ui-test-manifest" }
compose-ui-test-junit4 = { module = "androidx.compose.ui:ui-test-junit4" }
compose-material-icons-extended = { module = "androidx.compose.material:material-icons-extended" }

# Navigation
navigation-compose = { module = "androidx.navigation:navigation-compose", version.ref = "navigationCompose" }

# Room
room-runtime = { module = "androidx.room:room-runtime", version.ref = "room" }
room-compiler = { module = "androidx.room:room-compiler", version.ref = "room" }
room-ktx = { module = "androidx.room:room-ktx", version.ref = "room" }
room-testing = { module = "androidx.room:room-testing", version.ref = "room" }

# Hilt
hilt-android = { module = "com.google.dagger:hilt-android", version.ref = "hilt" }
hilt-compiler = { module = "com.google.dagger:hilt-compiler", version.ref = "hilt" }
hilt-navigation-compose = { module = "androidx.hilt:hilt-navigation-compose", version.ref = "hiltNavigationCompose" }

# Coroutines
kotlinx-coroutines-android = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-android", version.ref = "coroutines" }
kotlinx-coroutines-test = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-test", version.ref = "coroutines" }

# Testing
junit = { module = "junit:junit", version.ref = "junit" }
androidx-junit = { module = "androidx.test.ext:junit", version.ref = "junitExt" }
androidx-espresso-core = { module = "androidx.test.espresso:espresso-core", version.ref = "espresso" }
turbine = { module = "app.cash.turbine:turbine", version.ref = "turbine" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
```

**Step 2: Create project-level build.gradle.kts**

```kotlin
// build.gradle.kts (project root)
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}
```

**Step 3: Create settings.gradle.kts**

```kotlin
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolution {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Haven"
include(":app")
```

**Step 4: Create gradle.properties**

```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
```

**Step 5: Create app/build.gradle.kts**

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.haven.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.haven.app"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "com.haven.app.HiltTestRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    // Navigation
    implementation(libs.navigation.compose)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.room.testing)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.turbine)
}
```

**Step 6: Create Gradle wrapper**

Run:
```bash
# Download Gradle wrapper (8.9 to match AGP 8.7.x)
gradle wrapper --gradle-version 8.9
```

If `gradle` CLI is not installed, manually create wrapper files:
- `gradlew` (Unix shell script)
- `gradlew.bat` (Windows batch)
- `gradle/wrapper/gradle-wrapper.properties`
- `gradle/wrapper/gradle-wrapper.jar`

The wrapper properties should specify:
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.9-bin.zip
```

**Step 7: Create proguard-rules.pro**

```
# app/proguard-rules.pro
# Haven - ProGuard rules
# Add project specific ProGuard rules here.
```

**Step 8: Verify project compiles (will fail — no source yet, that's expected)**

This step just confirms the Gradle files parse correctly:
```bash
./gradlew tasks --quiet
```
Expected: Gradle syncs and shows available tasks (may warn about missing source directories).

**Step 9: Commit**

```bash
git add gradle/ build.gradle.kts settings.gradle.kts gradle.properties app/build.gradle.kts app/proguard-rules.pro gradlew gradlew.bat
git commit -m "chore: add Gradle project structure with version catalog"
```

---

## Task 2: Android Manifest + Application Class

Create the minimum files needed for the app to compile: manifest, Application class, and placeholder MainActivity.

**Files:**
- Create: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/java/com/haven/app/HavenApp.kt`
- Create: `app/src/main/java/com/haven/app/ui/MainActivity.kt`

**Step 1: Create AndroidManifest.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:name=".HavenApp"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="Haven"
        android:supportsRtl="true"
        android:theme="@style/Theme.Haven">

        <activity
            android:name=".ui.MainActivity"
            android:exported="true"
            android:theme="@style/Theme.Haven">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

**Step 2: Create HavenApp.kt**

```kotlin
package com.haven.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HavenApp : Application()
```

**Step 3: Create placeholder MainActivity**

```kotlin
package com.haven.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Text("Haven")
        }
    }
}
```

**Step 4: Create minimal theme resource**

Create `app/src/main/res/values/themes.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.Haven" parent="android:Theme.Material.Light.NoActionBar" />
</resources>
```

Create default launcher icon placeholder (or use Android defaults). At minimum:
- `app/src/main/res/mipmap-hdpi/ic_launcher.webp` (copy Android default or skip — build will warn but not fail without custom icon)

Alternative: create a minimal adaptive icon via `app/src/main/res/values/ic_launcher_background.xml` or just add the mipmap reference later. For now, remove the `android:icon` attribute from the manifest to avoid build failure if no icon file exists.

**Step 5: Build**

```bash
./gradlew assembleDebug
```
Expected: BUILD SUCCESSFUL

**Step 6: Commit**

```bash
git add app/src/
git commit -m "chore: add manifest, HavenApp, and placeholder MainActivity"
```

---

## Task 3: Room Entities

Implement all 10 database tables as Room `@Entity` classes. This is the complete data model from `docs/schema.sql`.

**Files (all under `app/src/main/java/com/haven/app/data/entity/`):**
- Create: `MeasurementType.kt`
- Create: `Category.kt`
- Create: `EntryType.kt`
- Create: `Label.kt`
- Create: `Tag.kt`
- Create: `LabelTag.kt`
- Create: `Entry.kt`
- Create: `EntryLabel.kt`
- Create: `AnchorActivity.kt`
- Create: `AnchorTag.kt`

**Step 1: MeasurementType entity**

```kotlin
package com.haven.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "measurement_type")
data class MeasurementType(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val displayName: String
)
```

**Step 2: Category entity**

```kotlin
package com.haven.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String
)
```

**Step 3: EntryType entity**

```kotlin
package com.haven.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "entry_type",
    foreignKeys = [
        ForeignKey(
            entity = MeasurementType::class,
            parentColumns = ["id"],
            childColumns = ["measurement_type_id"]
        )
    ],
    indices = [Index("measurement_type_id")]
)
data class EntryType(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "measurement_type_id")
    val measurementTypeId: Long,
    val prompt: String? = null,
    val icon: String? = null,
    @ColumnInfo(name = "is_enabled", defaultValue = "1")
    val isEnabled: Boolean = true,
    @ColumnInfo(name = "is_default", defaultValue = "1")
    val isDefault: Boolean = true,
    @ColumnInfo(name = "sort_order", defaultValue = "0")
    val sortOrder: Int = 0
)
```

**Step 4: Label entity**

```kotlin
package com.haven.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "label",
    foreignKeys = [
        ForeignKey(
            entity = EntryType::class,
            parentColumns = ["id"],
            childColumns = ["entry_type_id"]
        ),
        ForeignKey(
            entity = Label::class,
            parentColumns = ["id"],
            childColumns = ["parent_id"]
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"]
        )
    ],
    indices = [
        Index("entry_type_id"),
        Index("parent_id"),
        Index("category_id")
    ]
)
data class Label(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "entry_type_id")
    val entryTypeId: Long,
    val name: String,
    @ColumnInfo(name = "parent_id")
    val parentId: Long? = null,
    @ColumnInfo(name = "category_id")
    val categoryId: Long? = null,
    @ColumnInfo(name = "is_default", defaultValue = "1")
    val isDefault: Boolean = true,
    @ColumnInfo(name = "is_enabled", defaultValue = "1")
    val isEnabled: Boolean = true,
    @ColumnInfo(name = "sort_order", defaultValue = "0")
    val sortOrder: Int = 0,
    @ColumnInfo(name = "seed_version", defaultValue = "1")
    val seedVersion: Int = 1
)
```

**Step 5: Tag entity**

```kotlin
package com.haven.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tag")
data class Tag(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "tag_group")
    val tagGroup: String,
    @ColumnInfo(name = "seed_version", defaultValue = "1")
    val seedVersion: Int = 1
)
```

**Step 6: LabelTag join entity**

```kotlin
package com.haven.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "label_tag",
    primaryKeys = ["label_id", "tag_id"],
    foreignKeys = [
        ForeignKey(entity = Label::class, parentColumns = ["id"], childColumns = ["label_id"]),
        ForeignKey(entity = Tag::class, parentColumns = ["id"], childColumns = ["tag_id"])
    ],
    indices = [Index("label_id"), Index("tag_id")]
)
data class LabelTag(
    @ColumnInfo(name = "label_id")
    val labelId: Long,
    @ColumnInfo(name = "tag_id")
    val tagId: Long
)
```

**Step 7: Entry entity**

```kotlin
package com.haven.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "entry",
    foreignKeys = [
        ForeignKey(
            entity = EntryType::class,
            parentColumns = ["id"],
            childColumns = ["entry_type_id"]
        )
    ],
    indices = [
        Index("entry_type_id"),
        Index("timestamp"),
        Index("source_type")
    ]
)
data class Entry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "entry_type_id")
    val entryTypeId: Long,
    @ColumnInfo(name = "source_type", defaultValue = "'log'")
    val sourceType: String = "log",
    val timestamp: String,
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    @ColumnInfo(name = "numeric_value")
    val numericValue: Double? = null,
    val notes: String? = null
)
```

**Step 8: EntryLabel join entity**

```kotlin
package com.haven.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "entry_label",
    primaryKeys = ["entry_id", "label_id"],
    foreignKeys = [
        ForeignKey(
            entity = Entry::class,
            parentColumns = ["id"],
            childColumns = ["entry_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(entity = Label::class, parentColumns = ["id"], childColumns = ["label_id"])
    ],
    indices = [Index("entry_id"), Index("label_id")]
)
data class EntryLabel(
    @ColumnInfo(name = "entry_id")
    val entryId: Long,
    @ColumnInfo(name = "label_id")
    val labelId: Long
)
```

**Step 9: AnchorActivity entity**

```kotlin
package com.haven.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "anchor_activity",
    foreignKeys = [
        ForeignKey(entity = Label::class, parentColumns = ["id"], childColumns = ["label_id"])
    ],
    indices = [Index("label_id")]
)
data class AnchorActivity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "label_id")
    val labelId: Long,
    val title: String,
    val icon: String? = null,
    @ColumnInfo(name = "default_effort")
    val defaultEffort: Int,
    @ColumnInfo(name = "user_effort")
    val userEffort: Int? = null,
    @ColumnInfo(name = "is_enabled", defaultValue = "1")
    val isEnabled: Boolean = true,
    @ColumnInfo(name = "is_default", defaultValue = "1")
    val isDefault: Boolean = true,
    @ColumnInfo(name = "seed_version", defaultValue = "1")
    val seedVersion: Int = 1
)
```

**Step 10: AnchorTag join entity**

```kotlin
package com.haven.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "anchor_tag",
    primaryKeys = ["anchor_activity_id", "tag_id"],
    foreignKeys = [
        ForeignKey(entity = AnchorActivity::class, parentColumns = ["id"], childColumns = ["anchor_activity_id"]),
        ForeignKey(entity = Tag::class, parentColumns = ["id"], childColumns = ["tag_id"])
    ],
    indices = [Index("anchor_activity_id"), Index("tag_id")]
)
data class AnchorTag(
    @ColumnInfo(name = "anchor_activity_id")
    val anchorActivityId: Long,
    @ColumnInfo(name = "tag_id")
    val tagId: Long
)
```

**Step 11: Build to verify entities compile**

```bash
./gradlew assembleDebug
```
Expected: BUILD SUCCESSFUL (Room won't process annotations until we create the Database class, but entities should compile as plain data classes)

**Step 12: Commit**

```bash
git add app/src/main/java/com/haven/app/data/entity/
git commit -m "feat: add all 10 Room entity classes matching schema.sql"
```

---

## Task 4: DAOs

Create DAOs for Phase 1 operations. Focus on what the Tend page, logging forms, and Trace page need.

**Files (all under `app/src/main/java/com/haven/app/data/dao/`):**
- Create: `EntryTypeDao.kt`
- Create: `LabelDao.kt`
- Create: `TagDao.kt`
- Create: `EntryDao.kt`

**Step 1: EntryTypeDao**

```kotlin
package com.haven.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.haven.app.data.entity.EntryType
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryTypeDao {
    @Query("SELECT * FROM entry_type WHERE is_enabled = 1 ORDER BY sort_order")
    fun getEnabled(): Flow<List<EntryType>>

    @Query("SELECT * FROM entry_type WHERE id = :id")
    suspend fun getById(id: Long): EntryType?

    @Insert
    suspend fun insertAll(entryTypes: List<EntryType>)
}
```

**Step 2: LabelDao**

```kotlin
package com.haven.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.haven.app.data.entity.Label
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao {
    @Query("SELECT * FROM label WHERE entry_type_id = :entryTypeId AND is_enabled = 1 ORDER BY sort_order")
    fun getByEntryType(entryTypeId: Long): Flow<List<Label>>

    @Query("SELECT * FROM label WHERE entry_type_id = :entryTypeId AND parent_id IS NULL AND is_enabled = 1 ORDER BY sort_order")
    fun getTopLevel(entryTypeId: Long): Flow<List<Label>>

    @Query("SELECT * FROM label WHERE parent_id = :parentId AND is_enabled = 1 ORDER BY sort_order")
    fun getChildren(parentId: Long): Flow<List<Label>>

    @Insert
    suspend fun insertAll(labels: List<Label>)

    @Insert
    suspend fun insert(label: Label): Long
}
```

**Step 3: TagDao**

```kotlin
package com.haven.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.haven.app.data.entity.LabelTag
import com.haven.app.data.entity.Tag

@Dao
interface TagDao {
    @Query("SELECT * FROM tag WHERE tag_group = :group")
    suspend fun getByGroup(group: String): List<Tag>

    @Insert
    suspend fun insertAll(tags: List<Tag>)

    @Insert
    suspend fun insertLabelTags(labelTags: List<LabelTag>)
}
```

**Step 4: EntryDao**

```kotlin
package com.haven.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.haven.app.data.entity.Entry
import com.haven.app.data.entity.EntryLabel
import kotlinx.coroutines.flow.Flow

data class EntryWithDetails(
    val id: Long,
    val entryTypeId: Long,
    val entryTypeName: String,
    val entryTypeIcon: String?,
    val sourceType: String,
    val timestamp: String,
    val createdAt: String,
    val numericValue: Double?,
    val notes: String?,
    val labelNames: String?  // comma-separated, from GROUP_CONCAT
)

@Dao
interface EntryDao {
    @Insert
    suspend fun insert(entry: Entry): Long

    @Insert
    suspend fun insertEntryLabels(entryLabels: List<EntryLabel>)

    @Transaction
    suspend fun insertWithLabels(entry: Entry, labelIds: List<Long>): Long {
        val entryId = insert(entry)
        if (labelIds.isNotEmpty()) {
            insertEntryLabels(labelIds.map { EntryLabel(entryId, it) })
        }
        return entryId
    }

    @Query("""
        SELECT e.id, e.entry_type_id AS entryTypeId, et.name AS entryTypeName, et.icon AS entryTypeIcon,
               e.source_type AS sourceType, e.timestamp, e.created_at AS createdAt,
               e.numeric_value AS numericValue, e.notes,
               GROUP_CONCAT(l.name, ', ') AS labelNames
        FROM entry e
        JOIN entry_type et ON e.entry_type_id = et.id
        LEFT JOIN entry_label el ON e.id = el.entry_id
        LEFT JOIN label l ON el.label_id = l.id
        GROUP BY e.id
        ORDER BY e.timestamp DESC
    """)
    fun getAllWithDetails(): Flow<List<EntryWithDetails>>

    @Query("""
        SELECT e.id, e.entry_type_id AS entryTypeId, et.name AS entryTypeName, et.icon AS entryTypeIcon,
               e.source_type AS sourceType, e.timestamp, e.created_at AS createdAt,
               e.numeric_value AS numericValue, e.notes,
               GROUP_CONCAT(l.name, ', ') AS labelNames
        FROM entry e
        JOIN entry_type et ON e.entry_type_id = et.id
        LEFT JOIN entry_label el ON e.id = el.entry_id
        LEFT JOIN label l ON el.label_id = l.id
        WHERE e.entry_type_id = :entryTypeId
        GROUP BY e.id
        ORDER BY e.timestamp DESC
    """)
    fun getByTypeWithDetails(entryTypeId: Long): Flow<List<EntryWithDetails>>

    @Query("""
        SELECT COALESCE(SUM(e.numeric_value), 0)
        FROM entry e
        WHERE e.entry_type_id = :entryTypeId
        AND e.timestamp BETWEEN :dayStart AND :dayEnd
    """)
    fun getDailyTotal(entryTypeId: Long, dayStart: String, dayEnd: String): Flow<Double>
}
```

**Step 5: Create MeasurementTypeDao and CategoryDao (needed for seed data insertion)**

```kotlin
// MeasurementTypeDao.kt
package com.haven.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import com.haven.app.data.entity.MeasurementType

@Dao
interface MeasurementTypeDao {
    @Insert
    suspend fun insertAll(types: List<MeasurementType>)
}
```

```kotlin
// CategoryDao.kt
package com.haven.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import com.haven.app.data.entity.Category

@Dao
interface CategoryDao {
    @Insert
    suspend fun insertAll(categories: List<Category>)
}
```

**Step 6: Build**

```bash
./gradlew assembleDebug
```
Expected: BUILD SUCCESSFUL

**Step 7: Commit**

```bash
git add app/src/main/java/com/haven/app/data/dao/
git commit -m "feat: add DAOs for Phase 1 queries"
```

---

## Task 5: Database Class + Seed Data

Create the Room database with all entities registered and seed data delivered via `onCreate` callback.

**Files:**
- Create: `app/src/main/java/com/haven/app/data/HavenDatabase.kt`
- Create: `app/src/main/java/com/haven/app/data/SeedData.kt`

**Step 1: Create SeedData.kt**

This file contains all Phase 1 seed data as constants. Food labels include ~25 common foods with tags for dairy, gluten, FODMAP, etc.

```kotlin
package com.haven.app.data

import com.haven.app.data.entity.Category
import com.haven.app.data.entity.EntryType
import com.haven.app.data.entity.Label
import com.haven.app.data.entity.LabelTag
import com.haven.app.data.entity.MeasurementType
import com.haven.app.data.entity.Tag

object SeedData {

    val measurementTypes = listOf(
        MeasurementType(id = 1, name = "numeric", displayName = "Enter a number"),
        MeasurementType(id = 2, name = "label_select", displayName = "Select one or more"),
        MeasurementType(id = 3, name = "label_select_severity", displayName = "Select and rate severity"),
        MeasurementType(id = 4, name = "label_category_select", displayName = "Select by category"),
    )

    val categories = listOf(
        Category(id = 1, name = "Connect"),
        Category(id = 2, name = "Move"),
        Category(id = 3, name = "Reflect"),
        Category(id = 4, name = "Breathe"),
        Category(id = 5, name = "Nourish"),
        Category(id = 6, name = "Create"),
        Category(id = 7, name = "Ground"),
        Category(id = 8, name = "Structure"),
    )

    val entryTypes = listOf(
        EntryType(id = 1, name = "Food", measurementTypeId = 2, prompt = "What did you eat?", icon = "food", sortOrder = 1),
        EntryType(id = 2, name = "Emotion", measurementTypeId = 2, prompt = "How are you feeling?", icon = "emotion", sortOrder = 2),
        EntryType(id = 3, name = "Hydration", measurementTypeId = 1, prompt = "How much did you drink?", icon = "hydration", sortOrder = 3),
        EntryType(id = 4, name = "Sleep", measurementTypeId = 1, prompt = "How many hours did you sleep?", icon = "sleep", sortOrder = 4),
        EntryType(id = 5, name = "Symptom", measurementTypeId = 3, prompt = "What are you experiencing?", icon = "symptom", sortOrder = 5),
        EntryType(id = 6, name = "Activity", measurementTypeId = 4, prompt = "What did you do?", icon = "activity", sortOrder = 6),
    )

    // Food labels (entryTypeId = 1) — flat, no parents
    val foodLabels = listOf(
        Label(id = 1, entryTypeId = 1, name = "Cheese", sortOrder = 1),
        Label(id = 2, entryTypeId = 1, name = "Bread", sortOrder = 2),
        Label(id = 3, entryTypeId = 1, name = "Rice", sortOrder = 3),
        Label(id = 4, entryTypeId = 1, name = "Chicken", sortOrder = 4),
        Label(id = 5, entryTypeId = 1, name = "Eggs", sortOrder = 5),
        Label(id = 6, entryTypeId = 1, name = "Milk", sortOrder = 6),
        Label(id = 7, entryTypeId = 1, name = "Yogurt", sortOrder = 7),
        Label(id = 8, entryTypeId = 1, name = "Pasta", sortOrder = 8),
        Label(id = 9, entryTypeId = 1, name = "Beef", sortOrder = 9),
        Label(id = 10, entryTypeId = 1, name = "Fish", sortOrder = 10),
        Label(id = 11, entryTypeId = 1, name = "Tofu", sortOrder = 11),
        Label(id = 12, entryTypeId = 1, name = "Salad", sortOrder = 12),
        Label(id = 13, entryTypeId = 1, name = "Fruit", sortOrder = 13),
        Label(id = 14, entryTypeId = 1, name = "Vegetables", sortOrder = 14),
        Label(id = 15, entryTypeId = 1, name = "Coffee", sortOrder = 15),
        Label(id = 16, entryTypeId = 1, name = "Tea", sortOrder = 16),
        Label(id = 17, entryTypeId = 1, name = "Soup", sortOrder = 17),
        Label(id = 18, entryTypeId = 1, name = "Pizza", sortOrder = 18),
        Label(id = 19, entryTypeId = 1, name = "Chocolate", sortOrder = 19),
        Label(id = 20, entryTypeId = 1, name = "Ice Cream", sortOrder = 20),
        Label(id = 21, entryTypeId = 1, name = "Nuts", sortOrder = 21),
        Label(id = 22, entryTypeId = 1, name = "Beans", sortOrder = 22),
        Label(id = 23, entryTypeId = 1, name = "Oats", sortOrder = 23),
        Label(id = 24, entryTypeId = 1, name = "Soda", sortOrder = 24),
        Label(id = 25, entryTypeId = 1, name = "Alcohol", sortOrder = 25),
    )

    // Food tags
    val foodTags = listOf(
        Tag(id = 1, name = "dairy", tagGroup = "food"),
        Tag(id = 2, name = "gluten", tagGroup = "food"),
        Tag(id = 3, name = "FODMAP", tagGroup = "food"),
        Tag(id = 4, name = "caffeine", tagGroup = "food"),
        Tag(id = 5, name = "sugar", tagGroup = "food"),
        Tag(id = 6, name = "alcohol", tagGroup = "food"),
        Tag(id = 7, name = "processed", tagGroup = "food"),
        Tag(id = 8, name = "high protein", tagGroup = "food"),
    )

    // Food label → tag mappings
    val foodLabelTags = listOf(
        // Cheese → dairy, FODMAP
        LabelTag(labelId = 1, tagId = 1), LabelTag(labelId = 1, tagId = 3),
        // Bread → gluten
        LabelTag(labelId = 2, tagId = 2),
        // Chicken → high protein
        LabelTag(labelId = 4, tagId = 8),
        // Eggs → high protein
        LabelTag(labelId = 5, tagId = 8),
        // Milk → dairy, FODMAP
        LabelTag(labelId = 6, tagId = 1), LabelTag(labelId = 6, tagId = 3),
        // Yogurt → dairy, FODMAP
        LabelTag(labelId = 7, tagId = 1), LabelTag(labelId = 7, tagId = 3),
        // Pasta → gluten
        LabelTag(labelId = 8, tagId = 2),
        // Beef → high protein
        LabelTag(labelId = 9, tagId = 8),
        // Fish → high protein
        LabelTag(labelId = 10, tagId = 8),
        // Coffee → caffeine
        LabelTag(labelId = 15, tagId = 4),
        // Tea → caffeine
        LabelTag(labelId = 16, tagId = 4),
        // Pizza → gluten, dairy, processed
        LabelTag(labelId = 18, tagId = 2), LabelTag(labelId = 18, tagId = 1), LabelTag(labelId = 18, tagId = 7),
        // Chocolate → sugar, caffeine
        LabelTag(labelId = 19, tagId = 5), LabelTag(labelId = 19, tagId = 4),
        // Ice Cream → dairy, sugar
        LabelTag(labelId = 20, tagId = 1), LabelTag(labelId = 20, tagId = 5),
        // Beans → FODMAP, high protein
        LabelTag(labelId = 22, tagId = 3), LabelTag(labelId = 22, tagId = 8),
        // Oats → gluten (cross-contamination common)
        LabelTag(labelId = 23, tagId = 2),
        // Soda → sugar, caffeine
        LabelTag(labelId = 24, tagId = 5), LabelTag(labelId = 24, tagId = 4),
        // Alcohol → alcohol
        LabelTag(labelId = 25, tagId = 6),
    )
}
```

**Step 2: Create HavenDatabase.kt**

```kotlin
package com.haven.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.haven.app.data.dao.CategoryDao
import com.haven.app.data.dao.EntryDao
import com.haven.app.data.dao.EntryTypeDao
import com.haven.app.data.dao.LabelDao
import com.haven.app.data.dao.MeasurementTypeDao
import com.haven.app.data.dao.TagDao
import com.haven.app.data.entity.AnchorActivity
import com.haven.app.data.entity.AnchorTag
import com.haven.app.data.entity.Category
import com.haven.app.data.entity.Entry
import com.haven.app.data.entity.EntryLabel
import com.haven.app.data.entity.EntryType
import com.haven.app.data.entity.Label
import com.haven.app.data.entity.LabelTag
import com.haven.app.data.entity.MeasurementType
import com.haven.app.data.entity.Tag

@Database(
    entities = [
        MeasurementType::class,
        Category::class,
        EntryType::class,
        Label::class,
        Tag::class,
        LabelTag::class,
        Entry::class,
        EntryLabel::class,
        AnchorActivity::class,
        AnchorTag::class,
    ],
    version = 1,
    exportSchema = true
)
abstract class HavenDatabase : RoomDatabase() {
    abstract fun measurementTypeDao(): MeasurementTypeDao
    abstract fun categoryDao(): CategoryDao
    abstract fun entryTypeDao(): EntryTypeDao
    abstract fun labelDao(): LabelDao
    abstract fun tagDao(): TagDao
    abstract fun entryDao(): EntryDao

    companion object {
        const val NAME = "haven_db"
    }
}
```

**Step 3: Build**

```bash
./gradlew assembleDebug
```
Expected: BUILD SUCCESSFUL

**Step 4: Commit**

```bash
git add app/src/main/java/com/haven/app/data/HavenDatabase.kt app/src/main/java/com/haven/app/data/SeedData.kt
git commit -m "feat: add Room database class and Phase 1 seed data"
```

---

## Task 6: Hilt DI Module

Wire the database, DAOs, and repositories into Hilt's dependency graph. Create the seed data callback that runs on first launch.

**Files:**
- Create: `app/src/main/java/com/haven/app/di/DatabaseModule.kt`
- Create: `app/src/main/java/com/haven/app/data/SeedDatabaseCallback.kt`

**Step 1: Create SeedDatabaseCallback**

```kotlin
package com.haven.app.data

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider

class SeedDatabaseCallback(
    private val databaseProvider: Provider<HavenDatabase>
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            val database = databaseProvider.get()
            database.measurementTypeDao().insertAll(SeedData.measurementTypes)
            database.categoryDao().insertAll(SeedData.categories)
            database.entryTypeDao().insertAll(SeedData.entryTypes)
            database.labelDao().insertAll(SeedData.foodLabels)
            database.tagDao().insertAll(SeedData.foodTags)
            database.tagDao().insertLabelTags(SeedData.foodLabelTags)
        }
    }
}
```

**Step 2: Create DatabaseModule**

```kotlin
package com.haven.app.di

import android.content.Context
import androidx.room.Room
import com.haven.app.data.HavenDatabase
import com.haven.app.data.SeedDatabaseCallback
import com.haven.app.data.dao.CategoryDao
import com.haven.app.data.dao.EntryDao
import com.haven.app.data.dao.EntryTypeDao
import com.haven.app.data.dao.LabelDao
import com.haven.app.data.dao.MeasurementTypeDao
import com.haven.app.data.dao.TagDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        databaseProvider: Provider<HavenDatabase>
    ): HavenDatabase {
        return Room.databaseBuilder(
            context,
            HavenDatabase::class.java,
            HavenDatabase.NAME
        )
            .addCallback(SeedDatabaseCallback(databaseProvider))
            .build()
    }

    @Provides
    fun provideMeasurementTypeDao(db: HavenDatabase): MeasurementTypeDao = db.measurementTypeDao()

    @Provides
    fun provideCategoryDao(db: HavenDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideEntryTypeDao(db: HavenDatabase): EntryTypeDao = db.entryTypeDao()

    @Provides
    fun provideLabelDao(db: HavenDatabase): LabelDao = db.labelDao()

    @Provides
    fun provideTagDao(db: HavenDatabase): TagDao = db.tagDao()

    @Provides
    fun provideEntryDao(db: HavenDatabase): EntryDao = db.entryDao()
}
```

**Step 3: Build**

```bash
./gradlew assembleDebug
```
Expected: BUILD SUCCESSFUL

**Step 4: Commit**

```bash
git add app/src/main/java/com/haven/app/di/ app/src/main/java/com/haven/app/data/SeedDatabaseCallback.kt
git commit -m "feat: add Hilt DI module and seed data callback"
```

---

## Task 7: Repositories

Create repository classes that wrap DAOs. ViewModels will depend on these, never on DAOs directly.

**Files (under `app/src/main/java/com/haven/app/data/repository/`):**
- Create: `EntryTypeRepository.kt`
- Create: `LabelRepository.kt`
- Create: `EntryRepository.kt`

**Step 1: EntryTypeRepository**

```kotlin
package com.haven.app.data.repository

import com.haven.app.data.dao.EntryTypeDao
import com.haven.app.data.entity.EntryType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntryTypeRepository @Inject constructor(
    private val entryTypeDao: EntryTypeDao
) {
    fun getEnabled(): Flow<List<EntryType>> = entryTypeDao.getEnabled()

    suspend fun getById(id: Long): EntryType? = entryTypeDao.getById(id)
}
```

**Step 2: LabelRepository**

```kotlin
package com.haven.app.data.repository

import com.haven.app.data.dao.LabelDao
import com.haven.app.data.entity.Label
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LabelRepository @Inject constructor(
    private val labelDao: LabelDao
) {
    fun getByEntryType(entryTypeId: Long): Flow<List<Label>> = labelDao.getByEntryType(entryTypeId)

    fun getTopLevel(entryTypeId: Long): Flow<List<Label>> = labelDao.getTopLevel(entryTypeId)

    fun getChildren(parentId: Long): Flow<List<Label>> = labelDao.getChildren(parentId)
}
```

**Step 3: EntryRepository**

```kotlin
package com.haven.app.data.repository

import com.haven.app.data.dao.EntryDao
import com.haven.app.data.dao.EntryWithDetails
import com.haven.app.data.entity.Entry
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntryRepository @Inject constructor(
    private val entryDao: EntryDao
) {
    suspend fun insertWithLabels(entry: Entry, labelIds: List<Long>): Long =
        entryDao.insertWithLabels(entry, labelIds)

    fun getAllWithDetails(): Flow<List<EntryWithDetails>> = entryDao.getAllWithDetails()

    fun getByTypeWithDetails(entryTypeId: Long): Flow<List<EntryWithDetails>> =
        entryDao.getByTypeWithDetails(entryTypeId)

    fun getDailyTotal(entryTypeId: Long, dayStart: String, dayEnd: String): Flow<Double> =
        entryDao.getDailyTotal(entryTypeId, dayStart, dayEnd)
}
```

**Step 4: Build**

```bash
./gradlew assembleDebug
```
Expected: BUILD SUCCESSFUL

**Step 5: Commit**

```bash
git add app/src/main/java/com/haven/app/data/repository/
git commit -m "feat: add repository layer wrapping DAOs"
```

---

## Task 8: Bottom Navigation Shell

Replace the placeholder MainActivity with a working bottom nav hosting 5 destinations. Tend and Trace get placeholder screens. Weave, Anchor, and Settings show "Coming Soon" text.

**Files:**
- Modify: `app/src/main/java/com/haven/app/ui/MainActivity.kt`
- Create: `app/src/main/java/com/haven/app/ui/navigation/HavenNavigation.kt`
- Create: `app/src/main/java/com/haven/app/ui/tend/TendScreen.kt`
- Create: `app/src/main/java/com/haven/app/ui/trace/TraceScreen.kt`
- Create: `app/src/main/java/com/haven/app/ui/placeholder/PlaceholderScreen.kt`

**Step 1: Create navigation definition**

```kotlin
package com.haven.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Anchor
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class HavenDestination(
    val label: String,
    val icon: ImageVector,
    val route: String
) {
    Tend("Tend", Icons.Rounded.Favorite, "tend"),
    Trace("Trace", Icons.Rounded.History, "trace"),
    Weave("Weave", Icons.Rounded.Insights, "weave"),
    Anchor("Anchor", Icons.Rounded.Anchor, "anchor"),
    Settings("Settings", Icons.Rounded.Settings, "settings"),
}
```

**Step 2: Create placeholder screen**

```kotlin
package com.haven.app.ui.placeholder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$title — coming soon",
            style = MaterialTheme.typography.titleMedium
        )
    }
}
```

**Step 3: Create stub TendScreen**

```kotlin
package com.haven.app.ui.tend

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun TendScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Tend")
    }
}
```

**Step 4: Create stub TraceScreen**

```kotlin
package com.haven.app.ui.trace

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun TraceScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Trace")
    }
}
```

**Step 5: Update MainActivity with bottom nav**

```kotlin
package com.haven.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.haven.app.ui.navigation.HavenDestination
import com.haven.app.ui.placeholder.PlaceholderScreen
import com.haven.app.ui.tend.TendScreen
import com.haven.app.ui.trace.TraceScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                HavenApp()
            }
        }
    }
}

@Composable
fun HavenApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                HavenDestination.entries.forEach { destination ->
                    NavigationBarItem(
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HavenDestination.Tend.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(HavenDestination.Tend.route) { TendScreen() }
            composable(HavenDestination.Trace.route) { TraceScreen() }
            composable(HavenDestination.Weave.route) { PlaceholderScreen("Weave") }
            composable(HavenDestination.Anchor.route) { PlaceholderScreen("Anchor") }
            composable(HavenDestination.Settings.route) { PlaceholderScreen("Settings") }
        }
    }
}
```

**Step 6: Build**

```bash
./gradlew assembleDebug
```
Expected: BUILD SUCCESSFUL

**Step 7: Commit**

```bash
git add app/src/main/java/com/haven/app/ui/
git commit -m "feat: add bottom navigation shell with 5 destinations"
```

---

## Task 9: Instrumented DAO Tests

Write instrumented tests verifying the database layer works end-to-end: seed data populates, entries can be created, labels attach correctly, and the daily total query works.

**Files:**
- Create: `app/src/androidTest/java/com/haven/app/HiltTestRunner.kt`
- Create: `app/src/androidTest/java/com/haven/app/data/dao/EntryTypeDaoTest.kt`
- Create: `app/src/androidTest/java/com/haven/app/data/dao/EntryDaoTest.kt`
- Create: `app/src/androidTest/java/com/haven/app/data/dao/LabelDaoTest.kt`

**Step 1: Create HiltTestRunner**

```kotlin
package com.haven.app

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
```

**Step 2: Create a base test helper**

Create `app/src/androidTest/java/com/haven/app/data/dao/BaseDaoTest.kt`:

```kotlin
package com.haven.app.data.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.haven.app.data.HavenDatabase
import com.haven.app.data.SeedData
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before

abstract class BaseDaoTest {
    protected lateinit var db: HavenDatabase

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            HavenDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    protected suspend fun seedPhase1Data() {
        db.measurementTypeDao().insertAll(SeedData.measurementTypes)
        db.categoryDao().insertAll(SeedData.categories)
        db.entryTypeDao().insertAll(SeedData.entryTypes)
        db.labelDao().insertAll(SeedData.foodLabels)
        db.tagDao().insertAll(SeedData.foodTags)
        db.tagDao().insertLabelTags(SeedData.foodLabelTags)
    }
}
```

**Step 3: Create EntryTypeDaoTest**

```kotlin
package com.haven.app.data.dao

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class EntryTypeDaoTest : BaseDaoTest() {

    @Test
    fun getEnabled_returnsAllSixEntryTypes_orderedBySortOrder() = runTest {
        seedPhase1Data()
        db.entryTypeDao().getEnabled().test {
            val types = awaitItem()
            assertEquals(6, types.size)
            assertEquals("Food", types[0].name)
            assertEquals("Activity", types[5].name)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun getById_returnsCorrectEntryType() = runTest {
        seedPhase1Data()
        val food = db.entryTypeDao().getById(1)
        assertEquals("Food", food?.name)
        assertEquals("What did you eat?", food?.prompt)
    }
}
```

**Step 4: Create LabelDaoTest**

```kotlin
package com.haven.app.data.dao

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class LabelDaoTest : BaseDaoTest() {

    @Test
    fun getByEntryType_returnsFoodLabels() = runTest {
        seedPhase1Data()
        db.labelDao().getByEntryType(1).test {
            val labels = awaitItem()
            assertEquals(25, labels.size)
            assertEquals("Cheese", labels[0].name)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun getByEntryType_returnsEmpty_forTypeWithNoLabelsYet() = runTest {
        seedPhase1Data()
        // Sleep (id=4) has no labels — it's numeric only
        db.labelDao().getByEntryType(4).test {
            val labels = awaitItem()
            assertEquals(0, labels.size)
            cancelAndConsumeRemainingEvents()
        }
    }
}
```

**Step 5: Create EntryDaoTest**

```kotlin
package com.haven.app.data.dao

import app.cash.turbine.test
import com.haven.app.data.entity.Entry
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EntryDaoTest : BaseDaoTest() {

    @Test
    fun insertWithLabels_createsEntryAndLinks() = runTest {
        seedPhase1Data()
        val entry = Entry(
            entryTypeId = 1, // Food
            timestamp = "2026-02-11T12:00:00Z",
            createdAt = "2026-02-11T12:00:05Z"
        )
        val entryId = db.entryDao().insertWithLabels(entry, listOf(1L, 2L)) // Cheese, Bread

        db.entryDao().getAllWithDetails().test {
            val entries = awaitItem()
            assertEquals(1, entries.size)
            assertEquals("Food", entries[0].entryTypeName)
            assertTrue(entries[0].labelNames?.contains("Cheese") == true)
            assertTrue(entries[0].labelNames?.contains("Bread") == true)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun insertNumericEntry_forSleep() = runTest {
        seedPhase1Data()
        val entry = Entry(
            entryTypeId = 4, // Sleep
            timestamp = "2026-02-11T07:00:00Z",
            createdAt = "2026-02-11T07:00:00Z",
            numericValue = 7.5
        )
        db.entryDao().insert(entry)

        db.entryDao().getAllWithDetails().test {
            val entries = awaitItem()
            assertEquals(1, entries.size)
            assertEquals("Sleep", entries[0].entryTypeName)
            assertEquals(7.5, entries[0].numericValue!!, 0.01)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun getDailyTotal_sumsHydrationEntries() = runTest {
        seedPhase1Data()
        // Insert two hydration entries
        db.entryDao().insert(Entry(
            entryTypeId = 3, timestamp = "2026-02-11T08:00:00Z",
            createdAt = "2026-02-11T08:00:00Z", numericValue = 8.0
        ))
        db.entryDao().insert(Entry(
            entryTypeId = 3, timestamp = "2026-02-11T12:00:00Z",
            createdAt = "2026-02-11T12:00:00Z", numericValue = 16.0
        ))

        db.entryDao().getDailyTotal(3, "2026-02-11T00:00:00Z", "2026-02-11T23:59:59Z").test {
            val total = awaitItem()
            assertEquals(24.0, total, 0.01)
            cancelAndConsumeRemainingEvents()
        }
    }
}
```

**Step 6: Verify tests compile**

```bash
./gradlew assembleDebugAndroidTest
```
Expected: BUILD SUCCESSFUL

Note: Running the tests requires `./gradlew connectedAndroidTest` with an emulator or device connected. Verify they compile; running them is optional if no device is available.

**Step 7: Commit**

```bash
git add app/src/androidTest/
git commit -m "test: add instrumented DAO tests for Phase 1 data layer"
```

---

## Task 10: Schema Export + Final Verification

Configure Room to export the schema JSON (useful for migration testing later) and do a final full build.

**Step 1: Add Room schema export to build.gradle.kts**

Add KSP arguments to `app/build.gradle.kts` inside the `ksp` block:

```kotlin
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
```

Add this after the `android { }` block in `app/build.gradle.kts`.

**Step 2: Add schemas directory to .gitignore exclusion**

The exported schema JSON should be committed (useful for migration testing). No .gitignore change needed since `schemas/` is not in the ignore list.

**Step 3: Full build**

```bash
./gradlew assembleDebug
```
Expected: BUILD SUCCESSFUL + `app/schemas/com.haven.app.data.HavenDatabase/1.json` generated

**Step 4: Run lint**

```bash
./gradlew lint
```
Expected: No errors (warnings acceptable)

**Step 5: Commit**

```bash
git add app/build.gradle.kts app/schemas/
git commit -m "chore: enable Room schema export"
```

---

## Task 11: Update Docs + PR

Update changelog, clean up roadmap, and create the PR.

**Step 1: Update docs/changelog.md**

Add under Phase 1:
```markdown
## Phase 1 — "I can log and see my logs"

### PR 1: Project Scaffolding + Database Layer
- **2026-02-XX** — Android project scaffolded with Kotlin 2.0, Compose, Room, Hilt (#X)
  - All 10 Room entities matching schema.sql
  - DAOs for entry creation, label lookup, date queries, daily totals
  - Repositories wrapping all DAOs
  - Phase 1 seed data: 6 entry types, 25 food labels, 8 food tags with mappings
  - Hilt DI wiring with seed data callback on first launch
  - Bottom nav shell with 5 destinations (Tend/Trace active, 3 placeholders)
  - Instrumented DAO tests
```

**Step 2: Commit docs**

```bash
git add docs/changelog.md
git commit -m "docs: update changelog for PR 1"
```

**Step 3: Create PR**

```bash
gh pr create --title "Phase 1 PR 1: project scaffolding + database layer" --body "..."
```

---

## Summary

| Task | What | Key Files |
|------|------|-----------|
| 1 | Gradle project structure | `gradle/libs.versions.toml`, `build.gradle.kts`, `app/build.gradle.kts` |
| 2 | Manifest + Application class | `AndroidManifest.xml`, `HavenApp.kt`, `MainActivity.kt` |
| 3 | Room entities (all 10) | `data/entity/*.kt` |
| 4 | DAOs (Phase 1) | `data/dao/*.kt` |
| 5 | Database class + seed data | `HavenDatabase.kt`, `SeedData.kt` |
| 6 | Hilt DI module | `di/DatabaseModule.kt`, `SeedDatabaseCallback.kt` |
| 7 | Repositories | `data/repository/*.kt` |
| 8 | Bottom nav shell | `MainActivity.kt`, `navigation/`, screen stubs |
| 9 | Instrumented tests | `androidTest/` |
| 10 | Schema export + verification | `build.gradle.kts` |
| 11 | Docs + PR | `changelog.md` |
