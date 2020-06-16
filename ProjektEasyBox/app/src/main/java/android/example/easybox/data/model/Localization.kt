package android.example.easybox.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "localizations")
data class Localization (
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0,
    var userId: Int,
    var name: String = "General localization")
