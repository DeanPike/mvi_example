package com.domain.mvi.util

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.domain.mvi.R
import com.google.android.material.snackbar.Snackbar

object SnackbarUtils {
    fun showSnackBar(fragment: Fragment, view: View, message: Int, @IdRes anchorViewId: Int? = null) {
        fragment.context?.let {
            showSnackBar(it, view, it.resources.getString(message), anchorViewId)
        }
    }

    fun showSnackBar(context: Context, view: View, message: Int, @IdRes anchorViewId: Int? = null) {
        showSnackBar(context, view, context.resources.getString(message), anchorViewId)
    }

    fun showSnackBar(context: Context, view: View, message: String, @IdRes anchorViewId: Int? = null) {
        context.let {
            val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            anchorViewId?.let {
                snackbar.setAnchorView(anchorViewId)
            }
            val snackbarView = snackbar.view
            snackbarView.setBackgroundColor(ContextCompat.getColor(it, R.color.primary))
            snackbar.show()
        }
    }
}