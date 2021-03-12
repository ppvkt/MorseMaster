package ru.hadron.morsemaster.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "stat")
data class Stat(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "symbol") var symbol: String,
    @ColumnInfo(name = "correct") var correct: Int = 0,

    @ColumnInfo(name = "mistake") var mistake: Int = 0,
    @ColumnInfo(name = "lastseen") var lastseen: Long
)

//----------------------

data class StatForStmNextSymbol (
    @ColumnInfo(name = "symbol") var symbol: String,
    @ColumnInfo(name = "correct") var correct: Int,
    @ColumnInfo(name = "ratio") var ratio: Int,
    @ColumnInfo(name = "level") var level: Int
)

data class StatForStmCountAdv (
    @ColumnInfo(name = "_count") var _count: Int,
    @ColumnInfo(name = "ratio") var ratio: Int
)

data class StatForStmNextAdv (
    @ColumnInfo(name = "symbol") var symbol: String,
    @ColumnInfo(name = "ratio")  var ratio: Int
)

data class StatForUpdateStat(
    @ColumnInfo(name = "symbol") var symbol: String,
    @ColumnInfo(name = "correct") var correct:Int
)


