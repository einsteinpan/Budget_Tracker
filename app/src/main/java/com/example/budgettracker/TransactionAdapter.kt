package com.example.budgettracker

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(
    private var transactions: List<Transaction>,
    private val onItemClick: ((Transaction) -> Unit)? = null // 添加点击处理函数
) : RecyclerView.Adapter<TransactionAdapter.TransactionHolder>() {

    class TransactionHolder(view: View): RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.label)
        val amount: TextView = view.findViewById(R.id.amount)
        val description: TextView = view.findViewById(R.id.description)
        val date: TextView = view.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout, parent, false)
        return TransactionHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        val transaction = transactions[position]

        val context = holder.itemView.context
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        holder.amount.text = if (transaction.amount >= 0) {
            "+ $%.2f".format(transaction.amount)
        } else {
            "- $%.2f".format(Math.abs(transaction.amount))
        }

        holder.amount.setTextColor(
            ContextCompat.getColor(context, if (transaction.amount >= 0) R.color.green else R.color.red)
        )

        holder.label.text = transaction.label
        holder.description.text = transaction.description
        holder.date.text = dateFormat.format(transaction.date)

        // 点击事件处理
        holder.itemView.setOnClickListener {
            // 创建 Intent 并启动 DetailedActivity
            val intent = Intent(context, DetailedActivity::class.java)
            intent.putExtra("transaction", transaction)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = transactions.size

    fun setData(transactions: List<Transaction>) {
        this.transactions = transactions
        notifyDataSetChanged()
    }
}