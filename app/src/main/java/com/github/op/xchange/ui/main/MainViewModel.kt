package com.github.op.xchange.ui.main

import android.arch.lifecycle.ViewModel
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.injection.XComponent
import com.github.op.xchange.repository.XChangeRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainViewModel : ViewModel(), XComponent.Injectable {

    @Inject lateinit var repository: XChangeRepository

    val selectedCurrenciesLiveData by lazy { SelectedCurrenciesLiveData(repository) }

    val rateHistoryLiveData by lazy {
        RatesLiveData(repository).apply {
            addSource(selectedCurrenciesLiveData) {
                it?.let {
                    selectedCurrencies = it.selection
                }
            }
        }
    }

    fun selectBaseCurrency(currency: Currency) = repository.selectBaseCurrency(currency)

    fun selectRelatedCurrency(currency: Currency) = repository.selectRelatedCurrency(currency)

    fun refreshHistory() = rateHistoryLiveData.refreshHistory()

    override fun inject(component: XComponent) {
        component.inject(this)
    }
}