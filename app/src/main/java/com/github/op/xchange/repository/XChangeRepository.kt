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

    val selectedCurrencyPair: Observable<CurrencyPair>

    fun getLatestRateValue(currencyPair: CurrencyPair): LiveData<RateEntry>

    fun getLatestRateValue2(currencyPair: CurrencyPair): Observable<RateEntry>

    fun getRateHistory(currencyPair: CurrencyPair): Observable<List<RateEntry>>

    fun selectBaseCurrency(currency: Currency)

    fun selectRelatedCurrency(currency: Currency)

    fun clearData(): Completable
}