package com.github.op.xchange.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.github.op.xchange.entity.RateEntry
import com.github.op.xchange.dao.RatesDao
import com.github.op.xchange.entity.Currency

@Database(entities = arrayOf(RateEntry::class), version = 6)
@TypeConverters(DateTypeConverter::class)
abstract class XChangeDatabase : RoomDatabase() {
    abstract fun ratesDao(): RatesDao
}