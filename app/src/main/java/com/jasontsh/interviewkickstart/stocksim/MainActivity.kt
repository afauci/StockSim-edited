package com.jasontsh.interviewkickstart.stocksim

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    var currentVal = StockUtil.STATIC_START_VAL
    val semaphore = Semaphore(1)
    val inflationSemaphore = Semaphore(1)
    var variedInflation = 0f
    var loggedIn = false
    var enabled = false
    val executor = Executors.newScheduledThreadPool(4)
    var currentIndex = 0
    var currentInflationIndex = 0
    val maxIndex = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        executor.submit {
//            while (true) {
//                semaphore.acquire()
//                if (loggedIn) {
//                    enabled = true
//                    break
//                }
//                semaphore.release()
//            }
//            while (true) {
//                semaphore.acquire()
//                if (!loggedIn) {
//                    enabled = false
//                    break
//                }
//                semaphore.release()
//            }
//        }
        val logInButton = findViewById<Button>(R.id.loggedIn)
        logInButton.setOnClickListener {
//            semaphore.acquire()
//            loggedIn = true
//            semaphore.release()
            enabled = true
        }
        val logOutButton = findViewById<Button>(R.id.loggedOut)
        logOutButton.setOnClickListener {
//            semaphore.acquire()
//            loggedIn = false
//            semaphore.release()
            enabled = false
        }
        val staticStocks = findViewById<TextView>(R.id.static_stocks)
        val staticButton = findViewById<Button>(R.id.static_stocks_button)
        staticButton.setOnClickListener {
            if (!enabled) {
                return@setOnClickListener
            }
            var s = "%.2f".format(currentVal)
            for (i in 0..5) {
                val nextPrice = StockUtil.generateNextStockPrice(currentVal)
                s += ", %.2f".format(nextPrice)
                currentVal = nextPrice
            }
            staticStocks.text = s
        }
        val increaseEd = findViewById<EditText>(R.id.increase_ed)
        val variantEd = findViewById<EditText>(R.id.variant_ed)
        val inflateEd = findViewById<EditText>(R.id.inflate_ed)
        val dynamicStocks = findViewById<TextView>(R.id.dynamic_stocks)
        dynamicStocks.text = "100.0,100.0,100.0,100.0,100.0"
        executor.scheduleAtFixedRate({
            if (!enabled) {
                return@scheduleAtFixedRate
            }
            val currentPrice = StockUtil.getPriceFromTextView(currentIndex, dynamicStocks)
            val newPrice = StockUtil.generateNextStockPrice(
                currentPrice,
                increaseEd.text.toString().toInt() / 100f,
                variantEd.text.toString().toInt() / 100f,
                inflateEd.text.toString().toInt() / 100f
            )
            currentIndex++
            if (currentIndex >= maxIndex) {
                currentIndex = 0
            }
            runOnUiThread { StockUtil.updatePositionInTextView(currentIndex, newPrice, dynamicStocks) }
        }, 0, 3, TimeUnit.SECONDS)

        val dynamicStocksWithInflation =
            findViewById<TextView>(R.id.dynamic_stocks_with_dynamic_inflation)
        dynamicStocksWithInflation.text = "100.0,100.0,100.0,100.0,100.0"
        executor.scheduleAtFixedRate({
            inflationSemaphore.acquire()
            variedInflation = variantEd.text.toString().toInt() / 100f
            inflationSemaphore.release()
        }, 0, 4800, TimeUnit.MILLISECONDS)
        executor.scheduleAtFixedRate({
            if (!enabled) {
                return@scheduleAtFixedRate
            }
            inflationSemaphore.acquire()
            val currentPrice = StockUtil.getPriceFromTextView(currentInflationIndex, dynamicStocks)
            val newPrice = StockUtil.generateNextStockPrice(
                currentPrice,
                increaseEd.text.toString().toInt() / 100f,
                variantEd.text.toString().toInt() / 100f,
                variedInflation
            )
            variedInflation = 0f
            inflationSemaphore.release()
            currentInflationIndex++
            if (currentInflationIndex >= maxIndex) {
                currentInflationIndex = 0
            }
            runOnUiThread {
                StockUtil.updatePositionInTextView(
                    currentInflationIndex,
                    newPrice,
                    dynamicStocksWithInflation
                )
            }
        }, 0, 3, TimeUnit.SECONDS)
    }
}