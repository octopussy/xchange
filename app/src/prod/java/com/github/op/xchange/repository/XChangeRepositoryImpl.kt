package com.github.op.xchange.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.arch.lifecycle.MutableLiveData
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.github.op.xchange.db.XChangeDatabase
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.entity.RateEntry
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

class XChangeRepositoryImpl(private val db: XChangeDatabase, private val rxPrefs: RxSharedPreferences) : XChangeRepository {

    private val firstCurrencyCode = rxPrefs.getString("firstCurrencyCode")
    private val secondCurrencyCode = rxPrefs.getString("secondCurrencyCode")

    override val selectedCurrencyPair: LiveData<Pair<Currency, Currency>> by lazy {
        val firstObs = firstCurrencyCode.asObservable()
        val secondObs = secondCurrencyCode.asObservable()
        val result: Observable<Pair<Currency, Currency>> = Observable.combineLatest(firstObs, secondObs, BiFunction { t1, t2 ->
            Pair(Currency(t1), Currency(t2))
        })

        LiveDataReactiveStreams.fromPublisher(result.toFlowable(BackpressureStrategy.LATEST))
    }

    private val _availableCurrencies = MutableLiveData<List<Currency>>()
    override val availableCurrencies: LiveData<List<Currency>>
        get() = _availableCurrencies

    init {
        _availableCurrencies.value = listOf(Currency("USD"), Currency("EUR"), Currency("RUB"))
    }

    override fun selectFirstCurrency(currency: Currency) = firstCurrencyCode.set(currency.code)

    override fun selectSecondCurrency(currency: Currency) = secondCurrencyCode.set(currency.code)

    override fun getRateHistory(currencyPair: Pair<Currency, Currency>): LiveData<List<RateEntry>> {
        return db.ratesDao().getRates(currencyPair.first.code, currencyPair.second.code)
    }
}