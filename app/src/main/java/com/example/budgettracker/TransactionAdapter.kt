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

class TransactionAdapter(private var transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionHolder>() {

    class TransactionHolder(view: View): RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.label)
        val amount: TextView = view.findViewById(R.id.amount)
        val description: TextView = view.findViewById(R.id.description)
        val date: TextView = view.findViewById(R.id.date) // 新增
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout, parent, false)
        return TransactionHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        val transaction = transactions[position]
        val context = holder.amount.context
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        if(transaction.amount >=0){
            holder.amount.text = "+ $%.2f".format(transaction.amount)
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.green))
        }else{
            holder.amount.text = "- $%.2f".format(Math.abs(transaction.amount))
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.red))
        }


        holder.label.text = transaction.label

        holder.itemView.setOnClickListener{
            val intent = Intent(context, DetailedActivity::class.java)
            intent.putExtra("transaction",transaction)
            context.startActivity(intent)
        }
        holder.description.text = transaction.description // 设置備註文本

        holder.label.text = transaction.label
        holder.description.text = transaction.description
        holder.amount.text = transaction.amount.toString() // 或者適當格式化金額
        holder.date.text = dateFormat.format(transaction.date) // 設置日期

        // ...
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    fun setData(transactions: List<Transaction>) {
        this.transactions = transactions
        notifyDataSetChanged()
    }
}
