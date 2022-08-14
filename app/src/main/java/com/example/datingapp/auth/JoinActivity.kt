package com.example.datingapp.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.datingapp.MainActivity
import com.example.datingapp.R
import com.example.datingapp.utils.FirebaseRef
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class JoinActivity : AppCompatActivity() {

    private val TAG = JoinActivity::class.java.simpleName

    private lateinit var auth: FirebaseAuth // firebase

    private var uid = ""        // UID 값
    private var nickname = ""   // 닉네임 값
    private var gender = ""     // 성별 값
    private var city = ""       // 지역 값
    private var age = ""        // 나이 값값

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        val profileImage = findViewById<ImageView>(R.id.joinImageArea)

        // 핸드폰 내에 파일함 열기
        // 최근에 핸드폰으로 다운받았던 사진이 나옴
        // 더보기 창을 통해 갤러리, 드라이브 등의 어플을 열 수 있고 어플을 클릭하여 사진을 선택하면 그 사진을 불러옴
        val getAction = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri ->
                profileImage.setImageURI(uri)
            }
        )

        profileImage.setOnClickListener {
            getAction.launch("image/*")
        }



        auth = Firebase.auth

        val joinBtn = findViewById<Button>(R.id.joinBtn)
        joinBtn.setOnClickListener {

            // email , password
            val email = findViewById<TextInputEditText>(R.id.emailArea)
            val pwd = findViewById<TextInputEditText>(R.id.pwdArea)
            Log.d(TAG,"email - ${email.text.toString()} / pwd - ${pwd.text.toString()} " )

            // 닉네임 , 성별, 지역, 나이, UID 값 저장하기
            gender = findViewById<TextInputEditText>(R.id.genderArea).text.toString()
            city = findViewById<TextInputEditText>(R.id.cityArea).text.toString()
            age = findViewById<TextInputEditText>(R.id.ageArea).text.toString()
            nickname = findViewById<TextInputEditText>(R.id.nicknameArea).text.toString()
            Log.d(TAG,"gender - $gender / city - $city / age - $age / nickname - $nickname " )

            //  신규 사용자의 이메일 주소와 비밀번호를 전달하여 신규 계정 생성
            auth.createUserWithEmailAndPassword(email.text.toString(), pwd.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")

                        uid = auth.currentUser?.uid.toString()  // uid 값 등록

                        val userInfo = UserInfoModel(
                            nickname,
                            age,
                            city,
                            gender,
                            uid
                        )

                        // database에 담기
                        FirebaseRef.userInfoRef.child(uid).setValue(userInfo)

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    }
                }
        }




    }
}