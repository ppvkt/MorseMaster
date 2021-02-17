package ru.hadron.morsemaster.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "opts")
data class Opts (
    @PrimaryKey
    var name: String? = null,
    var value:String? = null
) {
}