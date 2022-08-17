package com.example.datingapp.message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import com.example.datingapp.R
import com.example.datingapp.auth.UserInfoModel
import com.example.datingapp.message.fcm.NotiModel
import com.example.datingapp.message.fcm.PushNotification
import com.example.datingapp.message.fcm.RetrofitInstance
import com.example.datingapp.utils.FirebaseAuthUtils
import com.example.datingapp.utils.FirebaseRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// 내가 좋아요한 사람들이 나를 좋아요 한 리스트 (매칭된 리스트)
class MyLikeListActivity : AppCompatActivity() {

    private val TAG = MyLikeListActivity::class.java.simpleName
    private val uid = FirebaseAuthUtils.getUid()    // 나의 uid

    private val likeUserUidList = mutableListOf<String>()  // 내가 좋아요한 사람들의 uid 값들
    private val likeUserDataList = mutableListOf<UserInfoModel>()  // 내가 좋아요한 사람들의 uid 값들

    lateinit var listViewAdapter : MyLikeListViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_like_list)

        getMyUserLikeList()     // 내가 좋아요 한 사람 리스트 가져오기

        val userListView = findViewById<ListView>(R.id.userListView)
        listViewAdapter = MyLikeListViewAdapter(this, likeUserDataList)
        userListView.adapter = listViewAdapter

        userListView.setOnItemClickListener { parent, view, position, id ->
            Log.d(TAG, "리스트 클릭 - " + likeUserDataList[position].uid)
            // 리스트 선택한 사람이 나를 좋아요 했는지 체크
            checkMatching(likeUserDataList[position].uid)

            // PUSH 를 보낼 때 제목과 내용
            val notiModel = NotiModel("a","b")
            // 내가 선택한 사람의 토큰을 가지고 notiModel값을 담음
            val pushModel = PushNotification(notiModel, likeUserDataList[position].token.toString())
            // 다른 사람에게 메세지 보내기
            testPush(pushModel)

        }


    }

    // 내가 좋아요 한 사람의 리스트 불러오기
    private fun getMyUserLikeList(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for(dataModel in dataSnapshot.children){
                    Log.d(TAG, "내가 좋아요한 사람의 리스트 - ${dataModel.key.toString()}")
                    likeUserUidList.add(dataModel.key.toString())  // 내가 좋아요 한 사람들의 uid 값 담기
                }
                getUserDataList()   // 유저 리스트 전체 중에서 내가 좋아요 한 사람들의 정보만 가져오기
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userLikeRef.child(uid).addValueEventListener(postListener)
    }

    // 유저 리스트 전체 중에서 내가 좋아요 한 사람들의 정보만 가져오기
    private fun getUserDataList(){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // 가져오는 데이터의 자식데이터들을 하나씩 받아오기
                for(dataModel in dataSnapshot.children){
                    val user = dataModel.getValue(UserInfoModel::class.java)

                    // 내가 좋아요 한사람의 uid 리스트 중에 전체 유저 리스트 한명씩 가져올 때 가져오는 uid를 체크
                    if(likeUserUidList.contains(user?.uid)){
                        // -> 전체 유저 중에 내가 좋아요 한 사람의 정보만 가져 올 수 있음
                        likeUserDataList.add(user!!)
                    }

                }
                Log.d(TAG, "내가 좋아요 한 사람의 정보 : ${likeUserDataList.toString()}")
                listViewAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userInfoRef.addValueEventListener(postListener)

    }

    // 리스트 선택한 사람이 나를 좋아요 했는지 체크
    private fun checkMatching(otherUid:String){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // 선택한 사람이 좋아요를 한명도 안했을 경우
                if( dataSnapshot.children.count() == 0){
                    Toast.makeText(this@MyLikeListActivity,"매칭이 되지 않았습니다.",Toast.LENGTH_LONG).show()
                } else{
                    for(dataModel in dataSnapshot.children){
                        val likeUserKey = dataModel.key.toString()
                        // 선택한 사람의 좋아요 한 사람 리스트중에 내 uid가 있을 경우
                        if(likeUserKey == uid){
                            Toast.makeText(this@MyLikeListActivity,"매칭이 되었습니다.",Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(this@MyLikeListActivity,"매칭이 되지 않았습니다.",Toast.LENGTH_LONG).show()
                        }

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


    // PUSH 메세지 보내기
    private fun testPush(notification : PushNotification) = CoroutineScope(Dispatchers.IO).launch {

        RetrofitInstance.api.postNotification(notification)
        Toast.makeText(baseContext, "상대방에게 PUSh를 날렸습니다.", Toast.LENGTH_LONG).show()

    }

}