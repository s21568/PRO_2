package pl.edu.pja.pro_2

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import pl.edu.pja.pro_2.adapters.DeleteCallback
import pl.edu.pja.pro_2.adapters.WishAdapter
import pl.edu.pja.pro_2.database.WishDb
import pl.edu.pja.pro_2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val view by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val wishAdapter by lazy { WishAdapter(WishDb.open(this)) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        setUpAddButton()
        setUpTaskList()
        Notifications.createChannel(this)
    }

    private fun setUpAddButton() = view.addNewWishButton.setOnClickListener {
        startActivity(Intent(this, AddWishActivity::class.java))
    }

    private fun setUpTaskList() {
        val itemSwitch = ItemTouchHelper(DeleteCallback())
        view.wishList.apply {
            adapter = wishAdapter
            layoutManager = LinearLayoutManager(context)
            itemSwitch.attachToRecyclerView(this)
        }
        wishAdapter.load()
    }

    override fun onResume() {
        setUpTaskList()
        super.onResume()
    }



}