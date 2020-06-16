package android.example.easybox.data.dao

import android.example.easybox.App
import android.example.easybox.data.model.Box
import android.example.easybox.data.model.Item
import android.example.easybox.data.model.Localization
import android.example.easybox.data.model.LocalizationWithBoxes
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class LocalizationDao: BaseDao<Localization> {
    @Query("SELECT * FROM localizations WHERE id = :localizationId")
    abstract fun getLocalizationById(localizationId: Int): Localization?

    @Transaction
    @Query("SELECT * FROM localizations WHERE id=:localizationId" )
    protected abstract fun getBoxesOfLocalization(localizationId: Int): LocalizationWithBoxes?

    fun getLocalizationBoxes (localizationId: Int): List<Box>? {
        return App.database.localizations().getBoxesOfLocalization(localizationId)?.boxes
    }

    fun getAllLocalizationItems(localizationId: Int): List<Item> {
        val locBoxes = App.database.localizations().getBoxesOfLocalization(localizationId)
        val listOfItems =  mutableListOf<Item>()
        if (locBoxes?.boxes == null) {
            return listOfItems
        }
        locBoxes.boxes.map {
            box -> App.database.boxes().getBoxItems(box.id)?.forEach { listOfItems.add(it) }
        }
        return listOfItems
    }

}