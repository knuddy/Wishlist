package com.example.knuddj1wishlist.`interface`

import android.view.View
import com.example.knuddj1wishlist.helpers.WishlistRecyclerViewAdapter

interface IRecyclerViewItem {
    fun onItemClick(view: View, position: Int, reyclerView: WishlistRecyclerViewAdapter)
}