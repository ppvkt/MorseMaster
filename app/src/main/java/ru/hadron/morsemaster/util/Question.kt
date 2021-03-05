package ru.hadron.morsemaster.util

class Question (
    var symbol: String,
    var correct: Int
) {
    fun length(): Int {
        return symbol.length
    }

    fun getSecret(prefix: String): String {
        var res = prefix
        for (i in prefix.length until symbol.length) res += "*"
        return res
    }
}