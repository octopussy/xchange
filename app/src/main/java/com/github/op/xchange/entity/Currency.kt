package com.github.op.xchange.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.github.op.xchange.entity.Currency.Companion.TABLE_NAME

typealias CurrencyPair = Pair<Currency, Currency>

@Entity(tableName = TABLE_NAME)
data class Currency(@PrimaryKey val code: String) {

    override fun toString(): String = code

    companion object {
        const val TABLE_NAME = "currencies"
    }
}