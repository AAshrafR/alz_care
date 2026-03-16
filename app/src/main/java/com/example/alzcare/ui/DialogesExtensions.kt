package com.example.alzcare.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import androidx.fragment.app.Fragment

fun Fragment.showMessage(
    message: String,
    posActionName: String? = null,
    posAction: OnDialogActionClick? = null,
    negActionName: String? = null,
    negAction: OnDialogActionClick? = null
): AlertDialog {

    val dialogBuilder = AlertDialog.Builder(context)
    dialogBuilder.setMessage(message)
    return dialogBuilder.show()
}

fun Activity.showMessage(
    message: String,
    posActionName: String? = null,
    posAction: OnDialogActionClick? = null,
    negActionName: String? = null,
    negAction: OnDialogActionClick? = null,
    isCancelable: Boolean = true
): AlertDialog {

    val dialogBuilder = AlertDialog.Builder(this)
    dialogBuilder.setMessage(message)

    if (posActionName != null) {
        dialogBuilder.setPositiveButton(posActionName) { dialog, id ->
            dialog.dismiss()
            posAction?.onActionClick()
        }
    }
    if (negActionName != null) {
        dialogBuilder.setNegativeButton(negActionName) { dialog, id ->
            dialog.dismiss()
            negAction?.onActionClick()
        }
    }
    dialogBuilder.setCancelable(isCancelable)

    return dialogBuilder.show()
}

fun Activity.showLoadingProgressDialog(
    message: String,
    isCancelable: Boolean = true
): AlertDialog {
    val alertDialog = ProgressDialog(this)
    alertDialog.setMessage(message)
    alertDialog.setCancelable(isCancelable)
    return alertDialog
}