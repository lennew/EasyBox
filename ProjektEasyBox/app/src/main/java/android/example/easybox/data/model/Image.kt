package android.example.easybox.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "images")
data class Image(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,
        var path: String
) {
}