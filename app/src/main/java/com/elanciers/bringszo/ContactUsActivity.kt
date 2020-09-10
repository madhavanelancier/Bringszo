package com.elanciers.bringszo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.elanciers.bringszo.Common.Appconstands
import kotlinx.android.synthetic.main.activity_contact_us.*

class ContactUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Appconstands.changeStatusBarColor(this,R.color.statusbar)
        setContentView(R.layout.activity_contact_us)

        back.setOnClickListener {
            finish()
        }
    }
}
