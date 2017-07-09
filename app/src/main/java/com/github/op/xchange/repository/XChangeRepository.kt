package com.github.op.xchange.repository

import android.arch.lifecycle.LiveData
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.entity.CurrencyPair
import com.github.op.xchange.entity.RateEntry
import io.reactivex.Completable
import io.reactivex.Observable

/**
 * @author mcpussy
 * @date 08/07/2017
 */
interface XChangeRepository : CurrenciesDataSource {

    val selectedCurrencyPair: LiveData<CurrencyPair>

    val availableCurrencies: LiveData<CurrenciesData>

    val selectedCurrencyPair2: Observable<CurrencyPair>

    fun getRateHistory(currencyPair: CurrencyPair): LiveData<List<RateEntry>>

    fun getRateHistory2(currencyPair: CurrencyPair): Observable<List<RateEntry>>

    fun refreshAvailableCurrencies()

    fun selectBaseCurrency(currency: Currency)

    fun selectRelatedCurrency(currency: Currency)

    fun clearData(): Completable
}