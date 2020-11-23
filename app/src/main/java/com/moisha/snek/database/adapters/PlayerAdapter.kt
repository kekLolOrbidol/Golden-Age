package com.moisha.snek.database.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.moisha.snek.R
import com.moisha.snek.database.model.Player

class PlayerAdapter(context: Context, playerList: ArrayList<Player>) : BaseAdapter() {
    private val playerList: ArrayList<Player> = playerList
    private val context: Context = context

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return playerList.size
    }

    override fun getItem(position: Int): Any {
        return playerList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = inflater.inflate(R.layout.list_item_user, parent, false)

        val name = rowView.findViewById(R.id.list_item_name) as TextView

        val id = rowView.findViewById(R.id.list_item_id) as TextView

        val user: Player = getItem(position) as Player

        name.text = user.name
        id.text = user.id.toString()

        return rowView
    }

    fun addAll(data: Collection<Player>) {
        playerList.addAll(data)
        this.notifyDataSetChanged()
    }
}