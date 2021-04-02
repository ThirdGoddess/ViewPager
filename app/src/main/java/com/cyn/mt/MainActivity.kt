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