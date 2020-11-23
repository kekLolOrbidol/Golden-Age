package com.moisha.snek.database.model

import android.arch.persistence.room.*


@Entity(
    tableName = "highscores",
    foreignKeys = arrayOf(
        ForeignKey(
            onDelete = ForeignKey.CASCADE,
            entity = Player::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("uId")
        ),
        ForeignKey(
            onDelete = ForeignKey.CASCADE,
            entity = Level::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("levelId")
        )
    ),
    indices = arrayOf(
        Index(value = arrayOf("id"))
    )
)
class Highscore(uId: Int, levelId: Int, score: Int, speed: Int) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "uId")
    var uId: Int = uId

    @ColumnInfo(name = "levelId")
    var levelId: Int = levelId

    @ColumnInfo(name = "score")
    var score: Int = score

    @ColumnInfo(name = "speed")
    var speed: Int = speed
}