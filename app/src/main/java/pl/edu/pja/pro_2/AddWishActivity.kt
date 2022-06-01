package pl.edu.pja.pro_2

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.maps.model.LatLng
import pl.edu.pja.pro_2.database.WishDb
import pl.edu.pja.pro_2.database.WishDto
import pl.edu.pja.pro_2.databinding.ActivityAddWishBinding
import pl.edu.pja.pro_2.repository.SharedPrefsLocationRepository
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.concurrent.thread

const val FILENAME = "settings"

class AddWishActivity : AppCompatActivity() {

    private val view by lazy { ActivityAddWishBinding.inflate(layoutInflater) }
    private val executor by lazy { Executors.newSingleThreadExecutor() }
    private val db by lazy { WishDb.open(this) }
    private var photoTaken: Boolean = false
    private lateinit var photoFile: File
    lateinit var currentPhotoPath: String
    private val PICTURE_FROM_CAMERA: Int = 1
    private var uri: Uri? = null
    var dateNow: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        setUpReturnButton()
        setUpSaveButton()
        setUpTakePicture()
    }

    private fun setUpTakePicture() = view.addPictureButton.setOnClickListener {
        if (view.nameWishText.text.isNotBlank()) {
            view.nameWishDesc.setTextColor(view.descriptionDesc.textColors)
            view.pictureDesc.setTextColor(view.descriptionDesc.textColors)
            takePicture()
        } else {
            view.nameWishDesc.setTextColor(Color.RED)
            view.pictureDesc.setTextColor(Color.RED)
        }
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpSaveButton() = view.saveButton.setOnClickListener {
        val colorDef = view.locationDesc.textColors
        if (view.nameWishText.text.isNotBlank()) {
            view.nameWishDesc.setTextColor(colorDef)
            if (photoTaken) {
                view.pictureDesc.setTextColor(colorDef)
                executor.submit {
                    val mapShared =
                        getSharedPreferences(FILENAME, Context.MODE_WORLD_READABLE).all
                    val wishDto = WishDto(
                        0,
                        view.nameWishText.text.toString(),
                        view.addWishDesc.text.toString(),
                        mapShared["lat"].toString(),
                        mapShared["lng"].toString(),
                        "/storage/emulated/0/Pictures/img_${dateNow}.jpg"
                    )
                    db.wish.insert(wishDto)
                    setResult(Activity.RESULT_OK)
                    Geofencing.createGeoFence(
                        this,
                        LatLng(
                            mapShared["lat"].toString().toDouble(),
                            mapShared["lng"].toString().toDouble()
                        ),
                        view.nameWishText.text.toString()
                    )
                    finish()
                }
            } else {
                view.pictureDesc.setTextColor(Color.RED)
            }
        } else {
            view.nameWishDesc.setTextColor(Color.RED)
        }
    }

    private fun setUpReturnButton() = view.exitButton.setOnClickListener {
        finish()
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
                photoTaken = true
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }


}