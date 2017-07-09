package com.github.op.xchange.ui.main

import com.github.op.xchange.entity.RateEntry
import com.github.op.xchange.repository.CurrenciesData

class MainViewModel : android.arch.lifecycle.ViewModel(), com.github.op.xchange.injection.XComponent.Injectable {

    @javax.inject.Inject lateinit var repository: com.github.op.xchange.repository.XChangeRepository

    private lateinit var _rateHistory: android.arch.lifecycle.LiveData<List<RateEntry>>

    private var _baseCurrencyState = android.arch.lifecycle.MediatorLiveData<CurrencySelectionState>()
    private var _relCurrencyState = android.arch.lifecycle.MediatorLiveData<CurrencySelectionState>()

    val rateHistory: android.arch.lifecycle.LiveData<List<RateEntry>> get() = _rateHistory

    val firstCurrencyState: android.arch.lifecycle.LiveData<CurrencySelectionState>
        get() = _baseCurrencyState

    val secondCurrencyState: android.arch.lifecycle.LiveData<CurrencySelectionState>
        get() = _relCurrencyState

    fun selectBaseCurrency(currency: com.github.op.xchange.entity.Currency) = repository.selectBaseCurrency(currency)

    fun selectRelatedCurrency(currency: com.github.op.xchange.entity.Currency) = repository.selectRelatedCurrency(currency)

    override fun inject(component: com.github.op.xchange.injection.XComponent) {
        component.inject(this)

        _rateHistory = android.arch.lifecycle.Transformations.switchMap(repository.selectedCurrencyPair) { pair ->
            repository.updateRateHistory(pair)
            repository.getRateHistory(pair)
        }

        _baseCurrencyState.addSource(repository.availableCurrencies) { data ->
            when (data) {
                is CurrenciesData.Loading -> {}
                is CurrenciesData.Loaded -> {
                    repository.selectedCurrencyPair.value?.let {
                        _baseCurrencyState.postValue(MainViewModel.CurrencySelectionState(data.list, it.first))
                    }
                }
                is com.github.op.xchange.repository.CurrenciesData.Error -> {}
            }
        }

        _baseCurrencyState.addSource(repository.selectedCurrencyPair) { pair ->
            repository.availableCurrencies.value?.let {
                if (it is CurrenciesData.Loaded) {
                    _baseCurrencyState.postValue(MainViewModel.CurrencySelectionState(it.list, pair!!.first))
                }
            }
        }

        _relCurrencyState.addSource(repository.availableCurrencies) { data ->
            when (data) {
                is CurrenciesData.Loading -> {}
                is CurrenciesData.Loaded -> {
                    repository.selectedCurrencyPair.value?.let {
                        _relCurrencyState.postValue(MainViewModel.CurrencySelectionState(data.list, it.second))
                    }
                }
                is CurrenciesData.Error -> {}
            }
        }

        _relCurrencyState.addSource(repository.selectedCurrencyPair) { pair ->
            repository.availableCurrencies.value?.let {
                if (it is CurrenciesData.Loaded) {
                    _relCurrencyState.postValue(MainViewModel.CurrencySelectionState(it.list, pair!!.second))
                }
            }
        }
    }

    override fun onCleared() {
        _baseCurrencyState.removeSource(repository.selectedCurrencyPair)
        _baseCurrencyState.removeSource(repository.availableCurrencies)
        _relCurrencyState.removeSource(repository.selectedCurrencyPair)
        _relCurrencyState.removeSource(repository.availableCurrencies)
    }

    data class CurrencySelectionState(val list: List<com.github.op.xchange.entity.Currency>, val selectedCurrency: com.github.op.xchange.entity.Currency)
}