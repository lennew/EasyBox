package android.example.easybox.data.dao

import android.example.easybox.App
import android.example.easybox.data.model.Box
import android.example.easybox.data.model.BoxWithItems
import android.example.easybox.data.model.Item
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class BoxDao:BaseDao<Box> {
    @Query("SELECT * FROM boxes WHERE id = :boxId")
    abstract fun getBoxById(boxId: Int): Box?

    @Query("SELECT * FROM boxes WHERE code = :qrCode")
    abstract fun getBoxByQrCode(qrCode: String): Box?

    @Transaction
    @Query("SELECT * FROM boxes WHERE id=:boxId")
    protected abstract fun getItemsOfBox(boxId: Int) : BoxWithItems?

    fun getBoxItems(boxId: Int): List<Item>? {
        return App.database.boxes().getItemsOfBox(boxId)?.items
    }
}