package ru.hadron.morsemaster.db.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import ru.hadron.morsemaster.db.entity.Codes
import ru.hadron.morsemaster.db.entity.CodesGroup

data class CodesAndCodesGroup (
    @Embedded
    val codesGroup: CodesGroup,

    @Relation(parentColumn = "group_id", entityColumn = "id")
    val codes: Codes
) {
}