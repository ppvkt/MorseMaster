package ru.hadron.morsemaster.util

import ru.hadron.morsemaster.repositories.Storage
import timber.log.Timber

class CurrentLesson(
    storage: Storage,
   currsymbols: String?,
) {
    private var storage: Storage = storage
     var symbols: String = currsymbols.toString()
    lateinit var currentquestion: Question
    private var count = 0

    fun initStat() = symbols.let { storage.initStat(symbols = it) }


    fun getQuestion(): Question {
        val adv = storage.getCountAdv()
        val remain = symbols.length - adv

        var question = Question("test", 0)

        if (adv > 0 && (remain == 0 || count++ % (remain + 1) == 0)) {
            question = storage.getNextAdv(adv)
           // question.symbol = storage.getNextAdv(adv).symbol
           // question.correct = storage.getNextAdv(adv).correct
            Timber.e("====question = getNextAdv(adv)=====$question")
        } else {
            question = storage.getNextSymbol(remain)
          //  question.symbol = storage.getNextSymbol(remain).symbol
           // question.correct = storage.getNextSymbol(remain).correct
            Timber.e("=====question = getNextSymbol(remain)=====$question")
        }
        currentquestion = question
        return question
    }

    fun setAnswer(answer: String): Boolean {
        var correct = true
        for (i in 0 until Math.min(answer.length, currentquestion.length())) {
            val a = answer[i]
            val q = currentquestion.symbol[i]

            if (a == q) {
                storage.updateStat(symbol = q.toString(), correct = true)

            } else {
                correct = false
                storage.updateStat(symbol = a.toString(), correct = false)
            }
        }
        return correct
    }
}