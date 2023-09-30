package com.example.codesturadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.add_video).setOnClickListener {
            startActivity(Intent(this,add_video::class.java))
            finish()
        }
        findViewById<Button>(R.id.add_playlist).setOnClickListener {
            startActivity(Intent(this,add_playlist::class.java))
            finish()
        }
    }
}