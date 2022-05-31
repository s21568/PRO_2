package pl.edu.pja.pro_2.adapters

import android.annotation.SuppressLint
import android.location.Geocoder
import android.provider.MediaStore
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import pl.edu.pja.pro_2.database.WishDto
import pl.edu.pja.pro_2.databinding.WishItemCardBinding
import java.util.*

class WishVH(private val view: WishItemCardBinding) : RecyclerView.ViewHolder(view.root) {
    @SuppressLint("SetTextI18n")
    fun bind(wishDto: WishDto) {

        val geocoder: Geocoder = Geocoder(view.root.context, Locale.forLanguageTag("pl-PL"))
        val adress =
            geocoder.getFromLocation(wishDto.latitude.toDouble(), wishDto.longtitude.toDouble(), 1)

        var adresString = adress[0].thoroughfare
        if (adress[0].subThoroughfare != null) {
            adresString += " " + adress[0].subThoroughfare
        }
        adresString += (",\n " + adress[0].locality + ", " + adress[0].countryName)

        with(view) {
            wishNameCard.text = wishDto.name
            wishLocationCard.text = adresString
            wishPictureCard.setImageBitmap(wishDto.bitMapImage)
        }
    }
}