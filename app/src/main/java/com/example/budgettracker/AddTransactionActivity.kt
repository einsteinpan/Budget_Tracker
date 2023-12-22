package com.example.budgettracker

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var dateInput: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        val addTransactionBtn = findViewById<Button>(R.id.addTransactionBtn)
        val amountInput = findViewById<TextInputEditText>(R.id.amountInput)
        val descriptionInput = findViewById<TextInputEditText>(R.id.descriptionInput)
        val amountLayout: TextInputLayout = findViewById(R.id.amountLayout)
        val categoryAutoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.categoryAutoCompleteTextView)
        val closeBtn = findViewById<ImageButton>(R.id.closeBtn)
        dateInput = findViewById(R.id.date)


        // 从 Spinner 更改为 AutoCompleteTextView
        val categories = resources.getStringArray(R.array.category_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAutoCompleteTextView.setAdapter(adapter)

        // 设置 AutoCompleteTextView 的点击监听器
        categoryAutoCompleteTextView.setOnItemClickListener { adapterView, view, position, id ->
            // 在这里处理选择的类别
            val selectedCategory = categories[position]
            // 可以在这里执行相应的操作，例如显示选择的类别
        }

        // 初始化日曆
        val calendar = Calendar.getInstance()
        updateDateInView(calendar)

        // 日期選擇器
        dateInput.setOnClickListener {
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                updateDateInView(calendar)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        amountInput.addTextChangedListener {
            if (it?.length ?: 0 > 0) {
                amountLayout.error = null
            }
        }

        descriptionInput.addTextChangedListener {
            if (it?.length ?: 0 > 0) {
                descriptionInput.error = null
            }
        }

        addTransactionBtn.setOnClickListener {
            val category = categoryAutoCompleteTextView.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()
            val description = descriptionInput.text.toString()
            val date = calendar.time // 獲取日期

            if (amount == null) {
                amountLayout.error = "請輸入金額"
            } else {
                val transaction = Transaction(0, category, amount, description, date)
                insert(transaction)
            }
        }

        closeBtn.setOnClickListener {
            finish()
        }
    }

    private fun updateDateInView(calendar: Calendar) {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateInput.setText(format.format(calendar.time))
    }
    private fun insert(transaction: Transaction) {
        // 请确保在执行数据库操作时使用异步，例如使用 CoroutineScope 而不是 GlobalScope
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "transactions"
        ).build()

        GlobalScope.launch {
            db.transactionDao().insertAll(transaction)
            // 回到主线程关闭 Activity 可能需要调整，如使用 runOnUiThread { finish() }
            finish()
        }
    }
}

// 確保 Transaction 類包含日期字段
// data class Transaction(val id: Int, val category: String, val amount: Double, val description: String, val date: Date)
