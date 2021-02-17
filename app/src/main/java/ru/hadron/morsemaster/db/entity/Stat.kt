package ru.hadron.morsemaster.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "stat")
data class Stat(
    @PrimaryKey
    var symbol: String? = null,
    var correct: Int = 0,
    var mistake: Int = 0,
    var lastseen: Int? = null
) {
}