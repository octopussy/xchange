package com.github.op.xchange.ui.main

import android.arch.lifecycle.MediatorLiveData
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.entity.CurrencyPair
import com.github.op.xchange.repository.XChangeRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SelectedCurrenciesLiveData(val repository: XChangeRepository)
    : MediatorLiveData<SelectedCurrenciesLiveData.State>() {

    class State(val baseList: List<Currency>,
                val relList: List<Currency>,
                val selection: CurrencyPair)

    private var disposable: Disposable? = null

    override fun onActive() {
        super.onActive()
        if (disposable == null) {
            subscribe()
        }
    }

    override fun onInactive() {
        super.onInactive()
        unsubscribe()
        if (!hasObservers()) {
            disposable?.dispose()
            disposable = null
        }
    }

    private fun subscribe() {
        unsubscribe()
        disposable = repository.selectedCurrencyPair
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ selectedPair ->
                    val baseList = Currency.values().toList().filter { it.visible && selectedPair.second != it }
                    val relList = Currency.values().toList().filter { it.visible && selectedPair.first != it }
                    value = State(baseList, relList, selectedPair)
                }, {

                })
    }

    private fun unsubscribe() {
    }
}