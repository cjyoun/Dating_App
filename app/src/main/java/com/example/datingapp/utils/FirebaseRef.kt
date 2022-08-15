package com.example.datingapp.utils

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseRef {

    companion object{
        val database = Firebase.database
        val userInfoRef = database.getReference("userInfo") // 유저정보
        val userLikeRef = database.getReference("userLike") // 좋아요 정보
    }

}

