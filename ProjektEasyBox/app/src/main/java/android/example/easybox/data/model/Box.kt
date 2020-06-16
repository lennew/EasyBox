package android.example.easybox.data.model

import androidx.room.*

@Entity (tableName = "boxes", indices = [Index(value = ["code"], unique = true)])
data class Box (
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0,
    var localizationId: Int,
    var name: String = "General box",
    var comment: String = "",
    var code: String = "",
    var photo: String = ""
)


