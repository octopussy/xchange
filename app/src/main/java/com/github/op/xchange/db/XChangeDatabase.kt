package com.github.op.xchange.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.github.op.xchange.DbLocalDateTimeConverter
import com.github.op.xchange.entity.QuoteEntry
import com.github.op.xchange.dao.QuotesDao

@Database(entities = arrayOf(QuoteEntry::class), version = 7)
@TypeConverters(DbLocalDateTimeConverter::class)
abstract class XChangeDatabase : RoomDatabase() {
    abstract fun quotesDao(): QuotesDao
}