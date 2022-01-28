package com.jasontsh.interviewkickstart.stocksim

import android.widget.TextView

object StockUtil {
    const val STATIC_START_VAL = 100f
    fun generateNextStockPrice(currentPrice: Float, increasePercent: Float = 0.02f,
                               variantPercent: Float = 0.05f, inflationPercent: Float = 0f
    ): Float =
        currentPrice * (1f + increasePercent) * (Math.random()
            .toFloat() * (2f * variantPercent) - variantPercent + 1f) * (1f + inflationPercent)

    fun updatePositionInTextView(currentIndex: Int, price: Float, textView: TextView) {
        val s = textView.text
        val split = s.split(",")
        val newList = split.toMutableList()
        newList[currentIndex] = "%.2f".format(price)
        textView.text = newList.joinToString(",")
    }

    fun getPriceFromTextView(currentIndex: Int, textView: TextView): Float =
        textView.text.split(",")[currentIndex].toFloat()

}