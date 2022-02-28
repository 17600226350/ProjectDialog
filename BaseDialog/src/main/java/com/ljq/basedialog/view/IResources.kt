package com.ljq.basedialog.view

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

interface IResources {

    fun getContext():Context

    fun getResources(): Resources? = getContext().resources

    fun getString(@StringRes id: Int): String? = getContext().getString(id)

    fun getString(@StringRes id: Int, vararg formatArgs: Any?): String? = getResources()?.getString(id, *formatArgs)

    fun getDrawable(@DrawableRes id: Int): Drawable? = ContextCompat.getDrawable(getContext(), id)

    @ColorInt
    fun getColor(@ColorRes id: Int): Int = ContextCompat.getColor(getContext(), id)

    fun <S> getSystemService(serviceClass: Class<S>): S? = ContextCompat.getSystemService(getContext(), serviceClass)
}