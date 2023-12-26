package com.example.budgettracker

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Transaction::class], version = 2)
@TypeConverters(Converters::class) // 添加這行來使用您的轉換器
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}
