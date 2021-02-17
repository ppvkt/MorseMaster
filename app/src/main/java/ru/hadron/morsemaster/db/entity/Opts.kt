package ru.hadron.morsemaster.db.entity

import androidx.room.PrimaryKey

data class Opts (
    @PrimaryKey //    - todo {name	TEXT PRIMARY KEY ON CONFLICT REPLACE }
    var name: String? = null,
    var value:String? = null
) {
}