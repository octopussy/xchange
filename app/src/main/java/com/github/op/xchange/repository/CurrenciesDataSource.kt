package com.github.op.xchange.repository

import com.github.op.xchange.entity.Currency
import io.reactivex.Single

interface CurrenciesDataSource {
    fun getAvailableCurrencies2(): Single<List<Currency>>
}