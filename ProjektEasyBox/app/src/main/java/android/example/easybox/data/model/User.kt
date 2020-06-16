package android.example.easybox.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "users")
data class User (
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0,
    var login: String,
    var password: String)
{
}
