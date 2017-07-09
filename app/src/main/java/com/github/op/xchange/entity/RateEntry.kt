package com.github.op.xchange.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.github.op.xchange.entity.RateEntry.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class RateEntry(
        @ColumnInfo(name = FIELD_BASE_CODE) val baseCode: String,
        @ColumnInfo(name = FIELD_RELATED_CODE) val relatedCode: String,
        val rate: Float,
        val date: String) {

    @PrimaryKey(autoGenerate = true)
    var _id = 0

    companion object {
        const val TABLE_NAME = "rates"
        const val FIELD_BASE_CODE = "baseCode"
        const val FIELD_RELATED_CODE = "relatedCode"
    }
}