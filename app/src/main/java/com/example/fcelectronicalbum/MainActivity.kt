package com.example.fcelectronicalbum

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.fcelectronicalbum.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val imageViewList: List<ImageView> by lazy {
        mutableListOf<ImageView>().apply {
            add(findViewById(R.id.iv11))
            add(findViewById(R.id.iv12))
            add(findViewById(R.id.iv13))
            add(findViewById(R.id.iv21))
            add(findViewById(R.id.iv22))
            add(findViewById(R.id.iv23))
            add(findViewById(R.id.iv31))
            add(findViewById(R.id.iv32))
            add(findViewById(R.id.iv33))

        }
    }

    private val imageUriList: MutableList<Uri> = mutableListOf()

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        binding.addPhotoBt.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    //todo 권한이 부여되었을 때의 처리
                    navigatePhotos()
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    //todo 교육용 팝업 확인 후 권한 팝업 이동 기능
                    showPermissionContextPopUp()
                }
                else -> {
                    requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1000)
                }
            }
        }

        binding.startAlbumBt.setOnClickListener {
            val intent = Intent(this, PhotoActivity::class.java)
            imageUriList.forEachIndexed { index, uri ->
                intent.putExtra("photo$index",uri.toString())
            }
            intent.putExtra("photoListNum",imageUriList.size)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
          1000 -> {
              if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                  navigatePhotos()
              } else {
                  Toast.makeText(this,"권한을 거부하셨습니다.",Toast.LENGTH_LONG).show()
              }
          }
            else -> {

            }
        }
    }

    private fun navigatePhotos(){
        var intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK){
            return
        }

        when(requestCode){
            2000 -> {
                val selectedImageUri: Uri? = data?.data
                if (selectedImageUri != null){

                    if (imageUriList.size == 9){
                        Toast.makeText(this,"사진갯수를 초과하였습니다..",Toast.LENGTH_LONG).show()
                        return
                    }

                    imageUriList.add(selectedImageUri)
                    imageViewList[imageUriList.size-1].setImageURI(selectedImageUri)
                } else {
                    Toast.makeText(this,"사진을 가져오지 못했습니다.",Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                Toast.makeText(this,"사진을 가져오지 못했습니다.",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showPermissionContextPopUp() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("전자액자에 앱에서 사진을 불러오기 위해 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }
}