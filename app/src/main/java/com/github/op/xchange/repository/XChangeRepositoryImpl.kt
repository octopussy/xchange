package com.github.op.xchange.repository

import android.arch.lifecycle.*
import android.util.Log
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.github.op.xchange.api.FixerApi
import com.github.op.xchange.api.FixerResponse
import com.github.op.xchange.db.XChangeDatabase
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.entity.CurrencyPair
import com.github.op.xchange.entity.RateEntry
import io.reactivex.*
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDateTime
import java.util.concurrent.Executors

class XChangeRepositoryImpl(private val fixerApi: FixerApi,
                            private val db: XChangeDatabase,
                            rxPrefs: RxSharedPreferences,
                            private val localCurrenciesDataSource: CurrenciesDataSource,
                            private val remoteCurrenciesDataSource: CurrenciesDataSource) : XChangeRepository {

    private val executor = Executors.newSingleThreadExecutor()
    private val baseCurrencyCode = rxPrefs.getString("baseCurrencyCode")
    private val relatedCurrencyCode = rxPrefs.getString("relatedCurrencyCode")

    override val selectedCurrencyPair2: Observable<CurrencyPair>
        get() {
            val firstObs = baseCurrencyCode.asObservable()
            val secondObs = relatedCurrencyCode.asObservable()
            return Observable.combineLatest(firstObs, secondObs, BiFunction { t1, t2 ->
                Pair(Currency(t1), Currency(t2))
            })
        }

    override val selectedCurrencyPair: LiveData<CurrencyPair> by lazy {
        LiveDataReactiveStreams.fromPublisher(selectedCurrencyPair2.toFlowable(BackpressureStrategy.LATEST))
    }

    private val _availableCurrencies = MediatorLiveData<CurrenciesData>()
    override val availableCurrencies: LiveData<CurrenciesData>
        get() = _availableCurrencies

    init {
        refreshAvailableCurrencies()
    }

    override fun selectBaseCurrency(currency: Currency)
            = baseCurrencyCode.set(currency.code)

    override fun selectRelatedCurrency(currency: Currency)
            = relatedCurrencyCode.set(currency.code)

    override fun getRateHistory(currencyPair: CurrencyPair): LiveData<List<RateEntry>> {
        loadLatestRateFromServer(currencyPair)
        return db.ratesDao().getRates(currencyPair.first.code, currencyPair.second.code)
    }

    override fun getRateHistory2(currencyPair: CurrencyPair): Observable<List<RateEntry>> {
        loadLatestRateFromServer(currencyPair)
        return db.ratesDao().getRates2(currencyPair.first.code, currencyPair.second.code).toObservable()
    }

    override fun clearData(): Completable = Completable.fromAction {
        db.ratesDao().deleteAllRates()
        db.currenciesDao().deleteAllCurrencies()
    }

    override fun refreshAvailableCurrencies() {
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
                                refreshAvailableCurrencies()
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
                        .onErrorReturn {
                            Log.e("XChangeRepository", "load currencies error", it)
                            FixerResponse.ERROR
                        }
                        .subscribeOn(Schedulers.io()))

        return serverCall
    }

    private fun loadLatestRateFromServer(currencyPair: CurrencyPair) {
        val baseCurrencyCode = currencyPair.first.code
        val relatedCurrencyCode = currencyPair.second.code
        fixerApi.fetchLatestRate(baseCurrencyCode, relatedCurrencyCode)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (it != null && it.rates.containsKey(relatedCurrencyCode)) {
                        val rate = it.rates[relatedCurrencyCode] ?: -1f
                        with(db.ratesDao()) {
                            val existEntry = getLastRateSync(baseCurrencyCode, relatedCurrencyCode)
                            val now = LocalDateTime.now()
                            if (existEntry == null || existEntry.rate != rate) {
                                val newEntry = RateEntry(baseCurrencyCode, relatedCurrencyCode, rate, now)
                                addRate(newEntry)
                            } else {
                                existEntry.date = now
                                updateRate(existEntry)
                            }
                        }
                    }
                }, {

                })
    }

    override fun getAvailableCurrencies2(): Single<List<Currency>> {
        return Single.fromCallable { db.currenciesDao().currencies2 }
                .flatMap {
                    if (it.isEmpty()) {
                        fixerApi.defaultCall2()
                                .map { it.toCurrencyList() }
                                .doOnSuccess {
                                    db.currenciesDao().setCurrencies(it)
                                }
                    } else {
                        Single.just(it)
                    }
                }
    }

}
