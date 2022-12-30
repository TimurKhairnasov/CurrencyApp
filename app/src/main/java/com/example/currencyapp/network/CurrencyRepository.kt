package com.example.currencyapp.network

import com.example.currencyapp.model.CurrencyRate
import timber.log.Timber

class CurrencyRepository {

    companion object {
        private const val USD = "USD"
        private const val RUB = "RUB"
        private const val KZT = "KZT"
        const val AMOUNT = "amount"
        const val COUNT = "count"
    }

    private fun getCurrencyFromResponse(response: String, currencySymbol: String): Double {
        val map = response.split("Я получу").drop(1).map {
            val firstIteration = it.substring(0, it.indexOf("$currencySymbol</b>"))
            firstIteration.substring(firstIteration.lastIndexOf("<b>") + 3).replace(" ", "").trim()
        }.filterNot { it.isEmpty() }
        return map.firstOrNull()?.toDouble() ?: 0.0
    }

    private suspend fun getExchangeCount(
        count: Double,
        currencyFrom: String,
        currencyTo: String,
        currencySymbol: String,
        name: String
    ) = kotlin.runCatching {
        val response = API.currency.getExchangedCount(
            count = count.toLong(),
            currencyFrom = currencyFrom,
            currencyTo = currencyTo
        ).string()

        val amount =  getCurrencyFromResponse(response, currencySymbol)
        CurrencyRate(
            count = count,
            mutableCount = count,
            currencyFrom = currencyFrom,
            currencyTo = currencyTo,
            amount = amount,
            mutableAmount = amount,
            symbol = currencySymbol,
            name = name,
            lastInput = ""
        )
    }.getOrNull()

    suspend fun getAll(): Pair<Long, List<CurrencyRate>> = kotlin.runCatching {

        val list = listOfNotNull(
            getExchangeCount(
                count = 1.0,
                currencyFrom = USD,
                currencyTo = RUB,
                currencySymbol = "₽",
                name = "$/₽"
            ), getExchangeCount(
                count = 1.0,
                currencyFrom = RUB,
                currencyTo = KZT,
                currencySymbol = "₸",
                name = "₽/₸"
            ),
            getExchangeCount(
                count = 100000.0,
                currencyFrom = RUB,
                currencyTo = USD,
                currencySymbol = "$",
                name = "₽/$"
            )
        )

        Pair(System.currentTimeMillis() / 1000, list)
    }.getOrElse {
        Timber.e(it.localizedMessage)
        Pair(0L, listOf())
    }
}