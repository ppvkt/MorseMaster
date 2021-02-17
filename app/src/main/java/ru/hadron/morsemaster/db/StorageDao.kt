package ru.hadron.morsemaster.db

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.hadron.morsemaster.db.entity.Opts
import ru.hadron.morsemaster.db.entity.Stat

@Dao
interface StorageDao {

    @Query("SELECT code FROM codes WHERE symbol = :symbol")
    fun getStmCode(symbol: String): LiveData<String>

    @Query("SELECT symbol, correct, 5*correct/(correct+mistake/2) AS ratio, 100*correct/(correct+mistake/2) as level FROM stat WHERE NOT (level >= :level AND correct >= 10) ORDER BY lastseen ASC, ratio ASC LIMIT 2")
    fun getStmNextSymbol(level: Int): LiveData<String>

    @Query("SELECT symbol, 100*correct/(correct+mistake/2) AS ratio FROM stat WHERE ratio >= :ratio AND correct >= 10 ORDER BY lastseen, ratio")
    fun getStmNextAdv(ratio: Int): LiveData<String>

    @Query("SELECT count(*) as count, 100*correct/(correct+mistake/2) AS ratio FROM stat WHERE ratio >= :ratio AND correct >= 10")
    fun getStmCountAdv(ratio: Int): LiveData<String>

    @Query("SELECT min(100*correct/(correct+mistake/2)) AS worst FROM stat")
    fun getStmWorst(): LiveData<Int>

    @Query("SELECT value FROM opts WHERE name = :name")
    fun getStmOpt(name: String): LiveData<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setStmOpt(opts: Opts)

    //---

    @Query("SELECT symbols FROM lesson WHERE info = :info")
    fun getStmSymbolsFromLesson(info: String): LiveData<String>

    @Query("DELETE FROM stat")
    fun deleteStat()

    //"INSERT OR IGNORE INTO stat (symbol, correct, mistake, lastseen) VALUES (?, 0, 0, ?)"
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStat(stat: Stat)


    //"UPDATE stat SET correct = correct + 1, lastseen = ? WHERE symbol = ?"
    //"UPDATE stat SET mistake = mistake + 1, lastseen = ? WHERE symbol = ?"
    @Update
    suspend fun updateStat(stat: Stat)

    @Query("SELECT info FROM lesson")
    fun getInfoFromLesson(): LiveData<String>
}


//--------------
class AdvItem (
    var initSymbol: String
) {
    var symbol: String
    var  shuffle: Double

    init {
        this.symbol = initSymbol
        this.shuffle = Math.random()
    }
}

private class AdvItemShuffle
    : Comparator<AdvItem>
{
    override fun compare(a: AdvItem, b: AdvItem): Int {
        return if (a?.shuffle < b?.shuffle) 1
        else -1
    }
}