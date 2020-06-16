package android.example.easybox.data

import android.content.Context
import android.example.easybox.data.dao.*
import android.example.easybox.data.model.*
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
        entities = [User::class, Localization::class, Box::class, Item::class,
            Image::class],
        version = 1,
        exportSchema = false
)
abstract class AppDatabase :RoomDatabase() {
    abstract fun users(): UserDao
    abstract fun localizations(): LocalizationDao
    abstract fun boxes(): BoxDao
    abstract fun items(): ItemDao
    abstract fun images(): ImageDao

    companion object {
        private var INSTANCE: AppDatabase ? = null

        fun getAppDataBase(context: Context): AppDatabase ? {
            if (INSTANCE == null){
                synchronized(AppDatabase::class){
                    INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "easyBoxDatabase").allowMainThreadQueries().build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}