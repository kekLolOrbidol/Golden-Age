package com.moisha.snek.activities.gl

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.moisha.snek.R
import com.moisha.snek.database.DatabaseInstance
import com.moisha.snek.database.dao.LevelDao
import com.moisha.snek.database.model.Level
import com.moisha.snek.editor.EditorField
import com.moisha.snek.App
import com.moisha.snek.activities.forresult.SetNameActivity
import com.moisha.snek.activities.forresult.SetSizeActivity
import com.moisha.snek.activities.gl.surfaces.EditorSurface
import com.moisha.snek.utility.GsonStatic
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditorActivity : AppCompatActivity() {

    companion object {
        const val GET_NAME_REQUEST: Int = 1
        const val GET_SIZE_REQUEST: Int = 2
    }

    private lateinit var mGLView: EditorSurface
    private lateinit var editor: EditorField

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mGLView = EditorSurface(
            this@EditorActivity
        )

        setContentView(mGLView)

        if (savedInstanceState?.containsKey("level") ?: false) { //if recreated from existed state

            val jsonEditorField: String = savedInstanceState?.getString("level")!!

            this.editor = GsonStatic.unpackEditor(jsonEditorField)

        } else if (intent.hasExtra("level")) { // if called with level for editing

            val levelJson: String = intent.getStringExtra("level")

            val level: Level = GsonStatic.unpackLevel(levelJson)

            editor = EditorField(level)


        } else { // if called for new level creation - ask size

            val intent = Intent(
                this@EditorActivity,
                SetSizeActivity::class.java
            )

            startActivityForResult(intent, GET_SIZE_REQUEST)

        }

        if (::editor.isInitialized) { //if editor successfully initialized, draw contents after view is set

            mGLView.requestRender()

        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        if (::editor.isInitialized) {
            outState?.putString(
                "level",
                GsonStatic.packEditor(editor)
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GET_NAME_REQUEST) {
                if (data?.hasExtra(SetNameActivity.NAME_EXTRA) ?: false) {

                    saveLevel(
                        data?.getStringExtra(SetNameActivity.NAME_EXTRA) ?: resources.getString(R.string.empty_string)
                    )

                }
            } else if (requestCode == GET_SIZE_REQUEST) {

                if (data?.hasExtra("x") ?: false && data?.hasExtra("y") ?: false) {

                    val x: Int = data?.getIntExtra(
                        "x",
                        resources.getInteger(R.integer.min_level_width)
                    ) ?: resources.getInteger(R.integer.min_level_width)
                    val y: Int = data?.getIntExtra(
                        "y",
                        resources.getInteger(R.integer.min_level_height)
                    ) ?: resources.getInteger(R.integer.min_level_height)

                    if (::editor.isInitialized) {
                        editor.changeSize(x, y)
                    } else {
                        editor = EditorField(x, y)
                    }

                    mGLView.requestRender()
                }

            }
        } else if (requestCode == GET_SIZE_REQUEST && !::editor.isInitialized) {
            finish()
        }
    }

    fun error() {
        this.runOnUiThread {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                .setMessage(R.string.snek_size_error)
                .setTitle(R.string.short_snek_error)
                .setNeutralButton(
                    R.string.ok
                ) { _: DialogInterface, _: Int -> }
            val error: AlertDialog = builder.create()

            error.show()
        }
    }

    fun action(coords: IntArray) {

        if (coords[0] == -1) {
            when (coords[1]) {
                1 -> {
                    editor.setAction(EditorField.ACTION_SET_SNEK)
                }
                2 -> {
                    editor.setAction(EditorField.ACTION_SET_BARRIER)
                }
                3 -> {
                    editor.clearSnek()
                }
                4 -> {
                    editor.clearBarriers()
                }
                5 -> {
                    changeSize()
                }
                6 -> {
                    saveLevel()
                }
            }
        } else {
            editor.react(coords)
        }

        mGLView.requestRender()
    }

    val getField = fun(): Array<IntArray> {
        return if (::editor.isInitialized)
            editor.getField() else arrayOf(intArrayOf(0))
    }

    private fun changeSize() {
        val getSizeIntent = Intent(
            this@EditorActivity,
            SetSizeActivity::class.java
        )

        getSizeIntent.putExtra("x", editor.getX())
        getSizeIntent.putExtra("y", editor.getY())

        startActivityForResult(getSizeIntent, GET_SIZE_REQUEST)

        return
    }

    private fun saveLevel(name: String = resources.getString(R.string.empty_string)) {
        if (editor.getSnekSize() - 1 < resources.getInteger(R.integer.min_snek_size)) {
            error()
            return
        }

        if (!name.equals(resources.getString(R.string.empty_string))) {
            editor.levelName = name
        }

        if (editor.levelName.equals(resources.getString(R.string.empty_string))) {

            val getNameIntent = Intent(
                this@EditorActivity,
                SetNameActivity::class.java
            )

            getNameIntent.putExtra(
                SetNameActivity.TYPE_EXTRA_NAME,
                SetNameActivity.LEVEL_NAME_TYPE
            )

            startActivityForResult(
                getNameIntent,
                GET_NAME_REQUEST
            )

        } else {
            val uId = App.getUser()
            val level: Level = editor.getLevel(uId)

            doAsync {
                val lvldao: LevelDao = DatabaseInstance.getInstance(this@EditorActivity).levelDao()
                if (lvldao.nameUsed(level.name) > 0) {
                    level.id = lvldao.getIdByName(level.name)
                    lvldao.updateLevels(level)
                } else {
                    lvldao.insert(level)
                }
                uiThread {
                    finish()
                }
            }
        }

        return
    }

}
