package com.github.op.xchange.repository

import com.github.op.xchange.entity.Currency

sealed class CurrenciesData {
    object Loading : CurrenciesData()
    class Error(val message: String) : CurrenciesData()
    class Loaded(val list: List<Currency>): CurrenciesData()
}