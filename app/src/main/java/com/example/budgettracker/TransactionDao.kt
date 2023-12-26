package com.example.budgettracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.Date

@Dao
interface TransactionDao {
    @Query("SELECT * from transactions")
    fun getAll(): List<Transaction>

    @Insert
    fun insertAll(vararg transaction: Transaction)

    @Delete
    fun delete(transaction: Transaction)

    @Update
    fun update(vararg transaction: Transaction)

    @Query("DELETE FROM transactions")
    fun deleteAll()

    // 新增根據日期查詢交易的方法
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate")
    fun getTransactionsBetweenDates(startDate: Date, endDate: Date): List<Transaction>

    @Query("SELECT * FROM transactions WHERE date = :date")
    fun getTransactionsForDate(date: String): List<Transaction>
}



// 假設您有一個名為 TransactionDao 的 DAO 和一個 Transaction 實體


