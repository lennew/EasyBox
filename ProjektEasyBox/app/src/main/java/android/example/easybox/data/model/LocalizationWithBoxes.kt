package android.example.easybox.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class LocalizationWithBoxes (
        @Embedded val user: Localization,
        @Relation(
                parentColumn = "id",
                entityColumn = "localizationId"
        )
        val boxes: List<Box>?
){}