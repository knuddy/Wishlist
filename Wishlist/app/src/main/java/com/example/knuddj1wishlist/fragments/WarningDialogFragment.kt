package com.example.knuddj1wishlist.fragments

import android.os.Bundle
import android.os.Process
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.knuddj1wishlist.R
import com.example.knuddj1wishlist.`interface`.IWarningConfirm
import kotlinx.android.synthetic.main.dialog_fragement_exit.*
import kotlin.system.exitProcess

class WarningDialogFragment(private var listener: IWarningConfirm) : DialogFragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_fragement_exit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnCancel.setOnClickListener{dismiss()}
        btnConfirm.setOnClickListener {
            listener.onWarningConfirm()
        }
    }
}