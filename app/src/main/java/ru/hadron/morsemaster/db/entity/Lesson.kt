package ru.hadron.morsemaster.db.entity

import androidx.room.Entity

@Entity (tableName = "lesson")
data class Lesson (
    var info: String? = null,
    var symbol: String? = null
) {
}