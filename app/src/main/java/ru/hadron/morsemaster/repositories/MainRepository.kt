package ru.hadron.morsemaster.repositories

import ru.hadron.morsemaster.db.StorageDao
import ru.hadron.morsemaster.db.entity.Opts
import ru.hadron.morsemaster.db.entity.Stat
import javax.inject.Inject

class MainRepository @Inject constructor(
    val dao: StorageDao
){
    fun getStmCode(symbol: String) = dao.getStmCode(symbol)
    fun getStmNextSymbol(level: Int) = dao.getStmNextSymbol(level)
    fun getStmNextAdv(ratio: Int) = dao.getStmNextAdv(ratio)
    fun getStmCountAdv(ratio: Int) = dao.getStmCountAdv(ratio)
    fun getStmWorth() = dao.getStmWorst()
    fun getStmOpt(name: String) = dao.getStmOpt(name)
    suspend fun setStmOpt(opts: Opts) = dao.setStmOpt(opts)

    fun getStmSymbolsFromLesson(info: String) = dao.getStmSymbolsFromLesson(info)
    fun deleteStat() = dao.deleteStat()
    suspend fun insertStat(stat: Stat) = dao.insertStat(stat)
    suspend fun updateStat(stat: Stat) = dao.updateStat(stat)
    fun getInfoFromLesson() = dao.getInfoFromLesson()
}