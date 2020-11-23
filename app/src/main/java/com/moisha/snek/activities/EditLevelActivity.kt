package com.moisha.snek.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import com.moisha.snek.App
import com.moisha.snek.R
import com.moisha.snek.activities.gl.EditorActivity
import com.moisha.snek.activities.gl.LevelPreviewActivity
import com.moisha.snek.database.DatabaseInstance
import com.moisha.snek.database.adapters.LevelEditAdapter
import com.moisha.snek.database.model.Level
import com.moisha.snek.database.model.PlayerLevel
import com.moisha.snek.utility.GsonStatic
import kotlinx.android.synthetic.main.activity_edit_levels.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditLevelActivity : AppCompatActivity() {

    private var adapter: LevelEditAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_levels)

        adapter = LevelEditAdapter(this@EditLevelActivity, arrayListOf<Level>())
        edit_level_list.adapter = adapter

        doAsync {
            val data: Collection<Level> = getVals()
            uiThread {
                adapter!!.addAll(data)
            }
        }

        val listView: ListView = findViewById(R.id.edit_level_list)

        listView.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val level: Level = parent?.getItemAtPosition(position) as Level

                val levelJson: String = GsonStatic.packLevel(level)

                val previewIntent = Intent(
                    this@EditLevelActivity,
                    LevelPreviewActivity::class.java
                )
                previewIntent.putExtra("level", levelJson)

                startActivity(previewIntent)
            }
        }
    }

    fun moreLevels(view: View) {
        val addLevelIntent = Intent(
            this@EditLevelActivity,
            AddLevelActivity::class.java
        )

        startActivity(addLevelIntent)
    }

    fun deleteLevel(view: View) {
        val uId: Int = App.getUser()

        val row = view.parent as LinearLayout
        val levelId = row.getChildAt(3) as TextView
        val userId = row.getChildAt(4) as TextView


        if (uId == userId.text.toString().toInt()) {
            deleteSure(
                levelId.text.toString().toInt()
            )
        } else {
            deleteSure(
                levelId.text.toString().toInt(),
                uId
            )
        }
    }

    fun editLevel(view: View) {
        val row = view.parent as LinearLayout
        val levelId =
            (row.getChildAt(3) as TextView)
                .text
                .toString()
                .toInt()

        val editorIntent = Intent(
            this@EditLevelActivity,
            EditorActivity::class.java
        )

        doAsync {
            val level: Level =
                DatabaseInstance
                    .getInstance(this@EditLevelActivity)
                    .levelDao()
                    .getById(levelId)
            val jsonLevel: String = GsonStatic.packLevel(level)
            uiThread {
                editorIntent.putExtra("level", jsonLevel)
                startActivity(editorIntent)
            }
        }
    }

    private fun getVals(): List<Level> {
        val uId: Int = App.getUser()
        return DatabaseInstance
            .getInstance(this@EditLevelActivity)
            .levelDao()
            .getPlayerLevels(uId)
    }

    private fun delLevel(levelId: Int) {
        val level: Level =
            DatabaseInstance
                .getInstance(this@EditLevelActivity)
                .levelDao()
                .getById(levelId)
        DatabaseInstance
            .getInstance(this@EditLevelActivity)
            .levelDao()
            .delete(level)
    }

    private fun unlinkLevel(uId: Int, levelId: Int) {
        val playerLevel: PlayerLevel =
            DatabaseInstance
                .getInstance(this@EditLevelActivity)
                .playerLevelDao()
                .getByPlayerAndLevel(uId, levelId)
        DatabaseInstance
            .getInstance(this@EditLevelActivity)
            .playerLevelDao()
            .delete(playerLevel)
    }

    private fun deleteSure(levelId: Int, userId: Int = -1) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@EditLevelActivity)
        builder.setTitle(
            if (userId == -1) {
                R.string.alert_unlink
            } else {
                R.string.alert_delete
            }
        )
        builder.setMessage(R.string.sure)
        builder.setPositiveButton(
            R.string.yes,
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    doAsync {
                        if (userId == -1) {
                            delLevel(levelId)
                        } else {
                            unlinkLevel(userId, levelId)
                        }
                        val data: Collection<Level> = getVals()
                        uiThread {
                            adapter!!.newData(data)
                        }
                    }
                }
            }
        )
        builder.setNegativeButton(
            R.string.no,
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.cancel()
                }
            }
        )
        builder.create().show()
    }

}