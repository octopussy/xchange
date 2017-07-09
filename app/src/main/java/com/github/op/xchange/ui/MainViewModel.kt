package com.github.op.xchange.ui

import android.arch.lifecycle.*
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.entity.RateEntry
import com.github.op.xchange.injection.XComponent
import com.github.op.xchange.repository.CurrenciesData
import com.github.op.xchange.repository.XChangeRepository
import javax.inject.Inject

class MainViewModel : ViewModel(), XComponent.Injectable {

    @Inject lateinit var repository: XChangeRepository

    private lateinit var _rateHistory: LiveData<List<RateEntry>>

    private var _firstCurrencyState = MediatorLiveData<CurrencySelectionState>()
    private var _secondCurrencyState = MediatorLiveData<CurrencySelectionState>()

    val rateHistory: LiveData<List<RateEntry>> get() = _rateHistory

    val firstCurrencyState: LiveData<CurrencySelectionState>
        get() = _firstCurrencyState

    val secondCurrencyState: LiveData<CurrencySelectionState>
        get() = _secondCurrencyState

    fun selectBaseCurrency(currency: Currency) = repository.selectBaseCurrency(currency)

    fun selectRelatedCurrency(currency: Currency) = repository.selectRelatedCurrency(currency)

    override fun inject(component: XComponent) {
        component.inject(this)

        _rateHistory = Transformations.switchMap(repository.selectedCurrencyPair) { pair ->
            repository.updateRateHistory(pair)
            repository.getRateHistory(pair)
        }

        _firstCurrencyState.addSource(repository.availableCurrencies) { data ->
            when (data) {
                is CurrenciesData.Loading -> {}
                is CurrenciesData.Loaded -> {
                    repository.selectedCurrencyPair.value?.let {
                        _firstCurrencyState.postValue(CurrencySelectionState(data.list, it.first))
                    }
                }
                is CurrenciesData.Error -> {}
            }
        }

        _firstCurrencyState.addSource(repository.selectedCurrencyPair) { pair ->
            repository.availableCurrencies.value?.let {
                if (it is CurrenciesData.Loaded) {
                    _firstCurrencyState.postValue(CurrencySelectionState(it.list, pair!!.first))
                }
            }
        }

        _secondCurrencyState.addSource(repository.availableCurrencies) { data ->
            when (data) {
                is CurrenciesData.Loading -> {}
                is CurrenciesData.Loaded -> {
                    repository.selectedCurrencyPair.value?.let {
                        _secondCurrencyState.postValue(CurrencySelectionState(data.list, it.second))
                    }
                }
                is CurrenciesData.Error -> {}
            }
        }

        _secondCurrencyState.addSource(repository.selectedCurrencyPair) { pair ->
            repository.availableCurrencies.value?.let {
                if (it is CurrenciesData.Loaded) {
                    _secondCurrencyState.postValue(CurrencySelectionState(it.list, pair!!.second))
                }
            }
        }
    }

    override fun onCleared() {
        _firstCurrencyState.removeSource(repository.selectedCurrencyPair)
        _firstCurrencyState.removeSource(repository.availableCurrencies)
        _secondCurrencyState.removeSource(repository.selectedCurrencyPair)
        _secondCurrencyState.removeSource(repository.availableCurrencies)
    }

    data class CurrencySelectionState(val list: List<Currency>, val selectedCurrency: Currency)
}