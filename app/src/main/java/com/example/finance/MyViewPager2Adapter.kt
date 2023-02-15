package com.example.finance

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

//Адаптер, который будет переключать фрагменты
class MyViewPager2Adapter(fragmentActivity: FragmentActivity, private val fragmentList: List<Fragment>) : FragmentStateAdapter(fragmentActivity) {
    // Метод возвращает количество элементов
    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}