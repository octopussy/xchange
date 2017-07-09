package com.github.op.xchange.repository

import android.arch.lifecycle.*
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.github.op.xchange.api.FixerApi
import com.github.op.xchange.api.FixerResponse
import com.github.op.xchange.db.XChangeDatabase
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.entity.RateEntry
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors

class XChangeRepositoryImpl(private val fixerApi: FixerApi,
                            private val db: XChangeDatabase,
                            rxPrefs: RxSharedPreferences) : XChangeRepository {

    private val executor = Executors.newSingleThreadExecutor()
    private val baseCurrencyCode = rxPrefs.getString("baseCurrencyCode")
    private val relatedCurrencyCode = rxPrefs.getString("relatedCurrencyCode")

    override val selectedCurrencyPair: LiveData<Pair<Currency, Currency>> by lazy {
        val firstObs = baseCurrencyCode.asObservable()
        val secondObs = relatedCurrencyCode.asObservable()
        val result: Observable<Pair<Currency, Currency>> = Observable.combineLatest(firstObs, secondObs, BiFunction { t1, t2 ->
            Pair(Currency(t1), Currency(t2))
        })

        LiveDataReactiveStreams.fromPublisher(result.toFlowable(BackpressureStrategy.LATEST))
    }

    private val _availableCurrencies = MediatorLiveData<CurrenciesData>()
    override val availableCurrencies: LiveData<CurrenciesData>
        get() = _availableCurrencies

    init {
        updateCurrencies()
    }

    override fun selectBaseCurrency(currency: Currency) = baseCurrencyCode.set(currency.code)

    override fun selectRelatedCurrency(currency: Currency) = relatedCurrencyCode.set(currency.code)

    override fun getRateHistory(currencyPair: Pair<Currency, Currency>): LiveData<List<RateEntry>> {
        return db.ratesDao().getRates(currencyPair.first.code, currencyPair.second.code)
    }

    override fun updateRateHistory(currencyPair: Pair<Currency, Currency>) {
        val baseCurrencyCode = currencyPair.first.code
        val relatedCurrencyCode = currencyPair.second.code
        fixerApi.updateRateHistory(baseCurrencyCode, relatedCurrencyCode)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (it != null && it.rates.containsKey(relatedCurrencyCode)) {
                        val rate = it.rates[relatedCurrencyCode] ?: -1f
                        db.ratesDao().addRate(RateEntry(baseCurrencyCode, relatedCurrencyCode, rate, it.date))
                    }
                }, {

                })
    }

    override fun clearData(): Completable = Completable.fromAction {
        db.ratesDao().deleteAllRates()
        db.currenciesDao().deleteAllCurrencies()
    }


    override fun updateCurrencies() {
        val dbSource = db.currenciesDao().currencies
        with(_availableCurrencies) {
            postValue(CurrenciesData.Loading)

            addSource(dbSource) {
                if (it == null || it.isEmpty()) {
                    removeSource(dbSource)
                    val netCall = loadCurrenciesFromServer()
                    addSource(netCall) {
                        removeSource(netCall)
                        if (it != null && it != FixerResponse.ERROR) {
                            val list = it.toCurrencyList()
                            executor.execute {
                                db.currenciesDao().setCurrencies(list)
                                updateCurrencies()
                            }
                        } else {
                            postValue(CurrenciesData.Error("error"))
                        }
                    }
                } else {
                    postValue(CurrenciesData.Loaded(it))
                }
            }
        }
    }

    private fun loadCurrenciesFromServer(): LiveData<FixerResponse> {
        val serverCall = LiveDataReactiveStreams.fromPublisher(
                fixerApi.defaultCall()
                        .onErrorReturn { FixerResponse.ERROR }
                        .subscribeOn(Schedulers.io()))

        return serverCall
    }
}
