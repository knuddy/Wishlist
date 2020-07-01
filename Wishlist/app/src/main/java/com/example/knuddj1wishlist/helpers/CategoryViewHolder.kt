package com.example.knuddj1wishlist.helpers

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.knuddj1wishlist.R
import kotlinx.android.synthetic.main.wishlist_category.view.*

class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var txvCategoryName: TextView = view.findViewById(R.id.txvCategoryName)
    var rcvCategoryItem: RecyclerView = view.findViewById(R.id.rcvCategoryItems)
}