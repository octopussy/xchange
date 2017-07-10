package com.github.op.xchange.repository

import android.arch.lifecycle.LiveData
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.github.op.xchange.api.RemoteApi
import com.github.op.xchange.db.XChangeDatabase
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.entity.CurrencyPair
import com.github.op.xchange.entity.RateEntry
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDateTime

class XChangeRepositoryImpl(private val remoteApi: RemoteApi,
                            private val db: XChangeDatabase,
                            rxPrefs: RxSharedPreferences) : XChangeRepository {

    private val baseCurrencyCode = rxPrefs.getString("baseCurrencyCode")
    private val relatedCurrencyCode = rxPrefs.getString("relatedCurrencyCode")

    init {
        val baseCode = baseCurrencyCode.get()
        if (baseCode.isBlank() || !Currency.fromString(baseCode).visible) {
            selectBaseCurrency(Currency.USD)
            selectRelatedCurrency(Currency.RUB)
        }
    }

    override val selectedCurrencyPair: Observable<CurrencyPair>
        get() {
            val firstObs = baseCurrencyCode.asObservable()
            val secondObs = relatedCurrencyCode.asObservable()
            return Observable.combineLatest(firstObs, secondObs, BiFunction { t1, t2 ->
                Pair(Currency.fromString(t1), Currency.fromString(t2))
            })
        }

    override fun selectBaseCurrency(currency: Currency) = baseCurrencyCode.set(currency.name)

    override fun selectRelatedCurrency(currency: Currency) = relatedCurrencyCode.set(currency.name)

    override fun getRateHistory(currencyPair: CurrencyPair): Observable<List<RateEntry>> {
        val obs = db.ratesDao().getRates(currencyPair.first.name, currencyPair.second.name).toObservable()
        refreshRateHistory(currencyPair)
                .subscribeOn(Schedulers.io())
                .subscribe({}, {})
        return obs
    }

    override fun clearData(): Completable = Completable.fromAction {
        db.ratesDao().deleteAllRates()
    }

    private fun refreshRateHistory(currencyPair: CurrencyPair): Completable {
        val baseCurrencyCode = currencyPair.first.name
        val relatedCurrencyCode = currencyPair.second.name
        return remoteApi.getLatestRate(baseCurrencyCode, relatedCurrencyCode)
                .doOnSuccess {
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
                }.toCompletable()
    }
}
