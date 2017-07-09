package com.github.op.xchange.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.entity.RateEntry
import com.github.op.xchange.injection.XComponent
import com.github.op.xchange.repository.XChangeRepository
import javax.inject.Inject

class MainViewModel : ViewModel(), XComponent.Injectable {

    @Inject lateinit var repository: XChangeRepository

    val selectedCurrenciesLiveData by lazy { SelectedCurrenciesLiveData(repository) }

    val mainState2 by lazy {
        MediatorLiveData<MainState>().apply {
            addSource(selectedCurrenciesLiveData) {
                postValue(MainState.LoadingCurrencies)
                it?.let {
                    //it.first?.
                }
            }
        }
    }

    private lateinit var _rateHistory: LiveData<List<RateEntry>>
    val rateHistory: LiveData<List<RateEntry>> get() = _rateHistory

    private val _lastRateValue = MediatorLiveData<String>()
    val lastRateValue: LiveData<String>
        get() = _lastRateValue

    fun selectBaseCurrency(currency: Currency) = repository.selectBaseCurrency(currency)

    fun selectRelatedCurrency(currency: Currency) = repository.selectRelatedCurrency(currency)

    override fun inject(component: XComponent) {
        component.inject(this)

        _rateHistory = Transformations.switchMap(repository.selectedCurrencyPair) { pair ->
            return@switchMap repository.getRateHistory(pair)
        }

        _lastRateValue.addSource(_rateHistory) {
            if (it != null && it.isNotEmpty()) {
                _lastRateValue.postValue(it.first().rate.toString())
            }
        }
    }

    fun retryLoading() {
        repository.refreshAvailableCurrencies()
    }

    sealed class MainState {
        object LoadingCurrencies : MainState()
        object Loaded : MainState()
        object Error : MainState()
    }
}