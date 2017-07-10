package com.github.op.xchange.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.github.op.xchange.entity.QuoteEntry.Companion.TABLE_NAME
import org.threeten.bp.LocalDateTime

@Entity(tableName = TABLE_NAME)
data class QuoteEntry(
        @ColumnInfo(name = FIELD_PAIR) val pair: String,
        @ColumnInfo(name = FIELD_PRICE) val price: Float,
        @ColumnInfo(name = FIELD_DATE_TIME) val dateTime: LocalDateTime) {

    @PrimaryKey(autoGenerate = true)
    var _id = 0

    companion object {
        const val TABLE_NAME = "quotes"

        const val FIELD_PAIR = "pair"
        const val FIELD_PRICE = "price"
        const val FIELD_DATE_TIME = "datetime"
    }
}