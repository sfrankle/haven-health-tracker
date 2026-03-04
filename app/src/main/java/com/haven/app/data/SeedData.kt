package com.haven.app.data

import com.haven.app.data.entity.Category
import com.haven.app.data.entity.EntryType
import com.haven.app.data.entity.EntryTypeEntity
import com.haven.app.data.entity.Label
import com.haven.app.data.entity.LabelTag
import com.haven.app.data.entity.MeasurementType
import com.haven.app.data.entity.Tag

object SeedData {

    const val VERSION = 3

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
        EntryTypeEntity(id = 1, name = "Food", measurementTypeId = 2, prompt = "What did you eat?", icon = EntryType.FOOD, sortOrder = 1),
        EntryTypeEntity(id = 2, name = "Emotion", measurementTypeId = 2, prompt = "How are you feeling?", icon = EntryType.EMOTION, sortOrder = 2),
        EntryTypeEntity(id = 3, name = "Hydration", measurementTypeId = 1, prompt = "How much did you drink?", icon = EntryType.HYDRATION, sortOrder = 3),
        EntryTypeEntity(id = 4, name = "Sleep", measurementTypeId = 1, prompt = "How many hours did you sleep?", icon = EntryType.SLEEP, sortOrder = 4),
        EntryTypeEntity(id = 5, name = "Physical State", measurementTypeId = 3, prompt = "What are you experiencing?", icon = EntryType.SYMPTOM, sortOrder = 5),
        EntryTypeEntity(id = 6, name = "Activity", measurementTypeId = 4, prompt = "What did you do?", icon = EntryType.ACTIVITY, sortOrder = 6),
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

    // Meal source labels (entryTypeId = 1) — parent + children
    val mealSourceLabel = Label(id = 26, entryTypeId = 1, name = "Meal Source", sortOrder = 100)
    val mealSourceChildren = listOf(
        Label(id = 27, entryTypeId = 1, name = "Home Cooked", parentId = 26, sortOrder = 1),
        Label(id = 28, entryTypeId = 1, name = "Eating Out", parentId = 26, sortOrder = 2),
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
        Tag(id = 9, name = "meal_source", tagGroup = "food"),
    )

    // Food label -> tag mappings
    val foodLabelTags = listOf(
        LabelTag(labelId = 1, tagId = 1), LabelTag(labelId = 1, tagId = 3),   // Cheese -> dairy, FODMAP
        LabelTag(labelId = 2, tagId = 2),                                       // Bread -> gluten
        LabelTag(labelId = 4, tagId = 8),                                       // Chicken -> high protein
        LabelTag(labelId = 5, tagId = 8),                                       // Eggs -> high protein
        LabelTag(labelId = 6, tagId = 1), LabelTag(labelId = 6, tagId = 3),   // Milk -> dairy, FODMAP
        LabelTag(labelId = 7, tagId = 1), LabelTag(labelId = 7, tagId = 3),   // Yogurt -> dairy, FODMAP
        LabelTag(labelId = 8, tagId = 2),                                       // Pasta -> gluten
        LabelTag(labelId = 9, tagId = 8),                                       // Beef -> high protein
        LabelTag(labelId = 10, tagId = 8),                                      // Fish -> high protein
        LabelTag(labelId = 15, tagId = 4),                                      // Coffee -> caffeine
        LabelTag(labelId = 16, tagId = 4),                                      // Tea -> caffeine
        LabelTag(labelId = 18, tagId = 2), LabelTag(labelId = 18, tagId = 1), LabelTag(labelId = 18, tagId = 7), // Pizza -> gluten, dairy, processed
        LabelTag(labelId = 19, tagId = 5), LabelTag(labelId = 19, tagId = 4), // Chocolate -> sugar, caffeine
        LabelTag(labelId = 20, tagId = 1), LabelTag(labelId = 20, tagId = 5), // Ice Cream -> dairy, sugar
        LabelTag(labelId = 22, tagId = 3), LabelTag(labelId = 22, tagId = 8), // Beans -> FODMAP, high protein
        LabelTag(labelId = 23, tagId = 3),                                      // Oats -> FODMAP
        LabelTag(labelId = 24, tagId = 5), LabelTag(labelId = 24, tagId = 4), // Soda -> sugar, caffeine
        LabelTag(labelId = 25, tagId = 6),                                      // Alcohol -> alcohol
        LabelTag(labelId = 27, tagId = 9),                                      // Home Cooked -> meal_source
        LabelTag(labelId = 28, tagId = 9),                                      // Eating Out -> meal_source
    )

