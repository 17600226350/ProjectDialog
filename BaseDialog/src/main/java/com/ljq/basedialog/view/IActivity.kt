package com.ljq.basedialog.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent

interface IActivity {
    /**
     * 获取 Context 对象
     */
    fun getContext(): Context
    /**
     * 获取 Activity 对象
     */
    fun getActivity(): Activity? {
        var context: Context? = getContext()
        do {
            context = if (context is Activity) {
                return context
            } else if (context is ContextWrapper) {
                context.baseContext
            } else {
                return null
            }
        } while (context != null)
        return null
    }

    /**
     * 跳转 Activity 简化版
     */
    fun startActivity(clazz: Class<out Activity?>?) {
        startActivity(Intent(getContext(), clazz))
    }


    /**
     *
     * 跳转 Activity
     */
    fun startActivity(intent: Intent) {
        if (getContext() !is Activity) {
            // 如果当前的上下文不是 Activity，调用 startActivity 必须加入新任务栈的标记，否则会报错：android.util.AndroidRuntimeException
            // Calling startActivity() from outside of an Activity context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        getContext()?.startActivity(intent)
    }
}