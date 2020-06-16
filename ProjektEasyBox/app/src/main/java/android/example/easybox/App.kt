package android.example.easybox

import android.example.easybox.data.AppDatabase

open class App {
    companion object {
        lateinit var database: AppDatabase
    }
}