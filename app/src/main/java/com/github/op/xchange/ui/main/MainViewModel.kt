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

    //private var quoteHistoryProvider: ResourceProvider<List<QuoteEntry>>? = null

    private val _rateHistoryResource by lazy {
        Transformations.switchMap(selectedCurrenciesLiveData) {
            repository.getQuoteHistory(it.selection)
        }
    }

    val selectedCurrenciesLiveData by lazy { SelectedCurrenciesLiveData(repository) }

    val rateHistoryList by lazy {
        MediatorLiveData<Pair<List<QuoteVO>?, QuoteVO?>>().apply {
            this.addSource(_rateHistoryResource) {
                val list = makeVOList(it)
                if (list != null) {
                    value = Pair(list, list.firstOrNull())
                }
            }
        }
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    val isNoDataTextVisible by lazy {
        MediatorLiveData<Boolean>().apply {
            /*addSource(_rateHistoryResource) {
                it?.let {
                    val listEmpty = it.data?.isEmpty() ?: true
                    value = (it !is Resource.Loading) && listEmpty
                }
            }*/
        }
    }

    fun selectBaseCurrency(currency: Currency) = repository.selectBaseCurrency(currency)

    fun selectRelatedCurrency(currency: Currency) = repository.selectRelatedCurrency(currency)

    fun refreshHistory() {
        if (selectedCurrenciesLiveData.value != null) {
            _isLoading.value = true
            repository.fetchCurrentQuote(selectedCurrenciesLiveData.value!!.selection)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorComplete()
                    .subscribe {
                        _isLoading.value = false
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