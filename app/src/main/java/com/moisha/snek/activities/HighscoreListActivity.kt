package com.moisha.snek.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.moisha.snek.R
import com.moisha.snek.database.DatabaseInstance
import com.moisha.snek.database.adapters.HighscoreAdapter
import com.moisha.snek.database.model.HighscoreListItem
import kotlinx.android.synthetic.main.activity_highscore_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class HighscoreListActivity : AppCompatActivity() {

    private var levelId: Int = 0
    private var speed: Int = 0
    private var adapter: HighscoreAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highscore_list)

        adapter = HighscoreAdapter(
            this@HighscoreListActivity,
            arrayListOf<HighscoreListItem>()
        )
        highscore_list.adapter = adapter

        if (intent.hasExtra("level") && intent.hasExtra("speed")) {
            levelId = intent.getIntExtra("level", 0)
            speed = intent.getIntExtra("speed", 0)
        } else {
            this.finish() //if no values passed with intent
        }
        if (levelId == 0 || speed == 0) { //if wasn't usable values got from intent
            this.finish()
        } else {
            doAsync {
                val highscores: List<HighscoreListItem> = getVals()
                uiThread {
                    adapter!!.addAll(highscores)
                }
            }
        }
    }

    private fun getVals(): List<HighscoreListItem> {
        return DatabaseInstance
            .getInstance(this@HighscoreListActivity)
            .highscoreDao()
            .getByLevelAndSpeed(levelId, speed)
    }
}
