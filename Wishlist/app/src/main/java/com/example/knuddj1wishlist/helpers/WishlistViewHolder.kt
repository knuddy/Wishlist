package com.example.knuddj1wishlist.helpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.knuddj1wishlist.R

class WishlistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var imvItem: ImageView = view.findViewById(R.id.imvItem)
    var txvItemName: TextView = view.findViewById(R.id.txvItemName)
    var imvPurchased: ImageView = view.findViewById(R.id.imvPurchased)
}