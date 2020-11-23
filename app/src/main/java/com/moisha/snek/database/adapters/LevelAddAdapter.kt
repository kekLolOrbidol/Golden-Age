package com.moisha.snek.database.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.moisha.snek.R
import com.moisha.snek.database.model.Level

class LevelAddAdapter(context: Context, levelList: ArrayList<Level>) : BaseAdapter() {
    private val levelList: ArrayList<Level> = levelList
    private val context: Context = context

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return levelList.size
    }

    override fun getItem(position: Int): Any {
        return levelList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = inflater.inflate(R.layout.list_item_level_add, parent, false)

        val name = rowView.findViewById(R.id.list_item_lvlname) as TextView

        val id = rowView.findViewById(R.id.list_item_level_id) as TextView

        val level: Level = getItem(position) as Level

        name.text = level.name
        id.text = level.id.toString()

        return rowView
    }

    fun addAll(data: Collection<Level>) {
        levelList.addAll(data)
        this.notifyDataSetChanged()
    }
}