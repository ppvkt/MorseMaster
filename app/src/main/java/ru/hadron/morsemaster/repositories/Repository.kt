package ru.hadron.morsemaster.repositories

import ru.hadron.morsemaster.db.entity.Opts
import ru.hadron.morsemaster.db.entity.Stat

interface Repository {
   suspend fun getStmCode(symbol: String): String
   /* fun getStmNextSymbol(level: Int)

    fun getStmNextAdv(ratio: Int)
    fun getStmCountAdv(ratio: Int)
    fun getStmWorth()
    fun getStmOpt(name: String)
    suspend fun setStmOpt(opts: Opts)

    fun getStmSymbolsFromLesson(info: String)
    fun deleteStat()
    suspend fun insertStat(stat: Stat)
    suspend fun updateStat(stat: Stat)
    fun getInfoFromLesson()*/
}