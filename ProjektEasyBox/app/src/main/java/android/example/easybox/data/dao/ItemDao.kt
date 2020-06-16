package android.example.easybox.data.dao

import android.example.easybox.data.model.Item
import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class ItemDao: BaseDao<Item> {
    @Query("SELECT * FROM items WHERE id = :itemId")
        abstract fun getItemById(itemId: Int): Item?
}
