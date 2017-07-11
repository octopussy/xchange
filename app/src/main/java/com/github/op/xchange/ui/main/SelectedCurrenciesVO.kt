package com.github.op.xchange.ui.main

import com.github.op.xchange.entity.Currency
import com.github.op.xchange.entity.CurrencyPair

class SelectedCurrenciesVO(val baseList: List<Currency>,
                           val relList: List<Currency>,
                           val selection: CurrencyPair)