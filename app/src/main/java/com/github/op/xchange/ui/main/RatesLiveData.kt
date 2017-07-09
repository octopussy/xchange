package com.github.op.xchange.ui.main

import android.arch.lifecycle.MediatorLiveData
import com.github.op.xchange.entity.CurrencyPair
import com.github.op.xchange.entity.RateEntry
import com.github.op.xchange.repository.XChangeRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

class RatesLiveData(val repository: XChangeRepository)
    : MediatorLiveData<RatesLiveData.State>() {

    sealed class State {
        object Loading : State()
        class Success(val latestRate: RateEntry, val list: List<RateEntry>) : State()
        class Error(val throwable: Throwable) : State()
    }

    private var disposable: Disposable? = null

    var selectedCurrencies: CurrencyPair? = null
        set(value) {
            field = value
            this.value = State.Loading
            subscribe()
        }

    override fun onActive() {
        super.onActive()
        if (disposable == null) {
            subscribe()
        }
    }

    override fun onInactive() {
        super.onInactive()
        if (!hasObservers()) {
            disposable?.dispose()
            disposable = null
        }
    }

    private fun subscribe() {
        class Combine(val latestRate: RateEntry, val list: List<RateEntry>)

        if (selectedCurrencies != null) {
            disposable?.dispose()
            val latest = repository.getLatestRateValue2(selectedCurrencies!!)
            val history = repository.getRateHistory(selectedCurrencies!!)
            disposable = Observable.combineLatest(latest, history,
                    BiFunction<RateEntry, List<RateEntry>, Combine> { l, h ->
                        Combine(l, h)
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        this.value = State.Success(it.latestRate, it.list)
                    }, {
                        this.value = State.Error(it)
                    })
        }
    }
}