package com.example.budgettracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.room.Room
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChartAnalysisActivity : AppCompatActivity() {

    private lateinit var pieChartView: PieChartView
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart_analysis)

        pieChartView = findViewById(R.id.pieChartView)
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "transactions"
        ).build()

        fetchDataAndDisplay()

        val closeChartBtn = findViewById<ImageButton>(R.id.closeChartBtn)
        closeChartBtn.setOnClickListener {
            finish() // 关闭当前 Activity
        }
    }

    private fun fetchDataAndDisplay() {
        GlobalScope.launch {
            val transactions = db.transactionDao().getAll()
            if (transactions.isNotEmpty()) {
                // 筛选出花费的交易（金额小于零的交易）
                val expenses = transactions.filter { it.amount < 0 }

                // 按类别分组并计算每个类别的总花费
                val categorySums = expenses
                    .groupBy { it.label }
                    .mapValues { (_, txs) ->
                        txs.fold(0f) { sum, transaction ->
                            sum + transaction.amount.toFloat()
                        }
                    }

                runOnUiThread {
                    pieChartView.setData(categorySums)
                }
            }
        }
    }
}