package me.blog.korn123.easydiary.activities

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_settings2.*
import kotlinx.android.synthetic.main.layout_settings_progress.*
import me.blog.korn123.easydiary.R
import me.blog.korn123.easydiary.adapters.DotIndicatorPager2Adapter
import me.blog.korn123.easydiary.fragments.*

class SettingsActivity : EasyDiaryActivity() {

    /***************************************************************************************************
     *   global properties
     *
     ***************************************************************************************************/


    /***************************************************************************************************
     *   override functions
     *
     ***************************************************************************************************/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings2)
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setTitle(R.string.preferences_category_settings)
            setDisplayHomeAsUpEnabled(true)
        }

        val fragmentList = arrayListOf(SettingsBasic(), SettingsLock(), SettingsGMSBackup(), SettingsLocalBackup(), SettingsAppInfo())
        val adapter = DotIndicatorPager2Adapter(supportFragmentManager, fragmentList)
        view_pager2.adapter = adapter
        view_pager2.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                supportActionBar?.title = when (position) {
                    0 -> getString(R.string.preferences_category_settings)
                    1 -> getString(R.string.preferences_category_lock)
                    2 -> getString(R.string.preferences_category_backup_restore_device)
                    else -> getString(R.string.preferences_category_information)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        dots_indicator.setViewPager(view_pager2)

        progressContainer.setOnTouchListener { _, _ -> true }
    }

    fun updateUI() {
        super.onResume()
    }
}