package com.github.op.xchange.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.WorkerThread
import android.util.Log
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.github.op.xchange.api.QuoteDTO
import com.github.op.xchange.api.QuotesApi
import com.github.op.xchange.db.XChangeDatabase
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.entity.CurrencyPair
import com.github.op.xchange.entity.QuoteEntry
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

class XChangeRepositoryImpl(private val quotesApi: QuotesApi,
                            private val db: XChangeDatabase,
                            rxPrefs: RxSharedPreferences) : XChangeRepository {

    private val baseCurrencyCode = rxPrefs.getString("baseCurrencyCode")
    private val relatedCurrencyCode = rxPrefs.getString("relatedCurrencyCode")

    override val updateIntervalSecPref = rxPrefs.getLong("updateIntervalSec", 2 * 60 * 60)

    init {
        val baseCode = baseCurrencyCode.get()
        if (baseCode.isBlank() || !Currency.fromString(baseCode).visible) {
            selectBaseCurrency(Currency.USD)
            selectRelatedCurrency(Currency.RUB)
        }

        val firstObs = baseCurrencyCode.asObservable()
        val secondObs = relatedCurrencyCode.asObservable()
        val combined: Observable<CurrencyPair> = Observable.combineLatest(firstObs, secondObs, BiFunction { t1, t2 ->
            CurrencyPair(Currency.fromString(t1), Currency.fromString(t2))
        })

        combined.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    selectedCurrencyPair2.value = it
                }, {
                    Log.e("XChangeRepository", it.localizedMessage)
                })
    }

    override val selectedCurrencyPair: Observable<CurrencyPair>
        get() {
            val firstObs = baseCurrencyCode.asObservable()
            val secondObs = relatedCurrencyCode.asObservable()
            return Observable.combineLatest(firstObs, secondObs, BiFunction { t1, t2 ->
                CurrencyPair(Currency.fromString(t1), Currency.fromString(t2))
            })
        }

    override val selectedCurrencyPair2 = MutableLiveData<CurrencyPair>()

    override fun getQuoteHistory(currencyPair: CurrencyPair): LiveData<List<QuoteEntry>>
            = db.quotesDao().getQuoteHistory(currencyPair.toString())

    override fun selectBaseCurrency(currency: Currency) = baseCurrencyCode.set(currency.name)

    override fun selectRelatedCurrency(currency: Currency) = relatedCurrencyCode.set(currency.name)

    override fun swapSelectedCurrencies() {
        val c = baseCurrencyCode.get()
        baseCurrencyCode.set(relatedCurrencyCode.get())
        relatedCurrencyCode.set(c)
    }

    override fun fetchCurrentQuote(currencyPair: CurrencyPair, callback: (Boolean) -> Unit) {
        quotesApi.getLatestQuote(currencyPair.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { Single.fromCallable { saveQuotes(it) } }
                .subscribe({
                    callback(true)
                }, {
                    callback(false)
                })
    }

    override fun fetchCurrentQuoteForSelectedPair(callback: (Boolean) -> Unit) {
        val p = CurrencyPair(Currency.fromString(baseCurrencyCode.get()), Currency.fromString(relatedCurrencyCode.get()))
        fetchCurrentQuote(p, callback)
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
