package ru.hadron.morsemaster.db.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "stat")
data class Stat(
    @PrimaryKey(autoGenerate = false)
   @NonNull
    @ColumnInfo(name = "symbol") var symbol: String,
    @ColumnInfo(name = "correct") var correct: Int ,
    @ColumnInfo(name = "mistake") var mistake: Int ,
    @ColumnInfo(name = "lastseen") var lastseen: Long,

   //  @ColumnInfo(name = "ratio") var ratio: Int,
   //  @ColumnInfo(name = "level") var level: Int,
   //  @ColumnInfo(name = "count") var count: Int
)


data class StatForStmNextSymbol (
    @ColumnInfo(name = "symbol") var symbol: String,
    @ColumnInfo(name = "correct") var correct: Int,
    @ColumnInfo(name = "ratio") var ratio: Int,
    @ColumnInfo(name = "level") var level: Int
)

data class StatForStmCountAdv (
    @ColumnInfo(name = "count") var count: Int,
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
