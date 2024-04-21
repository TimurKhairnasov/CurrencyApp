package com.example.currencyapp.view

import android.animation.LayoutTransition
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyapp.R
import com.example.currencyapp.databinding.FragmentFirstBinding
import com.example.currencyapp.model.CurrencyRate
import com.example.currencyapp.viewmodel.CurrencyViewModel
import com.example.currencyapp.CurrencyWidget
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    private var mViewModel: CurrencyViewModel = CurrencyViewModel()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var currentId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_first, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.viewModel = mViewModel
        val adapter = CurrenciesAdapter(listOf(), mViewModel)
        _binding?.lifecycleOwner = requireActivity()
        _binding?.adapter = adapter
        _binding?.currencyList?.layoutManager = LinearLayoutManager(requireContext())
        _binding?.currencyList?.adapter = adapter
        mViewModel.currencyState.observe(requireActivity()) {
            updateMyWidgets(it.updatedAt, it.currencyRates)
        }
        _binding?.parent?.layoutTransition?.enableTransitionType(LayoutTransition.CHANGING)
    }

    private fun updateMyWidgets(updatedAt: Long, currencyRateList: List<CurrencyRate>) {
        context?.let {
            val man = AppWidgetManager.getInstance(it)
            val ids = man.getAppWidgetIds(
                ComponentName(it, CurrencyWidget::class.java)
            )
            val updateIntent = Intent()
            updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            updateIntent.putExtra(EXTRA_APPWIDGET_IDS, ids)
            updateIntent.putExtra(CurrencyWidget.UPDATED_AT, updatedAt)
            updateIntent.putExtra(
                CurrencyWidget.CURRENCIES_PARCELABLES,
                ArrayList(currencyRateList)
            )
            it.sendBroadcast(updateIntent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        currentId = 0
        currentId = -1
        currentId = -2
        currentId = -3
        currentId = 1
        currentId = 2
        currentId = 3
    }
}