package com.ljq.basedialog.view

import android.view.View
import androidx.annotation.IdRes

interface IClick : View.OnClickListener {
    fun <V : View?> findView(@IdRes id: Int): V?
    fun setOnClickListener(@IdRes vararg ids: Int) {
        setOnClickListener(this, *ids)
    }

    fun setOnClickListener(listener: View.OnClickListener?, @IdRes vararg ids: Int) {
        for (id in ids) {
            findView<View>(id)?.setOnClickListener(listener)
        }
    }

    fun setOnClickListener(vararg views: View?) {
        setOnClickListener(this, *views)
    }

    fun setOnClickListener(listener: View.OnClickListener?, vararg views: View?) {
        for (view in views) {
            view?.setOnClickListener(listener)
        }
    }

    override fun onClick(view: View) {}
}