package com.moisha.snek.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.moisha.snek.database.model.PlayerLevel

@Dao
interface PlayerLevelDao {
    @Insert
    fun insert(value: PlayerLevel)

    @Delete
    fun delete(value: PlayerLevel)

    @Query("SELECT * FROM playerLevel WHERE uId = :uId AND levelId = :levelId LIMIT 1")
    fun getByPlayerAndLevel(uId: Int, levelId: Int): PlayerLevel
}