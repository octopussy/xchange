package com.github.op.xchange.api

data class ApiResponse(val base: String, val rates: Map<String, Float>)