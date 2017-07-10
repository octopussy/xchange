package com.github.op.xchange.repository

import android.arch.lifecycle.LiveData
import android.support.annotation.WorkerThread
import android.util.Log
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.github.op.xchange.api.QuoteDTO
import com.github.op.xchange.api.RemoteApi
import com.github.op.xchange.db.XChangeDatabase
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.entity.CurrencyPair
import com.github.op.xchange.entity.QuoteEntry
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

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
                CurrencyPair(Currency.fromString(t1), Currency.fromString(t2))
            })
        }

    override fun getQuoteHistory(currencyPair: CurrencyPair): LiveData<List<QuoteEntry>>
            = db.quotesDao().getQuoteHistory(currencyPair.toString())

    override fun selectBaseCurrency(currency: Currency) = baseCurrencyCode.set(currency.name)

    override fun selectRelatedCurrency(currency: Currency) = relatedCurrencyCode.set(currency.name)

    override fun swapSelectedCurrencies() {
        val c = baseCurrencyCode.get()
        baseCurrencyCode.set(relatedCurrencyCode.get())
        relatedCurrencyCode.set(c)
    }

    override fun fetchCurrentQuote(currencyPair: CurrencyPair): Completable {
        return remoteApi.getLatestQuote(currencyPair.toString())
                .doOnError { Log.e("xchange", it.localizedMessage) }
                .doOnSuccess { saveQuotes(it) }.toCompletable()
    }

    @WorkerThread
    private fun saveQuotes(l: List<QuoteDTO>) {
        l.forEach {
            with(db.quotesDao()) {
                val existQuotes = getQuotesByTimeSync(it.pair, it.dateTime)
                if (existQuotes.isEmpty()) {
                    addQuoteEntry(QuoteEntry(it.pair, it.price, it.dateTime))
                }
            }
        }

    }

    override fun clearData(): Completable = Completable.fromAction {
        db.quotesDao().deleteAllRates()
    }
}
