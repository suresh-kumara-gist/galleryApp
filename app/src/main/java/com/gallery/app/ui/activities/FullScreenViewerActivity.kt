// ui/activities/FullScreenViewerActivity.kt
package com.gallery.app.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.gallery.app.data.model.MediaItem
import com.gallery.app.databinding.ActivityFullscreenViewerBinding
import com.gallery.app.ui.adapters.FullScreenAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FullScreenViewerActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityFullscreenViewerBinding
    private lateinit var adapter: FullScreenAdapter
    private var mediaList: List<MediaItem> = emptyList()
    private var currentPosition: Int = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreenViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        parseIntent()
        setupViewPager()
        setupToolbar()
    }
    
    private fun parseIntent() {
        val gson = Gson()
        val mediaListJson = intent.getStringExtra("media_list")
        currentPosition = intent.getIntExtra("position", 0)
        
        if (mediaListJson != null) {
            val type = object : TypeToken<List<MediaItem>>() {}.type
            mediaList = gson.fromJson(mediaListJson, type)
        }
    }
    
    private fun setupViewPager() {
        adapter = FullScreenAdapter(mediaList, this)
        binding.viewPager.adapter = adapter
        binding.viewPager.currentItem = currentPosition
        
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition = position
                updateTitle()
            }
        })
        
        updateTitle()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun updateTitle() {
        supportActionBar?.title = "${currentPosition + 1} / ${mediaList.size}"
    }
}