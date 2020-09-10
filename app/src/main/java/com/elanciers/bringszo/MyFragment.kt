package com.elanciers.bringszo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.elanciers.bringszo.Common.Appconstands.ImageDomain
import com.elanciers.bringszo.Common.Utils
import com.elanciers.bringszo.DataClass.ImageScroll

class MyFragment(val data : ImageScroll) : Fragment() {
    lateinit var utils : Utils

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.item_carousel, container, false)
        val messageTextView = v.findViewById<View>(R.id.imageView6) as ImageView
        Log.i("imageee", arguments!!.getString(EXTRA_MESSAGE))
        utils = Utils(activity!!)

        //messageTextView.setBackgroundResource(message);
        /*File f = new File(getArguments().getString(EXTRA_MESSAGE));
        Uri uri=Uri.parse(getArguments().getString(EXTRA_MESSAGE));
        //messageTextView.setImageURI(uri);*/
        println("data.id : "+data.id)
        messageTextView.setOnClickListener {
            println("cliked Imageview ")
            println("click data.id : "+data.id)
           /* val it = Intent(activity, ProductActivity::class.java)
            it.putExtra("name", data.content)
            it.putExtra("typeid", data.id)
            utils.setVendor("1",data.id.toString(),"",data.img.toString())
            startActivity(it)*/


        }
        //Glide.with(activity!!).load("https://image.shutterstock.com/image-photo/bright-spring-view-cameo-island-260nw-1048185397.jpg").into(messageTextView);
        Glide.with(activity!!).load(ImageDomain+data.img).into(messageTextView);
        return v
    }

    companion object {
        val EXTRA_MESSAGE = "EXTRA_MESSAGE"

        fun newInstance(message: String,data: ImageScroll): MyFragment {
            val f = MyFragment(data)
            val bdl = Bundle(1)
            bdl.putString(EXTRA_MESSAGE, message)
            f.arguments = bdl
            return f
        }
    }

}