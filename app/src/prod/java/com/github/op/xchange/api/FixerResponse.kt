package com.github.op.xchange.api

import com.github.op.xchange.entity.Currency

data class FixerResponse(
        val base: String,
        val date: String,
        val rates: Map<String, Float>
) {

    fun toCurrencyList(): List<Currency> = rates.map { Currency(it.key) }.toSet().toList()

    companion object {
        val ERROR = FixerResponse("", "", mapOf())
    }
}