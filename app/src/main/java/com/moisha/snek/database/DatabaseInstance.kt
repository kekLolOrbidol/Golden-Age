package com.moisha.snek.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.moisha.snek.database.converter.LevelConverter
import com.moisha.snek.database.dao.HighscoreDao
import com.moisha.snek.database.dao.LevelDao
import com.moisha.snek.database.dao.PlayerDao
import com.moisha.snek.database.dao.PlayerLevelDao
import com.moisha.snek.database.model.Highscore
import com.moisha.snek.database.model.Level
import com.moisha.snek.database.model.Player
import com.moisha.snek.database.model.PlayerLevel

@Database(
    version = 1,
    entities = arrayOf(Level::class, Player::class, Highscore::class, PlayerLevel::class),
    exportSchema = false
)
@TypeConverters(LevelConverter::class)
abstract class DatabaseInstance : RoomDatabase() {

    abstract fun levelDao(): LevelDao

    abstract fun playerDao(): PlayerDao

    abstract fun highscoreDao(): HighscoreDao

    abstract fun playerLevelDao(): PlayerLevelDao

    companion object {

        private var dbInst: DatabaseInstance? = null

        @Synchronized
        fun getInstance(context: Context): DatabaseInstance {
            if (dbInst == null) {
                dbInst = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseInstance::class.java,
                    "Snek"
                ).build()
            }
            return dbInst!!
        }
    }
}