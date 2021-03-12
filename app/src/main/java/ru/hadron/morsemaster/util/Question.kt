package ru.hadron.morsemaster.util

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
        for (i in prefix.length until _symbol.length step 1) res += "*"
        return res
    }
}