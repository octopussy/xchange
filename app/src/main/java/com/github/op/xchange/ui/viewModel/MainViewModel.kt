package com.github.op.xchange.ui.viewModel

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.entity.QuoteEntry
import com.github.op.xchange.injection.XComponent
import com.github.op.xchange.repository.XChangeRepository
import com.github.op.xchange.ui.main.QuoteVO
import com.github.op.xchange.ui.main.SelectedCurrenciesVO
import org.threeten.bp.LocalDateTime
import java.util.*
import javax.inject.Inject

class MainViewModel : ViewModel(), XComponent.Injectable {

    @Inject lateinit var repository: XChangeRepository

    private val _rawQuotesStream by lazy {
        Transformations.switchMap(selectedCurrenciesStream) {
            repository.getQuoteHistory(it.selection)
        }
    }

    val selectedCurrenciesStream by lazy {
        MediatorLiveData<SelectedCurrenciesVO>().apply {
            this.addSource(repository.selectedCurrencyPair2) { selectedPair->
                selectedPair?.let {
                    val baseList = Currency.values().toList().filter { it.visible && selectedPair.related != it }
                    val relList = Currency.values().toList().filter { it.visible && selectedPair.base != it }
                    value = SelectedCurrenciesVO(baseList, relList, selectedPair)
                    refreshHistory()
                }
            }
        }
    }

    val quotesStream by lazy {
        android.arch.lifecycle.MediatorLiveData<Pair<List<QuoteVO>?, QuoteVO?>>().apply {
            this.addSource(_rawQuotesStream) {
                val list = makeVOList(it)
                if (list != null) {
                    value = Pair(list, list.firstOrNull())
                }
            }
        }
    }

    val isLoading = android.arch.lifecycle.MutableLiveData<Boolean>()

    val isNoDataTextVisible by lazy {
        MediatorLiveData<Boolean>().apply {
            fun updateNoDataTextStatus() {
                val isEmpty = _rawQuotesStream.value?.isEmpty() ?: true
                val loading = isLoading.value ?: false
                value = isEmpty && !loading
            }

            addSource(_rawQuotesStream) { updateNoDataTextStatus() }
            addSource(isLoading) { updateNoDataTextStatus() }
        }
    }


    fun selectBaseCurrency(currency: Currency) = repository.selectBaseCurrency(currency)

    fun selectRelatedCurrency(currency: Currency) = repository.selectRelatedCurrency(currency)

    fun refreshHistory() {
        if (selectedCurrenciesStream.value != null) {
            isLoading.value = true
            repository.fetchCurrentQuote(selectedCurrenciesStream.value!!.selection) {
                isLoading.postValue(false)
            }
        }
    }

    fun swapCurrencies() {
        repository.swapSelectedCurrencies()
    }

    override fun inject(component: XComponent) {
        component.inject(this)
    }

    private fun makeVOList(l: List<QuoteEntry>?): List<QuoteVO>? {
        if (l == null) return null

        val map = TreeMap<LocalDateTime, QuoteEntry>()
        l.forEach { map.put(it.dateTime, it) }

        val result = mutableListOf<QuoteVO>()

        var pointer = map.pollLastEntry()
        while (pointer != null) {
            val prev = map.lastEntry()
            val thisRate = pointer.value.price
            val diff = if (prev != null) thisRate - prev.value.price else 0f

            result.add(QuoteVO(thisRate, diff, pointer.value.dateTime))
            pointer = map.pollLastEntry()
        }

        return result
    }

}