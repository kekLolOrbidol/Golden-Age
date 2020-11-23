package com.moisha.snek.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.moisha.snek.App
import com.moisha.snek.R
import com.moisha.snek.database.DatabaseInstance
import com.moisha.snek.database.adapters.LevelAdapter
import com.moisha.snek.database.model.Level
import kotlinx.android.synthetic.main.activity_highscore_level.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class HighscoreLevelActivity : AppCompatActivity() {

    private var adapter: LevelAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highscore_level)

        adapter = LevelAdapter(this@HighscoreLevelActivity, arrayListOf<Level>())
        level_list_highscores.adapter = adapter

        doAsync {
            val data: Collection<Level> = getVals()
            uiThread {
                adapter!!.addAll(data)
            }
        }

        val listView: ListView = findViewById(R.id.level_list_highscores)

        listView.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val level: Level = parent?.getItemAtPosition(position) as Level

                setSpeed(level)
            }
        }

    }

    fun setSpeed(level: Level) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@HighscoreLevelActivity)
        builder.setTitle(R.string.diff)
        builder.setItems(
            R.array.speeds,
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    val editorIntent: Intent = Intent(
                        this@HighscoreLevelActivity,
                        HighscoreListActivity::class.java
                    )
                    editorIntent.putExtra("level", level.id)
                    editorIntent.putExtra(
                        "speed",
                        when (which) {
                            1 -> {
                                resources.getInteger(R.integer.medium_move)
                            }
                            2 -> {
                                resources.getInteger(R.integer.fast_move)
                            }
                            else -> {
                                resources.getInteger(R.integer.slow_move)
                            }
                        }
                    )

                    startActivity(editorIntent)
                }
            }
        )
        builder.create().show()
    }

    private fun getVals(): List<Level> {
        val uId: Int = App.getUser()
        return DatabaseInstance.getInstance(this@HighscoreLevelActivity).levelDao().getPlayerLevels(uId)
    }
}
