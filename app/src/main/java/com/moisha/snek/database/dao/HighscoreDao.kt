package com.moisha.snek.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.moisha.snek.database.model.Highscore
import com.moisha.snek.database.model.HighscoreListItem

@Dao
interface HighscoreDao {
    @get:Query("SELECT * FROM highscores")
    val all: List<Highscore>

    @Query(
        "SELECT " +
                "players.name AS uName, " +
                "highscores.score AS score " +
                "FROM highscores " +
                "INNER JOIN players ON highscores.uId = players.id " +
                "WHERE speed = :speed AND levelID = :levelId " +
                "ORDER BY " +
                "score DESC, " +
                "uName ASC;"
    ) //fixed bug FE-01
    fun getByLevelAndSpeed(levelId: Int, speed: Int): List<HighscoreListItem>

    @Insert
    fun insert(highscore: Highscore)
}