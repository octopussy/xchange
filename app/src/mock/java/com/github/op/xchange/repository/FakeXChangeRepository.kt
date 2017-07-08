package com.github.op.xchange.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.entity.RateEntry

class FakeXChangeRepository : XChangeRepository {

    private val _history = MutableLiveData<List<RateEntry>>()

    override val availableCurrencies = MutableLiveData<List<Currency>>()

    override val selectedCurrencyPair =  MutableLiveData<Pair<Currency, Currency>>()

    init {
        availableCurrencies.value = listOf(Currency("USD"), Currency("EUR"), Currency("RUB"))
        _history.value = listOf(RateEntry("USDRUB", 10.1f, "7/7/1999", "12:00:00"),
                RateEntry("USDRUB", 100.1f, "7/7/1999", "12:00:00"),
                RateEntry("USDRUB", 5.2f, "7/7/1999", "12:00:00"),
                RateEntry("USDRUB", 6.1f, "7/7/1999", "12:00:00"),
                RateEntry("USDRUB", 9.1f, "7/7/1999", "12:00:00"),
                RateEntry("USDRUB", 10.1f, "7/7/1999", "12:00:00"))
        selectedCurrencyPair.value = Pair(
                availableCurrencies.value?.get(0)!!,
                availableCurrencies.value?.get(2)!!)
    }


    override fun getRateHistory(currencyPair: String): LiveData<List<RateEntry>> = _history

    override fun selectFirstCurrency(currency: Currency) {
        val value = selectedCurrencyPair.value!!.copy(first = currency)
        selectedCurrencyPair.value = value
    }

    override fun selectSecondCurrency(currency: Currency) {
        val value = selectedCurrencyPair.value!!.copy(second = currency)
        selectedCurrencyPair.value = value
    }
}