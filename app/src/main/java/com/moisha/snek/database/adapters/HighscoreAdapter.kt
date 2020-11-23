package com.moisha.snek.database.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.moisha.snek.R
import com.moisha.snek.database.model.HighscoreListItem

class HighscoreAdapter(context: Context, highscoreList: ArrayList<HighscoreListItem>) : BaseAdapter() {
    private val highscoreList: ArrayList<HighscoreListItem> = highscoreList
    private val context: Context = context

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return highscoreList.size
    }

    override fun getItem(position: Int): Any {
        return highscoreList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = inflater.inflate(R.layout.list_item_highscore, parent, false)

        val name = rowView.findViewById(R.id.list_item_player) as TextView

        val score = rowView.findViewById(R.id.list_item_score) as TextView

        val highscore: HighscoreListItem = getItem(position) as HighscoreListItem

        name.text = highscore.uName
        score.text = highscore.score.toString()

        return rowView
    }

    fun addAll(data: Collection<HighscoreListItem>) {
        highscoreList.addAll(data)
        this.notifyDataSetChanged()
    }
}