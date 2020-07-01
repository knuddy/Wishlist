package com.example.knuddj1wishlist.helpers

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.knuddj1wishlist.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_new_wishlist_item.*

class WishlistRecyclerViewAdapter(private var items: ArrayList<WishlistItem>, private var context: Context) :
    RecyclerView.Adapter<WishlistViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    fun getItem(position: Int) : WishlistItem? {
        return if(items.isNotEmpty()) items[position] else null
    }

    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        val item: WishlistItem = items[position]
        ImageCapture.BitmapProcessor.setImageToImageView(item.image.toString(), holder.imvItem, context)

        holder.txvItemName.text = item.name
        holder.imvItem.transitionName = "item_image_product" + position.toString()

        if(item.purchased) holder.imvPurchased.visibility = View.VISIBLE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.wishlist_item, parent, false)
        return WishlistViewHolder(view)
    }
}