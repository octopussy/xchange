package com.github.op.xchange

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class LocalDateGsonTypeAdapter : TypeAdapter<LocalDateTime>() {
    override fun read(`in`: JsonReader): LocalDateTime = LocalDateTime.parse(`in`.nextString(), DateTimeFormatter.ISO_LOCAL_DATE)

    override fun write(out: JsonWriter, value: LocalDateTime) {
        out.beginObject().jsonValue(value.format(DateTimeFormatter.ISO_LOCAL_DATE)).endObject()
    }
}

fun Float.asCurrencyValueString() : String = String.format("%.5f", this)

fun LocalDateTime.formatDateTime() : String = this.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))