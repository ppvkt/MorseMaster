package ru.hadron.morsemaster.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "codes")
data class Codes(
    var group_id: Int? = null,
    @PrimaryKey
    var symbol: String? = null,
    var code: String? = null
) {

}