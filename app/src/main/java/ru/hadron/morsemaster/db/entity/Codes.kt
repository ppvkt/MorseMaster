package ru.hadron.morsemaster.db.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "codes")
data class Codes(
    val group_id: Int,
    @PrimaryKey(autoGenerate = false)
    @NonNull
    val symbol: String,
    val code: String
) {

}