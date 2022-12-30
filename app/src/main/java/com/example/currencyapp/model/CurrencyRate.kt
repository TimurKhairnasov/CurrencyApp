package com.example.currencyapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CurrencyRate(
    val name: String,
    val count: Double,
    val mutableCount: Double,
    val currencyFrom: String,
    val currencyTo: String,
    val amount: Double,
    val mutableAmount: Double,
    val symbol: String,
    val lastInput: String
) : Parcelable