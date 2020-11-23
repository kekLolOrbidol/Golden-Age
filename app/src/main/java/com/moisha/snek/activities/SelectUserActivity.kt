package com.moisha.snek.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.moisha.snek.R
import com.moisha.snek.database.DatabaseInstance
import com.moisha.snek.database.adapters.PlayerAdapter
import com.moisha.snek.database.model.Player
import com.moisha.snek.App
import kotlinx.android.synthetic.main.activity_select_user.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class SelectUserActivity : AppCompatActivity() {
    private var adapter: PlayerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_user)

        adapter = PlayerAdapter(this@SelectUserActivity, arrayListOf<Player>())
        userList.adapter = adapter

        doAsync {
            val data: Collection<Player> = getVals()
            uiThread {
                adapter!!.addAll(data)
            }
        }

        val listView: ListView = findViewById(R.id.userList)

        listView.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val player: Player = parent?.getItemAtPosition(position) as Player
                App.setUser(player.id)

                startActivity(
                    Intent(
                        this@SelectUserActivity,
                        MainActivity::class.java
                    )
                )

            }
        }
    }

    private fun getVals(): List<Player> {
        return DatabaseInstance.getInstance(this@SelectUserActivity).playerDao().all
    }
}
