package com.github.op.xchange.ui.main

import android.arch.lifecycle.MediatorLiveData
import com.github.op.xchange.entity.CurrencyPair
import com.github.op.xchange.entity.RateEntry
import com.github.op.xchange.repository.XChangeRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class RatesLiveData(val repository: XChangeRepository)
    : MediatorLiveData<RatesLiveData.State>() {

    sealed class State {
        object Loading : State()
        class Success(val list: List<RateEntry>) : State()
        class Error(val throwable: Throwable): State()
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
        if (selectedCurrencies != null) {
            disposable?.dispose()
            disposable = repository.getRateHistory2(selectedCurrencies!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        this.value = State.Success(it)
                    }, {
                        this.value = State.Error(it)
                    })
        }
    }
}