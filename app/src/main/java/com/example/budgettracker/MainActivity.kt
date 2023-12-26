package com.example.budgettracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var deletedTransaction: Transaction
    private lateinit var transactions: List<Transaction>
    private lateinit var oldtransactions: List<Transaction>
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var db : AppDatabase
    private lateinit var balance: TextView
    private lateinit var budget: TextView
    private lateinit var expense: TextView

    private lateinit var currencySpinner: Spinner
    private var currentCurrency: String = "TWD" // 默认货币为新台币
    private val exchangeRates = mapOf("TWD" to 1.0, "JPY" to 0.28, "USD" to 30.0, "CNY" to 4.5, "KRW" to 0.025) // 固定汇率

    companion object {
        var currentCurrency: String = "TWD"
        val exchangeRates = mapOf("TWD" to 1.0, "JPY" to 0.28, "USD" to 30.0, "CNY" to 4.5, "KRW" to 0.025)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 加载保存的货币设置
        CurrencyManager.loadCurrencyFromPreferences(this)

        // 初始化货币 Spinner
        currencySpinner = findViewById(R.id.currencySpinner)

        val addBtn: FloatingActionButton = findViewById(R.id.addBtn)
        val addBtn2: FloatingActionButton = findViewById(R.id.addBtn2)
        val addBtn4: FloatingActionButton = findViewById(R.id.addBtn4)
        val trash: FloatingActionButton = findViewById(R.id.trash)
        val buttonOpenDateActivity: FloatingActionButton = findViewById(R.id.buttonOpenDateActivity)
        val btnsort: FloatingActionButton = findViewById(R.id.btnSort)//這也是sort
        CurrencyManager.saveCurrencyToPreferences(this)

        // 初始化货币 Spinner
        currencySpinner = findViewById(R.id.currencySpinner)
        val currencyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, exchangeRates.keys.toList())
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        currencySpinner.adapter = currencyAdapter

        // 设置货币选择监听器
        // 设置货币选择监听器
        currencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedCurrency = parent.getItemAtPosition(position) as String
                if (CurrencyManager.currentCurrency != selectedCurrency) {
                    CurrencyManager.convertTransactionAmounts(this@MainActivity, selectedCurrency, this@MainActivity)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        balance = findViewById(R.id.balance)
        budget = findViewById(R.id.budget)
        expense = findViewById(R.id.expense)

        transactions = arrayListOf()

        transactionAdapter = TransactionAdapter(transactions)
        layoutManager = LinearLayoutManager(this)

        db = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "transactions"
        ).build()

        val recyclerView: RecyclerView = findViewById(R.id.recyclerview)

        recyclerView.apply {
            adapter = transactionAdapter
            layoutManager = this@MainActivity.layoutManager
        }

        //swipe to remove
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteTransaction(transactions[viewHolder.adapterPosition])
            }
        }

        val swipeHelper = ItemTouchHelper(itemTouchHelper)
        swipeHelper.attachToRecyclerView(recyclerView)

        addBtn.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }

        addBtn2.setOnClickListener {
            val intent = Intent(this, question::class.java)
            startActivity(intent)
        }

        addBtn4.setOnClickListener {
            val intent = Intent(this, qrcode::class.java)
            startActivity(intent)
        }


    //date
        //date
        buttonOpenDateActivity.setOnClickListener {
            val intent = Intent(this, dateActivity::class.java)
            startActivity(intent)
        }

        // 图表分析按钮
        val chartAnalysisBtn: FloatingActionButton = findViewById(R.id.chartAnalysisBtn)
        chartAnalysisBtn.setOnClickListener {
            // 启动图表分析 Activity
            val intent = Intent(this, ChartAnalysisActivity::class.java)
            startActivity(intent)
        }

        //一鍵刪除所有資料
        trash.setOnClickListener {
            showDeleteConfirmationDialog()
            //deleteAllTransactions()
        }

        btnsort.setOnClickListener {//sort!!!
            showSortDialog()
        }


    }

    private fun fetchAll(){
        CoroutineScope(Dispatchers.IO).launch {
            transactions = db.transactionDao().getAll()

            runOnUiThread {
                updateDashboard()
                transactionAdapter.setData(transactions)
            }
        }
    }

    private fun updateDashboard() {
        // 获取当前选择的货币汇率，如果没有匹配的汇率，默认为 1.0
        val rate = exchangeRates[currentCurrency] ?: 1.0

        // 转换每笔交易的金额
        val totalAmountConverted = transactions.sumOf { it.amount * rate }
        val budgetAmountConverted = transactions.filter { it.amount > 0 }.sumOf { it.amount * rate }
        val expenseAmountConverted = transactions.filter { it.amount < 0 }.sumOf { it.amount * rate }

        // 更新UI
        balance.text = String.format("$ %.2f", totalAmountConverted)
        budget.text = String.format("$ %.2f", budgetAmountConverted)
        expense.text = String.format("$ %.2f", Math.abs(expenseAmountConverted)) // 将支出显示为正数
    }

    private fun undoDelete(){
        GlobalScope.launch {
            db.transactionDao().insertAll(deletedTransaction)
            transactions = oldtransactions
            runOnUiThread{
                transactionAdapter.setData(transactions)
                updateDashboard()
            }
        }
    }

    private fun showSnackbar(){
        val view = findViewById<View>(com.google.android.material.R.id.coordinator)
        val snackbar = Snackbar.make(view,"資料已刪除！",Snackbar.LENGTH_LONG)
        snackbar.setAction("復原"){
            undoDelete()
        }
            .setActionTextColor(ContextCompat.getColor(this, R.color.red))
            .setTextColor(ContextCompat.getColor(this, R.color.white))
            .show()
    }

    private fun deleteTransaction(transaction: Transaction){
        deletedTransaction = transaction
        oldtransactions = transactions

        GlobalScope.launch {
            db.transactionDao().delete(transaction)

            transactions = transactions.filter { it.id !=transaction.id }
            runOnUiThread{
                updateDashboard()
                transactionAdapter.setData(transactions)
                showSnackbar()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchAll()
    }

    private fun deleteAllTransactions() {
        GlobalScope.launch {
            db.transactionDao().deleteAll()  // 假設您的 DAO 有一個 deleteAll 方法

            transactions = emptyList()  // 清空當前的交易列表
            runOnUiThread {
                updateDashboard()
                transactionAdapter.setData(transactions)
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("確認刪除")
        builder.setMessage("您確定要刪除所有交易嗎？這將無法撤銷。")

        builder.setPositiveButton("確定") { dialog, which ->
            // 用户点击了确认，执行删除操作
            deleteAllTransactions()
        }

        builder.setNegativeButton("取消") { dialog, which ->
            // 用户点击了取消，仅关闭对话框，不执行任何操作
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showSortDialog() {
        val sortOptions = arrayOf("由低到高", "由高到低", "按新增順序", "按日期排序", "按類別排序")
        AlertDialog.Builder(this)
            .setTitle("選擇排序方式")
            .setItems(sortOptions) { _, which ->
                when (which) {
                    0 -> sortTransactions(ascending = false)
                    1 -> sortTransactions(ascending = true)
                    2 -> sortTransactionsByAddedOrder()
                    3 -> sortTransactionsByDate()
                    4 -> sortTransactionsByLabel()
                }
            }
            .show()
    }


    private fun sortTransactions(ascending: Boolean) {
        transactions = if (ascending) {
            transactions.sortedBy { it.amount }
        } else {
            transactions.sortedByDescending { it.amount }
        }
        transactionAdapter.setData(transactions)
    }

    private fun sortTransactionsByAddedOrder() {
        // 使用 date 字段來排序
        transactions = transactions.sortedBy { it.id }
        transactionAdapter.setData(transactions)
    }

    private fun sortTransactionsByDate() {
        transactions = transactions.sortedBy { it.date }
        transactionAdapter.setData(transactions)
    }

    private fun sortTransactionsByLabel() {
        transactions = transactions.sortedBy { it.label }
        transactionAdapter.setData(transactions)
    }
     fun onCurrencyConversionComplete() {
        fetchAll()
    }

}

