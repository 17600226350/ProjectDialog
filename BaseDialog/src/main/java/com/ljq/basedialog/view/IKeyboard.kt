package com.ljq.basedialog.view

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

interface IKeyboard{

    fun showKeyboard(view: View?) {
        if (view == null) {
            return
        }
        val manager = view.context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.showSoftInput(view, 0)
    }

    fun hideKeyboard(view: View?) {
        if (view == null) {
            return
        }
        val manager = view.context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    fun toggleSoftInput(view: View?) {
        if (view == null) {
            return
        }
        val manager = view.context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.toggleSoftInput(0, 0)
    }
}