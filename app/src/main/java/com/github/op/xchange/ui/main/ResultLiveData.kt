package com.github.op.xchange.ui.main

import android.arch.lifecycle.MediatorLiveData
import com.github.op.xchange.entity.CurrencyPair
import com.github.op.xchange.entity.RateEntry
import com.github.op.xchange.repository.XChangeRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ResultLiveData(val repository: XChangeRepository)
    : MediatorLiveData<Pair<List<RateEntry>?, Throwable?>>() {

    private var disposable: Disposable? = null

    var selectedCurrencies: CurrencyPair? = null
        set(value) {
            field = value
            if (value == null) return

            unsubscribe()

            disposable = repository.getRateHistory2(value)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        this.value = Pair(it, null)
                    }, {
                        this.value = Pair(null, it)
                    })
        }

    override fun onInactive() {
        super.onInactive()
        unsubscribe()
    }

    private fun unsubscribe() {
        disposable?.dispose()
    }
}