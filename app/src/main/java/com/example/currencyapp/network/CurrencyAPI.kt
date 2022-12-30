package com.example.currencyapp.network

import okhttp3.ResponseBody
import retrofit2.http.*

interface CurrencyAPI {

    companion object {
        private const val AKTOBE_COOKIE = "city=1005; _ym_uid=1664530481538460505; _ym_d=1664530481; _ga=GA1.2.1141683871.1664530481; app=71783g63qo0l9pr0olv16l5dvo; _csrf-frontend=64YHd93SFNNOwbIs33kvsSbTv6jgJttG; _ym_isad=1; _gid=GA1.2.1688680861.1672246888; _ym_visorc=b; _gat_gtag_UA_74032510_2=1"
    }

    @GET("/exchange/search")
    suspend fun getExchangedCount(
        @Header("Cookie") cookie: String = AKTOBE_COOKIE,
        @Query("ExchangeForm[method]") method: String = "from",
        @Query("ExchangeForm[count]") count: Long = 100000,
        @Query("ExchangeForm[currencyFrom]") currencyFrom: String = "RUB",
        @Query("ExchangeForm[currencyTo]") currencyTo: String = "USD"
    ): ResponseBody
}