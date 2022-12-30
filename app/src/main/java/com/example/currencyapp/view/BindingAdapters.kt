package com.example.currencyapp.view

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.currencyapp.model.CurrencyRate
import com.example.currencyapp.model.CurrencyState
import com.google.android.material.textfield.TextInputEditText
import timber.log.Timber

@BindingAdapter("setAdapter")
fun setAdapter(
    recyclerView: RecyclerView,
    adapter: CurrenciesAdapter?
) {
    adapter?.let {
        recyclerView.adapter = it
    }
}

@BindingAdapter("submitList")
fun submitList(recyclerView: RecyclerView, state: CurrencyState?) {
    val adapter = recyclerView.adapter as CurrenciesAdapter?
    adapter?.updateData(state?.currencyRates ?: listOf())
}

@BindingAdapter("clearInput")
fun clearInput(input: TextInputEditText, value: Boolean) {
    input.setText("")
}

@BindingAdapter("hint")
fun setDoubleHint(input: TextInputEditText, value: Double) {
    input.hint = value.toString()
}

@BindingAdapter("visibility")
fun visibility(view: LottieAnimationView, value: Boolean) {
    view.visibility = if (value) View.VISIBLE else View.GONE
}