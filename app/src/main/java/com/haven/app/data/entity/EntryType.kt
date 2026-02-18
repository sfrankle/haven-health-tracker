package com.haven.app.data.entity

enum class EntryType(val displayName: String, val dbKey: String) {
    FOOD("Food", "food"),
    EMOTION("Emotion", "emotion"),
    HYDRATION("Hydration", "hydration"),
    SLEEP("Sleep", "sleep"),
    SYMPTOM("Symptom", "symptom"),
    ACTIVITY("Activity", "activity");

    companion object {
        /** @throws NoSuchElementException if [key] doesn't match any known entry type. */
        fun fromDbKey(key: String): EntryType = entries.first { it.dbKey == key }
    }
}
