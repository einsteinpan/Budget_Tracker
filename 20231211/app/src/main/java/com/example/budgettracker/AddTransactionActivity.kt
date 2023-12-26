package com.example.budgettracker

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddTransactionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        val addTransactionBtn = findViewById<Button>(R.id.addTransactionBtn)
        val amountInput = findViewById<TextInputEditText>(R.id.amountInput)
        val descriptionInput = findViewById<TextInputEditText>(R.id.descriptionInput)
        val amountLayout: TextInputLayout = findViewById(R.id.amountLayout)
        val closeBtn = findViewById<ImageButton>(R.id.closeBtn)

        // 从 Spinner 更改为 AutoCompleteTextView
        val categoryAutoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.categoryAutoCompleteTextView)
        val categories = resources.getStringArray(R.array.category_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAutoCompleteTextView.setAdapter(adapter)

        // 设置 AutoCompleteTextView 的点击监听器
        categoryAutoCompleteTextView.setOnItemClickListener { adapterView, view, position, id ->
            // 在这里处理选择的类别
            val selectedCategory = categories[position]
            // 可以在这里执行相应的操作，例如显示选择的类别
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
            val category = categoryAutoCompleteTextView.text.toString() // 获取选择的类别
            val amount = amountInput.text.toString().toDoubleOrNull()
            val description = descriptionInput.text.toString()

            if (amount == null)
                amountLayout.error = "請輸入金額"
            else {
                // 确保 Transaction 类的构造器接受正确的参数
                val transaction = Transaction(0, category, amount, description)
                insert(transaction)
            }
        }

        closeBtn.setOnClickListener {
            finish()
        }
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