package com.example.codementor

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var greetingText: TextView
    private lateinit var profileImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        greetingText = findViewById(R.id.greetingText)
        profileImage = findViewById(R.id.profileImage)

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            fetchUserData(userId)
        }

        setupBottomNavigation()
        fetchResources()
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener  { item ->
            when (item.itemId) {
                R.id.navigation_home -> {

                    true
                }
                R.id.navigation_resources -> {
                    startActivity(Intent(this, ResourcesActivity::class.java))
                    true
                }
                R.id.navigation_challenges -> {
                    startActivity(Intent(this, ChallengesActivity::class.java))
                    true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.selectedItemId = R.id.navigation_home
    }

    private fun fetchResources() {
        val db = FirebaseFirestore.getInstance()

        db.collection("resources").limit(5).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val resource = document.toObject(Resource::class.java)
                    addResourceView(resource)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun addResourceView(resource: Resource) {
        val resourcesContainer = findViewById<LinearLayout>(R.id.resourcesContainer)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.resource_item, resourcesContainer, false)

        val resourceName = view.findViewById<TextView>(R.id.resourceName)
        val resourceCategory = view.findViewById<TextView>(R.id.resourceCategory)
        val resourceImage = view.findViewById<ImageView>(R.id.resourceImage)

        resourceName.text = resource.name
        resourceCategory.text = resource.category
        Glide.with(this).load(resource.image).into(resourceImage)

        resourcesContainer.addView(view)
    }

    private fun fetchUserData(userId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userProfileImageUrl = document.getString("profileImageUrl")

                    greetingText.text = document.getString("name")

                    userProfileImageUrl?.let { imageUrl ->
                        Glide.with(this)
                            .load(imageUrl)
                            .circleCrop() // Apply circular cropping
                            .into(profileImage)

                    }
                } else {
                    // Document does not exist
                    Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching user data: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}

data class Resource(
    val category: String = "",
    val content: String = "",
    val description: String = "",
    val image: String = "",
    val name: String = "",
    val rating: String = "",
    val url: String = ""
)
