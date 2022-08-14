package com.example.datingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.example.datingapp.auth.IntroActivity
import com.example.datingapp.auth.UserInfoModel
import com.example.datingapp.setting.SettingActivity
import com.example.datingapp.slider.CardStackAdapter
import com.example.datingapp.utils.FirebaseRef
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction

class MainActivity : AppCompatActivity() {

    lateinit var cardStackAdapter: CardStackAdapter
    lateinit var manager : CardStackLayoutManager

    private val TAG = MainActivity::class.java.simpleName

    private val userDataList = mutableListOf<UserInfoModel>()  // userInfo 값들

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cardStackView = findViewById<CardStackView>(R.id.cardStackView)
        manager = CardStackLayoutManager(baseContext, object : CardStackListener{
            override fun onCardDragging(direction: Direction?, ratio: Float) {

            }

            override fun onCardSwiped(direction: Direction?) {

            }

            override fun onCardRewound() {

            }

            override fun onCardCanceled() {

            }

            override fun onCardAppeared(view: View?, position: Int) {

            }

            override fun onCardDisappeared(view: View?, position: Int) {

            }

        })



        val setting = findViewById<ImageView>(R.id.goSettingBtn)
        setting.setOnClickListener {

            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        cardStackAdapter = CardStackAdapter(baseContext, userDataList)
        cardStackView.layoutManager = manager
        cardStackView.adapter = cardStackAdapter

        getUserData()

    }

    // firebase 유저 데이터 가져오기
    private fun getUserData(){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // 가져오는 데이터의 자식데이터들을 하나씩 받아오기
                for(dataModel in dataSnapshot.children){
                    val user = dataModel.getValue(UserInfoModel::class.java)
                    userDataList.add(user!!)
                }

                cardStackAdapter.notifyDataSetChanged() // 동기화 시켜주기
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
       FirebaseRef.userInfoRef.addValueEventListener(postListener)

    }

}