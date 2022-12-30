package com.example.currencyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyapp.model.CurrencyRate
import com.example.currencyapp.model.CurrencyState
import com.example.currencyapp.network.CurrencyRepository
import com.example.currencyapp.network.CurrencyRepository.Companion.AMOUNT
import com.example.currencyapp.network.CurrencyRepository.Companion.COUNT
import com.google.android.material.datepicker.DateValidatorPointBackward.before
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.math.abs


class CurrencyViewModel : ViewModel() {
    val _currencyState = MutableLiveData(CurrencyState())
    val currencyState: LiveData<CurrencyState> = _currencyState

    val _clearInput = MutableLiveData(false)
    val clearInput: LiveData<Boolean> = _clearInput

    val _loaderVisibility = MutableLiveData(true)
    val loaderVisibility: LiveData<Boolean> = _loaderVisibility

    private val currencyRepository = CurrencyRepository()

    init {
        update()
    }

    fun update() {
        _loaderVisibility.value = true
        viewModelScope.launch {
            val responseCurrencies = withContext(Dispatchers.IO) {
                currencyRepository.getAll()
            }
            _currencyState.value = CurrencyState(
                updatedAt = responseCurrencies.first,
                currencyRates = responseCurrencies.second
            )
            _loaderVisibility.value = false
        }
    }

    fun calculate() {
        Timber.d("Timber -- calculate ${tempCurrencies.size}")
        val tempCurrencies = tempCurrencies.map { rate ->
            Timber.d("Timber -- rate.lastInput ${rate.lastInput}")
            when (rate.lastInput) {
                AMOUNT -> {
                    val value = rate.mutableAmount * rate.count / rate.amount
                    rate.copy(
                        mutableCount = value
                    )
                }
                COUNT -> {
                    val value = rate.mutableCount * rate.amount / rate.count
                    rate.copy(
                        mutableAmount = value
                    )
                }
                else -> rate
            }
        }
        _currencyState.value?.let { state ->
            _currencyState.value = state.copy(
                currencyRates = state.currencyRates.map { rate ->
                    tempCurrencies.find { it.name == rate.name }?.let {
                        it
                    } ?: rate
                }
            )
        }
        _clearInput.value = _clearInput.value?.let { !it } ?: false
    }

    fun amountInputChanged(name: String, text: CharSequence, before: Int, count: Int) {
        val userChange = abs(count - before) == 1
        Timber.d("Timber -- $userChange")
        if (userChange) {
            val newValue = kotlin.runCatching { text.toString().toDouble() }.getOrDefault(0.0)
            val currencyRate = _currencyState.value?.currencyRates
                ?.find { it.name == name && newValue != it.mutableAmount }
                ?.let {
                    if (it.name == name && newValue != it.mutableAmount) {
                        it.copy(
                            mutableAmount = newValue,
                            lastInput = AMOUNT
                        )
                    } else it
                }
            currencyRate?.let {
                tempCurrencies = tempCurrencies.filterNot { it.name == name } + currencyRate
            }
        }
    }

    var tempCurrencies = emptyList<CurrencyRate>()

    fun countInputChanged(name: String, text: CharSequence, before: Int, count: Int) {
        val userChange = abs(count - before) == 1
        if (userChange) {
            val newValue = kotlin.runCatching { text.toString().toDouble() }.getOrDefault(0.0)
            val currencyRate = _currencyState.value?.currencyRates
                ?.find { it.name == name && newValue != it.mutableCount }
                ?.let {
                    if (it.name == name && newValue != it.mutableCount) {
                        it.copy(
                            mutableCount = newValue,
                            lastInput = COUNT
                        )
                    } else it
                }
            currencyRate?.let {
                tempCurrencies = tempCurrencies.filterNot { it.name == name } + currencyRate
            }
        }
    }
}