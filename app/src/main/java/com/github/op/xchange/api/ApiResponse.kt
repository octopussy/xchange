package com.github.op.xchange.api

import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDateTime

data class ApiResponse(val base: String, val rates: Map<String, Float>)

data class QuoteDTO(@SerializedName("symbol") val pair: String,
                    @SerializedName("timestamp") val dateTime: LocalDateTime,
                    val price: Float)