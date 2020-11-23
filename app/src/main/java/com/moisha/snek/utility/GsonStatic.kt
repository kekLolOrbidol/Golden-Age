package com.moisha.snek.utility

import com.google.gson.Gson
import com.moisha.snek.database.model.Level
import com.moisha.snek.editor.EditorField

class GsonStatic {
    companion object {
        val gson: Gson = Gson()

        fun packLevel(level: Level): String {
            return gson.toJson(level)
        }

        fun unpackLevel(level: String): Level {
            return gson.fromJson(
                level,
                Level::class.java
            )
        }

        fun packEditor(editor: EditorField): String {
            return gson.toJson(editor)
        }

        fun unpackEditor(editor: String): EditorField {
            return gson.fromJson(
                editor,
                EditorField::class.java
            )
        }
    }
}