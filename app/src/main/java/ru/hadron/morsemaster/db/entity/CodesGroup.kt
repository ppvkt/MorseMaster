package ru.hadron.morsemaster.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "codes_group")
data class CodesGroup(
    var info: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}