package com.example.MRTAPP.UI.Setting.User

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class Setting_Personal_Tab_Adapter (fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val fragmentList = ArrayList<Fragment>()
    private val fragmentTitle = ArrayList<String>()
    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        // 传递 stationName 给 Fragment
        val fragment = fragmentList[position]

        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitle[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        fragmentTitle.add(title)
    }
}