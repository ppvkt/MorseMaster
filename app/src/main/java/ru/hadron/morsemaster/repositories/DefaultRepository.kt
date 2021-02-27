package ru.hadron.morsemaster.repositories

import ru.hadron.morsemaster.db.StorageDao
import ru.hadron.morsemaster.db.entity.*
import javax.inject.Inject

class DefaultRepository @Inject constructor(
    val dao: StorageDao
): Repository {
    suspend fun getStmCode(symbol: String): String = dao.getStmCode(symbol)
   suspend fun getStmNextSymbol(level: Int) = dao.getStmNextSymbol(level)

   suspend fun getStmNextAdv(ratio: Int) = dao.getStmNextAdv(ratio)
  suspend  fun getStmCountAdv(ratio: Int) = dao.getStmCountAdv(ratio)
    fun getStmWorth() = dao.getStmWorst()
    fun getStmOpt(name: String) = dao.getStmOpt(name)
    suspend fun setStmOpt(opts: Opts) = dao.setStmOpt(opts)

   suspend fun getStmSymbolsFromLesson(info: String) = dao.getStmSymbolsFromLesson(info)
    fun deleteStat() = dao.deleteStat()
    suspend fun insertStat(stat: Stat) = dao.insertStat(stat)

   //suspend fun updateStat(stat: Stat) = dao.updateStat(stat)

    suspend fun updateStatIfAnswerCorrect(lastseen: Long, symbol: String) = dao.updateStatIfCorrectAnswer(lastseen, symbol)
    suspend fun updateStatIfAnswerNotCorrect(lastseen: Long, symbol: String) = dao.updateStatIfNotCorrectAnswer(lastseen, symbol)

    fun getInfoFromLesson() = dao.getInfoFromLesson()

    //---
    suspend fun insertCvsLesson(lesson: Lesson) = dao.insertCvsLesson(lesson)
    suspend fun insertCvsCodes(codes: Codes) = dao.insertCvsCodes(codes)
    suspend fun insertCvsCodesGroup(codesGroup: CodesGroup) = dao.insertCvsCodesGroup(codesGroup)
}