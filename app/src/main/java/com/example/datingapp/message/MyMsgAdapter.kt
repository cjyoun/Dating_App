package com.example.datingapp.message

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.datingapp.R

class MyMsgAdapter(val context: Context, val items: MutableList<MsgModel>) : BaseAdapter() {
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var convertView = convertView
        if(convertView==null){
            convertView = LayoutInflater.from(parent?.context).inflate(R.layout.my_msg_lv_item, parent, false)

        }

        val nickname = convertView!!.findViewById<TextView>(R.id.myMsgNicknameArea)
        nickname.text = items[position].nickname
        val textArea = convertView!!.findViewById<TextView>(R.id.myMsgTxtArea)
        textArea.text = items[position].sendTxt

        return convertView!!

    }
}