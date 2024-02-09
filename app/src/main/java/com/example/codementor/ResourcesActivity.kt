package com.example.codementor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import android.speech.tts.TextToSpeech
import java.util.Locale

class ResourcesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resources)

        setupBottomNavigation()
        fetchResources()
    }

    private fun fetchResources() {
        val db = FirebaseFirestore.getInstance()

        db.collection("resources").get()
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

        // Set an OnClickListener for the view
        view.setOnClickListener {
            val detailIntent = Intent(this, ResourceDetailActivity::class.java).apply {
                putExtra("name", resource.name)
                putExtra("category", resource.category)
                putExtra("description", resource.description)
                putExtra("content", resource.content)
                putExtra("rating", resource.rating)
                putExtra("image", resource.image)
                putExtra("url", resource.url)
            }
            startActivity(detailIntent)
        }
        resourcesContainer.addView(view)
    }


    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener  { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.navigation_resources -> {
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
        bottomNavigationView.selectedItemId = R.id.navigation_resources
    }
}

class ResourceDetailActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech
    private lateinit var btnReadAloud: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resource_detail)

        textToSpeech = TextToSpeech(this, this)

        btnReadAloud = findViewById(R.id.btnReadAloud)
        btnReadAloud.setOnClickListener {
            readContentAloud()
        }

        setupBottomNavigation()

        // Fetching all views
        val resourceNameTextView = findViewById<TextView>(R.id.resourceDetailName)
        val resourceCategoryTextView = findViewById<TextView>(R.id.resourceDetailCategory)
        val resourceDescriptionTextView = findViewById<TextView>(R.id.resourceDetailDescription)
        val resourceContentTextView = findViewById<TextView>(R.id.resourceDetailContent)
        val resourceRatingTextView = findViewById<TextView>(R.id.resourceDetailRating)
        val resourceImageView = findViewById<ImageView>(R.id.resourceDetailImage)
        val resourceUrlButton = findViewById<Button>(R.id.resourceDetailUrl)

        // Retrieving data from intent
        val name = intent.getStringExtra("name")
        val category = intent.getStringExtra("category")
        val description = intent.getStringExtra("description")
        val content = intent.getStringExtra("content")
        val rating = intent.getStringExtra("rating")
        val image = intent.getStringExtra("image")
        val url = intent.getStringExtra("url")

        // Setting data to views
        resourceNameTextView.text = name
        resourceCategoryTextView.text = "Category: $category"
        resourceDescriptionTextView.text = "Description: $description"
        resourceContentTextView.text = "$content"
        resourceRatingTextView.text = "Rating: $rating"
        Glide.with(this).load(image).into(resourceImageView)

        // Handling URL button click
        resourceUrlButton.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Invalid URL", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Language not supported or missing data", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "TTS Initialization failed!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readContentAloud() {
        val contentToRead = findViewById<TextView>(R.id.resourceDetailContent).text.toString()
        textToSpeech.speak(contentToRead, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener  { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.navigation_resources -> {
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
         bottomNavigationView.selectedItemId
    }
}
