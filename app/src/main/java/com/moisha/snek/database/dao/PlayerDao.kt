package com.moisha.snek.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.moisha.snek.database.model.Player

@Dao
interface PlayerDao {

    @get:Query("SELECT * FROM players")
    val all: List<Player>

    @Insert
    fun insert(player: Player)

    @Query("SELECT COUNT(id) FROM players WHERE name = :name")
    fun nameUsed(name: String): Int

    @Query("SELECT id FROM players WHERE name = :name LIMIT 1")
    fun getIdByName(name: String): Int

    @Query("SELECT name FROM players WHERE id = :id LIMIT 1")
    fun getNameById(id: Int): String
}