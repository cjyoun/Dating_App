package com.example.datingapp.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.datingapp.R
import com.example.datingapp.auth.UserInfoModel
import com.example.datingapp.utils.FirebaseAuthUtils
import com.example.datingapp.utils.FirebaseRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MyPageActivity : AppCompatActivity() {

    private val TAG = MyPageActivity::class.java.simpleName

    private val uid = FirebaseAuthUtils.getUid()    // 기기 uid 가져오기

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)


    }

    private fun getMyData(){

        val myUid = findViewById<TextView>(R.id.myUid)              // uid
        val myNickname = findViewById<TextView>(R.id.myNickname)    // 닉네임
        val myAge = findViewById<TextView>(R.id.myAge)              // 나이
        val myGender = findViewById<TextView>(R.id.myGender)        // 성별
        val myCity = findViewById<TextView>(R.id.myCity)            // 지역
        val myImage = findViewById<ImageView>(R.id.myImage)         // 프로필사진

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val data = dataSnapshot.getValue(UserInfoModel::class.java)

                myUid.text = data!!.uid
                myNickname.text = data!!.nickname
                myAge.text = data!!.age
                myGender.text = data!!.gender
                myCity.text = data!!.city

                // storage에 있는 이미지를 가지고 Glide로 imageView에 넣기
                val storageRef = Firebase.storage.reference.child(data.uid+".png")
                storageRef.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
                    if(task.isSuccessful){
                        Glide.with(baseContext)
                            .load(task.result)
                            .into(myImage)
                    }
                })

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userInfoRef.child(uid).addValueEventListener(postListener)
    }




}