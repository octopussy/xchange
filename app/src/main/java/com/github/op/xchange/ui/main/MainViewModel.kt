package com.github.op.xchange.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
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

    private val _rateHistoryResource by lazy {
        Transformations.switchMap(selectedCurrenciesLiveData) {
            rateHistoryProvider = repository.getRateHistoryProvider(it.selection)
            rateHistoryProvider!!.reload()
            rateHistoryProvider!!.result
        }
    }

    val selectedCurrenciesLiveData by lazy { SelectedCurrenciesLiveData(repository) }

    val rateHistoryList by lazy {
        MediatorLiveData<Pair<List<RateVO>?, RateVO?>>().apply {
            this.addSource(_rateHistoryResource) {
                val list = makeVOList(it?.data)
                if (list != null) {
                    value = Pair(list, list.firstOrNull())
                }
            }
        }
    }

    val isLoading by lazy {
        MediatorLiveData<Boolean>().apply {
            addSource(_rateHistoryResource) {
                it?.let { value = it is Resource.Loading }
            }
        }
    }

    val isNoDataTextVisible by lazy {
        MediatorLiveData<Boolean>().apply {
            addSource(_rateHistoryResource) {
                it?.let {
                    val listEmpty = it.data?.isEmpty() ?: true
                    value = (it !is Resource.Loading) && listEmpty
                }
            }
        }
    }

    fun selectBaseCurrency(currency: Currency) = repository.selectBaseCurrency(currency)

    fun selectRelatedCurrency(currency: Currency) = repository.selectRelatedCurrency(currency)

    fun refreshHistory() {
        rateHistoryProvider?.reload()
    }

    fun swapCurrencies() {
        repository.swapSelectedCurrencies()
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