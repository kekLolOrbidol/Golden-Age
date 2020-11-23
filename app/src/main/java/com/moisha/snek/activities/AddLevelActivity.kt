package com.moisha.snek.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.moisha.snek.App
import com.moisha.snek.R
import com.moisha.snek.activities.gl.LevelPreviewActivity
import com.moisha.snek.database.DatabaseInstance
import com.moisha.snek.database.adapters.LevelAddAdapter
import com.moisha.snek.database.model.Level
import com.moisha.snek.database.model.PlayerLevel
import com.moisha.snek.utility.GsonStatic
import kotlinx.android.synthetic.main.activity_add_level.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class AddLevelActivity : AppCompatActivity() {

    private var adapter: LevelAddAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_level)

        adapter = LevelAddAdapter(
            this@AddLevelActivity,
            arrayListOf<Level>()
        )
        add_level_list.adapter = adapter

        doAsync {
            val data: Collection<Level> = getVals()
            uiThread {
                adapter!!.addAll(data)
            }
        }

        val listView: ListView = findViewById(R.id.add_level_list)

        listView.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val level: Level = parent?.getItemAtPosition(position) as Level

                val levelJson: String = GsonStatic.packLevel(level)

                val previewIntent = Intent(
                    this@AddLevelActivity,
                    LevelPreviewActivity::class.java
                )
                previewIntent.putExtra("level", levelJson)

                startActivity(previewIntent)
            }
        }
    }

    fun addLevel(view: View) {
        val button = view as Button
        val row = view.parent as LinearLayout
        val id = row.getChildAt(2) as TextView

        makePlayerLevel(
            id.text.toString().toInt()
        )

        button.visibility = View.GONE
    }

    private fun makePlayerLevel(levelId: Int) {
        doAsync {
            val uId: Int = App.getUser()
            val playerLevel = PlayerLevel(uId, levelId)

            DatabaseInstance
                .getInstance(this@AddLevelActivity)
                .playerLevelDao()
                .insert(playerLevel)
        }
    }

    private fun getVals(): List<Level> {
        val uId: Int = App.getUser()

        return DatabaseInstance
            .getInstance(this@AddLevelActivity)
            .levelDao()
            .getUnplayableLevels(uId)
    }
}
