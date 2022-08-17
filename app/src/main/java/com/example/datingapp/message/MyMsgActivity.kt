package com.example.datingapp.message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.example.datingapp.MainActivity
import com.example.datingapp.R
import com.example.datingapp.auth.UserInfoModel
import com.example.datingapp.utils.FirebaseAuthUtils
import com.example.datingapp.utils.FirebaseRef
import com.example.datingapp.utils.MyInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MyMsgActivity : AppCompatActivity() {

    private val TAG = MyMsgActivity::class.java.simpleName
    private val uid = FirebaseAuthUtils.getUid()    // 기기 uid 가져오기

    lateinit var listViewAdapter: MyMsgAdapter
    val msgList = mutableListOf<MsgModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_msg)

        getMyMsg()

        val listView = findViewById<ListView>(R.id.msgListView)

        listViewAdapter = MyMsgAdapter(this, msgList)
        listView.adapter = listViewAdapter


    }

    // 내가 받은 메세지 리스트 받아오기
    private fun getMyMsg(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                msgList.clear()

                for(dataModel in dataSnapshot.children){

                    val msg = dataModel.getValue(MsgModel::class.java)
                    Log.d(TAG,msg.toString())
                    msgList.add(msg!!)

                }

                msgList.reverse() // 최신순으로 정렬
                listViewAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userMsgRef.child(uid).addValueEventListener(postListener)
    }

}