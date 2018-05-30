package com.vogella.retrofitgithub.ui.fragments

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.vogella.retrofitgithub.R
import kotlinx.android.synthetic.main.dialog_login.*

class CredentialsDialog : DialogFragment() {

    var listener: ICredentialsDialogListener? = null

    interface ICredentialsDialogListener {
        fun onDialogPositiveClick(username: String, password: String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_login, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (activity is ICredentialsDialogListener) {
            listener = activity as ICredentialsDialogListener?
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = activity!!.layoutInflater.inflate(R.layout.dialog_login, null)

        var usernameEditText = view.findViewById(R.id.username_edittext) as EditText
        var passwordEditText = view.findViewById(R.id.password_edittext) as EditText

        val builder = AlertDialog.Builder(activity!!)
                .setView(view)
                .setTitle("Credentials")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Continue", DialogInterface.OnClickListener { _, _ ->
                    listener?.onDialogPositiveClick(usernameEditText.text.toString(),
                            passwordEditText.text.toString())
                })
        return builder.create()
    }
}