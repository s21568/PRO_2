package pl.edu.pja.pro_2.database

import android.graphics.BitmapFactory
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
class WishDto(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val description: String,
    val latitude: String,
    val longtitude: String,
    val picture_url: String
) {
    @delegate:Ignore
    val bitMapImage by lazy {
        BitmapFactory.decodeFile(picture_url)
    }
}