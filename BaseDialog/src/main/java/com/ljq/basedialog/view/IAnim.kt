package com.ljq.basedialog.view

import com.ljq.basedialog.R


interface IAnim {
    /** 缩放动画  */
    companion object {
        /** 默认动画效果  */
        const val ANIM_DEFAULT = -1

        /** 没有动画效果  */
        const val ANIM_EMPTY = 0

        /** 缩放动画  */
        val ANIM_SCALE = R.style.ScaleAnimStyle

        /** IOS 动画  */
        val ANIM_IOS = R.style.IOSAnimStyle

        /** 吐司动画  */
        const val ANIM_TOAST = android.R.style.Animation_Toast

        /** 顶部弹出动画  */
        val ANIM_TOP = R.style.TopAnimStyle

        /** 底部弹出动画  */
        val ANIM_BOTTOM = R.style.BottomAnimStyle

        /** 左边弹出动画  */
        val ANIM_LEFT = R.style.LeftAnimStyle

        /** 右边弹出动画  */
        val ANIM_RIGHT = R.style.RightAnimStyle
    }
}