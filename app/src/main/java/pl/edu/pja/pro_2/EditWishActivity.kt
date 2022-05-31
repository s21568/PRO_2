package pl.edu.pja.pro_2

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import pl.edu.pja.pro_2.database.WishDb
import pl.edu.pja.pro_2.database.WishDto
import pl.edu.pja.pro_2.databinding.ActivityEditWishBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class EditWishActivity : AppCompatActivity() {

    private val view by lazy { ActivityEditWishBinding.inflate(layoutInflater) }
    private val executor by lazy { Executors.newSingleThreadExecutor() }
    private val db by lazy { WishDb.open(this) }
    private val KEY_LAT = "lat"
    private val KEY_LNG = "lng"
    private lateinit var photoFile: File
    lateinit var currentPhotoPath: String
    private val PICTURE_FROM_CAMERA: Int = 1
    private var uri: Uri? = null
    private var dateNow: String? = null
    private var dbId: Int = 1
    private var wishList: List<WishDto> = mutableListOf()
    private var bundle: Bundle? = null
    private var intentval = 1

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        setUpValues()
        setUpReturnButton()
        setUpEditButton()
        setUpTakePicture()
    }

    private fun setUpValues() {
        executor.submit {
            wishList = db.wish.getall()
            bundle = intent.extras
            intentval = bundle!!.getInt("id")
            dbId = db.wish.findByName(wishList[intentval].name).id.toInt()
            view.editTextTextPersonName.setText(wishList[intentval].name)
            view.editTextDescription.setText(wishList[intentval].description)
            val mapShared = getSharedPreferences(FILENAME, Context.MODE_PRIVATE)
            mapShared.edit()
                .putFloat(KEY_LAT, wishList[intentval].latitude.toFloat())
                .putFloat(KEY_LNG, wishList[intentval].longtitude.toFloat())
                .apply()
            view.pictureWish.setImageBitmap(BitmapFactory.decodeFile(wishList[intentval].picture_url))
        }
    }

    private fun setUpEditButton() = view.saveButton.setOnClickListener {
        val colorDef = view.locationDesc.textColors
        if (view.editTextTextPersonName.text.isNotBlank()) {
            view.nameWishDesc.setTextColor(colorDef)
            view.pictureDesc.setTextColor(colorDef)
            executor.submit {
                var picUrl: String = if (dateNow != null) {
                    "/storage/emulated/0/Pictures/img_${dateNow}.jpg"
                } else {
                    wishList[intentval].picture_url
                }
                val mapShared =
                    getSharedPreferences(FILENAME, Context.MODE_PRIVATE).all
                val wishDto = WishDto(
                    dbId.toLong(),
                    view.editTextTextPersonName.text.toString(),
                    view.editTextDescription.text.toString(),
                    mapShared["lat"].toString(),
                    mapShared["lng"].toString(),
                    picUrl

                )
                db.wish.update(wishDto)
                setResult(Activity.RESULT_OK)
                finish()
            }
        } else {
            view.nameWishDesc.setTextColor(Color.RED)
        }
    }

    private fun setUpTakePicture() = view.addPictureButton.setOnClickListener {
        if (view.editTextTextPersonName.text.isNotBlank()) {
            view.nameWishDesc.setTextColor(view.descriptionDesc.textColors)
            view.pictureDesc.setTextColor(view.descriptionDesc.textColors)
            takePicture()
        } else {
            view.nameWishDesc.setTextColor(Color.RED)
            view.pictureDesc.setTextColor(Color.RED)
        }
    }

    private fun setUpReturnButton() = view.exitButton.setOnClickListener {
        finish()
    }

    private fun takePicture() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = createImageFile()
        val uri = FileProvider.getUriForFile(this, "pl.edu.pja.pro_2.files", photoFile)
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(pictureIntent, PICTURE_FROM_CAMERA)

    }

    @SuppressLint("SimpleDateFormat")
    fun createImageFile(): File {
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        dateNow = simpleDateFormat.format(Date())
        return File.createTempFile(
            "img_${dateNow}",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICTURE_FROM_CAMERA) {
                uri = FileProvider.getUriForFile(this, "pl.edu.pja.pro_2.files", photoFile)
                view.pictureWish.setImageURI(uri)
                val images = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
                val info = ContentValues().apply {
                    put(
                        MediaStore.Images.Media.DISPLAY_NAME,
                        "img_${dateNow}.jpg"
                    )
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                }
                val imgUri = contentResolver.insert(images, info)
                imgUri ?: return
                val out = contentResolver.openOutputStream(imgUri)
                val bitmap = view.pictureWish.drawable.toBitmap()
                out.use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
                }
                out ?: return
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}