package com.example.knuddj1wishlist.helpers

import android.content.Context
import com.example.knuddj1wishlist.R
import java.lang.StringBuilder
import java.util.*

class CurrencyUtil(context: Context) {
    private var currency = getCurrency(context)

    private fun getCurrency(context: Context) : Currency{
        val sharedPrefKey = context.resources.getString(R.string.sharedPrefCCKey)
        val sharedPrefDefault = context.resources.getString(R.string.sharedPrefCCDefault)
        val sharedPrefs =   context.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE)
        return Currency.getInstance(sharedPrefs.getString(sharedPrefKey, sharedPrefDefault))
    }

    fun getCurrencySymbol(): String{
        return currency.symbol
    }

    fun getCurrencyCode(): String{
        return currency.currencyCode
    }

    fun buildValueString(value: Double) : String{
        val strBuilder = StringBuilder()
        strBuilder.append(this.getCurrencySymbol())
        strBuilder.append(String.format("%.2f", value))
        strBuilder.append(" ")
        strBuilder.append(this.getCurrencyCode())
        return strBuilder.toString()
    }

    fun getValueConvertedToCurrency(value: Double, cc: String) : Double {
        val currentCC = this.getCurrencyCode()
        val convertedValue = when{
            cc == "NZD" && currentCC == "USD" -> 0.65
            cc == "NZD" && currentCC == "EUR" -> 0.57
            cc == "EUR" && currentCC == "NZD" -> 1.75
            cc == "EUR" && currentCC == "USD" -> 1.14
            cc == "USD" && currentCC == "EUR" -> 0.88
            cc == "USD" && currentCC == "NZD" -> 1.15
            else -> 1.0
        }
        return value * convertedValue
    }
}