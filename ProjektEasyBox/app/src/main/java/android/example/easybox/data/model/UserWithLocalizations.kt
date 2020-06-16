package android.example.easybox.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithLocalizations (
        @Embedded val user: User,
        @Relation(
                parentColumn = "id",
                entityColumn = "userId"
        )
        val localizations: List<Localization>
){}