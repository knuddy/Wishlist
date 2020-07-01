package com.example.knuddj1wishlist.helpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DBHelper(context: Context)  :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "db_playlist"
        const val TABLE_NAME = "wishlist_items"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DATE_TIME = "date_time"
        const val COLUMN_IMAGE = "image"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_PRICE = "price"
        const val COLUMN_STORE = "store"
        const val COLUMN_NOTES = "notes"
        const val COLUMN_CC = "countryCode"
        const val COLUMN_PURCHASED = "purchased"
        const val DATABASE_CREATE: String = "CREATE TABLE $TABLE_NAME(" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_NAME TEXT," +
                "$COLUMN_DATE_TIME TEXT," +
                "$COLUMN_IMAGE TEXT," +
                "$COLUMN_CATEGORY TEXT," +
                "$COLUMN_CC TEXT," +
                "$COLUMN_PRICE DOUBLE, " +
                "$COLUMN_STORE TEXT," +
                "$COLUMN_NOTES TEXT," +
                "$COLUMN_PURCHASED BOOLEAN" +
                ")"
    }

    object DateTime {
        fun currentDateTime(): String {
            val date: Date = Calendar.getInstance().time
            val outputPattern = "dd/MM/yyyy kk:mm:ss"
            val outputFormat = SimpleDateFormat(outputPattern, Locale.ENGLISH)
            return outputFormat.format(date)
        }

        fun formatDateTime(dateTime: String) : String {
            val inputPattern = "dd/MM/yyyy kk:mm:ss"
            val outputPattern = "dd/MM/yyyy kk:mm:ss a"

            val inputFormat = SimpleDateFormat(inputPattern, Locale.ENGLISH)
            val outputFormat = SimpleDateFormat(outputPattern, Locale.ENGLISH)

            lateinit var date: Date
            lateinit var str: String

            try{
                date = inputFormat.parse(dateTime)!!
                str = outputFormat.format(date)
            } catch (e: ParseException){
                e.printStackTrace()
            }

            return str
        }
    }
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(DATABASE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insert(item: WishlistItem) : Long {
        val db: SQLiteDatabase = this.writableDatabase
        val values: ContentValues = getContentValues(item)
        val id: Long = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    fun selectAll(): ArrayList<WishlistCategory> {
        val categoryMap = hashMapOf<String, ArrayList<WishlistItem>>()
        val selectQuery = "SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_DATE_TIME ASC"
        val db : SQLiteDatabase = this.writableDatabase
        val cursor : Cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()){
            do {
                val item = WishlistItem()
                val category: String = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY))
                item.id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                item.name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                item.date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_TIME))
                item.image = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE))
                item.category = category
                item.price = cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE))
                item.countryCode = cursor.getString(cursor.getColumnIndex(COLUMN_CC))
                item.store = cursor.getString(cursor.getColumnIndex(COLUMN_STORE))
                item.notes = cursor.getString(cursor.getColumnIndex(COLUMN_NOTES))
                item.purchased = cursor.getInt(cursor.getColumnIndex(COLUMN_PURCHASED)) > 0

                if(!categoryMap.containsKey(category))
                    categoryMap[category] = ArrayList()
                categoryMap[category]?.add(item)

            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        val categories = ArrayList<WishlistCategory>()
        for ((categoryName, items) in categoryMap) {
            categories.add(WishlistCategory(categoryName, items))
        }
        return categories
    }

    fun update(id: Long, item: WishlistItem) : Int {
        val db : SQLiteDatabase = this.writableDatabase
        val values: ContentValues = getContentValues(item)
        return db.update(
            TABLE_NAME, values, "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }

    fun delete(id: Long){
        val db: SQLiteDatabase = this.writableDatabase
        db.delete(
            TABLE_NAME, "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
        db.close()
    }

    private fun getContentValues(item: WishlistItem) : ContentValues{
        val values = ContentValues()
        values.put(COLUMN_NAME, item.name)
        values.put(COLUMN_DATE_TIME, item.date)
        values.put(COLUMN_IMAGE, item.image)
        values.put(COLUMN_CATEGORY, item.category)
        values.put(COLUMN_PRICE, item.price)
        values.put(COLUMN_CC, item.countryCode)
        values.put(COLUMN_STORE, item.store)
        values.put(COLUMN_NOTES, item.notes)
        values.put(COLUMN_PURCHASED, item.purchased)
        return values
    }
}