package ru.hadron.morsemaster.db.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import javax.annotation.Generated

@Entity (tableName = "stat")
data class Stat(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "symbol") var symbol: String,
    @ColumnInfo(name = "correct") var correct: Int = 100,

    @ColumnInfo(name = "mistake") var mistake: Int = 0,
    @ColumnInfo(name = "lastseen") var lastseen: Long

/*    @ColumnInfo(name = "ratio") var ratio: Int,
    @ColumnInfo(name = "level") var level: Int,

    @ColumnInfo(name = "count") var count: Int,
    @ColumnInfo(name = "worst") var worst: Int*/
)

/*@Entity (tableName = "statWithWorst")
data class StatWithWorst(
    @PrimaryKey(autoGenerate = false)
    @Embedded var stat: Stat,

    @ColumnInfo(name = "worst") var worst: Int //= 100*stat.correct/(stat.correct+stat.mistake/2)
)*/
//----------------------

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


