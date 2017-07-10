package com.github.op.xchange.entity

typealias CurrencyPair = Pair<Currency, Currency>

enum class Currency(val visible:Boolean = true) {
    UNKNOWN(visible = false),
    USD,
    EUR,
    UAH,
    RUB,
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

    companion object {
        fun fromString(s: String): Currency {
            try {
                return valueOf(s)
            } catch (_: IllegalArgumentException){
                return UNKNOWN
            }
        }
    }
}