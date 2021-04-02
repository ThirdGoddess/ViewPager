package com.cyn.mt

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.NestedScrollingParent2
import androidx.core.widget.NestedScrollView

/**
 * @author cyn
 */
class CoordinatorScrollview : NestedScrollView, NestedScrollingParent2 {
    private var maxScrollY = 0

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!,
        attrs
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context!!, attrs, defStyleAttr)

    override fun onStartNestedScroll(
        child: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return true
    }

    /**
     * 设置最大滑动距离
     *
     * @param maxScrollY 最大滑动距离
     */
    fun setMaxScrollY(maxScrollY: Int) {
        this.maxScrollY = maxScrollY
    }

    /**
     * @param target   触发嵌套滑动的View
     * @param dx       表示 View 本次 x 方向的滚动的总距离
     * @param dy       表示 View 本次 y 方向的滚动的总距离
     * @param consumed 表示父布局消费的水平和垂直距离
     * @param type     触发滑动事件的类型
     */
    override fun onNestedPreScroll(
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        if (dy > 0 && scrollY < maxScrollY) {
            scrollBy(0, dy)
            consumed[1] = dy
        }
    }
}