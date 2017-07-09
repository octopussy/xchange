package com.github.op.xchange

import android.arch.lifecycle.MediatorLiveData
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import io.reactivex.disposables.Disposable
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class LocalDateGsonTypeAdapter : TypeAdapter<LocalDate>() {
    override fun read(`in`: JsonReader): LocalDate = LocalDate.parse(`in`.nextString(), DateTimeFormatter.ISO_LOCAL_DATE)

    override fun write(out: JsonWriter, value: LocalDate) {
        out.beginObject().jsonValue(value.format(DateTimeFormatter.ISO_LOCAL_DATE)).endObject()
    }
}

/*
abstract class RxMediatorLiveData<T> : MediatorLiveData<T>() {
    private var disposable: Disposable? = null

    override fun onActive() {
        super.onActive()
        if (disposable == null) {
            subscribe()
        }
    }

    override fun onInactive() {
        super.onInactive()
        unsubscribe()
        disposable?.dispose()
        disposable = null
    }

    abstract fun subscribe()
    abstract fun unsubscribe()
} */