    // -------------------------------------------------------------------------
    // Emotion labels (entryTypeId = 2) — nested: valence parent → emotion child
    // -------------------------------------------------------------------------

    val emotionParentLabels = listOf(
        Label(id = 29, entryTypeId = 2, name = "Pleasant", sortOrder = 1, seedVersion = 3),
        Label(id = 30, entryTypeId = 2, name = "Neutral", sortOrder = 2, seedVersion = 3),
        Label(id = 31, entryTypeId = 2, name = "Unpleasant", sortOrder = 3, seedVersion = 3),
    )

    val emotionChildLabels = listOf(
        // Pleasant (parentId = 29)
        Label(id = 32, entryTypeId = 2, name = "Happy", parentId = 29, sortOrder = 1, seedVersion = 3),
        Label(id = 33, entryTypeId = 2, name = "Content", parentId = 29, sortOrder = 2, seedVersion = 3),
        Label(id = 34, entryTypeId = 2, name = "Hopeful", parentId = 29, sortOrder = 3, seedVersion = 3),
        Label(id = 35, entryTypeId = 2, name = "Excited", parentId = 29, sortOrder = 4, seedVersion = 3),
        Label(id = 36, entryTypeId = 2, name = "Energised", parentId = 29, sortOrder = 5, seedVersion = 3),
        Label(id = 37, entryTypeId = 2, name = "Peaceful", parentId = 29, sortOrder = 6, seedVersion = 3),
        Label(id = 38, entryTypeId = 2, name = "Playful", parentId = 29, sortOrder = 7, seedVersion = 3),
        Label(id = 39, entryTypeId = 2, name = "Proud", parentId = 29, sortOrder = 8, seedVersion = 3),
        Label(id = 40, entryTypeId = 2, name = "Trusting", parentId = 29, sortOrder = 9, seedVersion = 3),
        // Neutral (parentId = 30)
        Label(id = 41, entryTypeId = 2, name = "Calm", parentId = 30, sortOrder = 1, seedVersion = 3),
        Label(id = 42, entryTypeId = 2, name = "Balanced", parentId = 30, sortOrder = 2, seedVersion = 3),
        Label(id = 43, entryTypeId = 2, name = "Comfortable", parentId = 30, sortOrder = 3, seedVersion = 3),
        Label(id = 44, entryTypeId = 2, name = "Thoughtful", parentId = 30, sortOrder = 4, seedVersion = 3),
        Label(id = 45, entryTypeId = 2, name = "Mellow", parentId = 30, sortOrder = 5, seedVersion = 3),
        Label(id = 46, entryTypeId = 2, name = "Fulfilled", parentId = 30, sortOrder = 6, seedVersion = 3),
        // Unpleasant (parentId = 31)
        Label(id = 47, entryTypeId = 2, name = "Anxious", parentId = 31, sortOrder = 1, seedVersion = 3),
        Label(id = 48, entryTypeId = 2, name = "Overwhelmed", parentId = 31, sortOrder = 2, seedVersion = 3),
        Label(id = 49, entryTypeId = 2, name = "Irritable", parentId = 31, sortOrder = 3, seedVersion = 3),
        Label(id = 50, entryTypeId = 2, name = "Sad", parentId = 31, sortOrder = 4, seedVersion = 3),
        Label(id = 51, entryTypeId = 2, name = "Frustrated", parentId = 31, sortOrder = 5, seedVersion = 3),
        Label(id = 52, entryTypeId = 2, name = "Angry", parentId = 31, sortOrder = 6, seedVersion = 3),
        Label(id = 53, entryTypeId = 2, name = "Fearful", parentId = 31, sortOrder = 7, seedVersion = 3),
        Label(id = 54, entryTypeId = 2, name = "Down", parentId = 31, sortOrder = 8, seedVersion = 3),
    )

