package com.github.op.xchange.repository

import android.arch.lifecycle.LiveData
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.entity.CurrencyPair
import com.github.op.xchange.entity.RateEntry
import io.reactivex.Completable
import io.reactivex.Observable

interface XChangeRepository {

    val selectedCurrencyPair: Observable<CurrencyPair>

    fun getRateHistory(currencyPair: CurrencyPair): Observable<List<RateEntry>>

    fun selectBaseCurrency(currency: Currency)

    fun selectRelatedCurrency(currency: Currency)

    fun clearData(): Completable
}