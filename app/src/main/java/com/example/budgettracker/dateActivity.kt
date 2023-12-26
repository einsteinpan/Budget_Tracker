package com.example.budgettracker

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class dateActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var calendarView: CalendarView
    private lateinit var expensesTextView: TextView
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.date)
        val addBtn: FloatingActionButton = findViewById(R.id.addBtn)
        // 初始化視圖
        calendarView = findViewById(R.id.calendarView)
        expensesTextView = findViewById(R.id.textViewExpenses)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewTransactions)

        // 初始化數據庫
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "transactions").build()

        // 初始化適配器和 RecyclerView
        transactionAdapter = TransactionAdapter(emptyList()) { transaction ->
            // 当点击某个项时执行
            val intent = Intent(this, DetailedActivity::class.java)
            intent.putExtra("transaction", transaction)
            startActivity(intent)
        }

        recyclerView.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(this@dateActivity)
        }
        // 設置日曆視圖的日期變更監聽器
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }.time
            loadTransactions(selectedDate)
        }

        addBtn.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }


    }

    private fun loadTransactions(selectedDate: Date) {
        // 將選定日期的時間設置為當天開始
        val startDate = Calendar.getInstance().apply {
            time = selectedDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        // 將選定日期的時間設置為當天結束
        val endDate = Calendar.getInstance().apply {
            time = selectedDate
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.time

        // 使用 Coroutine 异步從數據庫加載數據
        CoroutineScope(Dispatchers.IO).launch {
            val transactions = db.transactionDao().getTransactionsBetweenDates(startDate, endDate)

            // 转换交易金额为当前货币
            val convertedTransactions = convertCurrency(transactions)

            withContext(Dispatchers.Main) {
                // 更新适配器数据
                transactionAdapter.setData(convertedTransactions)
                updateExpensesDisplay(convertedTransactions, selectedDate)
            }
        }
    }
    private fun convertCurrency(transactions: List<Transaction>): List<Transaction> {
        val rate = MainActivity.exchangeRates[MainActivity.currentCurrency] ?: 1.0
        return transactions.map { transaction ->
            Transaction(
                transaction.id,
                transaction.label,
                transaction.amount * rate,
                transaction.description,
                transaction.date
            )
        }
    }
    private fun updateExpensesDisplay(transactions: List<Transaction>, date: Date) {
        if (transactions.isEmpty()) {
            val formattedDate = dateFormat.format(date)
            expensesTextView.text = "於 $formattedDate 沒有交易紀錄。"
        } else {
            val rate = MainActivity.exchangeRates[MainActivity.currentCurrency] ?: 1.0
            val totalNegativeExpenses = transactions.filter { it.amount < 0 }.sumOf { it.amount * rate }
            val formattedTotal = String.format(Locale.getDefault(), "%.2f", Math.abs(totalNegativeExpenses))
            expensesTextView.text = "今日花費：$formattedTotal 元"
        }
    }

}