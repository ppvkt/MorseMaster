package ru.hadron.morsemaster.db.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity (tableName = "stat")
data class Stat(
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name = "symbol") var symbol: String,
    @ColumnInfo(name = "correct") var correct: Int = 0,
    @ColumnInfo(name = "mistake") var mistake: Int = 0,
    @ColumnInfo(name = "lastseen") var lastseen: Int? = null,

    @ColumnInfo(name = "level") var level: Int = 0,
    @ColumnInfo(name = "ratio") var ratio: Int = 0,

    @ColumnInfo(name = "count") var count:Int = 0
)


data class StatMinimum(
   var symbol: String,
    var correct: Int,
   var ratio: Int
)

data class StatMinimum2(
    var count: Int,
    var ratio: Int
)

data class StatMinimum3(
    var symbol: String,
    var ratio: Int
)