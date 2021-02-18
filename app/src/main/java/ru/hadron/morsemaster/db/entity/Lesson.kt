package ru.hadron.morsemaster.db.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "lesson")
data class Lesson (
    @PrimaryKey(autoGenerate = false)
    @NonNull
    val info: String,
    val symbols: String
) {
/*    @PrimaryKey(autoGenerate = true)
    val id: Int? = null*/
}