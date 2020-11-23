package com.moisha.snek.database.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.moisha.snek.App
import com.moisha.snek.R
import com.moisha.snek.database.model.Level

class LevelEditAdapter(context: Context, levelList: ArrayList<Level>) : BaseAdapter() {
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
        val rowView = inflater.inflate(R.layout.list_item_level_edit, parent, false)

        val name = rowView.findViewById(R.id.list_item_lvlname_e) as TextView
        val userId = rowView.findViewById(R.id.list_item_user_id) as TextView
        val levelId = rowView.findViewById(R.id.list_item_level_id_e) as TextView
        val editButton = rowView.findViewById(R.id.button_edit_level) as Button
        val deleteButton = rowView.findViewById(R.id.button_delete_level) as Button

        val level: Level = getItem(position) as Level

        val uId: Int = App.getUser()

        if (uId != level.uId) {
            editButton.visibility = View.GONE
            deleteButton.setText(R.string.button_unlink)
        }

        name.text = level.name
        levelId.text = level.id.toString()
        userId.text = level.uId.toString()

        return rowView
    }

    fun addAll(data: Collection<Level>) {
        levelList.addAll(data)
        this.notifyDataSetChanged()
    }

    fun newData(data: Collection<Level>) {
        levelList.clear()
        levelList.addAll(data)
        this.notifyDataSetChanged()
    }

}