package com.elanciers.bringszo

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.elanciers.bringszo.Common.Appconstands.loading_show
import com.elanciers.bringszo.Common.Utils
import com.elanciers.bringszo.retrofit.ApproveUtils
import com.elanciers.bringszo.retrofit.Resp
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_signin.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SigninActivity : AppCompatActivity() {
    val tag = "Signin"
    val activity = this
    lateinit var utils : Utils
    lateinit var pDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setContentView(R.layout.activity_signin)
        utils = Utils(activity)
        pDialog = Dialog(activity)

        signinbtn.setOnClickListener {
            if (mobile.text.length==10){
                SendLogin(mobile.text.toString().trim())
            }else{
                mobile.setError("Enter Mobile No")
                //Toast.makeText(activity,"Enter Mobile No",Toast.LENGTH_LONG).show()
            }
        }
    }

    fun SendLogin(mobile:String){
       /* val pDialo = ProgressDialog(activity);
        pDialo.setMessage("Loading....");
        pDialo.setIndeterminate(false);
        pDialo.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialo.setCancelable(false);
        //pDialo.setMax(3)
        pDialo.show()*/
        pDialog=Dialog(activity)
        loading_show(activity,pDialog).show()
        FirebaseMessaging.getInstance()
            .subscribeToTopic(getString(R.string.msg_subscribed))

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(tag, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                utils.setToken(token!!)
                // Log and toast
                //val msg = getString(R.string.msg_token_fmt, token)
                Log.d(tag, token)
                //Toast.makeText(activity, token, Toast.LENGTH_SHORT).show()
                val call = ApproveUtils.Get.Login("1",mobile,token!!,utils.zoneid())
                call.enqueue(object : Callback<Resp> {
                    override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                        Log.e("$tag response", response.toString())
                        if (response.isSuccessful()) {
                            val example = response.body() as Resp
                            println(example)
                            if (example.status == "Success") {
                                val it = Intent(activity, OtpActivity::class.java)
                                it.putExtra("type","signin")
                                it.putExtra("otp",example.message)
                                it.putExtra("mobile",mobile)
                                startActivity(it)
                                finish()
                            } else {
                                Toast.makeText(
                                    activity,
                                    example.message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        pDialog.dismiss()
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
                        //loading_show(activity).dismiss()
                    }
                })
            })

    }
}
