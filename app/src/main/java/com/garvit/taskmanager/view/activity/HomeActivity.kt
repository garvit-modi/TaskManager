package com.garvit.taskmanager.view.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.garvit.taskmanager.R
import com.garvit.taskmanager.databinding.ActivityHomeBinding
import com.garvit.taskmanager.view.fragment.*

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    var tag = "1"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.red)
        }
        setUpToolbar()
        val actionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        ) {
            private val scaleFactor = 25f

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

                drawerView.bringToFront();
                drawerView.requestLayout();

                super.onDrawerSlide(drawerView, slideOffset)
                val slideX = drawerView.width * slideOffset
                binding.coordinationLayout.translationX = slideX
                binding.coordinationLayout.scaleX = 1 - slideOffset / scaleFactor
                binding.coordinationLayout.scaleY = 1 - slideOffset / scaleFactor
            }

        }


        binding.textAdd.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        loadFragment(DashFragment(), "Inbox")
        binding.navigationView.setNavigationItemSelectedListener {
            var fragment: Fragment? = null
            var title = ""
            when (it.itemId) {
                R.id.itDash -> {
                    tag = "1"
                    fragment = DashFragment()
                    title = "Inbox"
                }
                R.id.itAll -> {
                    tag = "2"
                    fragment = CompletedFragment()
                    title = "Completed"
                }
                R.id.itChart -> {
                    tag = "3"
                    fragment = ChartFragment()
                    title = "Chart"
                }
                R.id.itAbout -> {
                    tag = "5"
                    fragment = AboutFragment()
                    title = "About"

                }
            }
            loadFragment(fragment, title)

            return@setNavigationItemSelectedListener true
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadFragment(fragment: Fragment?, title: String): Boolean {
        binding.textHeader.text = title
        if (tag == "1" || tag == "2") {
            binding.textAdd.visibility = View.VISIBLE
        } else {
            binding.textAdd.visibility = View.GONE
        }
        if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame, fragment, tag)
                .commit()
            binding.drawerLayout.closeDrawers()
            return true
        }
        return false
    }

    fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}