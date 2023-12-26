package com.example.budgettracker

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailedActivity : AppCompatActivity() {
    private lateinit var transaction: Transaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)

        val labelLayout = findViewById<TextInputLayout>(R.id.labelLayout)
        val labelAutoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.labelAutoCompleteTextView)
        val amountLayout = findViewById<TextInputLayout>(R.id.amountLayout)
        val amountInput = findViewById<TextInputEditText>(R.id.amountInput)
        val descriptionLayout = findViewById<TextInputLayout>(R.id.descriptionLayout)
        val descriptionInput = findViewById<TextInputEditText>(R.id.descriptionInput)
        val updateBtn = findViewById<Button>(R.id.updateBtn)
        val closeBtn = findViewById<ImageButton>(R.id.closeBtn)
        val rootView = findViewById<ConstraintLayout>(R.id.rootView)

        val categories = resources.getStringArray(R.array.category_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        labelAutoCompleteTextView.setAdapter(adapter)

        transaction = intent.getSerializableExtra("transaction") as Transaction
        labelAutoCompleteTextView.setText(transaction.label, false)
        amountInput.setText(transaction.amount.toString())
        descriptionInput.setText(transaction.description)

        labelAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            updateBtn.visibility = Button.VISIBLE
        }

        amountInput.addTextChangedListener {
            updateBtn.visibility = Button.VISIBLE
        }

        descriptionInput.addTextChangedListener {
            updateBtn.visibility = Button.VISIBLE
        }

        updateBtn.setOnClickListener {
            val label = labelAutoCompleteTextView.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()
            val description = descriptionInput.text.toString()

            if (label.isEmpty()) {
                labelLayout.error = "請選擇正確類別"
            } else if (amount == null) {
                amountLayout.error = "請輸入正確金額"
            } else {
                val updatedTransaction = Transaction(transaction.id, label, amount, description)
                update(updatedTransaction)
            }
        }

        closeBtn.setOnClickListener {
            finish()
        }
    }

    private fun update(transaction: Transaction) {
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "transactions"
        ).build()

        GlobalScope.launch {
            db.transactionDao().update(transaction)
            finish()
        }
    }
}
