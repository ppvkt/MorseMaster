package ru.hadron.morsemaster.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.hadron.morsemaster.db.entity.*
import ru.hadron.morsemaster.db.entity.relations.CodesAndCodesGroup

@Database(
    entities = [
        CodesGroup::class,
        Codes::class,
        Stat::class,
        Opts::class,
        Lesson::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MorseDatabase : RoomDatabase() {
    abstract fun getStorageDao(): StorageDao
}