package com.haven.app.data.entity

enum class EntryTypeIcon(val key: String) {
    FOOD("food"),
    EMOTION("emotion"),
    HYDRATION("hydration"),
    SLEEP("sleep"),
    SYMPTOM("symptom"),
    ACTIVITY("activity");

    companion object {
        fun fromKey(key: String): EntryTypeIcon =
            entries.first { it.key == key }
    }
}
