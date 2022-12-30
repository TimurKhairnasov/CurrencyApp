package com.example.currencyapp

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import android.widget.RemoteViews
import com.example.currencyapp.model.CurrencyRate
import com.example.currencyapp.network.CurrencyRepository
import kotlinx.coroutines.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

/**
 * Implementation of App Widget functionality.
 */
class CurrencyWidget : AppWidgetProvider() {

    companion object {
        const val CURRENCIES_PARCELABLES = "currencies_parcelables"
        const val UPDATED_AT = "updated_at"
        const val REFRESH = "refresh"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val currencies = CurrencyRepository().getAll()
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(
                    context, appWidgetManager, appWidgetId, currencies.second,
                    updatedAt = currencies.first
                )
            }
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == REFRESH) {
            val widgetId = intent.extras?.getInt(EXTRA_APPWIDGET_IDS)
            if (widgetId == null || context == null) return
            onUpdate(context, AppWidgetManager.getInstance(context), intArrayOf(widgetId))
            return
        }

        if (intent?.hasExtra(CURRENCIES_PARCELABLES) != true) {
            super.onReceive(context, intent)
            return
        }

        val ids = intent.extras?.getIntArray(EXTRA_APPWIDGET_IDS)
        val parcelables = if (Build.VERSION.SDK_INT >= TIRAMISU) {
            intent.extras?.getParcelableArrayList(CURRENCIES_PARCELABLES, CurrencyRate::class.java)
        } else {
            intent.extras?.getParcelableArrayList(CURRENCIES_PARCELABLES)
        }?.toList()

        val updatedAt = intent.extras?.getLong(UPDATED_AT)

        if (context == null || parcelables == null || ids == null) {
            super.onReceive(context, intent)
            return
        }
        ids.forEach {
            updateAppWidget(
                context = context,
                appWidgetManager = AppWidgetManager.getInstance(context),
                appWidgetId = it,
                data = parcelables,
                updatedAt = updatedAt
            )
        }
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    data: List<CurrencyRate>,
    updatedAt: Long?
) {
    val views = RemoteViews(context.packageName, R.layout.currency_widget)

    // 1 row
    data.getOrNull(0)?.let { item ->
        views.setTextViewText(R.id.currency_name_1, item.name)
        views.setTextViewText(R.id.currency_rate_1, item.amount.toString())
    }

    // 2 row
    data.getOrNull(1)?.let { item ->
        views.setTextViewText(R.id.currency_name_2, item.name)
        views.setTextViewText(R.id.currency_rate_2, item.amount.toString())
    }

    // 3 row
    data.getOrNull(2)?.let { item ->
        val df = DecimalFormat("#.###")
        df.roundingMode = RoundingMode.CEILING

        views.setTextViewText(R.id.currency_name_3, item.name)
        val text = "${item.count / 1000}k / " + "${df.format(item.amount / 1000)}k"
        views.setTextViewText(R.id.currency_rate_3, text)
    }

    updatedAt?.let {
        val calendar = Calendar.getInstance(Locale.ROOT)
        calendar.timeInMillis = it * 1000L
        val currentDate = android.text.format.DateFormat.format("dd.MM.yyyy", calendar).toString()
        val currentTime = android.text.format.DateFormat.format("HH:mm", calendar).toString()
        views.setTextViewText(R.id.update_time_data, "$currentTime $currentDate")

        val refreshIntent = Intent(context, CurrencyWidget::class.java)
        refreshIntent.action = CurrencyWidget.REFRESH
        refreshIntent.putExtra(EXTRA_APPWIDGET_IDS, appWidgetId)
        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_MUTABLE)

        views.setOnClickPendingIntent(R.id.widget_update, pendingIntent)
    }
    appWidgetManager.updateAppWidget(appWidgetId, views)
}