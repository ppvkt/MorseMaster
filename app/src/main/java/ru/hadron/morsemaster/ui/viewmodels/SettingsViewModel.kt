package ru.hadron.morsemaster.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hadron.morsemaster.db.entity.Lesson
import ru.hadron.morsemaster.db.entity.Stat
import ru.hadron.morsemaster.repositories.DefaultRepository
import ru.hadron.morsemaster.util.Question
import java.util.*

class SettingsViewModel @ViewModelInject constructor(
    val repository: DefaultRepository
) : ViewModel() {

    private val adv_level = 75
    private val adv_max = 3

    class AdvItem(
        var initSymbol: String
    ) {
        var symbol: String
        var shuffle: Double

        init {
            this.symbol = initSymbol
            this.shuffle = Math.random()
        }
    }

    private class AdvItemShuffle
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
                val code = repository.getStmCode(symbol = c.toString())
                if (code.isNotEmpty()) {
                    res += "$code "
                }
            }
        }
        return res
    }

    fun loadLesson(info: String): Lesson? {
        var symbols = repository.getStmSymbolsFromLesson(info = info)
        return if (symbols.isNotEmpty()) {
            Lesson(info = info, symbols = symbols?.split(" ").toString())
        } else {
            null
        }
    }

    fun clearStat() {
        repository.deleteStat()
    }

    fun initStat(symbols: Array<String>) {
        for (symbol in symbols) {
            var stat: Stat  = Stat(
                symbol = symbol, 0, 0,
                lastseen = System.currentTimeMillis() / (30 * 1000)
            )

            stat.symbol = symbol
            stat.lastseen = System.currentTimeMillis() / (30 * 1000)

            viewModelScope.launch {
                repository.insertStat(stat = stat)
            }
        }
    }

    fun updateStat(symbol: String, correct: Boolean) {
        if (correct) {
            viewModelScope.launch {
                repository.updateStatIfAnswerCorrect(
                    lastseen = System.currentTimeMillis() / (30*1000) - (Math.random() * 3.0f).toInt(), //?
                    symbol = symbol
                )
            }
        } else {
            viewModelScope.launch {
                repository.updateStatIfAnswerNotCorrect(
                    lastseen = System.currentTimeMillis() / (30*1000) - (Math.random() * 3.0f).toInt(),
                    symbol = symbol
                )
            }
        }
    }

    fun getNextSymbol(remain: Int): Question {
        val rs = repository.getStmNextSymbol(adv_level)
        if (remain > 1 && Math.random() > 0.5) {
            //rs.next
            //rs.next
            // to do smth
        }
        return Question(rs[0].symbol, rs[0].correct) //?
    }

    fun getCountAdv(): Int {
        val stm_count_adv = repository.getStmCountAdv(ratio = adv_level)
        return stm_count_adv.indexOf(0)  //?
    }

    fun getNextAdv(adv: Int): Question {
        val rs = repository.getStmNextAdv(adv_level)
        var items: Vector<AdvItem> = Vector<AdvItem>()
        var question = ""
        var min_ratio = 99

        //while ()
        min_ratio = Math.min(min_ratio, rs.ratio)
        items.add(AdvItem(rs.symbol))

        val count = 2 + (adv_max - 1) * (min_ratio - adv_level) / (100 - adv_level)

        if (items.size > count) {
            items = Vector(items.subList(0, count))
        } else {
            for (i in items.size until count) {
                val item = items[i % adv]
                items.add(AdvItem(item.symbol))
            }
        }

        items.sortWith(AdvItemShuffle())

        for (i in 0 until count) question += items[i].symbol

        return Question(question, 999)
    }

    fun getLessons(): String? {           //?
        return repository.getInfoFromLesson().value
    }

    //------from ex lesson class --------------

    lateinit var symbols: Array<String>
    private var count = 0
    private var question: Question = Question("", 0)

    fun getQuestion(): Question {
        val adv = this.getCountAdv()
        val remain = symbols.size - adv

        if (adv > 0 && (remain == 0 || count++ % (remain + 1) == 0)) {
            question = getNextAdv(adv)
        } else {
            question= getNextSymbol(remain)
        }
        return question
    }

    fun setAnswer(answer: String): Boolean {
        var correct = true
        for (i in 0 until Math.min(answer.length, question.length())) {
            val a = answer[i]
            val q = question.symbol[i]

            if (a == q) {
                this.updateStat(symbol = q.toString(), correct = true)

            } else {
                correct = false
                this.updateStat(symbol = a.toString(), correct = false)
            }
        }
        return correct
    }
}

