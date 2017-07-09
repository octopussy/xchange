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

    private var currencies = listOf<Currency>()

    sealed class State {
        object Loading : State()
        class Success(val baseList: List<Currency>,
                      val relList: List<Currency>,
                      val selection: CurrencyPair) : State()

        class Error(val throwable: Throwable) : State()
    }


    private var disposable: Disposable? = null

    init {
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
        unsubscribe()
        disposable = null
    }

    private fun subscribe() {
        value = State.Loading
        repository.getAvailableCurrencies2()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result, error ->
                    if (result != null) {
                        currencies = result
                        subscribeToSelection()
                    } else {
                        value = State.Error(error)
                    }
                }
    }

    private fun subscribeToSelection() {
        unsubscribe()
        disposable = repository.selectedCurrencyPair2
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    value = State.Success(currencies, currencies, it)
                }, {
                    value = State.Error(it)
                })
    }

    private fun unsubscribe() {
        disposable?.dispose()
    }
}