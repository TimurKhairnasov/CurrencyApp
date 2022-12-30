package com.example.currencyapp.model

data class CurrencyState(
    var updatedAt: Long = 0,
    var currencyRates: List<CurrencyRate> = emptyList()
)