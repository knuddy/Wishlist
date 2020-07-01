package com.example.knuddj1wishlist.activities
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.knuddj1wishlist.R
import com.example.knuddj1wishlist.`interface`.ICurrencyChange
import com.example.knuddj1wishlist.`interface`.IDatabaseCommand
import com.example.knuddj1wishlist.`interface`.IRecyclerViewItem
import com.example.knuddj1wishlist.`interface`.IWarningConfirm
import com.example.knuddj1wishlist.enum.DatabaseStatus
import com.example.knuddj1wishlist.enum.SortingField
import com.example.knuddj1wishlist.fragments.SettingsDialogFragment
import com.example.knuddj1wishlist.fragments.WarningDialogFragment
import com.example.knuddj1wishlist.helpers.*
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess


class MainActivity : BaseActivity(), IRecyclerViewItem, IWarningConfirm, IDatabaseCommand, ICurrencyChange{
    private lateinit var categoryRecyclerViewAdapter: CategoryRecyclerViewAdapter
    private lateinit var dbHelper: DBHelper
    private lateinit var currencyUtil: CurrencyUtil
    private var sortingField: SortingField = SortingField.NEWEST

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        displayToolbar(false)

        currencyUtil = CurrencyUtil(this)
        dbHelper = DBHelper(this@MainActivity)
        categoryRecyclerViewAdapter = CategoryRecyclerViewAdapter(ArrayList(), this@MainActivity, this)
        rcvWishlist.layoutManager = LinearLayoutManager(this)
        rcvWishlist.adapter = categoryRecyclerViewAdapter

        fabAddItem.setOnClickListener {
            val intent = Intent(this, NewWishlistItemActivity::class.java)
            intent.putExtra("enum", DatabaseStatus.INSERT)
            startActivityForResult(intent, 0)
        }

        radioGroup.check(R.id.rbNewest)
        radioGroup.setOnCheckedChangeListener(SortingRGOnCheckedChangeListener())

        readDatabase()
        setupNotification()
    }

    inner class SortingRGOnCheckedChangeListener : RadioGroup.OnCheckedChangeListener {
        override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
            val rbText = findViewById<RadioButton>(checkedId).text.toString()
            sortingField = when(rbText){
                getString(R.string.radio_btn_newest_text) -> SortingField.NEWEST
                getString(R.string.radio_btn_oldest_text) -> SortingField.OLDEST
                getString(R.string.radio_btn_alphabetical_text) -> SortingField.ALPHABETICAL
                else -> SortingField.NONE
            }
            categoryRecyclerViewAdapter.sortItems(sortingField)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(resultCode) {
            DatabaseStatus.INSERT.value -> {
                val newItem: WishlistItem = data?.getParcelableExtra("newItem") as WishlistItem
                DatabaseAsyncTask(this, dbHelper, DatabaseStatus.INSERT,this@MainActivity).execute(newItem)
            }
            DatabaseStatus.EDIT.value -> {
                val editedItem: WishlistItem = data?.getParcelableExtra("editedItem") as WishlistItem
                DatabaseAsyncTask(this, dbHelper, DatabaseStatus.EDIT,this@MainActivity).execute(editedItem)
            }
            DatabaseStatus.DELETE.value -> {
                val itemToDelete: WishlistItem = data?.getParcelableExtra("itemToDelete") as WishlistItem
                DatabaseAsyncTask(this, dbHelper, DatabaseStatus.DELETE,this@MainActivity).execute(itemToDelete)
            }
        }
    }

    override fun onCommandExecuted(noError: Boolean) {
        if(noError)
            readDatabase()
    }

    private fun readDatabase(){
        categoryRecyclerViewAdapter.loadNewData(dbHelper.selectAll())
        categoryRecyclerViewAdapter.sortItems(sortingField)
        txvWishlistTotal.text = CurrencyUtil(this).buildValueString(categoryRecyclerViewAdapter.getWishlistTotal())
    }

    override fun notifyCurrencyChange() {
        txvWishlistTotal.text = CurrencyUtil(this).buildValueString(categoryRecyclerViewAdapter.getWishlistTotal())
    }

    override fun onItemClick(view: View, position: Int, adapter: WishlistRecyclerViewAdapter) {
        val item: WishlistItem? = adapter.getItem(position)
        if (item != null){
            val intent = Intent(this, ItemDetailsActivity::class.java)
            intent.putExtra("item", item)

            val imvProduct: ImageView = view.findViewById(R.id.imvItem)
            intent.putExtra("imProductTransitionName", imvProduct.transitionName)
            val imProduct: Pair<View, String> = Pair.create(imvProduct, imvProduct.transitionName)

            val options: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, imProduct)
            startActivityForResult(intent, 0, options.toBundle())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.action_exit -> {
                WarningDialogFragment(this).show(supportFragmentManager, null)
                true
            }
            R.id.action_privacy_policy -> {
                val webView = WebView(this@MainActivity)
                webView.loadUrl(getString(R.string.privacy_policy_url))
                val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                builder.setView(webView)
                builder.setCancelable(true)
                builder.setNeutralButton("Dismiss", {dialog, which ->  dialog.dismiss() })
                builder.show()
                true
            }
            R.id.action_settings -> {
                SettingsDialogFragment(this).show(supportFragmentManager, null)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onWarningConfirm() {
        Process.killProcess(Process.myPid());
        exitProcess(1);
    }

    private fun setupNotification(){
        val prefKey = resources.getString(R.string.sharedPrefNotifKey)
        val sharedPref = getSharedPreferences(prefKey, Context.MODE_PRIVATE)

        if(sharedPref.getBoolean(prefKey, true)){
            showNotification()
            createNotificationChannel()
        }
    }

    private fun showNotification() {
        val resultIntent = Intent(this@MainActivity, MainActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentIntent(resultPendingIntent)
            .setContentTitle("Wish List")
            .setSmallIcon(R.drawable.ic_decoration)
            .setContentText("Currently, you have " + categoryRecyclerViewAdapter.getNumUnpurchasedItems().toString() + " unpurchased items.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)){
            notify(0, builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "knuddj1wishlist"
            val descriptionText = "Notifications"
            val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
            val channel: NotificationChannel =
                NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "com.example.knuddj1wishlist"
    }

    override fun onResume() {
        super.onResume()
        setupNotification()
    }
}
