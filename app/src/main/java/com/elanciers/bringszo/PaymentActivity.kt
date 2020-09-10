package com.elanciers.bringszo

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.elanciers.bringszo.Common.Appconstands
import com.elanciers.bringszo.Common.Appconstands.loading_show
import com.elanciers.bringszo.Common.Connection
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class PaymentActivity : Activity(), PaymentResultListener {
    var purchaseamount: TextView? = null
    var totalamt: String? = null
    var mobile: String? = null
    var email: String? = null
    var rs: String? = null
    var amount: String? = null
    var doubleval: String? = null
    var amountint = 0
    var response = ""
    var Paymentmode = "Razorpay"
    var success = "success"
    var failed = "failed"
    public override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        totalamt = intent.extras.getString("totalamt")
        mobile = intent.extras.getString("mobile")
        email = intent.extras.getString("email")
        response = intent.extras.getString("response")
        Log.e("totalamt", totalamt)
        val st = StringTokenizer(totalamt, ".")
        rs = st.nextToken()
        amount = st.nextToken()
        doubleval = st.nextToken()
        purchaseamount = findViewById<View>(R.id.amount) as TextView
        purchaseamount!!.text = totalamt
        /*
         To ensure faster loading of the Checkout form,
          call this method as early as possible in your checkout flow.
         */
        Checkout.preload(applicationContext)
        // Payment button created by you in XML layout
        val button =
            findViewById<View>(R.id.btn_pay) as Button
        button.setOnClickListener { startPayment() }
    }

    fun startPayment() { /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        val activity: Activity = this
        val co = Checkout()
        try {
            val options = JSONObject()
            options.put("name", "Dofirst")
            options.put("description", "Purchase Amount")
            //You can omit the image option to fetch the image from dashboard
            options.put("image", R.mipmap.ic_launcher)
            options.put("currency", "INR")
            amountint = Integer.valueOf(amount)
            options.put("amount", amountint * 100)
            val preFill = JSONObject()
            preFill.put("email", email)
            preFill.put("contact", mobile)
            options.put("prefill", preFill)
            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_SHORT)
                .show()
            e.printStackTrace()
        }
    }

    /**
     * The name of the function has to be
     * onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    override fun onPaymentSuccess(razorpayPaymentID: String) {
        try {
            Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
            Log.i(
                "Payment Sucess",
                "Payment Successful: $razorpayPaymentID $response"
            )
            val task = Paymenttransfer()
            task.execute(razorpayPaymentID, success)
        } catch (e: Exception) {
            Log.e(TAG, "Exception in onPaymentSuccess", e)
        }
    }

    /**
     * The name of the function has to be
     * onPaymentError
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    override fun onPaymentError(code: Int, response: String) {
        try { // Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
            Log.i("Payment Error", "Payment failed: $code $response")
            val task = Paymenttransfer()
            task.execute("", failed)
        } catch (e: Exception) {
            Log.e(TAG, "Exception in onPaymentError", e)
        }
    }

    fun showthankspopup() { //open = new Dialog(LoginActivity.this,R.style.MyCustomTheme);
        val open = Dialog(this@PaymentActivity)
        open.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val popup = layoutInflater.inflate(R.layout.success_popup1, null)
        val ok = popup.findViewById<View>(R.id.yes) as TextView
        //open.getWindow().getAttributes().windowAnimations = R.style.MyCustomTheme;
        open.setContentView(popup)
        open.window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
        open.window.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        val lparam = WindowManager.LayoutParams()
        lparam.copyFrom(open.window.attributes)
        open.setCancelable(false)
        open.window.setLayout(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )
        open.show()
        ok.setOnClickListener {
            // TODO Auto-generated method stub
            open.dismiss()
            /*Intent intent = new Intent(PaymentActivity.this, JewelHome.class);
                    startActivity(intent);
                    finish();*/
        }
    }

    private inner class Paymenttransfer : AsyncTask<String?, String?, String?>() {
        var progbar: Dialog? = null
        override fun onPreExecute() {
            progbar = Dialog(this@PaymentActivity)
            loading_show(this@PaymentActivity, progbar!!).show()
        }

        override fun doInBackground(vararg param: String?): String? {
            var result: String? = null
            val con = Connection()
            try {
                val jobj = JSONObject()
                run {
                    jobj.put("id", response)
                    jobj.put("pmode", Paymentmode)
                    jobj.put("txid", param[0])
                    jobj.put("pstatus", param[1])
                }
                Log.e(
                    "Marketingordersvalues",
                    Appconstands.PAYMENTID + "" + jobj.toString()
                )
                result = con.sendHttpPostjson(Appconstands.PAYMENTID, jobj)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(resp: String?) {
            progbar!!.dismiss()
            try {
                if (resp != null) {
                    val array = JSONArray(resp)
                    val obj = array.getJSONObject(0)
                    if (obj.getString("Status") == "Success") {
                        showthankspopup()
                    } else {
                        Toast.makeText(this@PaymentActivity, "Cancel payment", Toast.LENGTH_SHORT)
                            .show()
                        /*Intent intent = new Intent(PaymentActivity.this, JewelHome.class);
                        startActivity(intent);*/
                    }
                } else {
                    Toast.makeText(
                        this@PaymentActivity,
                        "Oops something went wrong,please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@PaymentActivity,
                    "Please check your network connection",
                    Toast.LENGTH_SHORT
                ).show()
            }
            // resp is: {"name":"-KTryoFeoZ4Sv8CcPB_A"}
        }
    }

    override fun onBackPressed() { /* Intent intent = new Intent(PaymentActivity.this, JewelHome.class);
        startActivity(intent);*/
// finish();
// Toast.makeText(PaymentActivity.this, "No Permission to allow Front page", Toast.LENGTH_SHORT).show();
    }

    companion object {
        private val TAG = PaymentActivity::class.java.simpleName
    }
}