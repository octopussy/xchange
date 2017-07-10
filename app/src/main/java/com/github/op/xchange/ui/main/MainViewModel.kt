package com.github.op.xchange.ui.main

import android.arch.lifecycle.*
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.entity.QuoteEntry
import com.github.op.xchange.injection.XComponent
import com.github.op.xchange.repository.XChangeRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDateTime
import java.util.*
import javax.inject.Inject

class MainViewModel : ViewModel(), XComponent.Injectable {

    @Inject lateinit var repository: XChangeRepository

    private val _rawHistoryStream by lazy {
        Transformations.switchMap(selectedCurrenciesStream) {
            repository.getQuoteHistory(it.selection)
        }
    }

    val selectedCurrenciesStream by lazy { SelectedCurrenciesLiveData(this, repository) }

    val quotesStream by lazy {
        MediatorLiveData<Pair<List<QuoteVO>?, QuoteVO?>>().apply {
            this.addSource(_rawHistoryStream) {
                val list = makeVOList(it)
                if (list != null) {
                    value = Pair(list, list.firstOrNull())
                }
            }
        }
    }

    val isLoading = MutableLiveData<Boolean>()

    val isNoDataTextVisible by lazy {
        MediatorLiveData<Boolean>().apply {
            fun updateNoDataTextStatus() {
                val isEmpty = _rawHistoryStream.value?.isEmpty() ?: true
                val loading = isLoading.value ?: false
                value = isEmpty && !loading
            }

            addSource(_rawHistoryStream) { updateNoDataTextStatus() }
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