package com.android.mediapipe.onboarding

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.android.mediapipe.FirstPageActivity
import com.android.mediapipe.R

class AppIntroActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var adapter: ItemsAdapter
    private lateinit var slides_pager : ViewPager2
    private lateinit var arrow_next : ImageView
    private lateinit var skip_intro : TextView
    private lateinit var get_started : Button
    private lateinit var indicators_layout : LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_intro)
        val myshredpref = getSharedPreferences("myprefrerance", 0)
        val editor = myshredpref.edit()
        editor.putInt("entry", 1)
        editor.apply()
        slides_pager = findViewById(R.id.slides_pager)
        arrow_next = findViewById(R.id.arrow_next)
        skip_intro = findViewById(R.id.skip_intro)
        get_started = findViewById(R.id.get_started)
        indicators_layout = findViewById(R.id.indicators_layout)
        adapter = ItemsAdapter(
            listOf(
                R.raw.machine_algorithm,
                R.raw.scrolling,
                R.raw.social,
                R.raw.social_media,

            )
        )
        slides_pager.adapter = adapter
        slides_pager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
        (slides_pager.getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val indicators = arrayOfNulls<ImageView>(adapter.itemCount)
        val layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(0, 0, 15, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(this)
            indicators[i]?.let {
                it.setImageDrawable(
                    setDrawable(R.drawable.appintro_inactive_slide_indicator)
                )
                it.layoutParams = layoutParams
                indicators_layout.addView(it)
            }
        }
        setCurrentIndicator(0)

        arrow_next.setOnClickListener(this)
        skip_intro.setOnClickListener(this)
        get_started.setOnClickListener(this)
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, FirstPageActivity::class.java))
        finish()
    }

    private fun setDrawable(image: Int): Drawable? {
        return ContextCompat.getDrawable(this, image)
    }

    private fun setCurrentIndicator(position: Int) {
        val count = indicators_layout.childCount
        for (i in 0 until count) {
            if (i == position)
                (indicators_layout.getChildAt(i) as ImageView).setImageDrawable(
                    setDrawable(R.drawable.appintro_active_slide_indicator)
                )
            else
                (indicators_layout.getChildAt(i) as ImageView).setImageDrawable(
                    setDrawable(R.drawable.appintro_inactive_slide_indicator)
                )
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            arrow_next -> {
                if (slides_pager.currentItem + 1 < adapter.itemCount)
                    slides_pager.currentItem += 1
                else
                    navigateToLogin()
            }
            skip_intro -> navigateToLogin()
            get_started -> navigateToLogin()

        }
    }
}