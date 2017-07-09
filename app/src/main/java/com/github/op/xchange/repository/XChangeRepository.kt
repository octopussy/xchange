package com.github.op.xchange.repository

import android.arch.lifecycle.LiveData
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.entity.RateEntry

/**
 * @author mcpussy
 * @date 08/07/2017
 */
interface XChangeRepository {

    val selectedCurrencyPair: LiveData<Pair<Currency, Currency>>

    val availableCurrencies: LiveData<CurrenciesData>

    fun getRateHistory(currencyPair: Pair<Currency, Currency>): LiveData<List<RateEntry>>

    fun updateRateHistory(currencyPair: Pair<Currency, Currency>)

    fun selectBaseCurrency(currency: Currency)

    fun selectRelatedCurrency(currency: Currency)
}