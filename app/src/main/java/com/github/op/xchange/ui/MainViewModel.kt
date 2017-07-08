package com.github.op.xchange.ui

import android.arch.lifecycle.*
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.entity.RateEntry
import com.github.op.xchange.injection.XComponent
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

    fun selectFirstCurrency(currency: Currency) = repository.selectFirstCurrency(currency)

    fun selectSecondCurrency(currency: Currency) = repository.selectSecondCurrency(currency)

    override fun inject(component: XComponent) {
        component.inject(this)

        _rateHistory = Transformations.switchMap(repository.selectedCurrencyPair) { pair ->
            repository.getRateHistory(pair)
        }

        _firstCurrencyState.addSource(repository.availableCurrencies) { list ->
            repository.selectedCurrencyPair.value?.let {
                _firstCurrencyState.postValue(CurrencySelectionState(list!!, it.first))
            }
        }

        _firstCurrencyState.addSource(repository.selectedCurrencyPair) { pair ->
            repository.availableCurrencies.value?.let {
                _firstCurrencyState.postValue(CurrencySelectionState(it, pair!!.first))
            }
        }

        _secondCurrencyState.addSource(repository.availableCurrencies) { list ->
            repository.selectedCurrencyPair.value?.let {
                _secondCurrencyState.postValue(CurrencySelectionState(list!!, it.second))
            }
        }

        _secondCurrencyState.addSource(repository.selectedCurrencyPair) { pair ->
            repository.availableCurrencies.value?.let {
                _secondCurrencyState.postValue(CurrencySelectionState(it, pair!!.second))
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