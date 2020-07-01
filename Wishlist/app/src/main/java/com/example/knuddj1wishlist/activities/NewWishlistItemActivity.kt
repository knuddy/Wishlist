package com.example.knuddj1wishlist.activities


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.knuddj1wishlist.R
import com.example.knuddj1wishlist.enum.DatabaseStatus
import com.example.knuddj1wishlist.helpers.CurrencyUtil
import com.example.knuddj1wishlist.helpers.DBHelper
import com.example.knuddj1wishlist.helpers.ImageCapture
import com.example.knuddj1wishlist.helpers.WishlistItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_new_wishlist_item.*
import java.util.*

class NewWishlistItemActivity : BaseActivity() {
    private lateinit var imgCapture: ImageCapture
    private var imgFilePath: String = ""

    companion object{
        const val REQUEST_CODE_PERMISSION: Int = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_wishlist_item)

        imgCapture = ImageCapture(this@NewWishlistItemActivity)
        imgBtnCaptureImage.setOnClickListener { openCamera() }

        txvCurrencySymbol.text = CurrencyUtil(this).getCurrencySymbol()

        val status: DatabaseStatus = intent.getSerializableExtra("enum") as DatabaseStatus
        if(status == DatabaseStatus.EDIT){
            val itemToEdit: WishlistItem = intent.getParcelableExtra("itemToEdit") as WishlistItem
            setFieldsWithItemData(itemToEdit)
            imgBtnConfirm.setOnClickListener(EditButtonOnClickListener(itemToEdit))
        } else {
            imgBtnConfirm.setOnClickListener(InsertButtonOnClickListener())
        }

        imgBtnCancel.setOnClickListener{ finish() }

    }

    private fun setFieldsWithItemData(item: WishlistItem){
        fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
        edtItemName.text = item.name?.toEditable()
        edtItemCategory.text = item.category?.toEditable()
        edtItemPrice.text = CurrencyUtil(this).getValueConvertedToCurrency(item.price, item.countryCode!!).toString().toEditable()
        edtItemStoreName.text = item.store?.toEditable()
        edtItemNotes.text = item.notes?.toEditable()
        switchPurchased.isChecked = item.purchased
        imgFilePath = item.image.toString()
        ImageCapture.BitmapProcessor.setImageToImageView(imgFilePath, imvItemImage, this@NewWishlistItemActivity)
    }

    private fun fieldsAreValid(name: String, category: String, price: String, storeName: String) : Boolean {
        var isValid = false
        val errorTemplate = "Item %s cannot be blank!"
        when {
            name.isEmpty() -> { edtItemName.error = errorTemplate.format("name") }
            category.isEmpty() -> { edtItemCategory.error = errorTemplate.format("category") }
            price.isEmpty() -> { edtItemPrice.error = errorTemplate.format("price") }
            storeName.isEmpty() -> { edtItemStoreName.error = errorTemplate.format("store name") }
            imgFilePath.isEmpty() -> { Toast.makeText(applicationContext,"Missing product image!", Toast.LENGTH_LONG).show()}
            else -> { isValid = true }
        }
        return isValid
    }

    private fun getIncompleteItem() : WishlistItem?{
        var newItem: WishlistItem? = null

        val itemName: String = edtItemName.text.toString().trim()
        val itemCategory: String = edtItemCategory.text.toString().trim()
        val itemPriceStr: String = edtItemPrice.text.toString().trim()
        val itemStoreName: String = edtItemStoreName.text.toString().trim()

        if (fieldsAreValid(itemName, itemCategory, itemPriceStr, itemStoreName)){
            newItem = WishlistItem()
            newItem.name = itemName
            newItem.image = imgFilePath
            newItem.category = itemCategory
            newItem.price = itemPriceStr.toDouble()
            newItem.store = itemStoreName
            newItem.purchased = switchPurchased.isChecked
        }
        return newItem
    }

    inner class EditButtonOnClickListener(private var item: WishlistItem) : View.OnClickListener {
        override fun onClick(v: View) {
            val editedItem: WishlistItem? = getIncompleteItem()
            if(editedItem != null){
                editedItem.id = item.id
                editedItem.date = item.date
                editedItem.notes = item.notes
                editedItem.countryCode = CurrencyUtil(applicationContext).getCurrencyCode()

                val intentWithItem = Intent()
                intentWithItem.putExtra("editedItem", editedItem)
                setResult(DatabaseStatus.EDIT.value, intentWithItem)
                finish()
            }
        }
    }

    inner class InsertButtonOnClickListener : View.OnClickListener {
         override fun onClick(v: View) {
             val item: WishlistItem? = getIncompleteItem()
             if(item != null){
                 item.date = DBHelper.DateTime.currentDateTime()
                 item.notes = edtItemNotes.text.toString().trim()
                 item.countryCode = CurrencyUtil(applicationContext).getCurrencyCode()

                 val intentWithItem = Intent()
                 intentWithItem.putExtra("newItem", item)
                 setResult(DatabaseStatus.INSERT.value, intentWithItem)
                 finish()
             }
         }
     }

    private fun requestPermissions(){
        ActivityCompat.requestPermissions(
            this@NewWishlistItemActivity,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            REQUEST_CODE_PERMISSION
        )
    }

    private fun isPermissionGranted(): Boolean {
        if(ActivityCompat.checkSelfPermission(
                this@NewWishlistItemActivity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this@NewWishlistItemActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun openCamera(){
        if(isPermissionGranted()){
            val uri: Uri = imgCapture.prepare()
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val newIntent: Intent = intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            startActivityForResult(newIntent, REQUEST_CODE_PERMISSION)
        }else {
            requestPermissions()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_PERMISSION && resultCode == Activity.RESULT_OK){
            imgFilePath = imgCapture.imgFile.path
            ImageCapture.BitmapProcessor.setImageToImageView(imgFilePath, imvItemImage, this@NewWishlistItemActivity)
        }
    }
}
