package com.github.op.xchange.repository

import android.arch.lifecycle.LiveData
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.entity.RateEntry
import io.reactivex.Flowable

/**
 * @author mcpussy
 * @date 08/07/2017
 */
interface XChangeRepository {

    val selectedCurrencyPair: LiveData<Pair<Currency, Currency>>

    val availableCurrencies: LiveData<List<Currency>>

    fun getRateHistory(currencyPair: Pair<Currency, Currency>): LiveData<List<RateEntry>>

    fun selectFirstCurrency(currency: Currency)

    fun selectSecondCurrency(currency: Currency)
}