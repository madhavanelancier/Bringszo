package com.elanciers.bringszo

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import kotlinx.android.synthetic.main.activity_place_order.*

class PlaceOrder : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_order)
        val currentapiVersion = Build.VERSION.SDK_INT
        if (currentapiVersion <= 21) {
            //frameLayout1.visibility= View.GONE
            //frm.visibility= View.VISIBLE
        } else {
            //   logo!!.setImageResource(R.mipmap.login_bg)
            //frm.visibility= View.GONE
            //frameLayout1.visibility= View.VISIBLE
            val path = "android.resource://" + packageName + "/" + R.raw.order
            geoloc_anim.setVideoURI(Uri.parse(path))
            geoloc_anim.start()
            geoloc_anim.setZOrderOnTop(true)
            geoloc_anim.setOnPreparedListener(object: MediaPlayer.OnPreparedListener {
                override fun onPrepared(mp: MediaPlayer) {
                    mp.setLooping(true)
                    placeholder.setVisibility(View.GONE);
                }
            })
            //
            //imageView5!!.visibility = View.VISIBLE
        }

        Handler().postDelayed(Runnable {

                    startActivity(Intent(this, OrderActivity::class.java))
                    finish()

        }, 2500)


    }
}