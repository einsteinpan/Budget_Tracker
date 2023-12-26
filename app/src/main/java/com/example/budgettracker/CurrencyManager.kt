
import android.content.Context
import androidx.room.Room
import com.example.budgettracker.AppDatabase
import com.example.budgettracker.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object CurrencyManager {
    var currentCurrency: String = "TWD"
    val exchangeRates = mapOf("TWD" to 1.0, "JPY" to 4.6, "USD" to 0.033, "CNY" to 0.22, "KRW" to 41.8)

    interface CurrencyConversionListener {
        fun onCurrencyConversionComplete()
    }
    fun loadCurrencyFromPreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences("CurrencyPrefs", Context.MODE_PRIVATE)
        currentCurrency = sharedPreferences.getString("currentCurrency", "TWD") ?: "TWD"
    }

    fun saveCurrencyToPreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences("CurrencyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("currentCurrency", currentCurrency).apply()
    }

    fun convertTransactionAmounts(context: Context, newCurrency: String, listener: MainActivity) {
        val db = Room.databaseBuilder(context, AppDatabase::class.java, "transactions").build()

        CoroutineScope(Dispatchers.IO).launch {
            val oldRate = exchangeRates[currentCurrency] ?: 1.0
            val newRate = exchangeRates[newCurrency] ?: 1.0
            val transactions = db.transactionDao().getAll()

            transactions.forEach { transaction ->
                // 转换逻辑
                transaction.amount = (transaction.amount / oldRate) * newRate
                db.transactionDao().update(transaction)
            }

            currentCurrency = newCurrency

            withContext(Dispatchers.Main) {
                listener?.onCurrencyConversionComplete()
            }
        }
    }
}