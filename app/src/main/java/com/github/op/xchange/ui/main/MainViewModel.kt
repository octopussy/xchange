package com.github.op.xchange.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Transformations
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.entity.RateEntry
import com.github.op.xchange.injection.XComponent
import com.github.op.xchange.repository.CurrenciesData
import com.github.op.xchange.repository.XChangeRepository

class MainViewModel : android.arch.lifecycle.ViewModel(), XComponent.Injectable {

    @javax.inject.Inject lateinit var repository: XChangeRepository

    private lateinit var _rateHistory: LiveData<List<RateEntry>>
    val rateHistory: LiveData<List<RateEntry>> get() = _rateHistory

    private val _state = MediatorLiveData<MainState>()
    val state: LiveData<MainState>
        get() = _state

    private var _baseCurrencyState = MediatorLiveData<CurrencySelectionState>()
    val baseCurrencySpinnerState: LiveData<CurrencySelectionState>
        get() = _baseCurrencyState

    private var _relCurrencyState = MediatorLiveData<CurrencySelectionState>()
    val relCurrencySpinnerState: android.arch.lifecycle.LiveData<CurrencySelectionState>
        get() = _relCurrencyState

    fun selectBaseCurrency(currency: Currency) = repository.selectBaseCurrency(currency)

    fun selectRelatedCurrency(currency: Currency) = repository.selectRelatedCurrency(currency)

    override fun inject(component: XComponent) {
        component.inject(this)

        updateCurrencies()

        _rateHistory = Transformations.switchMap(repository.selectedCurrencyPair) { pair ->
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
                is CurrenciesData.Error -> {}
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

    fun retryLoading() {
        updateCurrencies()
    }

    private fun updateCurrencies() {
        _state.value = MainState.LoadingCurrencies
        _state.removeSource(repository.availableCurrencies)
        _state.addSource(repository.availableCurrencies) { data ->
            when (data) {
                is CurrenciesData.Loading -> _state.postValue(MainState.LoadingCurrencies)
                is CurrenciesData.Loaded -> {
                    _state.postValue(MainState.Loaded)
                    /*repository.selectedCurrencyPair.value?.let {
                        _baseCurrencyState.postValue(MainViewModel.CurrencySelectionState(data.list, it.first))
                    }*/
                }
                is CurrenciesData.Error -> _state.postValue(MainState.Error)
            }
        }

        repository.updateCurrencies()
    }

    override fun onCleared() {
        _baseCurrencyState.removeSource(repository.selectedCurrencyPair)
        _baseCurrencyState.removeSource(repository.availableCurrencies)
        _relCurrencyState.removeSource(repository.selectedCurrencyPair)
        _relCurrencyState.removeSource(repository.availableCurrencies)
    }
    sealed class MainState {
        object LoadingCurrencies : MainState()
        object Loaded : MainState()
        object Error : MainState()

    }

    data class CurrencySelectionState(val list: List<Currency>, val selectedCurrency: Currency)
}