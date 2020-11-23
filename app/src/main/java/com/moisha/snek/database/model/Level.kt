package com.moisha.snek.database.model

import android.arch.persistence.room.*

@Entity(
    tableName = "levels",
    foreignKeys = arrayOf(
        ForeignKey(
            onDelete = ForeignKey.CASCADE,
            entity = Player::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("uId")
        )
    ),
    indices = arrayOf(
        Index(value = arrayOf("id"))
    )
)
class Level constructor(
    size: IntArray,
    barriers: List<IntArray>,
    snek: List<IntArray>,
    direction: Int,
    uId: Int,
    name: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "name")
    var name: String = name

    @ColumnInfo(name = "size")
    var size: IntArray = size

    @ColumnInfo(name = "barriers")
    var barriers: List<IntArray> = barriers

    @ColumnInfo(name = "snek")
    var snek: List<IntArray> = snek

    @ColumnInfo(name = "direction")
    var direction: Int = direction

    @ColumnInfo(name = "uId")
    var uId: Int = uId
}