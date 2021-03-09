package ru.hadron.morsemaster.repositories

import kotlinx.coroutines.*
import ru.hadron.morsemaster.db.entity.*

import ru.hadron.morsemaster.util.Question
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class Storage @Inject constructor(
    val repository: DefaultRepository
) {

    private var adv_level = 75
    private var adv_max = 3

    class AdvItem (
        var initSymbol: String
    ) {
        var symbol: String
        var shuffle: Double

        init {
            this.symbol = initSymbol
            this.shuffle = Math.random()
        }
    }

    class AdvItemShuffle
        : Comparator<AdvItem> {
        override fun compare(a: AdvItem, b: AdvItem): Int {
            return if (a.shuffle < b.shuffle) 1
            else -1
        }
    }

    fun getCode(text: String): String {
        var res = ""
        for (c: Char in text.toCharArray()) {
            if (c.equals(" ")) {
                res += "|"
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    val code = repository.getStmCode(symbol = c.toString())
                    if (code.isNotEmpty()) {
                        res += "$code "
                    }
                }

            }
        }
        return res
    }

    fun loadLesson(info: String): Lesson? {
        var s: String? = null
        runBlocking {
            GlobalScope.async(Dispatchers.IO) {
                s = repository.getStmSymbolsFromLesson(info = info)
            }.await()
        }

        return if (s!!.isNotEmpty()) {
            Timber.e("-load lesson--return--${Lesson(info = info, symbols = s?.split(" ").toString())}----")
          Lesson(info = info, symbols = s?.split(" ").toString())
        } else {
            Timber.e("-load lesson----return null!!!!----")
            null
        }
    }

    fun clearStat() {
        GlobalScope.launch(Dispatchers.IO) {
            repository.deleteStat()
        }
    }
    fun insertStat(stat: Stat) {
        GlobalScope.launch(Dispatchers.IO) {
            repository.insertStat(stat)
        }
    }
    fun insertOrIgnoreStat(symbol: String, lastseen: Long) {
        GlobalScope.launch(Dispatchers.IO) {
            repository.insertOrIgnoreStat(symbol, lastseen)
        }
    }

    fun initStat(symbols: String) {
        if (symbols != null) {
            for (symbol in symbols) {
                /*  var stat: Stat  = Stat(
                      symbol = symbol.toString(), 0, 0,
                      lastseen = System.currentTimeMillis() / (30 * 1000)
                  )
                  insertStat(stat)*/
                insertOrIgnoreStat(symbol = symbol.toString(), lastseen = System.currentTimeMillis() / (30 * 1000) )
            }
        } else {
            Timber.e("-------symbols didnt loaded (:")
        }


    }

    fun updateStat(symbol: String, correct: Boolean) {
        if (correct) {
            GlobalScope.launch(Dispatchers.IO) {
                repository.updateStatIfAnswerCorrect(
                    lastseen = System.currentTimeMillis() / (30*1000) - (Math.random() * 3.0f).toInt(), //?
                    symbol = symbol
                )
            }
        } else {
            GlobalScope.launch(Dispatchers.IO) {
                repository.updateStatIfAnswerNotCorrect(
                    lastseen = System.currentTimeMillis() / (30*1000) - (Math.random() * 3.0f).toInt(),
                    symbol = symbol
                )
            }
        }
    }

    fun getNextSymbol(remain: Int): Question {
        var rs: List<StatForStmNextSymbol> ? = null
        GlobalScope.launch(Dispatchers.IO) {
            rs = repository.getStmNextSymbol(adv_level)
        }
        if (remain > 1 && Math.random() > 0.5) {
            //rs.next
            //rs.next
            // to do smth
        }
        val symbol = rs?.get(0)?.symbol!!
        val correct = rs?.get(0)?.correct!!
       // return Question(symbol = symbol, correct = correct) //?
        return Question(symbol = "", correct = 1) //test
    }

    fun getCountAdv(): Int {
        var stm_count_adv: List<StatForStmCountAdv>? = null
        GlobalScope.launch(Dispatchers.IO) {
            stm_count_adv = repository.getStmCountAdv(ratio = adv_level)
        }
        // stm_count_adv = stm.getCompleted()
        // return stm_count_adv.indexOf(0)  //?
        return stm_count_adv!!.indexOf(0)
    }

    fun getNextAdv(adv: Int): Question {
        var rs = StatForStmNextAdv("x", 0)

        runBlocking {
            val stm_next_adv =
                GlobalScope.async(Dispatchers.IO) {
                    repository.getStmNextAdv(adv_level)
                }
            runBlocking { rs = stm_next_adv.await() }
        }

        Timber.e("======>>> ${rs.symbol}")

        var items: Vector<Storage.AdvItem> = Vector<Storage.AdvItem>()
        var question = ""
        var min_ratio = 99

        rs.symbol.forEach {
            min_ratio = Math.min(min_ratio, rs.ratio)
            items.add(Storage.AdvItem(rs.symbol))
        }

        val count = 2 + (adv_max - 1) * (min_ratio - adv_level) / (100 - adv_level)

        if (items.size > count) {
            items = Vector(items.subList(0, count))
        } else {
            var ii = items.size - 1
            while (ii < count) {
                val item = items[ii % adv]
                items.add(Storage.AdvItem(item.symbol))
                ii++
            }
        }

        items.sortWith(Storage.AdvItemShuffle())

        for (i in 0 until count) question += items[i].symbol

        return Question(question, 999)
    }

    fun getLessons(): List<String>? {
        var items : List<String>? = null
        GlobalScope.launch(Dispatchers.IO) {
            items = repository.getInfoFromLesson().value
        }
        return items
    }

    val worth = repository.getStmWorth()

    fun setAdvLevel(value: Int) { adv_level = value }
    fun setAdvMax(value: Int) {adv_max = value }

    //-------

    fun getInfoFromLesson()  = repository.getInfoFromLesson()
    fun insertCvsLesson(lesson: Lesson) {
        GlobalScope.launch(Dispatchers.IO) {
            repository.insertCvsLesson(lesson)
        }
    }


   fun insertCvsCodes(codes: Codes) {
       GlobalScope.launch(Dispatchers.IO) {
           repository.insertCvsCodes(codes)
       }
   }
   fun insertCvsCodesGroup(codesGroup: CodesGroup) {
       GlobalScope.launch(Dispatchers.IO) {
           repository.insertCvsCodesGroup(codesGroup)
       }
   }
}