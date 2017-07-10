package com.github.op.xchange.repository

import android.arch.lifecycle.LiveData
import android.support.annotation.WorkerThread
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

    override fun swapCurrencies() {
        val c = baseCurrencyCode.get()
        baseCurrencyCode.set(relatedCurrencyCode.get())
        relatedCurrencyCode.set(c)
    }

    override fun getRateHistoryProvider(currencyPair: CurrencyPair): ResourceProvider<List<RateEntry>> {
        return object : NetworkResourceProvider<List<RateEntry>, RateEntry>() {
            override fun saveCallResult(item: RateEntry) {
                insertOrUpdateRate(currencyPair, item)
            }

            override fun shouldFetch(data: List<RateEntry>?): Boolean = true

            override fun loadFromDb(): LiveData<List<RateEntry>> {
                return db.ratesDao().getRatesLD(currencyPair.first.name, currencyPair.second.name)
            }

            override fun createApiCall(): Single<RateEntry> {
                val baseCode = currencyPair.first.name
                val relCode = currencyPair.second.name
                return remoteApi.getLatestRate(currencyPair.first.name, currencyPair.second.name).map {
                    val rate = it.rates[relCode] ?: throw RuntimeException("Unknown currency")
                    return@map RateEntry(baseCode, relCode, rate, LocalDateTime.now())
                }
            }
        }
    }

    override fun clearData(): Completable = Completable.fromAction {
        db.ratesDao().deleteAllRates()
    }

    @WorkerThread
    private fun insertOrUpdateRate(currencyPair: CurrencyPair, item: RateEntry) {
        val baseCode = currencyPair.first.name
        val relCode = currencyPair.second.name

        with(db.ratesDao()) {
            val existEntry = getLatestRateSync(baseCode, relCode)
            val now = LocalDateTime.now()
            if (existEntry == null || existEntry.rate != item.rate) {
                addRate(item)
            } else {
                existEntry.date = now
                updateRate(existEntry)
            }
        }
    }
}
