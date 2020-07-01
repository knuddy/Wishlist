package com.example.knuddj1wishlist.activities

import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.View
import com.example.knuddj1wishlist.R
import com.example.knuddj1wishlist.`interface`.IWarningConfirm
import com.example.knuddj1wishlist.enum.DatabaseStatus
import com.example.knuddj1wishlist.fragments.WarningDialogFragment
import com.example.knuddj1wishlist.helpers.CurrencyUtil
import com.example.knuddj1wishlist.helpers.DBHelper
import com.example.knuddj1wishlist.helpers.ImageCapture
import com.example.knuddj1wishlist.helpers.WishlistItem
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_item_details.*
import java.util.*
import kotlin.system.exitProcess

class ItemDetailsActivity : BaseActivity(), IWarningConfirm {
    private lateinit var item: WishlistItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_details)
        imvProductImage.transitionName = intent.getStringExtra("imProductTransitionName")
        val parcelable: WishlistItem? = intent.extras?.getParcelable("item")
        item= parcelable as WishlistItem

        setupViewsWithItemData(item)

        imgBtnDelete.setOnClickListener {
            WarningDialogFragment(this).show(supportFragmentManager, null)
        }

        imgBtnEdit.setOnClickListener {
            val intent = Intent(this, NewWishlistItemActivity::class.java)
            intent.putExtra("enum", DatabaseStatus.EDIT)
            intent.putExtra("itemToEdit", item)
            startActivityForResult(intent, 0)
        }

        imgBtnReturn.setOnClickListener {
            supportFinishAfterTransition();
        }
    }

    override fun onWarningConfirm() {
        val intentWithItem = Intent()
        intentWithItem.putExtra("itemToDelete", item)
        setResult(DatabaseStatus.DELETE.value, intentWithItem)
        finish()
    }

    private fun setupViewsWithItemData(item: WishlistItem){
        val currencyUtil = CurrencyUtil(this)
        txvName.text = item.name
        txvPrice.text = currencyUtil.buildValueString(currencyUtil.getValueConvertedToCurrency(item.price, item.countryCode!!))
        txvPurchased.text = if(item.purchased) "PURCHASED" else "NOT PURCHASED"
        txvCategory.text = item.category
        txvDate.text = DBHelper.DateTime.formatDateTime(item.date!!)
        txvAdditionalNotesBody.text = item.notes
        ImageCapture.BitmapProcessor.setImageToImageView(item.image.toString(), imvProductImage, this@ItemDetailsActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == DatabaseStatus.EDIT.value){
            val editedItem: WishlistItem = data?.getParcelableExtra("editedItem") as WishlistItem
            val intentWithItem = Intent()
            intentWithItem.putExtra("editedItem", editedItem)
            setResult(DatabaseStatus.EDIT.value, intentWithItem)
            setupViewsWithItemData(editedItem)
        }
    }
}
