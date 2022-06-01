package pl.edu.pja.pro_2.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface WishDao {
    @Query("Select * from WishDto;")
    fun getall(): List<WishDto>

    @Insert
    fun insert(wish: WishDto)

    @Query("SELECT * FROM WishDto where name=:name")
    fun findByName(name: String): WishDto

    @Query("SELECT * FROM WishDto where latitude=:lat and longtitude=:lng")
    fun findByLatLng(lat: Float, lng: Float): WishDto

    @Query("Delete from WishDto where id= :id ")
    fun delete(id: Long)

    @Update
    fun update(wish: WishDto): Int
}