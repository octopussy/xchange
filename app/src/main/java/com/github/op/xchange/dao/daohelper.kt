package com.github.op.xchange.dao

import com.github.op.xchange.entity.RateEntry

object DaoHelper {

    @JvmField
    val QUERY_RATE_LIST =
            "SELECT * FROM ${RateEntry.TABLE_NAME} WHERE ${RateEntry.FIELD_BASE_CODE} = :baseCode " +
            "AND ${RateEntry.FIELD_RELATED_CODE} = :relatedCode " +
            "ORDER BY ${RateEntry.FIELD_DATE} DESC"
}