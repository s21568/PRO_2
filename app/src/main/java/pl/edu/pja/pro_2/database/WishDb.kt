package pl.edu.pja.pro_2.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

private const val DATABASE_FILENAME = "WishList"

@Database(
    entities = [WishDto::class],
    version = 1
)

abstract class WishDb : RoomDatabase() {
    abstract val wish: WishDao

    companion object {
        fun open(context: Context) = Room.databaseBuilder(
            context, WishDb::class.java, DATABASE_FILENAME
        ).build()
    }

}