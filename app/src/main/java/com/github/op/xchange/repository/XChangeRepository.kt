package com.github.op.xchange.repository

import android.arch.lifecycle.LiveData
import com.f2prateek.rx.preferences2.Preference
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.entity.CurrencyPair
import com.github.op.xchange.entity.QuoteEntry
import io.reactivex.Completable
import io.reactivex.Observable


interface XChangeRepository {

    val selectedCurrencyPair: Observable<CurrencyPair>

    val selectedCurrencyPair2: LiveData<CurrencyPair>

    fun getQuoteHistory(currencyPair: CurrencyPair): LiveData<List<QuoteEntry>>

    fun selectBaseCurrency(currency: Currency)

    fun selectRelatedCurrency(currency: Currency)

    fun swapSelectedCurrencies()

    fun fetchCurrentQuote(currencyPair: CurrencyPair, callback: (Boolean) -> Unit)

    fun fetchCurrentQuoteForSelectedPair(callback: (Boolean) -> Unit)

    fun clearData(): Completable

    val updateIntervalSecPref: Preference<Long>
}