package com.github.op.xchange.entity

import android.content.Context

typealias CurrencyPair = Pair<Currency, Currency>

enum class Currency(val visible: Boolean = true) {
    UNKNOWN(visible = false),
    USD,
    EUR,
    RUB,
    UAH,
    AUD,
    BGN,
    BRL,
    CAD,
    CHF,
    CNY,
    CZK,
    DKK,
    GBP,
    HKD,
    HRK,
    HUF,
    IDR,
    ILS,
    INR,
    JPY,
    KRW,
    MXN,
    MYR,
    NOK,
    NZD,
    PHP,
    PLN,
    RON,
    SEK,
    SGD,
    THB,
    TRY,
    ZAR;

    override fun toString(): String = name

    fun format(context: Context): String {
        val id = context.resources.getIdentifier("currency_${name.toLowerCase()}", "string", context.packageName)
        return if (id > 0) {
            "$name - ${context.resources.getString(id)}"
        } else {
            name
        }
    }

    companion object {
        fun fromString(s: String): Currency {
            try {
                return valueOf(s)
            } catch (_: IllegalArgumentException) {
                return UNKNOWN
            }
        }
    }
}