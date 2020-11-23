package com.moisha.snek.activities.forresult

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.moisha.snek.R
import com.moisha.snek.database.DatabaseInstance
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class SetNameActivity : AppCompatActivity() {

    private var type: Int = SetNameActivity.UNKNOWN_TYPE

    companion object {
        const val UNKNOWN_TYPE: Int = 0
        const val LEVEL_NAME_TYPE: Int = 1
        const val USERNAME_TYPE: Int = 2
        const val TYPE_EXTRA_NAME: String = "type"
        const val NAME_EXTRA: String = "name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_name)

        if (intent.hasExtra(SetNameActivity.TYPE_EXTRA_NAME)) {
            val type: Int = intent.getIntExtra(
                SetNameActivity.TYPE_EXTRA_NAME,
                SetNameActivity.UNKNOWN_TYPE
            )
            this.type = type
            val nameInput: EditText = findViewById(R.id.name)
            nameInput.hint =
                if (type == SetNameActivity.LEVEL_NAME_TYPE) {
                    resources.getString(R.string.hint_level_name)
                } else if (type == SetNameActivity.USERNAME_TYPE) {
                    resources.getString(R.string.hint_username)
                } else {
                    resources.getString(R.string.empty_string)
                }
        }
    }

    fun back(view: View) {
        this.finish()
    }

    fun sendResult(view: View) {

        val name = findViewById<EditText>(R.id.name).text.toString()
        var used = 0

        doAsync {

            if (type == SetNameActivity.LEVEL_NAME_TYPE) {
                used = DatabaseInstance
                    .getInstance(this@SetNameActivity)
                    .levelDao()
                    .nameUsed(name)
            } else if (type == SetNameActivity.USERNAME_TYPE) {
                used = DatabaseInstance
                    .getInstance(this@SetNameActivity)
                    .playerDao()
                    .nameUsed(name)
            }

            uiThread {

                if (name.equals(resources.getString(R.string.empty_string))) {

                    findViewById<TextView>(R.id.name_error).setText(R.string.name_empty_error)

                } else if (used > 0) {

                    findViewById<TextView>(R.id.name_error).setText(R.string.name_error)

                } else {

                    val result: Intent = Intent()

                    result.putExtra(
                        SetNameActivity.NAME_EXTRA,
                        name
                    )

                    setResult(Activity.RESULT_OK, result)

                    finish()

                }
            }
        }
    }
}
