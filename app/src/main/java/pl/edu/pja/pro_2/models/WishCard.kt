package pl.edu.pja.pro_2.models

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pl.edu.pja.pro_2.R

class WishCard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wish_item_card)
    }
}