package com.github.op.xchange.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.github.op.xchange.dao.CurrenciesDao
import com.github.op.xchange.entity.RateEntry
import com.github.op.xchange.dao.RatesDao
import com.github.op.xchange.entity.Currency

@Database(entities = arrayOf(RateEntry::class, Currency::class), version = 1)
//@TypeConverters(DateTypeConverter::class)
abstract class XChangeDatabase : RoomDatabase() {
    abstract fun ratesDao(): RatesDao
    abstract fun currenciesDao(): CurrenciesDao
}