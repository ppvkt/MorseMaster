package ru.hadron.morsemaster.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "codes")
data class Codes (
    val group_id: String, //Int
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "symbol") val symbol: String,
    @ColumnInfo(name = "code") val code: String
) {
}