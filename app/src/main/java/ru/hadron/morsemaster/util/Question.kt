package ru.hadron.morsemaster.util

import timber.log.Timber

class Question (
    var symbol: String,
    var correct: Int
) {
    var _symbol: String = symbol
    var _correct: Int = correct

    fun length(): Int {
        return _symbol.length
    }

    fun getSecret(prefix: String): String {
        var res = prefix
        //for (i in prefix.length until _symbol.length) res += "*"
        for ( i in prefix.length .._symbol.length - 1) {  // -1?
           // res += "*"
            res+= "\u26A1"
        }
        Timber.e("  **res ===> $res")
        return res
    }
}