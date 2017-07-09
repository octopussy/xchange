package com.github.op.xchange.ui.main

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.injection.XComponent
import com.github.op.xchange.repository.XChangeRepository
import javax.inject.Inject

class MainViewModel : ViewModel(), XComponent.Injectable {

    @Inject lateinit var repository: XChangeRepository

    val selectedCurrenciesLiveData by lazy { SelectedCurrenciesLiveData(repository) }

    val rateHistory2 by lazy {
        RatesLiveData(repository).apply {
            addSource(selectedCurrenciesLiveData) {
                it?.let {
                    selectedCurrencies =
                            if (it is SelectedCurrenciesLiveData.State.Success)
                                it.selection
                            else
                                null
                }
            }
        }
    }

    val lastRateValue by lazy {
        MediatorLiveData<Float>().apply {
            addSource(rateHistory2) {
                if (it is RatesLiveData.State.Success) {
                    value = it.list.maxBy { it.date }?.rate
                }
            }
        }
    }

    fun selectBaseCurrency(currency: Currency) = repository.selectBaseCurrency(currency)

    fun selectRelatedCurrency(currency: Currency) = repository.selectRelatedCurrency(currency)

    override fun inject(component: XComponent) {
        component.inject(this)

        /*_lastRateValue.addSource(_rateHistory) {
            if (it != null && it.isNotEmpty()) {
                _lastRateValue.postValue(it.first().rate.toString())
            }
        }*/
    }

    fun retryLoading() {
        repository.refreshAvailableCurrencies()
    }
}