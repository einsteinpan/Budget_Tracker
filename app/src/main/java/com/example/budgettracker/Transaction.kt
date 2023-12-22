package com.example.budgettracker

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Ignore
import java.io.Serializable
import java.util.Date
import com.example.budgettracker.Transaction


@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val label: String,
    val amount: Double,
    val description: String,
    @ColumnInfo(name = "date") val date: Date

// 添加日期字段
) : Serializable {
}




