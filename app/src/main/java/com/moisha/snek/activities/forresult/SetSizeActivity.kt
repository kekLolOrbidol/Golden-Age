package com.moisha.snek.activities.forresult

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.google.gson.Gson
import com.moisha.snek.R
import com.moisha.snek.database.model.Level

class SetSizeActivity : AppCompatActivity() {

    private val gson: Gson = Gson()
    private lateinit var level: Level

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_size)

        if (intent.hasExtra("x") && intent.hasExtra("y")) {

            findViewById<EditText>(R.id.set_size_x).setText(
                intent.getIntExtra("x", resources.getInteger(R.integer.min_level_width)).toString()
            )
            findViewById<EditText>(R.id.set_size_y).setText(
                intent.getIntExtra("y", resources.getInteger(R.integer.min_level_height)).toString()
            )

        }
    }

    fun startEditor(view: View) {
        val x: Int = findViewById<EditText>(R.id.set_size_x).text.toString().toInt()
        val y: Int = findViewById<EditText>(R.id.set_size_y).text.toString().toInt()

        var hasErrors: Boolean = false

        if (x < resources.getInteger(R.integer.min_level_width)
            || x > resources.getInteger(R.integer.max_level_width)
        ) {
            findViewById<TextView>(R.id.x_set_error).setText(R.string.x_error)
            hasErrors = true
        } else {
            findViewById<TextView>(R.id.x_set_error).setText(R.string.empty_string)
        }

        if (y < resources.getInteger(R.integer.min_level_height)
            || y > resources.getInteger(R.integer.max_level_height)
        ) {
            findViewById<TextView>(R.id.y_set_error).setText(R.string.y_error)
            hasErrors = true
        } else {
            findViewById<TextView>(R.id.y_set_error).setText(R.string.empty_string)
        }

        if (!hasErrors) {

            val result: Intent = Intent()

            result.putExtra("x", x)
            result.putExtra("y", y)

            setResult(Activity.RESULT_OK, result)

            finish()

        }

    }
}
