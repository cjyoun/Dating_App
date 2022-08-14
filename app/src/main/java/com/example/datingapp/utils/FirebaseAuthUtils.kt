package com.example.datingapp.utils

import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthUtils {

    companion object{

        private lateinit var auth : FirebaseAuth

        // 현재 유저의 UID 값 가져오기
        fun getUid() : String{
            auth = FirebaseAuth.getInstance()
            return auth.currentUser?.uid.toString()
        }

    }
}