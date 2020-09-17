package com.elanciers.bringszo

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.content_scrolling.*
import android.net.Uri
import android.view.animation.Animation
import android.view.animation.Transformation
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.elanciers.bringszo.Common.Appconstands
import com.elanciers.bringszo.Common.Appconstands.isValidEmail
import com.elanciers.bringszo.Common.DBController
import com.elanciers.bringszo.Common.Utils
import com.elanciers.bringszo.retrofit.ApproveUtils
import com.elanciers.bringszo.retrofit.Resp
import kotlinx.android.synthetic.main.activity_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileActivity : AppCompatActivity() {
    val tag = "Profile"
    val activity = this
    lateinit var utils : Utils
    lateinit var db : DBController
    lateinit var pDialog: Dialog
    var packagenm = "com.elanciers.bringszo"
    lateinit var ab : ActionBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        utils = Utils(activity)
        db = DBController(activity)
        pDialog = Dialog(activity)
        changeStatusBarColor()
        val mToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(mToolbar);
        ab = supportActionBar!!
        ab!!.setDisplayShowHomeEnabled(true)
        ab!!.setDisplayHomeAsUpEnabled(true)
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_24px)
        setTit()

        version.setText("Version "+BuildConfig.VERSION_NAME)

        manage_address.setOnClickListener{
            startActivity(Intent(this, ManageAddressActivity::class.java))
        }

        contact_us.setOnClickListener{
            startActivity(Intent(this, ContactUsActivity::class.java))
        }

        terms.setOnClickListener {
            startActivity(Intent(this, Termsconditions::class.java))

        }

        invite.setOnClickListener {

            startActivity(Intent(this, Referral_Activity::class.java))



        }

        rate_us.setOnClickListener {
            val i = Intent(android.content.Intent.ACTION_VIEW)
            i.data = Uri.parse("https://play.google.com/store/apps/details?id=$packagenm")
            startActivity(i)
        }

        referral.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=$packagenm");
            intent.setType("text/plain");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Share"));
        }

        logout.setOnClickListener {
            val alert = AlertDialog.Builder(activity)
            alert.setTitle("Logout")
            alert.setMessage("Are you sure want to Logout?")
            alert.setPositiveButton("yes",object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.dismiss()
                    logout(it)
                }

            })
            alert.setNegativeButton("no",object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.dismiss()
                }

            })
            alert.show()
        }
    }
    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.statusbar)
        }
    }

    fun collapse(v: View) {
        val initialHeight = v.getMeasuredHeight()

        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    v.setVisibility(View.GONE)
                } else {
                    v.getLayoutParams().height = initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // 1dp/ms
        a.setDuration((((initialHeight / v.getContext().getResources().getDisplayMetrics().density).toLong()) ))
        v.startAnimation(a)
    }

    fun expand(v: View) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val targetHeight = v.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                v.layoutParams.height = if (interpolatedTime == 1f)
                    ViewGroup.LayoutParams.WRAP_CONTENT
                else
                    (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // 1dp/ms
        a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

    fun setTit(){
        val mToolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(mToolbar);

        ab = supportActionBar!!

        ab!!.setDisplayShowHomeEnabled(true)

        ab!!.setDisplayHomeAsUpEnabled(true)

        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_24px)

        ab!!.title = utils.name().toString().toUpperCase()
        ab!!.subtitle = utils.mobile()

        mob.setText(utils.mobile())


        mail.setText(utils.email())
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.profile_page, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.getItemId()
        when(id){
            R.id.pro_edit -> {
                EditProfile()
                return true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun logout(view: View){
        utils.setLogin(false)
        db.dropHoleCart()
        val its = Intent(activity,SignActivity::class.java)
        its.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(its)
        finish()
        //finishAffinity()
    }

    fun EditProfile(){
        val openwith = Dialog(activity)
        openwith.setOnDismissListener {
            println("dismiss")
        }
        openwith.requestWindowFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
        val popUpView = activity.layoutInflater.inflate(R.layout.edit_profile_layout, null)
        /*popUpView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val eventConsumed = gestureDetector.onTouchEvent(event);
                println("eventConsumed : "+eventConsumed)
                if (eventConsumed) {
                    return true;
                } else {
                    return false;
                }
            }

        });*/
        val emp_lay = popUpView.findViewById(R.id.emp_lay) as LinearLayout
        val update = popUpView.findViewById(R.id.update) as TextView
        val name = popUpView.findViewById(R.id.name) as EditText
        val mobile = popUpView.findViewById(R.id.mobileno) as EditText
        val email = popUpView.findViewById(R.id.email) as EditText
        val animMoveToTop = AnimationUtils.loadAnimation(activity, R.anim.bottom_top)
        popUpView.animation =animMoveToTop
        name.setText(utils.name())
        mobile.setText(utils.mobile())
        email.setText(utils.email())

        update.setOnClickListener {
            if (name.text.toString().trim().isNotEmpty() && mobile.text.toString().trim().length==10){
                if (email.text.toString().trim().isNotEmpty()){
                    if (isValidEmail(email)){
                        pDialog = Dialog(activity)
                        Appconstands.loading_show(activity, pDialog).show()
                        val call = ApproveUtils.Get.profile_update(utils.userid(),name.text.toString().trim(),mobile.text.toString().trim(),email.text.toString().trim(),utils.zoneid())
                        call.enqueue(object : Callback<Resp> {
                            override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                                Log.e("$tag response", response.toString())
                                if (response.isSuccessful()) {
                                    val example = response.body() as Resp
                                    println(example)
                                    if (example.status == "Success") {
                                        println("example.message : "+example.message)
                                        Toast.makeText(
                                            activity,
                                            example.message,
                                            Toast.LENGTH_LONG
                                        ).show()
                                        utils.setUser(utils.userid(),name.text.toString().trim(), mobile.text.toString().trim(), email.text.toString().trim())

                                        openwith.dismiss()
                                        finish()

                                        startActivity(Intent(activity, ProfileActivity::class.java))

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
                    }else{
                        email.setError("Enter Valid Email")
                    }
                }else{
                    pDialog = Dialog(activity)
                    Appconstands.loading_show(activity, pDialog).show()
                    val call = ApproveUtils.Get.profile_update(utils.userid(),name.text.toString().trim(),mobile.text.toString().trim(),email.text.toString().trim(),utils.zoneid())
                    call.enqueue(object : Callback<Resp> {
                        override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                            Log.e("$tag response", response.toString())
                            if (response.isSuccessful()) {
                                val example = response.body() as Resp
                                println(example)
                                if (example.status == "Success") {
                                    println("example.message : "+example.message)
                                    Toast.makeText(
                                        activity,
                                        example.message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                    utils.setUser(utils.userid(),name.text.toString().trim(), mobile.text.toString().trim(), email.text.toString().trim())
                                    openwith.dismiss()
                                    setTit()
                                    finish()
                                    startActivity(Intent(activity, ProfileActivity::class.java))

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
            }else{
                if (name.text.toString().trim().isEmpty()){
                    name.setError("Enter Name")
                }
                if (mobile.text.toString().trim().isEmpty()){
                    name.setError("Enter Valid Mobile Number")
                }
            }
        }

        emp_lay.setOnClickListener {
            openwith.dismiss()
        }

        openwith.setContentView(popUpView);
        openwith.setCancelable(true)
        openwith.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        openwith.window!!.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        openwith.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        val displaymetrics = DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        val width =  (displaymetrics.widthPixels * 1);
        val height =  (displaymetrics.heightPixels * 1);
        openwith.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        openwith.show()

        /*val openwith = BottomSheetDialog(activity)
        openwith.setOnDismissListener {
            println("dismiss")
        }
        openwith.requestWindowFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
        val popUpView = activity.layoutInflater.inflate(R.layout.edit_profile_layout, null)
        val update = popUpView.findViewById(R.id.update) as TextView
        val name = popUpView.findViewById(R.id.name) as EditText
        val mobile = popUpView.findViewById(R.id.mobileno) as EditText
        val email = popUpView.findViewById(R.id.email) as EditText
        val animMoveToTop = AnimationUtils.loadAnimation(activity, R.anim.bottom_top)
        popUpView.animation =animMoveToTop
        name.setText(utils.name())
        mobile.setText(utils.mobile())
        email.setText(utils.email())


        openwith.setContentView(popUpView);
        openwith.setCancelable(true)
        openwith.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        openwith.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        openwith.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        val displaymetrics = DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        val width =  (displaymetrics.widthPixels * 1);
        val height =  (displaymetrics.heightPixels * 1);
        openwith.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        openwith.show()*/
    }

}
