package com.github.op.xchange.ui.main

sealed class RateHistoryState {
    class Error(val list: List<RateVO>?, val throwable: Throwable) : RateHistoryState()
    class Loading(val list: List<RateVO>?) : RateHistoryState()
    class Success(val list: List<RateVO>) : RateHistoryState()
}