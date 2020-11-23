package com.moisha.snek.activities

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import com.moisha.snek.App
import com.moisha.snek.R
import com.moisha.snek.activities.forresult.SetNameActivity
import com.moisha.snek.database.DatabaseInstance
import com.moisha.snek.database.model.Player
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class LoginActivity : AppCompatActivity() {

    companion object {
        const val GET_NAME_REQUEST: Int = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val listView: ListView = findViewById(R.id.loginMenu)

        listView.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (id.toInt()) {
                    0 -> startActivity(
                        Intent(
                            this@LoginActivity,
                            SelectUserActivity::class.java
                        )
                    )
                    1 -> getUsername()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == LoginActivity.GET_NAME_REQUEST) {
                if (data?.hasExtra(SetNameActivity.NAME_EXTRA) ?: false) {
                    val name: String? = data!!.getStringExtra(SetNameActivity.NAME_EXTRA)
                    if (name != null) {
                        registerUsser(name)
                    }
                }
            }
        }
    }

    private fun registerUsser(name: String) {
        if (name.equals(resources.getString(R.string.empty_string))) {
            return
        }

        doAsync {
            val dao = DatabaseInstance.getInstance(this@LoginActivity).playerDao()

            if (dao.nameUsed(name) == 0) {
                dao.insert(Player(name))
                val uId: Int = dao.getIdByName(name)
                uiThread {
                    App.setUser(uId)
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun getUsername() {
        val setNameIntent = Intent(
            this@LoginActivity,
            SetNameActivity::class.java
        )

        setNameIntent.putExtra(SetNameActivity.TYPE_EXTRA_NAME, SetNameActivity.USERNAME_TYPE)

        startActivityForResult(
            setNameIntent,
            LoginActivity.GET_NAME_REQUEST
        )
    }
}
