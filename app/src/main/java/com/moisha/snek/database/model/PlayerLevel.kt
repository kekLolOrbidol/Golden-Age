package com.moisha.snek.database.model

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE

@Entity(
    tableName = "playerLevel",
    foreignKeys = arrayOf(
        ForeignKey(
            onDelete = CASCADE,
            entity = Player::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("uId")
        ),
        ForeignKey(
            onDelete = CASCADE,
            entity = Level::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("levelId")
        )
    ),
    indices = arrayOf(
        Index(value = arrayOf("id")),
        Index(value = arrayOf("uId", "levelId"), unique = true)
    )
)
class PlayerLevel(uId: Int, levelId: Int) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "uId")
    var uId: Int = uId

    @ColumnInfo(name = "levelId")
    var levelId: Int = levelId
}