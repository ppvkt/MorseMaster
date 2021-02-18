package ru.hadron.morsemaster.db.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "opts")
data class Opts (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name ="name")val name: String,
    @ColumnInfo(name ="value") val value: String
) {
}