package com.example.baseactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.baseactivity.databinding.ActivityMainBinding
import com.remi.remiads.dialog.DialogLoadAds

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}