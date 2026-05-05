package com.example.mobileforquizapp.quiz

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileforquizapp.R

abstract class UserBaseActivity : AppCompatActivity() {

    protected var token: String? = null

    abstract fun currentNavItem(): Int

    companion object {
        const val NAV_HOME    = 0
        const val NAV_RESULTS = 1
        const val NAV_LEAGUES = 2
        const val NAV_PROFILE = 3
    }

    protected fun setupNav() {
        token = getSharedPreferences("MyApp", MODE_PRIVATE)
            .getString("jwt_token", null)
            ?: intent.getStringExtra("jwt_token")

        val navHome    = findViewById<LinearLayout>(R.id.navHome)    ?: return
        val navResults = findViewById<LinearLayout>(R.id.navResults) ?: return
        val navLeagues = findViewById<LinearLayout>(R.id.navLeagues) ?: return
        val navProfile = findViewById<LinearLayout>(R.id.navProfile) ?: return

        val allNavs = listOf(navHome, navResults, navLeagues, navProfile)

        setSelected(allNavs[currentNavItem()], allNavs)

        navHome.setOnClickListener {
            if (currentNavItem() != NAV_HOME) {
                startActivity(Intent(this, UserDashboardActivity::class.java).apply {
                    putExtra("jwt_token", token)
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                })
            }
        }

        navResults.setOnClickListener {
            if (currentNavItem() != NAV_RESULTS) {
                startActivity(Intent(this, UserResultsActivity::class.java).apply {
                    putExtra("jwt_token", token)
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                })
            }
        }

        navLeagues.setOnClickListener {
            if (currentNavItem() != NAV_LEAGUES) {
                startActivity(Intent(this, LeaderboardActivity::class.java).apply {
                    putExtra("jwt_token", token)
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                })
            }
        }

        navProfile.setOnClickListener {
            if (currentNavItem() != NAV_PROFILE) {
                startActivity(Intent(this, UserProfileActivity::class.java).apply {
                    putExtra("jwt_token", token)
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                })
            }
        }
    }

    private fun setSelected(selected: LinearLayout, allNavs: List<LinearLayout>) {
        val activeColor   = Color.parseColor("#630ed4")
        val inactiveColor = Color.parseColor("#94a3b8")

        allNavs.forEach { nav ->
            val icon  = nav.getChildAt(0) as? ImageView ?: return@forEach
            val label = nav.getChildAt(1) as? TextView  ?: return@forEach

            if (nav == selected) {
                nav.setBackgroundResource(R.drawable.nav_selected_bg)
                icon.imageTintList = ColorStateList.valueOf(activeColor)
                label.setTextColor(activeColor)
                label.typeface = Typeface.create("sans-serif", Typeface.BOLD)
            } else {
                nav.background = null
                icon.imageTintList = ColorStateList.valueOf(inactiveColor)
                label.setTextColor(inactiveColor)
                label.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
            }
        }
    }
}