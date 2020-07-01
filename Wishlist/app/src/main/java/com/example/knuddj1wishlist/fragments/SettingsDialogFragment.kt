package com.example.knuddj1wishlist.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Process
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.knuddj1wishlist.R
import com.example.knuddj1wishlist.`interface`.ICurrencyChange
import com.example.knuddj1wishlist.`interface`.IWarningConfirm
import kotlinx.android.synthetic.main.dialog_fragement_exit.*
import kotlinx.android.synthetic.main.dialog_fragment_settings.*
import kotlin.system.exitProcess

class SettingsDialogFragment(private var listener: ICurrencyChange) : DialogFragment(){

    private lateinit var ccSharedPref: SharedPreferences
    private lateinit var notifSharedPref: SharedPreferences
    private lateinit var spinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinner = spinnerCurrencyCodes
        ccSharedPref = context!!.getSharedPreferences(resources.getString(R.string.sharedPrefCCKey),Context.MODE_PRIVATE)

        val countryCodes = resources.getStringArray(R.array.countryUnits)
        val adapter = ArrayAdapter<String>(context as Context, android.R.layout.simple_spinner_item, countryCodes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val countryUnit = ccSharedPref.getString(resources.getString(R.string.sharedPrefCCKey), resources.getString(R.string.sharedPrefCCDefault))
        if(countryUnit == null){
            ccSharedPref.edit().putString(resources.getString(R.string.sharedPrefCCKey), countryUnit).apply()
        }
        val pos = adapter.getPosition(countryUnit)
        spinner.setSelection(pos)

        spinner.onItemSelectedListener = SpinnerOnItemSelectedListener()

        val notifPrefKey = resources.getString(R.string.sharedPrefNotifKey)
        notifSharedPref = context!!.getSharedPreferences(notifPrefKey, Context.MODE_PRIVATE)
        switchNotifications.isChecked = notifSharedPref.getBoolean(notifPrefKey, true)
        switchNotifications.setOnCheckedChangeListener(SwitchOnCheckedChangedListener())
    }

    inner class SwitchOnCheckedChangedListener : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            notifSharedPref.edit().putBoolean(resources.getString(R.string.sharedPrefCCDefault), isChecked).apply()
        }
    }

    inner class SpinnerOnItemSelectedListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            ccSharedPref.edit().putString(resources.getString(R.string.sharedPrefCCKey), spinner.selectedItem.toString()).apply()
            listener.notifyCurrencyChange()
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}