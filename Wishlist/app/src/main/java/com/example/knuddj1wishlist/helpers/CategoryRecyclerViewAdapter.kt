package com.example.knuddj1wishlist.helpers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.knuddj1wishlist.`interface`.IRecyclerViewItem
import com.example.knuddj1wishlist.enum.SortingField


class CategoryRecyclerViewAdapter(
    private var categories: ArrayList<WishlistCategory>,
    private var context: Context,
    private var listener: IRecyclerViewItem
): RecyclerView.Adapter<CategoryViewHolder>(){

    override fun getItemCount(): Int {
        return categories.size
    }

    fun getWishlistTotal() : Double {
        var wishlistTotalAmount = 0.0
        val currencyUtil = CurrencyUtil(context)
        for(category in categories){
            wishlistTotalAmount += category.categoryItems.sumByDouble { currencyUtil.getValueConvertedToCurrency(it.price, it.countryCode!!) }
        }
        return wishlistTotalAmount
    }

    fun getNumUnpurchasedItems(): Int{
        var numUnpurchased = 0
        for(category in categories){
            numUnpurchased += category.categoryItems.sumBy {if(!it.purchased) 1 else 0}
        }
        return numUnpurchased
    }

    fun sortItems(fieldToSortBy: SortingField){
        for(category in categories){
            when(fieldToSortBy){
                SortingField.NEWEST -> category.categoryItems.sortByDescending {it.date}
                SortingField.OLDEST -> category.categoryItems.sortBy {it.date}
                SortingField.ALPHABETICAL -> category.categoryItems.sortBy {it.name}
            }
        }
        notifyDataSetChanged()
    }

    fun loadNewData(newItems: ArrayList<WishlistCategory>){
        categories = newItems
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category: WishlistCategory = categories[position]

        holder.rcvCategoryItem.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.rcvCategoryItem.addOnItemTouchListener(RecyclerItemOnClickListener(context, holder.rcvCategoryItem, listener))
        holder.rcvCategoryItem.adapter = WishlistRecyclerViewAdapter(category.categoryItems, context)

        holder.txvCategoryName.text = category.categoryName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(com.example.knuddj1wishlist.R.layout.wishlist_category, parent, false)
        return CategoryViewHolder(view)
    }

}