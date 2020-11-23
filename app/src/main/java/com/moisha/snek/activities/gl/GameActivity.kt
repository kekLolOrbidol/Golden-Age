package com.moisha.snek.activities.gl

import android.app.AlertDialog
import android.content.DialogInterface
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.moisha.snek.App
import com.moisha.snek.R
import com.moisha.snek.database.DatabaseInstance
import com.moisha.snek.database.model.Highscore
import com.moisha.snek.database.model.Level
import com.moisha.snek.game.Game
import com.moisha.snek.activities.gl.surfaces.GameSurface
import com.moisha.snek.game.State
import com.moisha.snek.utility.GsonStatic
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.concurrent.timerTask

class GameActivity : AppCompatActivity() {

    private lateinit var mGLView: GameSurface

    private lateinit var game: Game
    private var speed: Int = 0
    private val timer: Timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGLView = GameSurface(
            this@GameActivity
        )

        setContentView(mGLView)
        mGLView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

        if (savedInstanceState?.containsKey("state") ?: false &&
            savedInstanceState?.containsKey("speed") ?: false &&
            intent.hasExtra("level")
        ) {
            //if recreated from existing game

            val state: State = savedInstanceState?.getSerializable("state") as State
            val uId: Int = App.getUser()
            val level: Level = getLevel()

            speed = savedInstanceState.getInt("speed")

            game = Game(level, uId, speed, state)

        } else if (intent.hasExtra("level")) { // if called with level to play

            val uId: Int = App.getUser()
            val level: Level = getLevel()

            setSpeed()

            game = Game(level, uId, speed)

        } else { //if no ways to initialize game logic
            finish()
        }

        if (::game.isInitialized && speed > 0) {
            startGame()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (::game.isInitialized) {
            outState?.putSerializable("state", game.getState())
            outState?.putInt("speed", speed)
        }
    }

    fun action(coords: IntArray) {
        if (coords[0] == -1) {
            when (coords[1]) {
                2 -> {
                    game.setDirection(
                        Game.DIRECTOPN_LEFT
                    )
                }
                3 -> {
                    game.setDirection(
                        Game.DIRECTION_DOWN
                    )
                }
                4 -> {
                    game.setDirection(
                        Game.DIRECTION_UP
                    )
                }
                5 -> {
                    game.setDirection(
                        Game.DIRECTION_RIGHT
                    )
                }
            }
        }
    }

    private fun startGame() {
        //main game loop, based on java.util.timer
        if (::game.isInitialized) {
            timer.scheduleAtFixedRate(
                timerTask {
                    if (game.move()) {
                        mGLView.requestRender()
                    } else {
                        storeResult()
                        timer.cancel()
                        timer.purge()
                    }
                },
                speed.toLong(),
                speed.toLong()
            )
        }
    }

    private fun getLevel(): Level {
        return GsonStatic.unpackLevel(
            intent.getStringExtra("level")
        )
    }

    private fun setSpeed() {
        speed = intent.getIntExtra(
            "speed",
            resources.getInteger(
                R.integer.slow_move
            )
        )
    }

    private fun storeResult() {
        if (::game.isInitialized) {
            val result: Highscore = game.getResult()
            doAsync {
                DatabaseInstance.getInstance(this@GameActivity).highscoreDao().insert(result)
                uiThread {
                    showScore(result)
                }
            }
        }
    }

    private fun showScore(score: Highscore) {
        this.runOnUiThread {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@GameActivity)
                .setMessage(
                    (
                            resources.getString(R.string.score_body) +
                                    score.score.toString()
                            ) as CharSequence
                )
                .setTitle(R.string.score_title)
                .setNeutralButton(
                    R.string.ok
                ) { _: DialogInterface, _: Int ->
                    this@GameActivity.finish()
                }
            val alert: AlertDialog = builder.create()

            alert.show()
        }
    }

    val getField = fun(): Array<IntArray> {
        return if (::game.isInitialized)
            game.getField() else arrayOf(intArrayOf(0))
    }
}
