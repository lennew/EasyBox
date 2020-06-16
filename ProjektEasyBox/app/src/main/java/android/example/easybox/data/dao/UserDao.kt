package android.example.easybox.data.dao

import android.example.easybox.App
import android.example.easybox.data.model.*
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class UserDao : BaseDao<User> {
    @Query("SELECT * FROM users WHERE login = :login")
    abstract fun getUserByLogin(login: String): User?

    @Query("SELECT * FROM users WHERE id = :id")
    abstract fun getUserById(id: Int): User?

    @Transaction
    @Query("SELECT * FROM users WHERE id=:userId" )
    protected abstract fun getLocalizationsOfUser(userId: Int): UserWithLocalizations

    fun getUserLocalizations(userId: Int): List<Localization>? {
        return App.database.users().getLocalizationsOfUser(userId).localizations
    }

    fun getAllUserItems(userId: Int): List<Item>? {
        val userLocalizations = App.database.users().getLocalizationsOfUser(userId)
        val listOfItems = mutableListOf<Item>()

        userLocalizations.localizations.map {
            loc -> App.database.localizations().getAllLocalizationItems(loc.id).forEach{ listOfItems.add(it)}
        }
        return listOfItems
    }

    fun getAllUserBoxes(userId: Int): List<Box> {
        val listOfLocalizations = getLocalizationsOfUser(userId);
        val listOfBoxes = mutableListOf<Box>()

        listOfLocalizations.localizations.map {
            loc -> App.database.localizations().getLocalizationBoxes(loc.id)?.forEach { listOfBoxes.add(it) }
        }
        return listOfBoxes;
    }
}