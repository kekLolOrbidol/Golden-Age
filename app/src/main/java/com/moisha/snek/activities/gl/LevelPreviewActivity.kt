package com.moisha.snek.activities.gl

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.moisha.snek.activities.gl.surfaces.PreviewSurface
import com.moisha.snek.database.model.Level
import com.moisha.snek.editor.EditorField
import com.moisha.snek.utility.GsonStatic

class LevelPreviewActivity : AppCompatActivity() {

    private lateinit var mGLView: PreviewSurface
    private lateinit var editor: EditorField

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mGLView = PreviewSurface(
            this@LevelPreviewActivity
        )

        setContentView(mGLView)

        if (savedInstanceState?.containsKey("level") ?: false) { //if recreated after resizing

            val jsonEditorField: String = savedInstanceState?.getString("level")!!

            this.editor = GsonStatic.unpackEditor(jsonEditorField)

        } else if (intent.hasExtra("level")) { // if called with level for preview

            val levelJson: String = intent.getStringExtra("level")

            val level: Level = GsonStatic.unpackLevel(levelJson)

            editor = EditorField(level)

        }
    }

    val getField = fun(): Array<IntArray> {
        return if (::editor.isInitialized)
            editor.getField() else arrayOf(intArrayOf(0))
    }
}
