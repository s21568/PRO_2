package pl.edu.pja.pro_2.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.HandlerCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import pl.edu.pja.pro_2.EditWishActivity
import pl.edu.pja.pro_2.Geofencing
import pl.edu.pja.pro_2.R
import pl.edu.pja.pro_2.database.WishDb
import pl.edu.pja.pro_2.database.WishDto
import pl.edu.pja.pro_2.databinding.ActivityEditWishBinding
import pl.edu.pja.pro_2.databinding.WishItemCardBinding
import kotlin.concurrent.thread

class WishAdapter(private val db: WishDb) : RecyclerView.Adapter<WishVH>() {

    private var wishList = listOf<WishDto>()
    private val mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())
    private var con: Context? = null

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishVH {
        val view = WishItemCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return WishVH(view).also { holder ->

            con=holder.itemView.context
            view.root.setOnClickListener {
                val intent =
                    Intent(holder.itemView.context, EditWishActivity::class.java)
                intent.putExtra("id", holder.layoutPosition)
                holder.itemView.context.startActivity(intent)
            }

            view.root.setOnLongClickListener {
                val builder = AlertDialog.Builder(holder.itemView.context)
                builder.setTitle(holder.itemView.context.getString(R.string.wanna_delete_this_wish_title))
                builder.setMessage(holder.itemView.context.getString(R.string.wanna_delete_this_wish_desc))
                builder.setPositiveButton(holder.itemView.context.getString(R.string.yes)) { dialog, whitch ->
                    Toast.makeText(
                        holder.itemView.context,
                        holder.itemView.context.getString(R.string.yes),
                        Toast.LENGTH_SHORT
                    ).show()
                    val wish = wishList[holder.layoutPosition]
                    con?.let { Geofencing.removeGeofence(it,LatLng(wish.latitude.toDouble(),wish.longtitude.toDouble())) }
                    delete(holder.layoutPosition)
                    notifyDataSetChanged()
                }
                builder.setNegativeButton(holder.itemView.context.getString(R.string.no)) { dialog, whitch ->
                    Toast.makeText(
                        holder.itemView.context,
                        holder.itemView.context.getString(R.string.no),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                builder.show()
                return@setOnLongClickListener true
            }

        }
    }

    override fun onBindViewHolder(holder: WishVH, position: Int) {
        holder.bind(wishList[position])
    }

    override fun getItemCount(): Int = wishList.size

    @SuppressLint("NotifyDataSetChanged")
    fun load() = thread {
        wishList = db.wish.getall() as MutableList<WishDto>
        mainHandler.post { notifyDataSetChanged() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun delete(layoutPosition: Int) = thread {
        val wish = wishList[layoutPosition]
        db.wish.delete(wish.id)
        load()
    }
}