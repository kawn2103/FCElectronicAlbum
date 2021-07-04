package com.example.fcelectronicalbum

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fcelectronicalbum.databinding.ActivityPhotoBinding
import java.util.*
import kotlin.concurrent.timer

class PhotoActivity : AppCompatActivity() {

    private val photoList = mutableListOf<Uri>()

    private var currentPosition = 0

    private var timer: Timer? = null

    private lateinit var binding: ActivityPhotoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getImageSrc()
    }

    private fun startTimer(){
        timer = timer(period = 5*1000){
            runOnUiThread {
                val current = currentPosition
                val next = if (photoList.size <= currentPosition + 1) 0 else currentPosition + 1

                binding.firstIv.setImageURI(photoList[current])

                binding.SecondIv.alpha = 0f
                binding.SecondIv.setImageURI(photoList[next])
                binding.SecondIv.animate()
                    .alpha(1.0f)
                    .setDuration(1000)
                    .start()

                currentPosition = next

            }
        }
    }
    
    override fun onStart() {
        super.onStart()
        startTimer()
    }

    override fun onStop() {
        super.onStop()
        timer?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

    private fun getImageSrc(){
        val size = intent.getIntExtra("photoListNum",0)
        for (i in 0..size){
            intent.getStringExtra("photo$i")?.let {
                photoList.add(Uri.parse(it))
            }
        }
    }
}