package com.moisha.snek.database.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(
    tableName = "players",
    indices = arrayOf(
        Index(value = arrayOf("id")),
        Index(value = arrayOf("name"), unique = true)
    )
)
class Player(name: String) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "name")
    var name: String = name
}