    // -------------------------------------------------------------------------
    // Physical State labels (entryTypeId = 5) — flat
    // Includes both difficult states and positive/neutral states
    // -------------------------------------------------------------------------

    val physicalStateLabels = listOf(
        // Difficult states
        Label(id = 55, entryTypeId = 5, name = "Headache", sortOrder = 1, seedVersion = 3),
        Label(id = 56, entryTypeId = 5, name = "Fatigue", sortOrder = 2, seedVersion = 3),
        Label(id = 57, entryTypeId = 5, name = "Nausea", sortOrder = 3, seedVersion = 3),
        Label(id = 58, entryTypeId = 5, name = "Bloating", sortOrder = 4, seedVersion = 3),
        Label(id = 59, entryTypeId = 5, name = "Cramps", sortOrder = 5, seedVersion = 3),
        Label(id = 60, entryTypeId = 5, name = "Brain fog", sortOrder = 6, seedVersion = 3),
        Label(id = 61, entryTypeId = 5, name = "Back pain", sortOrder = 7, seedVersion = 3),
        Label(id = 62, entryTypeId = 5, name = "Joint pain", sortOrder = 8, seedVersion = 3),
        Label(id = 63, entryTypeId = 5, name = "Sore throat", sortOrder = 9, seedVersion = 3),
        Label(id = 64, entryTypeId = 5, name = "Congestion", sortOrder = 10, seedVersion = 3),
        Label(id = 65, entryTypeId = 5, name = "Low energy", sortOrder = 11, seedVersion = 3),
        // Positive/neutral states
        Label(id = 66, entryTypeId = 5, name = "Settled stomach", sortOrder = 12, seedVersion = 3),
        Label(id = 67, entryTypeId = 5, name = "High energy", sortOrder = 13, seedVersion = 3),
        Label(id = 68, entryTypeId = 5, name = "Clear-headed", sortOrder = 14, seedVersion = 3),
        Label(id = 69, entryTypeId = 5, name = "Pain-free", sortOrder = 15, seedVersion = 3),
        Label(id = 70, entryTypeId = 5, name = "Well-rested", sortOrder = 16, seedVersion = 3),
    )

    // -------------------------------------------------------------------------
    // Activity labels (entryTypeId = 6) — flat with categoryId
    // -------------------------------------------------------------------------

    val activityLabels = listOf(
        Label(id = 71, entryTypeId = 6, name = "Walk", categoryId = 2, sortOrder = 1, seedVersion = 3),
        Label(id = 72, entryTypeId = 6, name = "Run", categoryId = 2, sortOrder = 2, seedVersion = 3),
        Label(id = 73, entryTypeId = 6, name = "Cycling", categoryId = 2, sortOrder = 3, seedVersion = 3),
        Label(id = 74, entryTypeId = 6, name = "Swimming", categoryId = 2, sortOrder = 4, seedVersion = 3),
        Label(id = 75, entryTypeId = 6, name = "Strength training", categoryId = 2, sortOrder = 5, seedVersion = 3),
        Label(id = 76, entryTypeId = 6, name = "Yoga", categoryId = 2, sortOrder = 6, seedVersion = 3),
        Label(id = 77, entryTypeId = 6, name = "Stretching", categoryId = 2, sortOrder = 7, seedVersion = 3),
        Label(id = 78, entryTypeId = 6, name = "Meditation", categoryId = 3, sortOrder = 8, seedVersion = 3),
        Label(id = 79, entryTypeId = 6, name = "Deep breathing", categoryId = 4, sortOrder = 9, seedVersion = 3),
        Label(id = 80, entryTypeId = 6, name = "Gardening", categoryId = 7, sortOrder = 10, seedVersion = 3),
        Label(id = 81, entryTypeId = 6, name = "Housework", categoryId = 8, sortOrder = 11, seedVersion = 3),
    )

