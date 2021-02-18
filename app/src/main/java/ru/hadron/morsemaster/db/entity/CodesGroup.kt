package ru.hadron.morsemaster.db.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "codes_group")
data class CodesGroup(
    @PrimaryKey(autoGenerate = false)
    @NonNull
    val id: Int,

    val info: String

) {

}