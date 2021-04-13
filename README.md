### 老规矩，先上图，看看是不是你想要的

美团：
![美团](https://img-blog.csdnimg.cn/20210401165127867.gif#pic_left)

---

### 来一个图形分析

接下来我要写一个简单示例，先分析一下布局，见下图，最外层是NestedScrollView，之后嵌套一个LinearLayout头部，中间TabLayout选择器，底部一个ViewPager
ViewPager高度需要动态控制，看自己的需求了，如果是美团那种效果，就是
ViewPager高度 = NestedScrollView高度 - TabLayout高度
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210401193122862.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQwODgxNjgw,size_16,color_FFFFFF,t_70)


---

### 话不多说，代码实现

接下来我写一个例子，如果按照普通控件的嵌套方式来实现，那么肯定存在滑动冲突，会出现RecyclerView先进行滑动其次才是ScrollView滑动，那么就需要先重写NestedScrollView控件，用于控制最大的滑动距离，当达到最大滑动距离，再分发给RecyclerView滑动！

---

#### NestedScrollView重写

需要继承自NestedScrollView并重写onStartNestedScroll和onNestedPreScroll方法，如下

```kotlin
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
```

#### 布局文件

我按照美团的布局大体写出这样的布局

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".MainActivity">

    <!--titleBar-->
    <LinearLayout
        android:id="@+id/titleBar"
        android:layout_width="match_parent"
        android:layout_height="45dp"
        android:gravity="center_vertical"
        android:orientation="horizontal"
        android:paddingLeft="18dp"
        android:paddingRight="18dp">

        <EditText
            android:layout_width="0dp"
            android:layout_height="35dp"
            android:layout_marginEnd="12dp"
            android:layout_marginRight="12dp"
            android:layout_weight="1"
            android:background="@drawable/edit_style"
            android:paddingLeft="12dp"
            android:paddingRight="12dp" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="35dp"
            android:background="@drawable/button_style"
            android:gravity="center"
            android:paddingLeft="15dp"
            android:paddingRight="15dp"
            android:text="搜索"
            android:textColor="#333333"
            android:textStyle="bold" />

    </LinearLayout>

    <!--coordinatorScrollView-->
    <com.cyn.mt.CoordinatorScrollview
        android:id="@+id/coordinatorScrollView"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical">

            <!--相当于分析图中头部的LinearLayout，模拟动态添加的情况-->
            <LinearLayout
                android:id="@+id/titleLinerLayout"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical" />

            <!--相当于分析图中红色标记处TabLayout-->
            <com.google.android.material.tabs.TabLayout
                android:id="@+id/tabLayout"
                android:layout_width="match_parent"
                android:layout_height="wrap_content" />

            <!--相当于分析图中绿色标记处ViewPager，代码中动态设置高度-->
            <androidx.viewpager.widget.ViewPager
                android:id="@+id/viewPager"
                android:layout_width="match_parent"
                android:layout_height="wrap_content" />

        </LinearLayout>

    </com.cyn.mt.CoordinatorScrollview>

</LinearLayout>
```

---


#### Fragment
加入，在Fragment中放入RecyclerView，提供给ViewPager使用，这里代码就不贴了，可以直接下源码！源码在文章末尾！

---

#### 主要代码（重点来了）

coordinatorScrollView最大滑动距离即是titleLinerLayout的高度，所以实现titleLinerLayout的post方法，来监听titleLinerLayout的高度，由于这一块布局常常是通过网络请求后加载，所以，网络请求完毕后要再次实现post设置coordinatorScrollView最大滑动距离，如第80行代码和第90行代码，在这里，我并不推荐使用多次回调监听的方法！使用post只用调用一次，如果使用多次监听View变化的方法，应该在最后一次网络请求完毕后将此监听事件remove掉！

```kotlin
package com.cyn.mt

import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.LayoutInflater.from
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.title_layout.view.*


class MainActivity : AppCompatActivity() {

    //屏幕宽
    var screenWidth = 0

    //屏幕高
    var screenHeight = 0

    //tabLayout的文本和图片
    private val tabTextData = arrayOf("常用药品", "夜间送药", "隐形眼镜", "成人用品", "医疗器械", "全部商家")
    private val tabIconData = arrayOf(
        R.mipmap.tab_icon,
        R.mipmap.tab_icon,
        R.mipmap.tab_icon,
        R.mipmap.tab_icon,
        R.mipmap.tab_icon,
        R.mipmap.tab_icon
    )
    private var fragmentData = mutableListOf<Fragment>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        initData()
    }

    private fun initView() {

        //获取屏幕宽高
        val resources: Resources = this.resources
        val dm: DisplayMetrics = resources.displayMetrics
        screenWidth = dm.widthPixels
        screenHeight = dm.heightPixels

        //状态栏沉浸
        StatusBarUtil.immersive(this)

        //titleBar填充
        StatusBarUtil.setPaddingSmart(this, titleBar)

        //状态栏字体颜色设置为黑色
        StatusBarUtil.darkMode(this)

        //动态设置ViewPager高度
        coordinatorScrollView.post {
            val layoutParams = viewPager.layoutParams
            layoutParams.width = screenWidth
            layoutParams.height = coordinatorScrollView.height - tabLayout.height
            viewPager.layoutParams = layoutParams
        }

    }

    private fun initData() {

        //我模拟在头部动态添加三个布局，就用图片代替了，要设置的图片高度都是我提前算好的，根据屏幕的比例来计算的
        val titleView1 = getTitleView(screenWidth * 0.42F, R.mipmap.title1)
        val titleView2 = getTitleView(screenWidth * 0.262F, R.mipmap.title2)
        titleLinerLayout.addView(titleView1)
        titleLinerLayout.addView(titleView2)

        //设置最大滑动距离
        titleLinerLayout.post {
            coordinatorScrollView.setMaxScrollY(titleLinerLayout.height)
        }

        //用于请求网络后动态添加子布局
        Handler().postDelayed({
            val titleView3 = getTitleView(screenWidth * 0.589F, R.mipmap.title3)
            titleLinerLayout.addView(titleView3)

            //再次设置最大滑动距离
            titleLinerLayout.post {
                coordinatorScrollView.setMaxScrollY(titleLinerLayout.height)
            }

        }, 200)

        //添加TabLayout
        for (i in tabTextData.indices) {
            tabLayout.addTab(tabLayout.newTab())
            tabLayout.getTabAt(i)!!.setText(tabTextData[i]).setIcon(tabIconData[i])

            //添加Fragment
            fragmentData.add(TestFragment.newInstance(tabTextData[i]))
        }

        //Fragment ViewPager
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, fragmentData)

        //TabLayout关联ViewPager
        tabLayout.setupWithViewPager(viewPager)

        //设置TabLayout数据
        for (i in tabTextData.indices) {
            tabLayout.getTabAt(i)!!.setText(tabTextData[i]).setIcon(tabIconData[i])
        }
    }

    /**
     * 获取一个title布局
     * 我这里就用三张图片模拟的
     *
     * @height 要设置的图片高度
     */
    private fun getTitleView(height: Float, res: Int): View {
        val inflate = from(this).inflate(R.layout.title_layout, null, false)
        val layoutParams = inflate.titleImage.layoutParams
        layoutParams.width = screenWidth
        layoutParams.height = height.toInt()
        inflate.titleImage.setImageResource(res)
        return inflate
    }
}
```

---

### 最终效果
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402120239341.gif)
至此结束！
