package com.github.op.xchange.repository

import com.github.op.xchange.api.FixerApi
import com.github.op.xchange.db.XChangeDatabase
import com.github.op.xchange.entity.Currency
import io.reactivex.Single

class RemoteCurrenciesDataSource(private val api: FixerApi) : CurrenciesDataSource {
    override fun getAvailableCurrencies(): Single<List<Currency>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}