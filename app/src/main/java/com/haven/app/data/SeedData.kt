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
        LabelTag(labelId = 23, tagId = 2),                                      // Oats -> gluten
        LabelTag(labelId = 24, tagId = 5), LabelTag(labelId = 24, tagId = 4), // Soda -> sugar, caffeine
        LabelTag(labelId = 25, tagId = 6),                                      // Alcohol -> alcohol
    )
}
