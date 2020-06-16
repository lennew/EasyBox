package android.example.easybox.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "items")
data class Item(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,
        var boxId: Int,
        var name: String,
        var comment: String = "",
        var ean: String = "",
        var keywords: String = "",
        var mainImage: String = "",
        var additionalImage1: String = "",
        var additionalImage2: String = "",
        var additionalImage3: String = ""

        ) {
}