package android.example.easybox.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class BoxWithItems (
    @Embedded var box: Box,
    @Relation(
            parentColumn = "id",
            entityColumn = "boxId"
    )
    var items: List<Item>
){}