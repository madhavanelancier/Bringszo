package com.elanciers.bringszo

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.elanciers.bringszo.Common.Appconstands
import com.elanciers.bringszo.Common.Appconstands.RequestPermissionCode
import com.elanciers.bringszo.Common.Appconstands.isValidEmail
import com.elanciers.bringszo.Common.Appconstands.loading_show
import com.elanciers.bringszo.Common.Utils
import com.elanciers.bringszo.retrofit.ApproveUtils
import com.elanciers.bringszo.retrofit.Resp
import com.elanciers.bringszo.retrofit.Respval
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_product_search.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.back
import kotlinx.android.synthetic.main.connection_error.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {
    val tag = "Signup"
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
        setContentView(R.layout.activity_signup)
        utils = Utils(activity)
        pDialog = Dialog(activity)
        if (CheckingPermissionIsEnabledOrNot()){

        }else {
            RequestMultiplePermission()
        }

        back.setOnClickListener {
            finish()
        }


        referal.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, star: Int, before: Int, count: Int) {
                if (Appconstands.net_status(activity)) {

                    if (referal.text.trim().length==10){
                        //src = searchedit.text.trim().toString()
                        //start=0
                        //Products.clear()
                        sponsor(referal.text.toString())

                    }
                    else if(referal.text.trim().isEmpty()){
                        linname.visibility=View.GONE
                    }
                }

                else{
                    connection.visibility= View.VISIBLE
                }

            }
        })


        signupbtn.setOnClickListener {
            if (CheckingPermissionIsEnabledOrNot()) {
                if (name.text.toString().trim().isNotEmpty() && mobile.text.toString().trim().length==10){
                    if (email.text.toString().trim().isNotEmpty()){
                        if (isValidEmail(email)){
                            SendLogin(name.text.toString().trim(),mobile.text.toString().trim(),email.text.toString().trim())
                        }else{
                            email.setError("Enter Valid Email")
                        }
                    }else{
                        SendLogin(name.text.toString().trim(),mobile.text.toString().trim(),email.text.toString().trim())
                    }
                }else{
                    if (name.text.toString().trim().isEmpty()){
                        name.setError("Enter Name")
                    }
                    if (mobile.text.toString().trim().isEmpty()){
                        name.setError("Enter Valid Mobile Number")
                    }
                }
            } else {
                RequestMultiplePermission()
            }
        }
    }

    fun sponsor(src:String){
        val call = ApproveUtils.Get.spons(src)
        call.enqueue(object : Callback<Respval> {
            override fun onResponse(call: Call<Respval>, response: Response<Respval>) {
                Log.e("$tag response", response.toString())
                if (response.isSuccessful()) {
                    val example = response.body() as Respval
                    println(example)
                    if (example.status == "Success") {
                        println("example.message : "+example.message)
                        val nameval=example.response!!.name.toString().toUpperCase()
                        linname.visibility=View.VISIBLE
                        referalnm.setText(nameval)
                    } else {
                        referal.setText(null)
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

            override fun onFailure(call: Call<Respval>, t: Throwable) {
                Log.e("$tag Fail response", t.toString())
                referal.setText(null)
                Toast.makeText(
                    activity,
                    "Invalid referral number",
                    Toast.LENGTH_LONG
                ).show()
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



    fun SendLogin(name:String,mobile:String,email:String){
        pDialog = Dialog(activity)
        /*val pDialo = ProgressDialog(activity);
        pDialo.setMessage("Loading....");
        pDialo.setIndeterminate(false);
        pDialo.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialo.setCancelable(false);
        //pDialo.setMax(3)
        pDialo.show()*/
        loading_show(activity,pDialog).show()
        FirebaseMessaging.getInstance()
                .subscribeToTopic(getString(R.string.msg_subscribed))
        /*var refreshedToken: String?
        if (utils.getToken().isEmpty()) {
            refreshedToken = FirebaseInstanceId.getInstance().token;
            utils.setToken(refreshedToken!!)
        } else {
            refreshedToken = utils.getToken()
        }*/
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

                val call = ApproveUtils.Get.Register("1",name,mobile,email,referal.text.toString().trim(),token!!)
                call.enqueue(object : Callback<Resp> {
                    override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                        Log.e("$tag response", response.toString())
                        if (response.isSuccessful()) {
                            val example = response.body() as Resp
                            println(example)
                            if (example.status == "Success") {
                                println("example.message : "+example.message)
                                val it = Intent(activity, OtpActivity::class.java)
                                it.putExtra("type","signup")
                                it.putExtra("otp",example.message)
                                it.putExtra("name",name)
                                it.putExtra("mobile",mobile)
                                it.putExtra("email",email)
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
            })

    }

    fun CheckingPermissionIsEnabledOrNot(): Boolean {

        val INTERNET = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.INTERNET)
        val ACCESS_NETWORK_STATEt = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_NETWORK_STATE)
        //val ACCESS_NOTIFICATION_POLICY = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_NOTIFICATION_POLICY)
        val ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        //val GPS_PROVIDER = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.GPS_PROVIDER)
        //val NETWORK_PROVIDER = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.NETWORK_PROVIDER)
        //val PASSIVE_PROVIDER = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.PASSIVE_PROVIDER)
        //val CALL_PHONE = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CALL_PHONE)
        val ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)


        return INTERNET == PackageManager.PERMISSION_GRANTED &&
                ACCESS_NETWORK_STATEt == PackageManager.PERMISSION_GRANTED &&
                //ACCESS_NOTIFICATION_POLICY == PackageManager.PERMISSION_GRANTED&&
                ACCESS_COARSE_LOCATION == PackageManager.PERMISSION_GRANTED&&
                //CALL_PHONE == PackageManager.PERMISSION_GRANTED&&
                ACCESS_FINE_LOCATION == PackageManager.PERMISSION_GRANTED
    }
    private fun RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(activity, arrayOf<String>(
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                //android.Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                //android.Manifest.permission.CALL_PHONE,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        ),RequestPermissionCode)

    }


}
