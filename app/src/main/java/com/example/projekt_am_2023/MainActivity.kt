package com.example.projekt_am_2023

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.projekt_am_2023.tag.AddTag
import com.example.projekt_am_2023.tag.EditTag
import com.example.projekt_am_2023.tag.Tag
import com.example.projekt_am_2023.tag.TagListFragment
import com.example.projekt_am_2023.task.AddTask
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity(), TagListFragment.TagListListener {
    private lateinit var viewPager: ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById<ViewPager2>(R.id.viewPager2).apply {
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {})
        }

        val listFragment = ListView.newInstance()
        val monthFragment = CalendarMonthView.newInstance()

        viewPager.adapter = CustomStateAdapter(this).apply {
            addFragment(listFragment)
            addFragment(monthFragment)
            addFragment(TagListFragment.newInstance())
        }

        TabLayoutMediator(findViewById(R.id.tabLayout), viewPager) { tab, position ->
            tab.setIcon(when(position) {
                0 -> { R.drawable.list_icon }
                1 -> { R.drawable.month_icon }
                2 -> { R.drawable.section_icon }
                else -> { R.drawable.none_icon }
            })
            tab.text = getString(when(position) {
                0 -> { R.string.listLabel }
                1 -> { R.string.monthLabel }
                2 -> { R.string.sectionsLabel }
                else -> { R.string.noneLabel }
            })
        }.attach()
    }

    private class CustomStateAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        private val fragments = mutableListOf<Fragment>()

        fun addFragment(fragment: Fragment) = fragments.add(fragment)
        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position]
    }

    fun onNewClick(ignoredView: View) {
        when(viewPager.currentItem) {
            2 ->  { startActivity((Intent(this, AddTag::class.java)))}
            else -> { startActivity(Intent(this, AddTask::class.java)) }
        }
    }

    override fun onTagSelected(tag: Tag) {
        val i = Intent(this, EditTag::class.java)
        i.putExtra("tag", tag)
        startActivity(i)
    }
}

