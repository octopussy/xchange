package com.github.op.xchange.repository

import android.arch.lifecycle.LiveData
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.entity.CurrencyPair
import com.github.op.xchange.entity.QuoteEntry
import io.reactivex.Completable
import io.reactivex.Observable

interface XChangeRepository {

    val selectedCurrencyPair: Observable<CurrencyPair>

    fun getQuoteHistory(currencyPair: CurrencyPair): LiveData<List<QuoteEntry>>

    fun selectBaseCurrency(currency: Currency)

    fun selectRelatedCurrency(currency: Currency)

    fun swapSelectedCurrencies()

    fun fetchCurrentQuote(currencyPair: CurrencyPair): Completable

    fun clearData(): Completable
}