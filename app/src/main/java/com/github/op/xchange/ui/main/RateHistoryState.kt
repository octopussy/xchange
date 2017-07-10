package com.github.op.xchange.ui.main

sealed class RateHistoryState {
    class Error(val list: List<QuoteVO>?, val throwable: Throwable) : RateHistoryState()
    class Loading(val list: List<QuoteVO>?) : RateHistoryState()
    class Success(val list: List<QuoteVO>) : RateHistoryState()
}