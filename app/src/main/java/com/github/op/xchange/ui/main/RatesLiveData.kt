package com.github.op.xchange.ui.main

import android.arch.lifecycle.MediatorLiveData
import com.github.op.xchange.entity.CurrencyPair
import com.github.op.xchange.entity.RateEntry
import com.github.op.xchange.repository.XChangeRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDateTime
import java.util.*

class RatesLiveData(val repository: XChangeRepository)
    : MediatorLiveData<RatesLiveData.State>() {

    sealed class State {
        object Loading : State()
        object SuccessEmpty : State()
        class Success(val latestRate: RateEntry, val list: List<RateVO>) : State()
        class Error(val throwable: Throwable) : State()
    }

    private var disposable: Disposable? = null

    var selectedCurrencies: CurrencyPair? = null
        set(value) {
            if (this.value == State.Loading) return
            field = value
            this.value = State.Loading
            subscribe()
        }

    override fun onActive() {
        super.onActive()
        if (disposable == null) {
            subscribe()
        }
    }

    override fun onInactive() {
        super.onInactive()
        if (!hasObservers()) {
            disposable?.dispose()
            disposable = null
        }
    }

    fun refreshHistory() {
        subscribe()
    }

    private fun subscribe() {
        if (selectedCurrencies != null) {
            disposable?.dispose()
            disposable = repository.getRateHistory(selectedCurrencies!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ rates ->
                        val latestRate = rates.maxBy { it.date }
                        if (latestRate != null) {
                            this.value = State.Success(latestRate, makeVOList(rates))
                        } else {
                            this.value = State.SuccessEmpty
                        }
                    }, {
                        this.value = State.Error(it)
                    })
        }
    }

    private fun makeVOList(l: List<RateEntry>): List<RateVO> {
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