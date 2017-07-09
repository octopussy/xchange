package com.github.op.xchange.repository

import android.arch.lifecycle.*
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.github.op.xchange.api.FixerApi
import com.github.op.xchange.db.XChangeDatabase
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.entity.CurrencyPair
import com.github.op.xchange.entity.RateEntry
import io.reactivex.*
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDateTime

class XChangeRepositoryImpl(private val fixerApi: FixerApi,
                            private val db: XChangeDatabase,
                            rxPrefs: RxSharedPreferences,
                            private val localCurrenciesDataSource: CurrenciesDataSource,
                            private val remoteCurrenciesDataSource: CurrenciesDataSource) : XChangeRepository {

    private val baseCurrencyCode = rxPrefs.getString("baseCurrencyCode")
    private val relatedCurrencyCode = rxPrefs.getString("relatedCurrencyCode")

    override val selectedCurrencyPair: Observable<CurrencyPair>
        get() {
            val firstObs = baseCurrencyCode.asObservable()
            val secondObs = relatedCurrencyCode.asObservable()
            return Observable.combineLatest(firstObs, secondObs, BiFunction { t1, t2 ->
                Pair(Currency(t1), Currency(t2))
            })
        }

    override fun selectBaseCurrency(currency: Currency)
            = baseCurrencyCode.set(currency.code)

    override fun selectRelatedCurrency(currency: Currency)
            = relatedCurrencyCode.set(currency.code)

    override fun getLatestRateValue(currencyPair: CurrencyPair): LiveData<RateEntry>
            = db.ratesDao().getLatestRateLiveData(currencyPair.first.code, currencyPair.second.code)

    override fun getLatestRateValue2(currencyPair: CurrencyPair): Observable<RateEntry>
            = db.ratesDao().getLatestRate(currencyPair.first.code, currencyPair.second.code).toObservable()

    override fun getRateHistory(currencyPair: CurrencyPair): Observable<List<RateEntry>> {
        loadLatestRateFromServer(currencyPair)
        return db.ratesDao().getRates(currencyPair.first.code, currencyPair.second.code).toObservable()
    }

    override fun clearData(): Completable = Completable.fromAction {
        db.ratesDao().deleteAllRates()
        db.currenciesDao().deleteAllCurrencies()
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
                            val existEntry = getLatestRateSync(baseCurrencyCode, relatedCurrencyCode)
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

    override fun getAvailableCurrencies(): Single<List<Currency>> {
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
