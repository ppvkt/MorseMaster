package ru.hadron.morsemaster.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "lesson")
data class Lesson (
    @ColumnInfo(name = "info") val info: String,
    @ColumnInfo(name = "symbols")  val symbols: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}