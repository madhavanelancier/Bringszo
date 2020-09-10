package com.elanciers.bringszo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Html
import android.util.AttributeSet
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.elanciers.bringszo.Common.Appconstands
import com.elanciers.bringszo.Common.Connection

import kotlinx.android.synthetic.main.activity_termsconditions.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Termsconditions : AppCompatActivity() {
    //lateinit var utils: Utils
    var progbar: Dialog? = null
    private var canvasLL: ImageView? = null
    var file: File? = null
    private var mSignature: signature? = null
    private var views: View? = null
    private var bitmap: Bitmap? = null
    private var btnSave: Button? = null
    private var clear: Button? = null
    val RequestPermissionCode = 7
    internal var Res_OTP = ""
    internal lateinit var byteArray: ByteArray
    var imagecode=""
    internal lateinit var fi: File
    val activity=this
    lateinit var pDialo : ProgressDialog
    var cloud_name=""
    var api_key=""
    var api_secret=""
    private val STROKE_WIDTH = 5f
    private val HALF_STROKE_WIDTH = STROKE_WIDTH / 2
    internal var DIRECTORY = Environment.getExternalStorageDirectory().path + "/Signature/"
    internal var pic_name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    internal var StoredPath = "$DIRECTORY$pic_name.png"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_termsconditions)

     /*   supportActionBar!!.title = "V3 Online TV"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)*/
      ///  utils = Utils(this)
       //actionBar.setTitle("Terms and conditions")

        imageButton.setOnClickListener {
            finish()
        }

        if(CheckingPermissionIsEnabledOrNot(this)){

        }
        else{
            RequestMultiplePermission(this)
        }

        mSignature = signature(applicationContext, null)
        mSignature!!.setBackgroundColor(Color.WHITE)

        file = File(DIRECTORY)
        if (!file!!.exists()) {
            file!!.mkdir()
        }
        loadprogress()
        GetInfoTask().execute()
       // stateload().execute()

    }

    fun toast(msg:String){
        val toast=Toast.makeText(applicationContext,msg,Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER,0,0)
        toast.show()
    }


    fun loadprogress() {
        progbar = Dialog(this)
        progbar!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progbar!!.getWindow().setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        progbar!!.setContentView(R.layout.load)
        progbar!!.setCancelable(false)
        progbar!!.show()
    }

    inner class GetInfoTask :
        AsyncTask<String?, Void?, String?>() {
        override fun onPreExecute() {
            //progbar.setVisibility(View.VISIBLE);
            Log.i("GetInfoTask", "started")
        }

        override fun doInBackground(vararg params: String?): String? {
            var result: String? = null
            val con =
                Connection()
            try {
                val jobj = JSONObject()
                jobj.put("uname", "id")

                Log.i(
                    "check Input",
                    Appconstands.terms + "    " + jobj.toString()
                )
                result = con.sendHttpPostjson2(Appconstands.terms, jobj, "")

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(resp: String?) {
            Log.i("tabresp", resp + "")
            progbar!!.dismiss()
            try {
                if (resp != null) {
                    val json = JSONObject(resp)
                    if (json.getString("Status") == "Success") {
                        val jarr = json.getJSONObject("Response")
                       // val terms=jarr.getString("terms")
                        val terms1=jarr.getString("content")

                        textView70.setText(Html.fromHtml("<p style=\"color:red\">"+terms1+"\n\n"))



                    }
                } else {
                    progbar!!.dismiss()
                }
            } catch (e: Exception) {
                progbar!!.dismiss()

                //retry!!.show()
                e.printStackTrace()
                // Toast.makeText(getApplicationContext(), "Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    inner class signature(context: Context, attrs: AttributeSet?) : View(context, attrs) {
        private val paint = Paint()
        private val path = Path()
        private var lastTouchX: Float = 0.toFloat()
        private var lastTouchY: Float = 0.toFloat()
        private val dirtyRect = RectF()

        init {
            paint.isAntiAlias = true
            paint.color = Color.BLACK
            paint.style = Paint.Style.STROKE
            paint.strokeJoin = Paint.Join.ROUND
            paint.strokeWidth = STROKE_WIDTH
        }

        @SuppressLint("WrongThread")
        fun save(v: View, StoredPath: String) {
            Log.v("log_tag", "Width: " + v.width)
            Log.v("log_tag", "Height: " + v.height)
            if (bitmap == null) {
                try {
                    bitmap = Bitmap.createBitmap(canvasLL!!.width, canvasLL!!.height, Bitmap.Config.RGB_565)

                } catch (e: Exception) {

                }

            }
            val canvas = Canvas(bitmap!!)
            try {
                // Output the file
                val mFileOutStream = FileOutputStream(StoredPath)
                v.draw(canvas)

                // Convert the output file to Image such as .png
                bitmap!!.compress(Bitmap.CompressFormat.PNG, 30, mFileOutStream)
                mFileOutStream.flush()
                mFileOutStream.close()

            } catch (e: Exception) {
                Log.v("log_tag", e.toString())
            }

        }

        fun clear() {
            path.reset()
            invalidate()

        }

        override fun onDraw(canvas: Canvas) {
            canvas.drawPath(path, paint)
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            val eventX = event.x
            val eventY = event.y

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    path.moveTo(eventX, eventY)
                    lastTouchX = eventX
                    lastTouchY = eventY
                    return true
                }

                MotionEvent.ACTION_MOVE,

                MotionEvent.ACTION_UP -> {

                    resetDirtyRect(eventX, eventY)
                    val historySize = event.historySize
                    for (i in 0 until historySize) {
                        val historicalX = event.getHistoricalX(i)
                        val historicalY = event.getHistoricalY(i)
                        expandDirtyRect(historicalX, historicalY)
                        path.lineTo(historicalX, historicalY)
                    }
                    path.lineTo(eventX, eventY)
                }

                else -> {
                    debug("Ignored touch event: $event")
                    return false
                }
            }

            invalidate((dirtyRect.left - HALF_STROKE_WIDTH).toInt(),
                (dirtyRect.top - HALF_STROKE_WIDTH).toInt(),
                (dirtyRect.right + HALF_STROKE_WIDTH).toInt(),
                (dirtyRect.bottom + HALF_STROKE_WIDTH).toInt())

            lastTouchX = eventX
            lastTouchY = eventY

            return true
        }

        private fun debug(string: String) {

            Log.v("log_tag", string)

        }

        private fun expandDirtyRect(historicalX: Float, historicalY: Float) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY
            }
        }

        private fun resetDirtyRect(eventX: Float, eventY: Float) {
            dirtyRect.left = Math.min(lastTouchX, eventX)
            dirtyRect.right = Math.max(lastTouchX, eventX)
            dirtyRect.top = Math.min(lastTouchY, eventY)
            dirtyRect.bottom = Math.max(lastTouchY, eventY)
        }

    }

    fun CheckingPermissionIsEnabledOrNot(context: Activity):Boolean {
        val ACCESS_NETWORK_STATEt = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        val ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        return ACCESS_NETWORK_STATEt == PackageManager.PERMISSION_GRANTED &&
                ACCESS_COARSE_LOCATION == PackageManager.PERMISSION_GRANTED

    }



    fun saveImage(myBitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            Environment.getExternalStorageDirectory().toString() + "IMAGE_DIRECTORY"
        )
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }

        try {
            val f = File(
                wallpaperDirectory, Calendar.getInstance()
                    .timeInMillis.toString() + ".jpg"
            )
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                activity!!,
                arrayOf(f.path),
                arrayOf("image/jpeg"), null
            )
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.absolutePath)

            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }
    private fun resize(image: Bitmap, maxWidth:Int, maxHeight:Int): Bitmap {
        if (maxHeight > 0 && maxWidth > 0)
        {
            val width = image.getWidth()
            val height = image.getHeight()
            val ratioBitmap = width.toFloat() / height.toFloat()
            val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
            var finalWidth = maxWidth
            var finalHeight = maxHeight
            if (ratioMax > ratioBitmap)
            {
                finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
            }
            else
            {
                finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
            }
            val images = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true)
            return images
        }
        else
        {
            return image
        }
    }


    private fun RequestMultiplePermission(context:Activity) {
        ActivityCompat.requestPermissions(context,arrayOf<String>(

            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE

        ), RequestPermissionCode
        )

    }
}