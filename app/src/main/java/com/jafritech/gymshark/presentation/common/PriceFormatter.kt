package com.jafritech.gymshark.presentation.common

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object PriceFormatter {

    /** 1000.0 -> "£1,000", 45.5 -> "£45.50", null -> null */
    fun format(price: Double?, locale: Locale = Locale.UK): String? {
        if (price == null) return null
        val isWhole = price % 1.0 == 0.0
        return NumberFormat.getCurrencyInstance(locale).apply {
            currency = Currency.getInstance("GBP")
            minimumFractionDigits = if (isWhole) 0 else 2
            maximumFractionDigits = if (isWhole) 0 else 2
        }.format(price)
    }
}