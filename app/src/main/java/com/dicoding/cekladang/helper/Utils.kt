package com.dicoding.cekladang.helper

import android.app.Activity
import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun showMaterialDialog(
    context: Context,
    title: String,
    message: String,
    positiveButtonText: String,
    onPositiveClick: (() -> Unit)? = null,
    negativeButtonText: String? = null,
    onNegativeClick: (() -> Unit)? = null,
) {
    if (context is Activity) {
        if (!context.isFinishing && !context.isDestroyed) {
            val builder = MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText) { dialog, _ ->
                    onPositiveClick?.invoke()
                    dialog.dismiss()
                }

            if (negativeButtonText != null) {
                builder.setNegativeButton(negativeButtonText) { dialog, _ ->
                    onNegativeClick?.invoke()
                    dialog.dismiss()
                }
            }

            builder.setCancelable(true)
            builder.show()
        }
    }
}

