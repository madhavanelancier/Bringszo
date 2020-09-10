package com.elanciers.bringszo.Common

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.elanciers.bringszo.R
import com.elanciers.bringszo.retrofit.ApproveUtils
import com.elanciers.bringszo.retrofit.Resp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object Appconstands {
    var LOCATION_REQUEST = 1000;
    var GPS_REQUEST = 1001;
    val RequestPermissionCode = 7
    val AddAddressCode = 5
    val AddCoupon = 7
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    lateinit var alert : AlertDialog

    val Domin ="https://www.bringszo.com/bring/api/"
    val ImageDomain ="https://www.bringszo.com/bring/"
    val rupees ="₹ "
    val new_reg = "customers/register"
    val login = Domin+"customers/store"
    val ventorlist = Domin+"customers/vendors/1"
    val homecat = Domin+"customers/category"
    val slider = Domin+"customers/home_slider"
    val otp = Domin+"customers/otp_check"
    val product = Domin+"product"
    val terms = Domin+"terms_condition"
    val addAddress = Domin+"addAddress"
    val editAddress = Domin+"editAddress"
    //https://www.bringszo.com/bring/api/userReward
    val userReward = Domin+"userReward"
    val deleteAddress = Domin+"deleteAddress"
    val order = Domin+"order"
    val profile_update = Domin+"profile_update"
    val search = "https://pova.co.in/panel/api/search"
    val POVA_PAYMENTS =  Domin + "pova_payments.api.php"//http://www.pova.co.in/api/
    val PAYMENTID = Domin + "payid.php"
    const val RADIUS_1000 = "1000"
    const val TYPE_BAR = "bar"
    const val GOOGLE_API_KEY = "AIzaSyD4Liy8N1PPV-BPYooPpjQGE5Nxl9LAgG8"

    val loc="https://www.bringszo.com/bring/api/getvalidzone"
    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        //if (LocaleHelper.getLanguage(this)!=LANGUAGE_TAMIL) {
        println("LocaleHelper.getLanguage(this) : "+LocaleManager(this).language)
        println("Lacal : "+ Locale.getDefault().language)
        if (LocaleManager(this).language== LocaleManager.LANGUAGE_TAMIL) {
            setNewLocale(LocaleManager.LANGUAGE_ENGLISH, true)
        }
        val resources = this.getResources();
        adrs.setText(resources.getString(R.string.adrs));
        //}
    }*/

    /*@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun setNewLocale(language: String, restartProcess: Boolean): Boolean {
        //LocaleHelper.setLocale(this, language);

        *//*val resources = this.getResources();
        adrs.setText(resources.getString(R.string.adrs));*//*
        //It is required to recreate the activity to reflect the change in UI.
        //recreate();
        App.localeManager.setNewLocale(this, language)

        val i = Intent(this, HomePage::class.java)

        ContextCompat.startActivity(this,i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK),null)

        if (restartProcess) {
            System.exit(0)
        } else {
            Toast.makeText(this, "Activity restarted", Toast.LENGTH_SHORT).show()
        }
        return true
    }*/
    fun isValidEmail(editText: EditText): Boolean {

        return Patterns.EMAIL_ADDRESS.matcher(editText.text.toString().trim { it <= ' ' }).matches()
    }
    fun changeStatusBarColor(context: Activity,color : Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = context.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = context.resources.getColor(color)
        }
    }
    fun net_status(context: Context):Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var connected=false
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).state == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(
                        ConnectivityManager.TYPE_WIFI).state == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true
        } else {
            //Toast.makeText(this,"No Network",Toast.LENGTH_LONG).show()
            connected = false
        }
        return connected
    }


    fun maintenance(activity: Activity){
        val call2 = ApproveUtils.Get.getMaintenance()
        call2.enqueue(object : Callback<Resp> {
            override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                Log.e("maintenance responce", response.toString())
                if (response.isSuccessful()) {
                    val example = response.body() as Resp
                    println(example)
                    if (example.status == "Success"){
                        Utils(activity).setMaintenance(example.message!!)
                        maintenancePopup(activity,example.message!!)
                    }else {
                        Utils(activity).setMaintenance("")
                        if (::alert.isInitialized){
                            if (alert.isShowing) {
                                alert.dismiss()
                            }
                        }
                        //Toast.makeText(this@Dashboard, example.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<Resp>, t: Throwable) {
                Log.e("maince Fail response", t.toString())
                if (t.toString().contains("time")) {
                    Toast.makeText(
                            activity,
                            "Poor network connection",
                            Toast.LENGTH_LONG
                    ).show()
                }
                if (t.toString().contains("Unable to resolve host")) {
                    if (Utils(activity).Maintenance().isNotEmpty()) {
                        maintenancePopup(activity, Utils(activity).Maintenance())
                    } else {
                        if ((::alert.isInitialized)) {
                            if (alert.isShowing) {
                                alert.dismiss()
                            }
                        }
                    }
                }
            }
        })
    }

    fun maintenancePopup(activity: Activity,message:String){
        if (!(::alert.isInitialized)){
            val alert11 = AlertDialog.Builder(activity)
            alert11.setCancelable(false)
            //alert11.setTitle("Maintenance")
            alert11.setMessage(message)
            alert11.setPositiveButton(
                    "OK",
                    object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog!!.dismiss()
                            //activity.finish()
                            //activity.finishAffinity();
                            /*val intent = Intent(activity, Login::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            activity.startActivity(intent)*/
                        }
                    })
            alert = alert11.create()
            alert.show()
        }else{
            if (!alert.isShowing) {
                val alert11 = AlertDialog.Builder(activity)
                alert11.setCancelable(false)
                //alert11.setTitle("Maintenance")
                alert11.setMessage(message)
                alert11.setPositiveButton(
                        "OK",
                        object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                dialog!!.dismiss()
                                //activity.finish()
                            }
                        })
                alert = alert11.create()
                alert.show()
            }
        }
    }

    fun CheckingPermissionIsEnabledOrNot(context: Activity): Boolean {

        val INTERNET = ContextCompat.checkSelfPermission(context, android.Manifest.permission.INTERNET)
        val ACCESS_NETWORK_STATEt = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_NETWORK_STATE)
        //val ACCESS_NOTIFICATION_POLICY = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NOTIFICATION_POLICY)
        val ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        //val GPS_PROVIDER = ContextCompat.checkSelfPermission(this, android.Manifest.permission.GPS_PROVIDER)
        //val NETWORK_PROVIDER = ContextCompat.checkSelfPermission(this, android.Manifest.permission.NETWORK_PROVIDER)
        //val PASSIVE_PROVIDER = ContextCompat.checkSelfPermission(this, android.Manifest.permission.PASSIVE_PROVIDER)
        val CALL_PHONE = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE)
        val ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)


        return INTERNET == PackageManager.PERMISSION_GRANTED &&
                ACCESS_NETWORK_STATEt == PackageManager.PERMISSION_GRANTED &&
                //ACCESS_NOTIFICATION_POLICY == PackageManager.PERMISSION_GRANTED&&
                ACCESS_COARSE_LOCATION == PackageManager.PERMISSION_GRANTED&&
                CALL_PHONE == PackageManager.PERMISSION_GRANTED&&
                ACCESS_FINE_LOCATION == PackageManager.PERMISSION_GRANTED
    }
    private fun RequestMultiplePermission(context: Activity) {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(context, arrayOf<String>(
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                //android.Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.CALL_PHONE,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        ),RequestPermissionCode)

    }

    /*val call = ApproveUtils.Get.Register()
                call.enqueue(object : Callback<Resp> {
                    override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                        Log.e("$tag responce", response.toString())
                        if (response.isSuccessful()) {
                            val example = response.body() as Resp
                            println(example)
                            if (example.status == "Success") {
                                val mem = example.response!!

                            } else {
                                Toast.makeText(
                                        this@SignupActivity,
                                        "Login failed",
                                        Toast.LENGTH_SHORT
                                ).show()

                            }
                        }
                        pDialo.dismiss()
                    }

                    override fun onFailure(call: Call<Resp>, t: Throwable) {
                        Log.e("$tag Fail response", t.toString())
                        if (t.toString().contains("time")) {
                            Toast.makeText(
                                    this@SignupActivity,
                                    "Poor network connection",
                                    Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                    this@SignupActivity,
                                    "Signup failed",
                                    Toast.LENGTH_SHORT
                            )
                                    .show()

                        }
                        pDialo.dismiss()
                    }
                })*/

    fun loading_show(mActivity:Activity,dialog: Dialog) : Dialog {
        val openwith = dialog
        openwith.requestWindowFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
        openwith.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        val popUpView = mActivity.layoutInflater.inflate(R.layout.loading_layout, null)

        //val pronm = popUpView.findViewById(R.id.pronm) as TextView

        openwith.setContentView(popUpView);
        openwith.setCancelable(false)
        openwith.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        openwith.window!!.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        openwith.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        val displaymetrics = DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        val width =  (displaymetrics.widthPixels * 1);
        val height =  (displaymetrics.heightPixels * 1);
        openwith.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //openwith.show()
        return openwith
    }
}