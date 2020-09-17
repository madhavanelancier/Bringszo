package com.elanciers.bringszo

import android.app.Dialog
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.elanciers.bringszo.Common.Appconstands
import com.elanciers.bringszo.Common.Appconstands.loading_show
import com.elanciers.bringszo.Common.Connection
import com.elanciers.bringszo.Common.DBController
import com.elanciers.bringszo.Common.Utils
import com.elanciers.bringszo.DataClass.AddressData
import com.elanciers.bringszo.retrofit.ApproveUtils
import com.elanciers.bringszo.retrofit.Resp
import kotlinx.android.synthetic.main.activity_otp.*
import kotlinx.android.synthetic.main.activity_termsconditions.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtpActivity : AppCompatActivity() {
    val tag = "Otp"
    val activity = this
    lateinit var utils : Utils
    lateinit var db : DBController
    lateinit var pDialog: Dialog

    var type = ""
    var otp = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setContentView(R.layout.activity_otp)
        utils = Utils(activity)
        pDialog = Dialog(activity)
        db = DBController(activity)

        type = intent.extras.getString("type")
        otp = intent.extras.getString("otp")
        back.setOnClickListener {
            finish()
        }
        println("otp : "+otp)

        submit.setOnClickListener {
            if (enotp.text.toString().trim().isNotEmpty()) {
                if (otp == enotp.text.toString().trim()) {
                    if (type=="signup") {
                        val name = intent.extras.getString("name")
                        val mobile = intent.extras.getString("mobile")
                        val email = intent.extras.getString("email")
                        CheckSignupOtp(name,mobile,email,otp,enotp.text.toString().trim())
                    }else{
                        val mobile = intent.extras.getString("mobile")
                        CheckSigninOtp(mobile,otp,enotp.text.toString().trim())
                    }
                } else {
                    Toast.makeText(activity, "Enter Correct OTP No.", Toast.LENGTH_LONG).show()
                }
            } else {
                enotp.setError("Enter Otp")
            }
        }
    }

    fun CheckSigninOtp(mob:String,otp:String,eotp:String){
        /*val pDialo = ProgressDialog(this);
        pDialo.setMessage("Loading....");
        pDialo.setIndeterminate(false);
        pDialo.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialo.setCancelable(false);
        pDialo.show()*/
        pDialog=Dialog(activity)
        loading_show(activity,pDialog).show()
        val call = ApproveUtils.Get.Otp("1",mob,otp,eotp,"",utils.zoneid())
        call.enqueue(object : Callback<Resp> {
            override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                Log.e("$tag responce", response.toString())
                if (response.isSuccessful()) {
                    val example = response.body() as Resp
                    println(example)
                    if (example.status == "Success") {
                        val ad = response.body() as Resp
                        val userid = if (ad.response!![0].id.isNullOrEmpty())"" else ad.response!![0].id.toString()
                        val name = if (ad.response!![0].name.isNullOrEmpty())"" else ad.response!![0].name.toString()
                        val email = if (ad.response!![0].email.isNullOrEmpty())"" else ad.response!![0].email.toString()
                        val mobile = if (ad.response!![0].mobile.isNullOrEmpty())"" else ad.response!![0].mobile.toString()
                        val address = ad.response!![0].address
                        utils.setUser(userid,name, mobile, email)
                        println("userid : "+userid)
                        utils.setLogin(true)
                        for (ads in 0 until address!!.size){
                            val adrs_id = if (address[ads].id.isNullOrEmpty())"" else address[ads].id.toString()
                            val type = if (address[ads].type.isNullOrEmpty())"" else address[ads].type.toString()
                            val title = if (address[ads].title.isNullOrEmpty())"" else address[ads].title.toString()
                            val flat_no = if (address[ads].flat_no.isNullOrEmpty())"" else address[ads].flat_no.toString()
                            val area = if (address[ads].area.isNullOrEmpty())"" else address[ads].area.toString()
                            val location = if (address[ads].location.isNullOrEmpty())"" else address[ads].location.toString()
                            val lat = if (address[ads].lat.isNullOrEmpty())"" else address[ads].lat.toString()
                            val lng = if (address[ads].lng.isNullOrEmpty())"" else address[ads].lng.toString()
                            val rs = AddressData()
                            rs.adrs_id=adrs_id
                            rs.adrs_type=type
                            rs.adrs_title=title
                            rs.address=location
                            rs.adrs_house=flat_no
                            rs.adrs_landmark=area
                            rs.adrs_latitude=lat
                            rs.adrs_longtitude=lng
                            db.AddressInsert(rs)
                        }
                        Toast.makeText(
                                activity,
                                example.message,
                                Toast.LENGTH_LONG
                        ).show()
                        startActivity(Intent(activity, OptionSelect_Activity::class.java))
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
    }

    fun CheckSignupOtp(name:String,mob:String,email:String,otp:String,eotp:String){

        Appconstands.loading_show(activity,pDialog).show()

        val call = ApproveUtils.Get.Otp("1",mob,otp,eotp,"",utils.zoneid())
        call.enqueue(object : Callback<Resp> {
            override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                Log.e("$tag responce", response.toString())
                if (response.isSuccessful()) {
                    val example = response.body() as Resp
                    println(example)
                    if (example.status == "Success") {
                        val ad = response.body() as Resp
                        val userid = if (ad.response!![0].id.isNullOrEmpty())"" else ad.response!![0].id.toString()
                        /*val name = if (ad.response!![0].name.isNullOrEmpty())"" else ad.response!![0].name.toString()
                        val email = if (ad.response!![0].email.isNullOrEmpty())"" else ad.response!![0].email.toString()
                        val mobile = if (ad.response!![0].mobile.isNullOrEmpty())"" else ad.response!![0].mobile.toString()
                        val address = ad.response!![0].address*/
                        utils.setUser(userid,name, mob, email)
                        utils.setLogin(true)
                        /*for (ads in 0 until address!!.size){
                            val type = if (address[ads].type.isNullOrEmpty())"" else address[ads].type.toString()
                            val title = if (address[ads].title.isNullOrEmpty())"" else address[ads].title.toString()
                            val flat_no = if (address[ads].flat_no.isNullOrEmpty())"" else address[ads].flat_no.toString()
                            val area = if (address[ads].area.isNullOrEmpty())"" else address[ads].area.toString()
                            val location = if (address[ads].location.isNullOrEmpty())"" else address[ads].location.toString()
                            val lat = if (address[ads].lat.isNullOrEmpty())"" else address[ads].lat.toString()
                            val lng = if (address[ads].lng.isNullOrEmpty())"" else address[ads].lng.toString()
                            val rs = AddressData()
                            rs.adrs_id=ads.toString()
                            rs.adrs_type=type
                            rs.adrs_title=title
                            rs.address=location
                            rs.adrs_house=flat_no
                            rs.adrs_landmark=area
                            rs.adrs_latitude=lat
                            rs.adrs_longtitude=lng
                            db.AddressInsert(rs)
                        }*/
                        Toast.makeText(
                                activity,
                                example.message,
                                Toast.LENGTH_LONG
                        ).show()
                        startActivity(Intent(activity, OptionSelect_Activity::class.java))
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
                //Appconstands.loading_show(activity).dismiss()
            }
        })
    }
}