    // -------------------------------------------------------------------------
    // Emotion tags (tag_group = "emotion")
    // -------------------------------------------------------------------------

    val emotionTags = listOf(
        Tag(id = 10, name = "nervous-system", tagGroup = "emotion", seedVersion = 3),
        Tag(id = 11, name = "high-arousal", tagGroup = "emotion", seedVersion = 3),
        Tag(id = 12, name = "low-arousal", tagGroup = "emotion", seedVersion = 3),
    )

    val emotionLabelTags = listOf(
        LabelTag(labelId = 47, tagId = 10), LabelTag(labelId = 47, tagId = 11), // Anxious -> nervous-system, high-arousal
        LabelTag(labelId = 48, tagId = 10), LabelTag(labelId = 48, tagId = 11), // Overwhelmed -> nervous-system, high-arousal
        LabelTag(labelId = 49, tagId = 11),                                       // Irritable -> high-arousal
        LabelTag(labelId = 50, tagId = 12),                                       // Sad -> low-arousal
        LabelTag(labelId = 52, tagId = 11),                                       // Angry -> high-arousal
        LabelTag(labelId = 54, tagId = 12),                                       // Down -> low-arousal
        LabelTag(labelId = 35, tagId = 11),                                       // Excited -> high-arousal
        LabelTag(labelId = 36, tagId = 11),                                       // Energised -> high-arousal
        LabelTag(labelId = 37, tagId = 10), LabelTag(labelId = 37, tagId = 12), // Peaceful -> nervous-system, low-arousal
        LabelTag(labelId = 41, tagId = 10), LabelTag(labelId = 41, tagId = 12), // Calm -> nervous-system, low-arousal
        LabelTag(labelId = 42, tagId = 10), LabelTag(labelId = 42, tagId = 12), // Balanced -> nervous-system, low-arousal
        LabelTag(labelId = 43, tagId = 12),                                       // Comfortable -> low-arousal
        LabelTag(labelId = 45, tagId = 12),                                       // Mellow -> low-arousal
    )

    // -------------------------------------------------------------------------
    // Physical State tags (tag_group = "symptom")
    // -------------------------------------------------------------------------

    val symptomTags = listOf(
        Tag(id = 13, name = "pain", tagGroup = "symptom", seedVersion = 3),
        Tag(id = 14, name = "energy", tagGroup = "symptom", seedVersion = 3),
        Tag(id = 15, name = "nervous-system", tagGroup = "symptom", seedVersion = 3),
        Tag(id = 16, name = "gut", tagGroup = "symptom", seedVersion = 3),
        Tag(id = 17, name = "digestive", tagGroup = "symptom", seedVersion = 3),
        Tag(id = 18, name = "FODMAP", tagGroup = "symptom", seedVersion = 3),
        Tag(id = 19, name = "hormonal", tagGroup = "symptom", seedVersion = 3),
        Tag(id = 20, name = "musculoskeletal", tagGroup = "symptom", seedVersion = 3),
        Tag(id = 21, name = "immune", tagGroup = "symptom", seedVersion = 3),
        Tag(id = 22, name = "head", tagGroup = "symptom", seedVersion = 3),
    )

