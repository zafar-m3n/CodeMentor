package com.example.codementor

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    fun signUpUser(view: View) {
        val name = findViewById<EditText>(R.id.name).text.toString().trim()
        val email = findViewById<EditText>(R.id.email).text.toString().trim()
        val username = findViewById<EditText>(R.id.username).text.toString().trim()
        val password = findViewById<EditText>(R.id.password).text.toString().trim()
        val defaultProfileImageUrl = "https://firebasestorage.googleapis.com/v0/b/codementor-6e6f5.appspot.com/o/profile.png?alt=media&token=25fba2a3-e7b9-4e63-adf6-29afbbf6c863"

        // Validate the input
        if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        // Proceed with creating the user
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val currentUser = auth.currentUser
                val userData = hashMapOf(
                    "name" to name,
                    "username" to username,
                    "email" to email,
                    "profileImageUrl" to defaultProfileImageUrl
                )

                currentUser?.let { user ->
                    db.collection("users").document(user.uid)
                        .set(userData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Sign up successful.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(baseContext, "Authentication failed: ${task.exception?.message}",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun signIn(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}