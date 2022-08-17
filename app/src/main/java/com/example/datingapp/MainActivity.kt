package com.example.datingapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.example.datingapp.auth.IntroActivity
import com.example.datingapp.auth.UserInfoModel
import com.example.datingapp.setting.SettingActivity
import com.example.datingapp.slider.CardStackAdapter
import com.example.datingapp.utils.FirebaseAuthUtils
import com.example.datingapp.utils.FirebaseRef
import com.example.datingapp.utils.MyInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction

class MainActivity : AppCompatActivity() {

    lateinit var cardStackAdapter: CardStackAdapter
    lateinit var manager : CardStackLayoutManager

    private val TAG = MainActivity::class.java.simpleName

    private val userDataList = mutableListOf<UserInfoModel>()  // userInfo 값들

    private val uid = FirebaseAuthUtils.getUid()    // 기기 uid 가져오기

    private lateinit var currentGender: String

    private var userCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cardStackView = findViewById<CardStackView>(R.id.cardStackView)
        manager = CardStackLayoutManager(baseContext, object : CardStackListener{
            override fun onCardDragging(direction: Direction?, ratio: Float) {

            }

            override fun onCardSwiped(direction: Direction?) {

                // 사진 오른쪽으로 보낼 때 (좋아요 표시)
                if(direction == Direction.Right){
                    //Toast.makeText(this@MainActivity, "오른쪽", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "넘긴사람 uid - ${userDataList[userCount].uid.toString()}")
                    // 유저의 좋아요를 표시
                    userLikeOtherUser(uid, userDataList[userCount].uid.toString())
                }

                // 사진 왼쪽으로 보낼 때 (거절 표시)
                if(direction == Direction.Left){
                    //Toast.makeText(this@MainActivity, "왼쪽", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "넘긴사람 uid - ${userDataList[userCount].uid.toString()}")

                }

                userCount += 1

                // userCount가 가져온 user데이터 만큼 되면
                if(userCount == userDataList.count()){
                    getUserDataList(currentGender)
                    Toast.makeText(this@MainActivity, "유저를 새롭게 받아오기", Toast.LENGTH_LONG).show()
                }

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

        getMyData() // 나의 데이터 가져오기

    }

    // firebase 유저 데이터 가져오기 (성별에 따른 유저 리스트 가져오기 )
    private fun getUserDataList(currentGender : String){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // 가져오는 데이터의 자식데이터들을 하나씩 받아오기
                for(dataModel in dataSnapshot.children){
                    val user = dataModel.getValue(UserInfoModel::class.java)

                    // 성별이 같을 때
                    if(user!!.gender.toString() == currentGender){

                    }else{ // 성별이 다를 때
                        userDataList.add(user!!)
                    }

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


    private fun getMyData(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG, dataSnapshot.toString())
                val data = dataSnapshot.getValue(UserInfoModel::class.java)

                Log.d(TAG,data?.gender.toString())
                currentGender = data?.gender.toString()
                getUserDataList(currentGender)    // 성별에 따른 유저 리스트 가져오기

                MyInfo.myNickname = data?.nickname.toString()   // MyInfo 라는 util 클래스 안에 닉네임 넣기

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userInfoRef.child(uid).addValueEventListener(postListener)
    }

    // 유저의 좋아요를 표시 ( 나의 uid와 나를 좋아요 한 사람의 uid )
    private fun userLikeOtherUser(myUid:String, otherUid:String){

        // UserLike 밑에 나의 uid 밑에 나를 좋아요한 사람의 uid 들을 쌓는데 그 값은 true
        // 내가 좋아요한 사람 리스트 쌓기
        FirebaseRef.userLikeRef.child(myUid).child(otherUid).setValue("true")

        getOtherUserLikeList(otherUid)
    }

    // 내가 좋아요 한 사람의 좋아요한 사람 리스트 불러오기 (내가 좋아요한 사람이 누구를 좋아요 했는지 알 수 있음)
    private fun getOtherUserLikeList(otherUid:String){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for(dataModel in dataSnapshot.children){
                    Log.d(TAG, "내가 좋아요한 사람의 좋아요한 사람 리스트 - ${dataModel.key.toString()}")
                    val likeUserKey = dataModel.key.toString()
                    // 리스트 중에 내 uid가 있는지 체크
                    if(uid == likeUserKey){
                        Toast.makeText(this@MainActivity, "매칭 완료", Toast.LENGTH_SHORT).show()
                        createNotificationChannel()
                        sendNotification()
                    }

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userLikeRef.child(otherUid).addValueEventListener(postListener)
    }

    // Notification
    //------------------------------------------------------------------------
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "name"
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Test_cChannel", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(){
        var builder = NotificationCompat.Builder(this, "Test_cChannel")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("매칭완료")
            .setContentText("매칭이 완료 되었습니다. 상대방도 나를 좋아해요.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)){
            notify(123,builder.build())
        }
    }
    //------------------------------------------------------------------------
}