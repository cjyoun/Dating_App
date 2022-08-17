package com.example.datingapp.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.datingapp.R
import com.example.datingapp.auth.IntroActivity
import com.example.datingapp.message.MyLikeListActivity
import com.example.datingapp.message.MyMsgActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // 마이페이지로 이동
        val myPageBtn = findViewById<Button>(R.id.goMyPageBtn)
        myPageBtn.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }

        // 로그아웃 하기
        val logout = findViewById<Button>(R.id.logoutBtn)
        logout.setOnClickListener {

            val auth = Firebase.auth
            auth.signOut()

            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
        }

        // 나의 좋아요 리스트로 이동하기
        val myLikeBtn = findViewById<Button>(R.id.myLikeBtn)
        myLikeBtn.setOnClickListener {

            val intent = Intent(this, MyLikeListActivity::class.java)
            startActivity(intent)
        }

        // 내가 받은 메세지로 이동하기
        val myMsgBtn = findViewById<Button>(R.id.myMsgBtn)
        myMsgBtn.setOnClickListener {

            val intent = Intent(this, MyMsgActivity::class.java)
            startActivity(intent)
        }
    }
}