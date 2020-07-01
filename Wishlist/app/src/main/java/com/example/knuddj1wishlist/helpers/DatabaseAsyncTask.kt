package com.example.knuddj1wishlist.helpers

import android.content.Context
import android.os.AsyncTask
import com.example.knuddj1wishlist.`interface`.IDatabaseCommand
import com.example.knuddj1wishlist.enum.DatabaseStatus

class DatabaseAsyncTask(
    private val listener: IDatabaseCommand,
    private val dbHelper: DBHelper,
    private val dbActionType: DatabaseStatus,
    context: Context
): AsyncTask<WishlistItem, Void, Boolean>() {

    private var progressBar = CustomProgressBar(context)

    override fun onPreExecute() {
        progressBar.show()
    }

    override fun onPostExecute(noError: Boolean) {
        progressBar.dismiss()
        listener.onCommandExecuted(noError)
    }

    override fun doInBackground(vararg item: WishlistItem): Boolean {
        var noError = true
        try {
            runDatabaseAction(item[0])
        } catch (e: Exception) {
            e.printStackTrace()
            noError = false
        }
        return noError
    }

    private fun runDatabaseAction(item: WishlistItem){
        when(dbActionType){
            DatabaseStatus.EDIT -> {
                dbHelper.update(item.id.toLong(), item)
            }
            DatabaseStatus.DELETE -> {
                dbHelper.delete(item.id.toLong())
            }
            DatabaseStatus.INSERT -> {
                dbHelper.insert(item)
            }
        }
    }
}