package com.elanciers.bringszo

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.elanciers.bringszo.Common.Utils
import com.elanciers.bringszo.retrofit.ApproveUtils
import com.elanciers.bringszo.retrofit.Resp
import kotlinx.android.synthetic.main.activity_mpin_.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Mpin_Activity : AppCompatActivity() {
    var otp_edit_box1: EditText? = null
    var otp_edit_box2:EditText? = null
    var otp_edit_box3: EditText? = null
    var otp_edit_box4: EditText? = null
    lateinit var utils : Utils
    lateinit var pDialog: Dialog
    var mpin=""

    val activity = this
    val tag = "Signup"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mpin_)
        utils = Utils(activity)
        pDialog = Dialog(activity)

        otp_edit_box1=findViewById(R.id.otp_edit_box1) as EditText
        otp_edit_box2=findViewById(R.id.otp_edit_box2) as EditText
        otp_edit_box3=findViewById(R.id.otp_edit_box3) as EditText
        otp_edit_box4=findViewById(R.id.otp_edit_box4) as EditText
        otp_edit_box1!!.addTextChangedListener(
            GenericTextWatcher(
                otp_edit_box1!!


            )
        )
        otp_edit_box2!!.addTextChangedListener(
            GenericTextWatcher(
                otp_edit_box2!!

            )
        )
        otp_edit_box3!!.addTextChangedListener(
            GenericTextWatcher(
                otp_edit_box3!!

            )
        )
        otp_edit_box4!!.addTextChangedListener(
            GenericTextWatcher(
                otp_edit_box4!!

            )
        )

        load()

        signinbtn.setOnClickListener {
            if(otp_edit_box2!!.text.toString().isNotEmpty()&&
                otp_edit_box1!!.text.toString().isNotEmpty()&&
                otp_edit_box4!!.text.toString().isNotEmpty()&&
                otp_edit_box3!!.text.toString().isNotEmpty())
            {
                val mpins=otp_edit_box1!!.text.toString()+
                otp_edit_box2!!.text.toString()+
                otp_edit_box3!!.text.toString()+
                otp_edit_box4!!.text.toString()

                if(mpin.isNotEmpty()) {
                    if (mpin == mpins) {
                        utils.setMpin(mpins)
                        startActivity(Intent(activity,OptionSelect_Activity::class.java))
                        finish()

                    } else {
                        Toast.makeText(applicationContext, "Incorrect Mpin", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    utils.setMpin(mpins)

                    insert()
                }

            }
            else{
                Toast.makeText(applicationContext,"Please fill all fields",Toast.LENGTH_SHORT).show()
            }
        }


    }


    fun load(){
        val call = ApproveUtils.Get.getmpin(utils.mobile())
        call.enqueue(object : Callback<Resp> {
            override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                Log.e("$tag response", response.toString())
                if (response.isSuccessful()) {
                    val example = response.body() as Resp
                    println(example)
                    if (example.status == "Success") {
                        if(example.message=="null"||example.message.isNullOrEmpty()){
                            textView18.setText("Set M-PIN")
                            textView19.setText("Please set your 4 digit m-pin")
                        }
                        else{
                            textView18.setText("Enter M-PIN")
                            textView19.setText("Please enter your 4 digit m-pin")
                            mpin=example.message.toString()
                            println("mpin"+mpin)
                        }

                    } else {
                        Toast.makeText(
                            activity,
                            example.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                pDialog.dismiss()
                //loading_dismiss()
                //loading_show(activity).dismiss()
            }

            override fun onFailure(call: Call<Resp>, t: Throwable) {
                Log.e("$tag Fail response", t.toString())
                if (t.toString().contains("time")) {
                    Toast.makeText(
                        activity,
                        "Poor network connection",
                        Toast.LENGTH_LONG
                    ).show()
                }
                pDialog.dismiss()
                //loading_dismiss()
                //loading_show(activity).dismiss()
            }
        })
    }

    fun insert(){
        val calls = ApproveUtils.Get.setmpin(utils.mobile(),utils.mpin())
        calls.enqueue(object : Callback<Resp> {
            override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                Log.e("$tag response", response.toString())
                if (response.isSuccessful()) {
                    val example = response.body() as Resp
                    println(example)
                    if (example.status == "Success") {
                        if(example.message=="null"){

                        }
                        else{
                            startActivity(Intent(activity,OptionSelect_Activity::class.java))
                            finish()
                        }

                    } else {
                        Toast.makeText(
                            activity,
                            example.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                pDialog.dismiss()
                //loading_dismiss()
                //loading_show(activity).dismiss()
            }

            override fun onFailure(call: Call<Resp>, t: Throwable) {
                Log.e("$tag Fail response", t.toString())
                if (t.toString().contains("time")) {
                    Toast.makeText(
                        activity,
                        "Poor network connection",
                        Toast.LENGTH_LONG
                    ).show()
                }
                pDialog.dismiss()
                //loading_dismiss()
                //loading_show(activity).dismiss()
            }
        })
    }







    inner class GenericTextWatcher(private val view: View) : TextWatcher {
        private var otp_edit_box2s: EditText?=null
        private var otp_edit_box1s: EditText?=null
        private var otp_edit_box3s: EditText?=null
        private var otp_edit_box4s: EditText?=null



        override fun afterTextChanged(editable: Editable) {
            // TODO Auto-generated method stub
            val text = editable.toString()

            when (view.id) {

                R.id.otp_edit_box1 -> if (text.length == 1) otp_edit_box2!!.requestFocus()
                R.id.otp_edit_box2 -> if (text.length == 1) otp_edit_box3!!.requestFocus() else if (text.length == 0) otp_edit_box1!!.requestFocus()
                R.id.otp_edit_box3 -> if (text.length == 1) otp_edit_box4!!.requestFocus() else if (text.length == 0) otp_edit_box2!!.requestFocus()
                R.id.otp_edit_box4 -> if (text.length == 0) otp_edit_box3!!.requestFocus()
            }
        }

        override fun beforeTextChanged(
            arg0: CharSequence,
            arg1: Int,
            arg2: Int,
            arg3: Int
        ) {
            // TODO Auto-generated method stub
        }

        override fun onTextChanged(
            arg0: CharSequence,
            arg1: Int,
            arg2: Int,
            arg3: Int
        ) {
            // TODO Auto-generated method stub
        }

    }
}