    val physicalStateLabelTags = listOf(
        LabelTag(labelId = 55, tagId = 22), LabelTag(labelId = 55, tagId = 13),                               // Headache -> head, pain
        LabelTag(labelId = 56, tagId = 14), LabelTag(labelId = 56, tagId = 15),                               // Fatigue -> energy, nervous-system
        LabelTag(labelId = 57, tagId = 16), LabelTag(labelId = 57, tagId = 17),                               // Nausea -> gut, digestive
        LabelTag(labelId = 58, tagId = 16), LabelTag(labelId = 58, tagId = 17), LabelTag(labelId = 58, tagId = 18), // Bloating -> gut, digestive, FODMAP
        LabelTag(labelId = 59, tagId = 16), LabelTag(labelId = 59, tagId = 17), LabelTag(labelId = 59, tagId = 19), LabelTag(labelId = 59, tagId = 13), // Cramps -> gut, digestive, hormonal, pain
        LabelTag(labelId = 60, tagId = 14), LabelTag(labelId = 60, tagId = 15),                               // Brain fog -> energy, nervous-system
        LabelTag(labelId = 61, tagId = 20), LabelTag(labelId = 61, tagId = 13),                               // Back pain -> musculoskeletal, pain
        LabelTag(labelId = 62, tagId = 20), LabelTag(labelId = 62, tagId = 13),                               // Joint pain -> musculoskeletal, pain
        LabelTag(labelId = 63, tagId = 21),                                                                    // Sore throat -> immune
        LabelTag(labelId = 64, tagId = 21),                                                                    // Congestion -> immune
        LabelTag(labelId = 65, tagId = 14), LabelTag(labelId = 65, tagId = 15),                               // Low energy -> energy, nervous-system
        LabelTag(labelId = 66, tagId = 16), LabelTag(labelId = 66, tagId = 17),                               // Settled stomach -> gut, digestive
        LabelTag(labelId = 67, tagId = 14), LabelTag(labelId = 67, tagId = 15),                               // High energy -> energy, nervous-system
        LabelTag(labelId = 68, tagId = 14), LabelTag(labelId = 68, tagId = 15),                               // Clear-headed -> energy, nervous-system
        LabelTag(labelId = 69, tagId = 13),                                                                    // Pain-free -> pain
        LabelTag(labelId = 70, tagId = 14),                                                                    // Well-rested -> energy
    )

    // -------------------------------------------------------------------------
    // Activity tags (tag_group = "activity")
    // -------------------------------------------------------------------------

    val activityTags = listOf(
        Tag(id = 23, name = "cardio", tagGroup = "activity", seedVersion = 3),
        Tag(id = 24, name = "high-intensity", tagGroup = "activity", seedVersion = 3),
        Tag(id = 25, name = "low-intensity", tagGroup = "activity", seedVersion = 3),
        Tag(id = 26, name = "strength", tagGroup = "activity", seedVersion = 3),
        Tag(id = 27, name = "flexibility", tagGroup = "activity", seedVersion = 3),
        Tag(id = 28, name = "grounding", tagGroup = "activity", seedVersion = 3),
    )

    val activityLabelTags = listOf(
        LabelTag(labelId = 71, tagId = 23), LabelTag(labelId = 71, tagId = 25), // Walk -> cardio, low-intensity
        LabelTag(labelId = 72, tagId = 23), LabelTag(labelId = 72, tagId = 24), // Run -> cardio, high-intensity
        LabelTag(labelId = 73, tagId = 23), LabelTag(labelId = 73, tagId = 24), // Cycling -> cardio, high-intensity
        LabelTag(labelId = 74, tagId = 23), LabelTag(labelId = 74, tagId = 24), // Swimming -> cardio, high-intensity
        LabelTag(labelId = 75, tagId = 26), LabelTag(labelId = 75, tagId = 24), // Strength training -> strength, high-intensity
        LabelTag(labelId = 76, tagId = 27), LabelTag(labelId = 76, tagId = 28), // Yoga -> flexibility, grounding
        LabelTag(labelId = 77, tagId = 27), LabelTag(labelId = 77, tagId = 25), // Stretching -> flexibility, low-intensity
        LabelTag(labelId = 78, tagId = 28),                                       // Meditation -> grounding
        LabelTag(labelId = 79, tagId = 28),                                       // Deep breathing -> grounding
        LabelTag(labelId = 80, tagId = 28), LabelTag(labelId = 80, tagId = 25), // Gardening -> grounding, low-intensity
        LabelTag(labelId = 81, tagId = 25),                                       // Housework -> low-intensity
    )
}
