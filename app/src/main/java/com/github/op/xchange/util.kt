package com.github.op.xchange

import android.arch.persistence.room.TypeConverter
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class DbLocalDateTimeConverter {
    @TypeConverter
    fun toDate(timestamp: Long?): LocalDateTime? {
        return if (timestamp == null) null else LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.ofTotalSeconds(0))
    }

    @TypeConverter
    fun toTimestamp(date: LocalDateTime?): Long? {
        return if (date == null) null else date.toInstant(ZoneOffset.ofTotalSeconds(0)).epochSecond
    }
}

class GsonLocalDateTimeTypeAdapter : TypeAdapter<LocalDateTime>() {
    override fun read(`in`: JsonReader): LocalDateTime {
        val timestamp = `in`.nextLong()
        return LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.ofTotalSeconds(0))
    }

    override fun write(out: JsonWriter, value: LocalDateTime) {
        out.beginObject().jsonValue(value.toInstant(ZoneOffset.ofTotalSeconds(0)).epochSecond.toString()).endObject()
    }
}

fun Float.asCurrencyValueString() : String = String.format("%.5f", this)

fun LocalDateTime.formatDateTime() : String = this.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))