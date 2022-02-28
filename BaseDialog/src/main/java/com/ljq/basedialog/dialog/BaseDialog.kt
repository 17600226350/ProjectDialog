package com.ljq.basedialog.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.SparseArray
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.*
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.ljq.basedialog.R
import com.ljq.basedialog.view.IClick
import com.ljq.basedialog.view.IHandler
import com.ljq.basedialog.view.IAnim
import com.ljq.basedialog.view.IActivity
import com.ljq.basedialog.view.IResources
import java.lang.ref.SoftReference
import java.util.*

class BaseDialog
@JvmOverloads
constructor(
    context: Context?,
    @StyleRes themeResId:
    Int = R.style.BaseDialogTheme
) : AppCompatDialog(context, themeResId),
    LifecycleOwner, IHandler, DialogInterface.OnShowListener,
    IClick,
    DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {

    private val mListeners: ListenersWrapper<BaseDialog> = ListenersWrapper(this)

    private val mLifecycle = LifecycleRegistry(this)
    private var mShowListeners: MutableList<OnShowListener>? = null
    private var mCancelListeners: MutableList<OnCancelListener>? = null
    private var mDismissListeners: MutableList<OnDismissListener>? = null

    /**
     * 获取 Dialog 的根布局
     */
    fun getContentView(): View? {
        val contentView = findView<View>(Window.ID_ANDROID_CONTENT)
        return if (contentView is ViewGroup &&
            contentView.childCount == 1
        ) {
            contentView.getChildAt(0)
        } else contentView
    }

    /**
     * 设置宽度
     */
    fun setWidth(width: Int) {
        val window = window
        if (window != null) {
            val params = window.attributes
            params.width = width
            window.attributes = params
        }
    }

    /**
     * 设置高度
     */
    fun setHeight(height: Int) {
        val window = window
        if (window != null) {
            val params = window.attributes
            params.height = height
            window.attributes = params
        }
    }

    /**
     * 设置水平偏移
     */
    fun setXOffset(offset: Int) {
        val window = window
        if (window != null) {
            val params = window.attributes
            params.x = offset
            window.attributes = params
        }
    }

    /**
     * 设置垂直偏移
     */
    fun setYOffset(offset: Int) {
        val window = window
        if (window != null) {
            val params = window.attributes
            params.y = offset
            window.attributes = params
        }
    }

    /**
     * 获取当前设置重心
     */
    fun getGravity(): Int {
        val window = window
        if (window != null) {
            val params = window.attributes
            return params.gravity
        }
        return Gravity.NO_GRAVITY
    }

    /**
     * 设置 Dialog 重心
     */
    fun setGravity(gravity: Int) {
        val window = window
        window?.setGravity(gravity)
    }

    /**
     * 设置 Dialog 的动画
     */
    fun setWindowAnimations(@StyleRes id: Int) {
        val window = window
        window?.setWindowAnimations(id)
    }


    /**
     * 获取 Dialog 的动画
     */
    fun getWindowAnimations(): Int {
        val window = window
        return window?.attributes?.windowAnimations ?: IAnim.ANIM_DEFAULT
    }

    /**
     * 设置自定义styles
     * */


    /**
     * 设置背景遮盖层开关
     */
    fun setBackgroundDimEnabled(enabled: Boolean) {
        val window = window
        if (window != null) {
            if (enabled) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            }
        }
    }

    /**
     * 设置背景遮盖层的透明度（前提条件是背景遮盖层开关必须是为开启状态）
     */
    fun setBackgroundDimAmount(@FloatRange(from = 0.0, to = 1.0) dimAmount: Float) {
        val window = window
        window?.setDimAmount(dimAmount)
    }

    override fun dismiss() {
        removeCallbacks()
        val focusView = currentFocus
        if (focusView != null) {
            ContextCompat.getSystemService(context, InputMethodManager::class.java)
                ?.hideSoftInputFromWindow(
                    focusView.windowToken,
                    0
                )
        }
        super.dismiss()
    }

    /**
     * 添加一个显示监听器
     *
     * @param listener      监听器对象
     */
    fun addOnShowListener(listener: OnShowListener) {
        if (mShowListeners == null) {
            mShowListeners = ArrayList()
            super.setOnShowListener(mListeners)
        }
        mShowListeners!!.add(listener)
    }

    /**
     * 添加一个取消监听器
     *
     * @param listener      监听器对象
     */
    fun addOnCancelListener(listener: OnCancelListener) {
        if (mCancelListeners == null) {
            mCancelListeners = ArrayList()
            super.setOnCancelListener(mListeners)
        }
        mCancelListeners!!.add(listener)
    }

    /**
     * 添加一个销毁监听器
     *
     * @param listener      监听器对象
     */
    fun addOnDismissListener(listener: OnDismissListener) {
        if (mDismissListeners == null) {
            mDismissListeners = ArrayList()
            super.setOnDismissListener(mListeners)
        }
        mDismissListeners!!.add(listener)
    }

    /**
     * 移除一个显示监听器
     *
     * @param listener      监听器对象
     */
    fun removeOnShowListener(listener: OnShowListener?) {
        if (mShowListeners != null) {
            mShowListeners!!.remove(listener)
        }
    }

    /**
     * 移除一个取消监听器
     *
     * @param listener      监听器对象
     */
    fun removeOnCancelListener(listener: OnCancelListener?) {
        if (mCancelListeners != null) {
            mCancelListeners!!.remove(listener)
        }
    }

    /**
     * 移除一个销毁监听器
     *
     * @param listener      监听器对象
     */
    fun removeOnDismissListener(listener: OnDismissListener?) {
        if (mDismissListeners != null) {
            mDismissListeners!!.remove(listener)
        }
    }

    /**
     * 设置显示监听器集合
     */
    private fun setOnShowListeners(listeners: MutableList<OnShowListener>?) {
        super.setOnShowListener(mListeners)
        mShowListeners = listeners
    }

    /**
     * 设置取消监听器集合
     */
    private fun setOnCancelListeners(listeners: MutableList<OnCancelListener>?) {
        super.setOnCancelListener(mListeners)
        mCancelListeners = listeners
    }

    /**
     * 设置销毁监听器集合
     */
    private fun setOnDismissListeners(listeners: MutableList<OnDismissListener>?) {
        super.setOnDismissListener(mListeners)
        mDismissListeners = listeners
    }

    override fun getLifecycle(): Lifecycle {
        return mLifecycle
    }

    fun setOnKeyListener(listener: OnKeyListener?) {
        val keyListenerWrapper = KeyListenerWrapper(listener)
        super.setOnKeyListener(keyListenerWrapper)
    }

    override fun onShow(dialog: DialogInterface?) {
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        if (mShowListeners == null) {
            return
        }
        for (i in mShowListeners!!.indices) {
            mShowListeners!![i].onShow(this)
        }
    }

    override fun onCancel(dialog: DialogInterface?) {
        if (mCancelListeners == null) {
            return
        }
        for (i in mCancelListeners!!.indices) {
            mCancelListeners!![i].onCancel(this)
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        if (mDismissListeners == null) {
            return
        }
        for (i in mDismissListeners!!.indices) {
            mDismissListeners!![i].onDismiss(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onStart() {
        super.onStart()
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    override fun onStop() {
        super.onStop()
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    open class Builder(context: Context?) : IActivity, IResources,
        IClick {
        /** Context 对象  */
        private val mContext: Context? = context

        /** Activity 对象  */
        private var mActivity: Activity? = getActivity()
        /**
         * 获取当前 Dialog 对象
         */
        /** Dialog 对象  */
        var dialog: BaseDialog? = null
        /**
         * 获取 Dialog 的根布局
         */
        /** Dialog 布局  */
        var contentView: View? = null
            private set

        /** 主题样式  */
        private var mThemeId = R.style.BaseDialogTheme

        /** 动画样式  */
        private var mAnimStyle: Int = IAnim.ANIM_DEFAULT

        /** 重心位置  */
        private var mGravity = Gravity.NO_GRAVITY

        /** 水平偏移  */
        private var mXOffset = 0

        /** 垂直偏移  */
        private var mYOffset = 0

        /** 宽度和高度  */
        private var mWidth = WindowManager.LayoutParams.WRAP_CONTENT
        private var mHeight = WindowManager.LayoutParams.WRAP_CONTENT

        /** 背景遮盖层开关  */
        private var mBackgroundDimEnabled = true

        /** 背景遮盖层透明度  */
        private var mBackgroundDimAmount = 0.5f

        /** 是否能够被取消  */
        private var mCancelable = true

        /** 点击空白是否能够取消  前提是这个对话框可以被取消  */
        private var mCanceledOnTouchOutside = true

        /** Dialog 创建监听  */
        private var mCreateListener: OnCreateListener? = null

        /** Dialog 显示监听  */
        private val mShowListeners: MutableList<OnShowListener> = ArrayList<OnShowListener>()

        /** Dialog 取消监听  */
        private val mCancelListeners: MutableList<OnCancelListener> = ArrayList<OnCancelListener>()

        /** Dialog 销毁监听  */
        private val mDismissListeners: MutableList<OnDismissListener> =
            ArrayList<OnDismissListener>()

        /** Dialog 按键监听  */
        private var mKeyListener: OnKeyListener? = null

        /** 点击事件集合  */
        private var mClickArray: SparseArray<OnClickListener<View>>? = null

        constructor(activity: Activity?) : this(activity as Context?)

        init {
            mActivity = getActivity()
        }

        /**
         * 设置主题 id
         */
        fun setThemeStyle(@StyleRes id: Int) = apply {
            mThemeId = id
            check(!isCreated()) {
                "Dialog 创建之后不能再设置主题 id"
            }
        }

        /**
         * 设置布局
         */
        fun setContentView(@LayoutRes id: Int) = apply {
            // 因为如果不传的话，XML 的根布局获取到的 LayoutParams 对象会为空，也就会导致宽高参数解析不出来
            return setContentView(
                LayoutInflater.from(mContext).inflate(
                    id,
                    FrameLayout(mContext!!),
                    false
                )
            )
        }

        fun setContentView(view: View?) = apply {
            // 请不要传入空的布局
            requireNotNull(view) { "view  = null?" }
            contentView = view
            if (isCreated()) {
                dialog!!.setContentView(view)
                return@apply
            }
            val layoutParams = contentView!!.layoutParams
            if (layoutParams != null && mWidth == ViewGroup.LayoutParams.WRAP_CONTENT && mHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
                // 如果当前 Dialog 的宽高设置了自适应，就以布局中设置的宽高为主
                setWidth(layoutParams.width)
                setHeight(layoutParams.height)
            }

            // 如果当前没有设置重心，就自动获取布局重心
            if (mGravity == Gravity.NO_GRAVITY) {
                if (layoutParams is FrameLayout.LayoutParams) {
                    setGravity(layoutParams.gravity)
                } else if (layoutParams is LinearLayout.LayoutParams) {
                    setGravity(layoutParams.gravity)
                } else {
                    // 默认重心是居中
                    setGravity(Gravity.CENTER)
                }
            }
        }

        /**
         * 设置重心位置
         */
        fun setGravity(gravity: Int) = apply {
            // 适配布局反方向
            mGravity = Gravity.getAbsoluteGravity(
                gravity,
                Resources.getSystem().configuration.layoutDirection
            )
            if (isCreated()) {
                dialog!!.setGravity(gravity)
            }
        }

        /**
         * 设置水平偏移
         */
        fun setXOffset(offset: Int) = apply {
            mXOffset = offset
            if (isCreated()) {
                dialog!!.setXOffset(offset)
            }
        }

        /**
         * 设置垂直偏移
         */
        fun setYOffset(offset: Int) = apply {
            mYOffset = offset
            if (isCreated()) {
                dialog!!.setYOffset(offset)
            }
        }

        /**
         * 设置宽度
         */
        fun setWidth(width: Int) = apply {
            mWidth = width
            if (isCreated()) {
                dialog!!.setWidth(width)
            }

            // 这里解释一下为什么要重新设置 LayoutParams
            // 因为如果不这样设置的话，第一次显示的时候会按照 Dialog 宽高显示
            // 但是 Layout 内容变更之后就不会按照之前的设置宽高来显示
            // 所以这里我们需要对 View 的 LayoutParams 也进行设置
            val params = if (contentView != null) contentView!!.layoutParams else null
            if (params != null) {
                params.width = width
                contentView!!.layoutParams = params
            }
        }

        /**
         * 设置高度
         */
        fun setHeight(height: Int) = apply {
            mHeight = height
            if (isCreated()) {
                dialog!!.setHeight(height)
            }

            // 这里解释一下为什么要重新设置 LayoutParams
            // 因为如果不这样设置的话，第一次显示的时候会按照 Dialog 宽高显示
            // 但是 Layout 内容变更之后就不会按照之前的设置宽高来显示
            // 所以这里我们需要对 View 的 LayoutParams 也进行设置
            val params = if (contentView != null) contentView!!.layoutParams else null
            if (params != null) {
                params.height = height
                contentView!!.layoutParams = params
            }
        }

        /**
         * 是否可以取消
         */
        fun setCancelable(cancelable: Boolean) = apply {
            mCancelable = cancelable
            if (isCreated()) {
                dialog!!.setCancelable(cancelable)
            }
        }

        /**
         * 是否可以通过点击空白区域取消
         */
        fun setCanceledOnTouchOutside(cancel: Boolean) = apply {
            mCanceledOnTouchOutside = cancel
            if (isCreated() && mCancelable) {
                dialog!!.setCanceledOnTouchOutside(cancel)
            }
        }

        /**
         * 设置动画，已经封装好几种样式，具体可见[IAnim]类
         */
        fun setAnimStyle(@StyleRes id: Int) = apply {
            mAnimStyle = id
            if (isCreated()) {
                dialog!!.setWindowAnimations(id)
            }
        }

        /**
         * 设置背景遮盖层开关
         */
        fun setBackgroundDimEnabled(enabled: Boolean) = apply {
            mBackgroundDimEnabled = enabled
            if (isCreated()) {
                dialog!!.setBackgroundDimEnabled(enabled)
            }
        }

        /**
         * 设置背景遮盖层的透明度（前提条件是背景遮盖层开关必须是为开启状态）
         */
        fun setBackgroundDimAmount(@FloatRange(from = 0.0, to = 1.0) dimAmount: Float) = apply {
            mBackgroundDimAmount = dimAmount
            if (isCreated()) {
                dialog!!.setBackgroundDimAmount(dimAmount)
            }
        }

        /**
         * 设置创建监听
         */
        fun setOnCreateListener(listener: OnCreateListener) = apply {
            mCreateListener = listener
        }

        /**
         * 添加显示监听
         */
        fun addOnShowListener(listener: OnShowListener) = apply {
            mShowListeners.add(listener)
        }

        /**
         * 添加取消监听
         */
        fun addOnCancelListener(listener: OnCancelListener) = apply {
            mCancelListeners.add(listener)
        }

        /**
         * 添加销毁监听
         */
        fun addOnDismissListener(listener: OnDismissListener) = apply {
            mDismissListeners.add(listener)
        }

        /**
         * 设置按键监听
         */
        fun setOnKeyListener(listener: OnKeyListener) = apply {
            mKeyListener = listener
            if (isCreated()) {
                dialog!!.setOnKeyListener(listener)
            }
        }

        /**
         * 设置文本
         */
        fun setText(@IdRes viewId: Int, @StringRes stringId: Int) = apply {
            setText(viewId, mContext?.getString(stringId))
        }

        fun setText(@IdRes id: Int, text: CharSequence?) = apply {
            (findView<View>(id) as TextView).text = text
        }

        /**
         * 设置文本颜色
         */
        fun setTextColor(@IdRes id: Int, @ColorInt color: Int) = apply {
            (findView<View>(id) as TextView).setTextColor(color)
        }

        /**
         * 设置提示
         */
        fun setHint(@IdRes viewId: Int, @StringRes stringId: Int) = apply {
            setHint(viewId, mContext?.getString(stringId))
        }

        fun setHint(@IdRes id: Int, text: CharSequence?) = apply {
            (findView<View>(id) as TextView).hint = text
        }

        /**
         * 设置可见状态
         */
        fun setVisibility(@IdRes id: Int, visibility: Int) = apply {
            findView<View>(id).visibility = visibility
        }

        /**
         * 设置背景
         */
        fun setBackground(@IdRes viewId: Int, @DrawableRes drawableId: Int) = apply {
            return setBackground(viewId, ContextCompat.getDrawable(mContext!!, drawableId))
        }

        fun setBackground(@IdRes id: Int, drawable: Drawable?) = apply {
            findView<View>(id).background = drawable
        }

        /**
         * 设置图片
         */
        fun setImageDrawable(@IdRes viewId: Int, @DrawableRes drawableId: Int) = apply {
            setBackground(viewId, ContextCompat.getDrawable(mContext!!, drawableId))
        }

        fun setImageDrawable(@IdRes id: Int, drawable: Drawable?) = apply {
            (findView<View>(id) as ImageView).setImageDrawable(drawable)
        }

        /**
         * 设置点击事件
         */
        fun setOnClickListener(@IdRes id: Int, listener: OnClickListener<View>) = apply {
            if (mClickArray == null) {
                mClickArray = SparseArray()
            }
            mClickArray!!.put(id, listener)
            if (isCreated()) {
                val view = dialog!!.findView<View>(id)
                view?.setOnClickListener(ViewClickWrapper(dialog!!, listener))
            }
        }

        /**
         * 创建
         */
        @SuppressLint("RtlHardcoded")
        fun create(): BaseDialog {
            // 判断布局是否为空
            requireNotNull(contentView) { "contentView为空" }

            // 如果当前正在显示
            if (isShowing()) {
                dismiss()
            }

            // 如果当前没有设置重心，就设置一个默认的重心
            if (mGravity == Gravity.NO_GRAVITY) {
                mGravity = Gravity.CENTER
            }

            // 如果当前没有设置动画效果，就设置一个默认的动画效果
            if (mAnimStyle == IAnim.ANIM_DEFAULT) {
                when (mGravity) {
                    Gravity.TOP -> mAnimStyle = IAnim.ANIM_TOP
                    Gravity.BOTTOM -> mAnimStyle = IAnim.ANIM_BOTTOM
                    Gravity.LEFT -> mAnimStyle = IAnim.ANIM_LEFT
                    Gravity.RIGHT -> mAnimStyle = IAnim.ANIM_RIGHT
                    else -> mAnimStyle = IAnim.ANIM_DEFAULT
                }
            }

            // 创建新的 Dialog 对象
            dialog = createDialog(mContext, mThemeId)
            dialog.apply {
                contentView?.let { setContentView(it) }
                setCancelable(mCancelable)
                if (mCancelable) {
                    setCanceledOnTouchOutside(mCanceledOnTouchOutside)
                }
                this?.setOnShowListeners(mShowListeners)
                this?.setOnCancelListeners(mCancelListeners)
                this?.setOnDismissListeners(mDismissListeners)
                mKeyListener?.let { dialog?.setOnKeyListener(it) }
            }

            val window = dialog!!.window
            window?.let {
                it.attributes = it.attributes.apply {
                    width = mWidth
                    height = mHeight
                    gravity = mGravity
                    x = mXOffset
                    y = mYOffset
                    windowAnimations = mAnimStyle
                }
                if (mBackgroundDimEnabled) {
                    it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    it.setDimAmount(mBackgroundDimAmount)
                } else {
                    it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                }
            }
            var i = 0
            while (mClickArray != null && i < mClickArray!!.size()) {
                val view = contentView!!.findViewById<View>(mClickArray!!.keyAt(i))
                view?.setOnClickListener(ViewClickWrapper(dialog!!, mClickArray!!.valueAt(i)))
                i++
            }

            // 将 Dialog 的生命周期和 Activity 绑定在一起
            mActivity?.let {
                DialogLifecycle(mActivity!!, dialog!!)
            }
            mCreateListener?.onCreate(dialog)
            return dialog!!
        }

        /**
         * 显示
         */
        fun show() {
            if (mActivity == null) {
                return
            }
            if (mActivity!!.isFinishing || mActivity!!.isDestroyed) {
                return
            }
            if (!isCreated()) {
                create()
            }
            if (isShowing()) {
                return
            }
            dialog!!.show()
        }

        /**
         * 销毁当前 Dialog
         */
        fun dismiss() {
            if (mActivity == null) {
                return
            }
            if (mActivity!!.isFinishing || mActivity!!.isDestroyed) {
                return
            }
            if (dialog == null) {
                return
            }
            dialog!!.dismiss()
        }

        /**
         * 延迟执行
         */
        fun post(r: Runnable) {
            if (isShowing()) {
                dialog?.post(r)
            } else {
                addOnShowListener(ShowPostWrapper(r))
            }
        }

        /**
         * 延迟一段时间执行
         */
        fun postDelayed(r: Runnable, delayMillis: Long) {
            if (isShowing()) {
                dialog?.postDelayed(r, delayMillis)
            } else {
                addOnShowListener(ShowPostDelayedWrapper(r, delayMillis))
            }
        }

        /**
         * 在指定的时间执行
         */
        fun postAtTime(r: Runnable, uptimeMillis: Long) {
            if (isShowing()) {
                dialog?.postAtTime(r, uptimeMillis)
            } else {
                addOnShowListener(ShowPostAtTimeWrapper(r, uptimeMillis))
            }
        }

        /**
         * 当前 Dialog 是否创建了
         */
        open fun isCreated(): Boolean {
            return dialog != null
        }

        /**
         * 当前 Dialog 是否显示了
         */
        open fun isShowing(): Boolean {
            return dialog != null && dialog!!.isShowing()
        }

        /**
         * 创建 Dialog 对象（子类可以重写此方法来改变 Dialog 类型）
         */
        fun createDialog(context: Context?, @StyleRes themeId: Int): BaseDialog {
            return BaseDialog(context, themeId)
        }

        /**
         * 根据 id 查找 View
         */
        override fun <V : View?> findView(@IdRes id: Int): V {
            checkNotNull(contentView) {
                "没有setContentView"
            }
            return contentView!!.findViewById(id)
        }

        override fun getContext(): Context {
            return mContext!!
        }
    }


    /**
     * Dialog 生命周期管理
     */
    private class DialogLifecycle constructor(activity: Activity, dialog: BaseDialog) :
        ActivityLifecycleCallbacks, OnShowListener, OnDismissListener {
        private var mDialog: BaseDialog? = null
        private var mActivity: Activity?

        /** Dialog 动画样式（避免 Dialog 从后台返回到前台后再次触发动画效果）  */
        private var mDialogAnim = 0
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityResumed(activity: Activity) {
            if (mActivity !== activity) {
                return
            }
            if (mDialog != null && mDialog!!.isShowing) {
                // 还原 Dialog 动画样式（这里必须要使用延迟设置，否则还是有一定几率会出现）
                mDialog!!.postDelayed({
                    if (mDialog != null && mDialog!!.isShowing) {
                        mDialog!!.setWindowAnimations(mDialogAnim)
                    }
                }, 100)
            }
        }

        override fun onActivityPaused(activity: Activity) {
            if (mActivity !== activity) {
                return
            }
            if (mDialog != null && mDialog!!.isShowing) {
                // 获取 Dialog 动画样式
                mDialogAnim = mDialog!!.getWindowAnimations()
                // 设置 Dialog 无动画效果
                mDialog!!.setWindowAnimations(IAnim.ANIM_EMPTY)
            }
        }

        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {
            if (mActivity !== activity) {
                return
            }
            if (mDialog != null) {
                mDialog!!.removeOnShowListener(this)
                mDialog!!.removeOnDismissListener(this)
                if (mDialog!!.isShowing) {
                    mDialog!!.dismiss()
                }
                mDialog = null
            }
            unregisterActivityLifecycleCallbacks()
            // 释放 Activity 对象
            mActivity = null
        }

        override fun onShow(dialog: BaseDialog?) {
            mDialog = dialog
            registerActivityLifecycleCallbacks()
        }

        override fun onDismiss(dialog: BaseDialog?) {
            mDialog = null
            unregisterActivityLifecycleCallbacks()
        }

        /**
         * 注册 Activity 生命周期监听
         */
        private fun registerActivityLifecycleCallbacks() {
            if (mActivity == null) {
                return
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mActivity!!.registerActivityLifecycleCallbacks(this)
            } else {
                mActivity!!.application.registerActivityLifecycleCallbacks(this)
            }
        }

        /**
         * 反注册 Activity 生命周期监听
         */
        private fun unregisterActivityLifecycleCallbacks() {
            if (mActivity == null) {
                return
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mActivity!!.unregisterActivityLifecycleCallbacks(this)
            } else {
                mActivity!!.application.unregisterActivityLifecycleCallbacks(this)
            }
        }

        init {
            mActivity = activity
            dialog.addOnShowListener(this)
            dialog.addOnDismissListener(this)
        }
    }

    inner class ListenersWrapper<T>(referent: T) :
        SoftReference<T>(referent), DialogInterface.OnShowListener,
        DialogInterface.OnCancelListener,
        DialogInterface.OnDismissListener where T : DialogInterface.OnShowListener?,
                                                T : DialogInterface.OnCancelListener?,
                                                T : DialogInterface.OnDismissListener? {
        override fun onShow(dialog: DialogInterface) {
            get()?.onShow(dialog)
        }

        override fun onCancel(dialog: DialogInterface) {
            get()?.onCancel(dialog)
        }

        override fun onDismiss(dialog: DialogInterface) {
            get()?.onDismiss(dialog)
        }
    }

    /**
     * 点击事件包装类
     */
    private class ViewClickWrapper constructor(
        private val mDialog: BaseDialog,
        listener: OnClickListener<View>
    ) :
        View.OnClickListener {
        private val mListener: OnClickListener<View> = listener
        override fun onClick(view: View) {
            mListener.onClick(mDialog, view)
        }
    }

    /**
     * 按键监听包装类
     */
    private class KeyListenerWrapper(listener: OnKeyListener?) :
        DialogInterface.OnKeyListener {
        private val mListener: OnKeyListener? = listener
        override fun onKey(dialog: DialogInterface, keyCode: Int, event: KeyEvent): Boolean {
            // 在横竖屏切换后监听对象会为空
            return if (mListener != null && dialog is BaseDialog) {
                mListener.onKey(dialog, event)
            } else false
        }

    }

    /**
     * 点击监听器
     */
    interface OnClickListener<V : View?> {
        fun onClick(dialog: BaseDialog?, view: V)
    }

    /**
     * 创建监听器
     */
    interface OnCreateListener {
        /**
         * Dialog 创建了
         */
        fun onCreate(dialog: BaseDialog?)
    }

    /**
     * 显示监听器
     */
    interface OnShowListener {
        /**
         * Dialog 显示了
         */
        fun onShow(dialog: BaseDialog?)
    }

    /**
     * 取消监听器
     */
    interface OnCancelListener {
        /**
         * Dialog 取消了
         */
        fun onCancel(dialog: BaseDialog?)
    }

    /**
     * 销毁监听器
     */
    interface OnDismissListener {
        /**
         * Dialog 销毁了
         */
        fun onDismiss(dialog: BaseDialog?)
    }

    /**
     * 按键监听器
     */
    interface OnKeyListener {
        /**
         * 触发了按键
         */
        fun onKey(dialog: BaseDialog?, event: KeyEvent?): Boolean
    }

    /**
     * post 任务包装类
     */
    private class ShowPostWrapper(r: Runnable) :
        OnShowListener {
        private val mRunnable: Runnable?
        override fun onShow(dialog: BaseDialog?) {
            if (mRunnable != null) {
                dialog?.removeOnShowListener(this)
                dialog?.post(mRunnable)
            }
        }

        init {
            mRunnable = r
        }
    }

    /**
     * postDelayed 任务包装类
     */
    private class ShowPostDelayedWrapper(r: Runnable, delayMillis: Long) :
        OnShowListener {
        private val mRunnable: Runnable?
        private val mDelayMillis: Long
        override fun onShow(dialog: BaseDialog?) {
            if (mRunnable != null) {
                dialog?.removeOnShowListener(this)
                dialog?.postDelayed(mRunnable, mDelayMillis)
            }
        }

        init {
            mRunnable = r
            mDelayMillis = delayMillis
        }
    }

    /**
     * postAtTime 任务包装类
     */
    private class ShowPostAtTimeWrapper(r: Runnable, uptimeMillis: Long) :
        OnShowListener {
        private val mRunnable: Runnable?
        private val mUptimeMillis: Long
        override fun onShow(dialog: BaseDialog?) {
            if (mRunnable != null) {
                dialog?.removeOnShowListener(this)
                dialog?.postAtTime(mRunnable, mUptimeMillis)
            }
        }

        init {
            mRunnable = r
            mUptimeMillis = uptimeMillis
        }
    }

    override fun <T : View?> findView(id: Int): T? {
        return delegate.findViewById(id);
    }

}