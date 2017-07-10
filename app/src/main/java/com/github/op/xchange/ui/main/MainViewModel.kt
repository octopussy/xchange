package com.github.op.xchange.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.entity.RateEntry
import com.github.op.xchange.injection.XComponent
import com.github.op.xchange.repository.Resource
import com.github.op.xchange.repository.ResourceProvider
import com.github.op.xchange.repository.XChangeRepository
import org.threeten.bp.LocalDateTime
import java.util.*
import javax.inject.Inject

class MainViewModel : ViewModel(), XComponent.Injectable {

    @Inject lateinit var repository: XChangeRepository

    private var rateHistoryProvider: ResourceProvider<List<RateEntry>>? = null

    private val _rateHistoryLiveData by lazy {
        Transformations.switchMap(selectedCurrenciesLiveData) {
            rateHistoryProvider = repository.getRateHistoryProvider(it.selection)
            rateHistoryProvider!!.reload()
            rateHistoryProvider!!.result
        }
    }

    val selectedCurrenciesLiveData by lazy { SelectedCurrenciesLiveData(repository) }

    val rateHistoryStateLiveData: LiveData<RateHistoryState> by lazy {
        Transformations.map(_rateHistoryLiveData) {
            when (it) {
                is Resource.Error -> RateHistoryState.Error(makeVOList(it.data), it.throwable)
                is Resource.Loading -> RateHistoryState.Loading(makeVOList(it.data))
                is Resource.Success -> RateHistoryState.Success(makeVOList(it.data)?: listOf())
            }
        }
    }

    fun selectBaseCurrency(currency: Currency) = repository.selectBaseCurrency(currency)

    fun selectRelatedCurrency(currency: Currency) = repository.selectRelatedCurrency(currency)

    fun refreshHistory() { rateHistoryProvider?.reload() }

    fun swapCurrencies() {
        repository.swapCurrencies()
    }

    override fun inject(component: XComponent) {
        component.inject(this)
    }

    private fun makeVOList(l: List<RateEntry>?): List<RateVO>? {
        if (l == null) return null

        val map = TreeMap<LocalDateTime, RateEntry>()
        l.forEach { map.put(it.date, it) }

        val result = mutableListOf<RateVO>()

        var pointer = map.pollLastEntry()
        while (pointer != null) {
            val prev = map.lastEntry()
            val thisRate = pointer.value.rate
            val diff = if (prev != null) thisRate - prev.value.rate else 0f

            result.add(RateVO(thisRate, diff, pointer.value.date))
            pointer = map.pollLastEntry()
        }

        return result
    }

}