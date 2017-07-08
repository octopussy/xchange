package com.github.op.xchange.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.github.op.xchange.entity.RateEntry.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class RateEntry(
        val code1: String,
        val code2: String,
        val rate: Float,
        val date: String, // "7/8/2017"
        val time: String) { // "8:02am"

    @PrimaryKey(autoGenerate = true)
    var _id = 0

    companion object {
        const val TABLE_NAME = "rates"
    }
}