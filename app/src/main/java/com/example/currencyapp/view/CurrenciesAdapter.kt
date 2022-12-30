package com.example.currencyapp.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyapp.R
import com.example.currencyapp.databinding.ItemButtonsBinding
import com.example.currencyapp.databinding.ItemCurrencyBinding
import com.example.currencyapp.model.CurrencyRate
import com.example.currencyapp.viewmodel.CurrencyViewModel

class CurrenciesAdapter(
    private var items: List<CurrencyRate>,
    private val viewModel: CurrencyViewModel
) :
    RecyclerView.Adapter<CurrenciesAdapter.CurrencyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        return when (viewType) {
            BUTTONS_VIEW_HOLDER -> {
                val itemBinding: ItemButtonsBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context), R.layout.item_buttons, parent, false
                )
                CurrencyButtonsViewHolder(itemBinding, viewModel)
            }

            // input
            else -> {
                val itemBinding: ItemCurrencyBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context), R.layout.item_currency, parent, false
                )
                CurrencyInputViewHolder(itemBinding, viewModel)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == items.size) BUTTONS_VIEW_HOLDER else INPUT_VIEW_HOLDER
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        items.getOrNull(position)?.let {
            holder.bind(it)
        } ?: holder.bind()
    }

    override fun getItemCount(): Int = if (items.isNotEmpty()) items.size + 1 else 0

    fun updateData(list: List<CurrencyRate>) {
        items = list
        notifyDataSetChanged()
    }

    abstract class CurrencyViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        abstract fun bind(rate: CurrencyRate)
        abstract fun bind()
    }

    open class CurrencyInputViewHolder(
        private val itemBinding: ItemCurrencyBinding,
        private val viewModel: CurrencyViewModel
    ) :
        CurrencyViewHolder(itemBinding.root) {
        override fun bind(rate: CurrencyRate) {
            itemBinding.model = viewModel
            itemBinding.rate = rate
            itemBinding.executePendingBindings()
        }

        override fun bind() {
            //
        }
    }

    class CurrencyButtonsViewHolder(
        private val itemBinding: ItemButtonsBinding,
        private val viewModel: CurrencyViewModel
    ) :
        CurrencyViewHolder(itemBinding.root) {
        override fun bind(rate: CurrencyRate) {
            //
        }

        override fun bind() {
            itemBinding.model = viewModel
            itemBinding.executePendingBindings()
        }
    }

    companion object {
        private const val INPUT_VIEW_HOLDER = 0
        private const val BUTTONS_VIEW_HOLDER = 1
    }
}