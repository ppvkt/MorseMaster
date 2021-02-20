package ru.hadron.repositiries

import ru.hadron.morsemaster.repositories.Repository

class FakeRepository : Repository {
    override fun getStmCode(symbol: String): String {
        TODO("Not yet implemented")
    }
}