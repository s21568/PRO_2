package pl.edu.pja.pro_2.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Wish(
    val id: Long,
    val name: String,
    val description: String,
    val latitude: Long,
    val longtitude: Long,
    val picture_url: String
) : Parcelable