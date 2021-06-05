package ru.hadron.morsemaster.db

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.hadron.morsemaster.db.entity.*

@Dao
interface StorageDao {

 @Transaction
 @Query("SELECT code FROM codes WHERE symbol = :symbol")
 suspend fun getStmCode(symbol: String): String

 @Transaction
 @Query("SELECT symbol, correct, 5*correct/(correct+mistake/2) AS ratio, 100*correct/(correct+mistake/2) AS level FROM stat WHERE NOT (level >= :level AND correct >= 10) ORDER BY lastseen ASC, ratio ASC LIMIT 2")
 suspend fun getStmNextSymbol(level: Int): List<StatForStmNextSymbol>

 @Transaction
 @Query("SELECT symbol, 100*correct/(correct+mistake/2) AS ratio FROM stat WHERE ratio >= :ratio AND correct >= 10 ORDER BY lastseen, ratio")
 suspend  fun getStmNextAdv(ratio: Int): List<StatForStmNextAdv>?

 @Transaction
 @Query("SELECT count(*) as _count, 100*correct/(correct+mistake/2) AS ratio FROM stat WHERE ratio >= :ratio AND correct >= 10")
 suspend  fun getStmCountAdv(ratio: Int): StatForStmCountAdv

 @Transaction
 //@Query("SELECT min(100*correct/(correct+mistake/2)) AS worst FROM stat")
 @Query("SELECT min(100*correct/(correct+mistake)) AS worst FROM stat")
 fun getStmWorst(): LiveData<Int>?

/* @Transaction
 @Query("SELECT value FROM opts WHERE name = :name")
 fun getStmOpt(name: String): LiveData<String>?*/
/*
 @Insert(onConflict = OnConflictStrategy.REPLACE)
 suspend fun setStmOpt(opts: Opts)*/
 //---

 @Transaction
 @Query("SELECT symbols FROM lesson WHERE info = :info")
 suspend fun getStmSymbolsFromLesson(info: String): String

 @Query("DELETE FROM stat")
 fun deleteStat()

 //"INSERT OR IGNORE INTO stat (symbol, correct, mistake, lastseen) VALUES (?, 0, 0, ?)"
 @Insert(onConflict = OnConflictStrategy.REPLACE)
 suspend fun insertStat(stat: Stat)

 @Query("INSERT OR IGNORE INTO stat (symbol, correct, mistake, lastseen) VALUES (:symbol, 0, 0, :lastseen)")
 suspend fun insertOrIgnoreStat(symbol: String, lastseen: Long)

 @Update
 suspend fun updateStat(stat: Stat)

 //"UPDATE stat SET correct = correct + 1, lastseen = ? WHERE symbol = ?"
 //"UPDATE stat SET mistake = mistake + 1, lastseen = ? WHERE symbol = ?"
 @Query("UPDATE stat SET correct = correct + 1, lastseen = :lastseen WHERE symbol = :symbol")
 suspend fun updateStatIfCorrectAnswer(lastseen: Long, symbol: String)

 @Query("UPDATE stat SET mistake = mistake + 1, lastseen = :lastseen WHERE symbol = :symbol")
 suspend fun updateStatIfNotCorrectAnswer(lastseen: Long, symbol: String)

 @Query("SELECT info FROM lesson")
 fun getInfoFromLesson(): LiveData<List<String>>

 ////----
 @Insert(onConflict = OnConflictStrategy.REPLACE)
 suspend fun insertCvsLesson(lesson: Lesson)

 @Insert(onConflict = OnConflictStrategy.REPLACE)
 suspend fun insertCvsCodes(codes: Codes)

 @Insert(onConflict = OnConflictStrategy.REPLACE)
 suspend fun insertCvsCodesGroup(codesGroup: CodesGroup)
}
