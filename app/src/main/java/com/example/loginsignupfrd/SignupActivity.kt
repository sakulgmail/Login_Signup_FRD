package com.example.loginsignupfrd

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loginsignupfrd.databinding.ActivitySignupBinding
import com.google.firebase.database.*

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

        binding.signupButton.setOnClickListener {
            val signupUsername = binding.signupUsername.text.toString()
            val signupPassword = binding.signupPassword.text.toString()
            if (signupUsername.isNotEmpty() && signupPassword.isNotEmpty()) {
                signupUser(signupUsername, signupPassword)
            } else {
                Toast.makeText(this@SignupActivity, "All fields are mandatory", Toast.LENGTH_SHORT).show()
            }
        }
        binding.loginRedirect.setOnClickListener {
            Toast.makeText(this@SignupActivity, "Redirecting to Login", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
        }
    }

    private fun signupUser(username: String, password: String) {
        databaseReference.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        val id = databaseReference.push().key
                        val userData = UserData(id, username, password)
                        databaseReference.child(id!!).setValue(userData)
                        Toast.makeText(this@SignupActivity, "User registered successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@SignupActivity, "Username already exists", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@SignupActivity, "Failed to register user: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
