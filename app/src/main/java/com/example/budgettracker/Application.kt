package com.example.budgettracker

import android.app.Application
import androidx.room.Room

class MyApp : Application() {

    lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "budget_tracker_db"
        )
            .fallbackToDestructiveMigration() // 啟用破壞性遷移
            .build()
    }
}
