package com.elanciers.bringszo

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_referral_.*

class Referral_Activity : AppCompatActivity() {
    var packagenm = "com.elanciers.bringszo"
    lateinit var ab : ActionBar

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_referral_)
        changeStatusBarColor()
       /* val mToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(mToolbar);
        ab = supportActionBar!!
        ab!!.setDisplayShowHomeEnabled(true)
        ab!!.setDisplayHomeAsUpEnabled(true)
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_24px)*/
        button4.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=$packagenm");
            intent.setType("text/plain");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Share"));
        }

        button5.setOnClickListener {
            startActivity(Intent(this,Myrewards_point::class.java))
        }

        back.setOnClickListener {
            finish()
        }


    }
    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.statusbar)
        }
    }
}