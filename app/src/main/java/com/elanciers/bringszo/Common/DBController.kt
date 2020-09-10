package com.elanciers.bringszo.Common


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.elanciers.bringszo.DataClass.*


import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date


class DBController(applicationcontext: Context) :
    SQLiteOpenHelper(applicationcontext, "domdox.db", null, 1) {
    override fun onCreate(database: SQLiteDatabase) {

        val cart =
                "CREATE TABLE IF NOT EXISTS Cart ( " +
                        "vendor_id INTEGER," +
                        "vendor_nm TEXT," +
                        "img TEXT," +
                        "cid TEXT," +
                        "cnm TEXT," +
                        "sid TEXT," +
                        "snm TEXT," +
                        "pid TEXT," +
                        "pnm TEXT," +
                        "qty TEXT," +
                        "opid TEXT," +
                        "opnm TEXT," +
                        "price TEXT)"
        database.execSQL(cart)

        val option =
                "CREATE TABLE IF NOT EXISTS Option ( " +
                        "pid TEXT," +
                        "opid TEXT," +
                        "opnm TEXT," +
                        "price TEXT)"
        database.execSQL(option)

        val location =
                "CREATE TABLE IF NOT EXISTS Location ( " +
                        "currentTimeMillis TEXT," +
                        "latitudeValue TEXT," +
                        "longitudeValue TEXT)"
        database.execSQL(location)

        val query3 = "CREATE TABLE IF NOT EXISTS Address (adrs_id INTEGER, adrs_type TEXT , adrs_title TEXT, address TEXT, adrs_house TEXT, adrs_landmark TEXT, adrs_latitude TEXT, adrs_longtitude TEXT)"
        database.execSQL(query3)

        val query5 =
            "CREATE TABLE IF NOT EXISTS Products (cart_id INTEGER PRIMARY KEY,pro_id TEXT, pro_name TEXT,pro_img TEXT, pro_qty TEXT, pro_nm TEXT, pro_price TEXT, pro_selpos TEXT)"
        database.execSQL(query5)
        /*val query2 =
            "CREATE TABLE IF NOT EXISTS Groupphoto (Cid TEXT PRIMARY KEY,encode TEXT, uri TEXT, path TEXT, sync INTEGER DEFAULT 0)"
        database.execSQL(query2)

        val query6 =
            "CREATE TABLE IF NOT EXISTS Feedback (Cid TEXT PRIMARY KEY,mid TEXT, reason TEXT, time TEXT, sync INTEGER DEFAULT 0)"
        database.execSQL(query6)

        val query7 =
            "CREATE TABLE IF NOT EXISTS verfication (vid INTEGER PRIMARY KEY, acode TEXT, gcode TEXT,selec TEXT,mname TEXT,mmob TEXT,mdob TEXT,mrationcard TEXT,mvoterid TEXT,maadhaar TEXT,mrelign TEXT,mcomunity TEXT,mlnamnt TEXT,nname TEXT,nmob TEXT,nrelation TEXT,ndob TEXT,nvoterid TEXT,naadhaar TEXT,nbusi TEXT,nyerofbusi TEXT,mhouse TEXT,mbirthplc TEXT,maddress TEXT,mcaddress TEXT)"
        database.execSQL(query7)

        val query8 =
            "CREATE TABLE IF NOT EXISTS Notification (Nid INTEGER PRIMARY KEY, Ntitle TEXT, Nmsg TEXT,Ndate TEXT,Nstatus TEXT)"
        database.execSQL(query8)*/
        Log.d(LOGCAT, "session created")
    }

    override fun onUpgrade(database: SQLiteDatabase, version_old: Int, current_version: Int) {

        val query = "DROP TABLE IF EXISTS Cart"
        database.execSQL(query)

        val query1 = "DROP TABLE IF EXISTS Option"
        database.execSQL(query1)

        val query3 = "DROP TABLE IF EXISTS Address"
        database.execSQL(query3)

        val query5 = "DROP TABLE IF EXISTS Products"
        database.execSQL(query5)

        /*val query2 = "DROP TABLE IF EXISTS GroupPhoto"
        database.execSQL(query2)*/
        onCreate(database)
    }

    fun checkItem1(pro_id: String, pro_nm: String): Boolean {
        val database = DatabaseManager.getInstance().openDatabase()
        val mCount = database!!.rawQuery("select count(*) from Products where pro_id=\"$pro_id\" AND pro_nm=\"$pro_nm\"", null)
        mCount.moveToFirst()
        val count = mCount.getInt(0)
        mCount.close()
        DatabaseManager.getInstance().closeDatabase()
        return if (count <= 0)
            false
        else
            true
    }
    fun AddressInsert(adrs:AddressData) : Int{
        try {
            val values = ContentValues()
            values.put("adrs_id",adrs.adrs_id)
            values.put("adrs_type",adrs.adrs_type)
            values.put("adrs_title",adrs.adrs_title)
            values.put("address",adrs.address)
            values.put("adrs_house",adrs.adrs_house)
            values.put("adrs_landmark",adrs.adrs_landmark)
            values.put("adrs_latitude",adrs.adrs_latitude)
            values.put("adrs_longtitude",adrs.adrs_longtitude)
            val database = DatabaseManager.getInstance().openDatabase()
            val cursor = database!!.rawQuery("SELECT * FROM Address WHERE adrs_id ='${adrs.adrs_id}'", null)
            if (cursor.moveToFirst()) {
                val d=database!!.update("Address", values, "adrs_id = ?", arrayOf(adrs.adrs_id))
                Log.d("Record  Already Exists", "Table is:Address : "+d)
                return d
            }else{
                val d= (database!!.insert("Address", null, values)).toInt()
                Log.d("New Record  ", "Table is:Address : "+d)
                return d
            }
        } catch (errorException: Exception) {
            Log.d("Exception occured", "Exception occured $errorException")
            return 0
        }
    }

    fun CartIn_Up(Cart:CartProduct) : Int{
        try {
            /*println("Cart.vendor_id : "+Cart.vendor_id)
            println("Cart.vendor_nm : "+Cart.vendor_nm)
            println("Cart.img : "+Cart.img)
            println("Cart.cid : "+Cart.cid)
            println("Cart.cnm : "+Cart.cnm)
            println("Cart.sid : "+Cart.sid)
            println("Cart.snm : "+Cart.snm)
            println("Cart.pid : "+Cart.pid)
            println("Cart.pnm : "+Cart.pnm)
            println("Cart.qty : "+Cart.qty)
            println("Cart.opid : "+Cart.opid)
            println("Cart.opnm : "+Cart.opnm)
            println("Cart.price : "+Cart.price)*/
            val values = ContentValues()
            values.put("vendor_id",Cart.vendor_id)
            values.put("vendor_nm",Cart.vendor_nm)
            values.put("img",Cart.img)
            values.put("cid",Cart.cid)
            values.put("cnm",Cart.cnm)
            values.put("sid",Cart.sid)
            values.put("snm",Cart.snm)
            values.put("pid",Cart.pid)
            values.put("pnm",Cart.pnm)
            values.put("qty",Cart.qty)
            values.put("opid",Cart.opid)
            values.put("opnm",Cart.opnm)
            values.put("price",Cart.price)
            if (Cart.qty!="0") {
                val database = DatabaseManager.getInstance().openDatabase()
                val cursor = database!!.rawQuery("SELECT * FROM Cart WHERE pid ='${Cart.pid}'AND opid='${Cart.opid}'", null)
                if (cursor.moveToFirst()) {
                    val values2 = ContentValues()
                    values2.put("vendor_id",Cart.vendor_id)
                    values2.put("vendor_nm",Cart.vendor_nm)
                    values2.put("img",Cart.img)
                    values2.put("pid",Cart.pid)
                    values2.put("pnm",Cart.pnm)
                    values2.put("qty",Cart.qty)
                    values2.put("opid",Cart.opid)
                    values2.put("opnm",Cart.opnm)
                    values2.put("price",Cart.price)
                    //val d=database!!.update("Cart", values, "vendor_id = ?", arrayOf(Cart.vendor_id))
                    val d = database!!.update("Cart", values2, "pid=\"${Cart.pid}\"AND opid=\"${Cart.opid}\"", null)
                    Log.d("Record  Already Exists", "Table is:Cart : " + d)
                    return d
                } else {
                    val d = (database!!.insert("Cart", null, values)).toInt()
                    Log.d("New Record  ", "Table is:Cart : " + d)
                    return d
                }
            }else{
                val d =delCart1(Cart.pid.toString(),Cart.opid.toString())
                return d
            }
        } catch (errorException: Exception) {
            Log.d("Exception occured", "Exception occured $errorException")
            return 0
        }
    }

    fun CartUp(Cart:CartProduct) : Int{
        try {
            val values = ContentValues()
            values.put("vendor_id",Cart.vendor_id)
            values.put("vendor_nm",Cart.vendor_nm)
            values.put("img",Cart.img)
            values.put("pid",Cart.pid)
            values.put("pnm",Cart.pnm)
            values.put("qty",Cart.qty)
            values.put("opid",Cart.opid)
            values.put("opnm",Cart.opnm)
            values.put("price",Cart.price)
            if (Cart.qty!="0") {
                val database = DatabaseManager.getInstance().openDatabase()
                //val d=database!!.update("Cart", values, "vendor_id = ?", arrayOf(Cart.vendor_id))
                val d = database!!.update("Cart", values, "pid=\"${Cart.pid}\"AND opid=\"${Cart.opid}\"", null)
                Log.d("Record  Already Exists", "Table is:Cart : " + d)
                return d
            }else{
                val d =delCart1(Cart.pid.toString(),Cart.opid.toString())
                return d
            }
        } catch (errorException: Exception) {
            Log.d("Exception occured", "Exception occured $errorException")
            return 0
        }
    }
    fun qty(pid : String) : String {
        val selectQuery = "SELECT SUM(qty) FROM Cart WHERE pid ='${pid}'"//AND opid='${opid}'
        val database = DatabaseManager.getInstance().openReadDatabase()
        var total = "0"
        val c = database!!.rawQuery(selectQuery, null)
        if (c.moveToFirst()) {
            //do {
                total = (c.getInt(0)).toString()
            //} while (c.moveToNext())
        }
        c.close()
        DatabaseManager.getInstance().closeDatabase()
        return total
    }

    fun varients(pid : String) : String {
        val selectQuery = "SELECT COUNT(pid) FROM Cart WHERE pid ='${pid}'"//AND opid='${opid}'
        val database = DatabaseManager.getInstance().openReadDatabase()
        var total = ""
        val c = database!!.rawQuery(selectQuery, null)
        if (c.moveToFirst()) {
            //do {
            total = (c.getInt(0)).toString()
            //} while (c.moveToNext())
        }
        c.close()
        DatabaseManager.getInstance().closeDatabase()
        return total
    }

    fun optqty(pid : String,opid:String) : String {
        val selectQuery = "SELECT qty FROM Cart WHERE pid ='${pid}'AND opid='${opid}'"//AND opid='${opid}'
        val database = DatabaseManager.getInstance().openReadDatabase()
        var total = "0"
        val c = database!!.rawQuery(selectQuery, null)
        if (c.moveToFirst()) {
            //do {
                total = (c.getInt(0)).toString()
            //} while (c.moveToNext())
        }
        c.close()
        DatabaseManager.getInstance().closeDatabase()
        return total
    }

    fun more(pid : String) : Int {
        val selectQuery = "SELECT qty FROM Cart WHERE pid ='${pid}'"//AND opid='${opid}'
        val database = DatabaseManager.getInstance().openReadDatabase()
        var total = 0
        val c = database!!.rawQuery(selectQuery, null)
        if (c.moveToFirst()) {
            do {
                total = total+1
            } while (c.moveToNext())
        }
        c.close()
        DatabaseManager.getInstance().closeDatabase()
        return total
    }

    fun LastItm(pid : String) : CartProduct {
        val selectQuery = "SELECT * FROM Cart WHERE pid ='${pid}'"//AND opid='${opid}'
        val database = DatabaseManager.getInstance().openReadDatabase()
        var cart = CartProduct()
        val c = database!!.rawQuery(selectQuery, null)
        if (c.moveToLast()) {
            //do {
                cart = CartProduct()
                cart.vendor_id = c.getString(c.getColumnIndex("vendor_id"))
                cart.vendor_nm = c.getString(c.getColumnIndex("vendor_nm"))
                cart.img = c.getString(c.getColumnIndex("img"))
                cart.cid = c.getString(c.getColumnIndex("cid"))
                cart.cnm = c.getString(c.getColumnIndex("cnm"))
                cart.sid = c.getString(c.getColumnIndex("sid"))
                cart.snm = c.getString(c.getColumnIndex("snm"))
                cart.pid = c.getString(c.getColumnIndex("pid"))
                cart.pnm = c.getString(c.getColumnIndex("pnm"))
                cart.qty = c.getString(c.getColumnIndex("qty"))
                cart.opid = c.getString(c.getColumnIndex("opid"))
                cart.opnm = c.getString(c.getColumnIndex("opnm"))
                cart.price = c.getString(c.getColumnIndex("price"))
            //} while (c.moveToNext())
        }
        c.close()
        DatabaseManager.getInstance().closeDatabase()
        return cart
    }

    val vendor_id: String
        get() {
            val selectQuery = "SELECT vendor_id FROM Cart"
            val database = DatabaseManager.getInstance().openReadDatabase()
            var total =""
            val c = database!!.rawQuery(selectQuery, null)
            if (c.moveToFirst()) {
                total = (c.getString(0))
            }
            c.close()
            DatabaseManager.getInstance().closeDatabase()
            return total
        }

    val vendor_nm: String
        get() {
            val selectQuery = "SELECT vendor_nm FROM Cart"
            val database = DatabaseManager.getInstance().openReadDatabase()
            var total =""
            val c = database!!.rawQuery(selectQuery, null)
            if (c.moveToFirst()) {
                total = (c.getString(0))
            }
            c.close()
            DatabaseManager.getInstance().closeDatabase()
            return total
        }

    val vendor_img: String
        get() {
            val selectQuery = "SELECT img FROM Cart"
            val database = DatabaseManager.getInstance().openReadDatabase()
            var total =""
            val c = database!!.rawQuery(selectQuery, null)
            if (c.moveToFirst()) {
                total = (c.getString(0))
            }
            c.close()
            DatabaseManager.getInstance().closeDatabase()
            return total
        }

    val cartTotal: Double
        get() {
            val selectQuery = "SELECT price, qty FROM Cart"
            val database = DatabaseManager.getInstance().openReadDatabase()
            var total = 0.0
            val c = database!!.rawQuery(selectQuery, null)
            if (c.moveToFirst()) {
                do {
                    total += java.lang.Double.parseDouble(c.getString(0)) * c.getString(1).toInt()
                } while (c.moveToNext())
            }
            c.close()
            DatabaseManager.getInstance().closeDatabase()
            return total
        }

    val cartItem: Int
        get() {
            val selectQuery = "SELECT SUM(qty) FROM Cart"
            val database = DatabaseManager.getInstance().openReadDatabase()
            var total = 0
            val c = database!!.rawQuery(selectQuery, null)
            if (c.moveToFirst()) {
                total = c.getInt(0)
            }
            c.close()
            DatabaseManager.getInstance().closeDatabase()
            return total
        }


    fun Total() : Int{
        try {
            var d=0
            val database = DatabaseManager.getInstance().openDatabase()
            val cursor = database!!.rawQuery("SELECT SUM(price) FROM Cart", null)
            if (cursor.moveToFirst()) {
                d = cursor.getInt(0)
                Log.d("Total", "Table is:Cart : "+d)
                return d
            }else{
                return d
            }
        } catch (errorException: Exception) {
            Log.d("Exception occured", "Exception occured $errorException")
            return 0
        }
    }

    fun CartList() : ArrayList<CartProduct> {
        val selectQuery = "SELECT * FROM Cart"//AND opid='${opid}'
        val database = DatabaseManager.getInstance().openReadDatabase()
        val cartlist = ArrayList<CartProduct>()
        val c = database!!.rawQuery(selectQuery, null)
        if (c.moveToFirst()) {
            do {
                val cart = CartProduct()
                cart.vendor_id = c.getString(c.getColumnIndex("vendor_id"))
                cart.vendor_nm = c.getString(c.getColumnIndex("vendor_nm"))
                cart.img = c.getString(c.getColumnIndex("img"))
                cart.cid = c.getString(c.getColumnIndex("cid"))
                cart.cnm = c.getString(c.getColumnIndex("cnm"))
                cart.sid = c.getString(c.getColumnIndex("sid"))
                cart.snm = c.getString(c.getColumnIndex("snm"))
                cart.pid = c.getString(c.getColumnIndex("pid"))
                cart.pnm = c.getString(c.getColumnIndex("pnm"))
                cart.qty = c.getString(c.getColumnIndex("qty"))
                cart.opid = c.getString(c.getColumnIndex("opid"))
                cart.opnm = c.getString(c.getColumnIndex("opnm"))
                cart.price = c.getString(c.getColumnIndex("price"))
                cartlist.add(cart)
            } while (c.moveToNext())
        }
        c.close()
        DatabaseManager.getInstance().closeDatabase()
        return cartlist
    }

    fun AddressList() : ArrayList<AddressData> {
        val selectQuery = "SELECT * FROM Address"//AND opid='${opid}'
        val database = DatabaseManager.getInstance().openReadDatabase()
        val adrslist = ArrayList<AddressData>()
        val c = database!!.rawQuery(selectQuery, null)
        if (c.moveToFirst()) {
            do {
                val cart = AddressData()
                cart.adrs_id = c.getString(c.getColumnIndex("adrs_id"))
                cart.adrs_type = c.getString(c.getColumnIndex("adrs_type"))
                cart.adrs_title = c.getString(c.getColumnIndex("adrs_title"))
                cart.address = c.getString(c.getColumnIndex("address"))
                cart.adrs_house = c.getString(c.getColumnIndex("adrs_house"))
                cart.adrs_landmark = c.getString(c.getColumnIndex("adrs_landmark"))
                cart.adrs_latitude = c.getString(c.getColumnIndex("adrs_latitude"))
                cart.adrs_longtitude = c.getString(c.getColumnIndex("adrs_longtitude"))
                adrslist.add(cart)
            } while (c.moveToNext())
        }
        c.close()
        DatabaseManager.getInstance().closeDatabase()
        return adrslist
    }

    fun Addressget(id : String) : AddressData {
        val selectQuery = "SELECT * FROM Address WHERE adrs_id ='${id}'"//AND opid='${opid}'
        val database = DatabaseManager.getInstance().openReadDatabase()
        val adrslist = AddressData()
        val c = database!!.rawQuery(selectQuery, null)
        if (c.moveToFirst()) {
            //do {
                adrslist.adrs_id = c.getString(c.getColumnIndex("adrs_id"))
                adrslist.adrs_type = c.getString(c.getColumnIndex("adrs_type"))
                adrslist.adrs_title = c.getString(c.getColumnIndex("adrs_title"))
                adrslist.address = c.getString(c.getColumnIndex("address"))
                adrslist.adrs_house = c.getString(c.getColumnIndex("adrs_house"))
                adrslist.adrs_landmark = c.getString(c.getColumnIndex("adrs_landmark"))
                adrslist.adrs_latitude = c.getString(c.getColumnIndex("adrs_latitude"))
                adrslist.adrs_longtitude = c.getString(c.getColumnIndex("adrs_longtitude"))
            //} while (c.moveToNext())
        }
        c.close()
        DatabaseManager.getInstance().closeDatabase()
        return adrslist
    }
    fun AddressType(type : String) : Boolean {
        try {
            val selectQuery = "SELECT * FROM Address WHERE adrs_type ='${type}'"//AND opid='${opid}'
            val database = DatabaseManager.getInstance().openReadDatabase()
            val adrslist = AddressData()
            val c = database!!.rawQuery(selectQuery, null)
            if (c.moveToFirst()) {
                return false
            }
            c.close()
            DatabaseManager.getInstance().closeDatabase()
            return true
        }catch (e:Exception){
            return true
        }
    }
    fun delAddress(adrs_id: String): Int{
        Log.i("delete Address", " started")
        val database = DatabaseManager.getInstance().openDatabase()
        val d= database!!.delete("Address", "adrs_id=\"$adrs_id\"", null)
        DatabaseManager.getInstance().closeDatabase()
        Log.i("delete Address", " ended")
        return d
    }

    fun delCart1(pid: String, opid: String): Int{
        Log.i("delete Cart", " started")
        val database = DatabaseManager.getInstance().openDatabase()
        val d= database!!.delete("Cart", "pid=\"$pid\" AND opid=\"$opid\"", null)
        DatabaseManager.getInstance().closeDatabase()
        Log.i("delete Cart", " ended")
        return d
    }
    fun upCart1(pro_id: String, pro_price: String, count: Int, pro_nm: String): Int {
        if (count == 0) {
            delCart1(pro_id, pro_nm)
            return 0
        } else {
            val database = DatabaseManager.getInstance().openDatabase()
            val values = ContentValues()
            values.put("pro_id", pro_id)
            values.put("pro_price", pro_price)
            Log.i("update database ", " ended")
            val resp = database!!.update("Product", values, "pro_id=\"$pro_id\" AND pro_nm=\"$pro_nm\"", null)
            Log.i("Qurey", database.update("Product", values, "pro_id=\"$pro_id\"AND pro_nm=\"$pro_nm\"", null).toString() + "")
            DatabaseManager.getInstance().closeDatabase()
            return resp
        }
        /*Log.i("insert cart", " started");
        SQLiteDatabase database = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put("sku", sku);
        values.put("price", price);
        values.put("cback", cback);
        database.update("cart", values,"",null);
        DatabaseManager.getInstance().closeDatabase();
        Log.i("insert cart", " ended");*/

    }

    fun updateCart1(count: Int, pro_id: String, pro_nm: String): Int {
        if (count == 0) {
            delCart1(pro_id, pro_nm)
            return 0
        } else {
            val database = DatabaseManager.getInstance().openDatabase()
            val values = ContentValues()
            values.put("count", count)
            Log.i("update database ", " ended")
            val resp = database!!.update("Products", values, "pro_id=\"$pro_id\" AND pro_nm=\"$pro_nm\"", null)
            Log.i("Qurey", database.update("Products", values, "pro_id=\"$pro_id\"AND pro_nm=\"$pro_nm\"", null).toString() + "")
            DatabaseManager.getInstance().closeDatabase()
            return resp
        }
    }

    fun dropHoleCart(){
        dropCart()
        dropOption()
    }

    fun dropCart() {
        val database = DatabaseManager.getInstance().openDatabase();
        val query = "DROP TABLE IF EXISTS Cart";
        database!!.execSQL(query);
        val cart =
                "CREATE TABLE IF NOT EXISTS Cart ( " +
                        "vendor_id INTEGER," +
                        "vendor_nm TEXT," +
                        "img TEXT," +
                        "cid TEXT," +
                        "cnm TEXT," +
                        "sid TEXT," +
                        "snm TEXT," +
                        "pid TEXT," +
                        "pnm TEXT," +
                        "qty TEXT," +
                        "opid TEXT," +
                        "opnm TEXT," +
                        "price TEXT)"
        database.execSQL(cart)
        DatabaseManager.getInstance().closeDatabase();
    }

    fun dropOption() {
        val database = DatabaseManager.getInstance().openDatabase();
        val query = "DROP TABLE IF EXISTS Option";
        database!!.execSQL(query);
        val option =
                "CREATE TABLE IF NOT EXISTS Option ( " +
                        "pid TEXT," +
                        "opid TEXT," +
                        "opnm TEXT," +
                        "price TEXT)"
        database.execSQL(option)
        DatabaseManager.getInstance().closeDatabase();
    }

    fun dropLocation() {
        val database = DatabaseManager.getInstance().openDatabase();
        val query = "DROP TABLE IF EXISTS Location";
        database!!.execSQL(query);
        val location =
            "CREATE TABLE IF NOT EXISTS Location ( " +
                    "currentTimeMillis TEXT," +
                    "latitudeValue TEXT," +
                    "longitudeValue TEXT)"
        database.execSQL(location)
        DatabaseManager.getInstance().closeDatabase();
    }

    fun dropAddress() {
        val database = DatabaseManager.getInstance().openDatabase();
        val query = "DROP TABLE IF EXISTS Address";
        database!!.execSQL(query);
        val query3 = "CREATE TABLE IF NOT EXISTS Address (adrs_id INTEGER, adrs_type TEXT , adrs_title TEXT, address TEXT, adrs_house TEXT, adrs_landmark TEXT, adrs_latitude TEXT, adrs_longtitude TEXT)"
        database.execSQL(query3)
        DatabaseManager.getInstance().closeDatabase();
    }

    fun dropProducts() {
        val database = DatabaseManager.getInstance().openDatabase();
        val query = "DROP TABLE IF EXISTS Products";
        database!!.execSQL(query);
        val query5 =
            "CREATE TABLE IF NOT EXISTS Products (cart_id INTEGER PRIMARY KEY,pro_id TEXT, pro_name TEXT,pro_img TEXT, pro_qty TEXT, pro_nm TEXT, pro_price TEXT, pro_selpos TEXT)"
        database.execSQL(query5)
        DatabaseManager.getInstance().closeDatabase();
    }

    fun dropFeedback() {
        val database = DatabaseManager.getInstance().openDatabase();
        val query = "DROP TABLE IF EXISTS Feedback";
        database!!.execSQL(query);
        val query6 =
            "CREATE TABLE IF NOT EXISTS Feedback (Cid TEXT PRIMARY KEY,mid TEXT, reason TEXT, time TEXT, sync INTEGER DEFAULT 0)"
        database.execSQL(query6)
        DatabaseManager.getInstance().closeDatabase();
    }
    fun dropNotification() {
        val database = DatabaseManager.getInstance().openDatabase();
        val query = "DROP TABLE IF EXISTS Notification";
        database!!.execSQL(query);
        val query8 =
            "CREATE TABLE IF NOT EXISTS Notification (Nid INTEGER PRIMARY KEY, Ntitle TEXT, Nmsg TEXT,Ndate TEXT,Nstatus TEXT)"
        database.execSQL(query8)
        DatabaseManager.getInstance().closeDatabase();
    }

    /*val consumerMail: String
        get() {
            val selectQuery = "SELECT  o.mail FROM login o limit 1"
            var `val` = ""
            val database = DatabaseManager.getInstance().openReadDatabase()
            val cursor = database.rawQuery(selectQuery, arrayOf())
            try {
                while (cursor!!.moveToNext()) {
                    `val` = cursor.getString(0)
                }
            } finally {
                cursor?.close()
            }
            DatabaseManager.getInstance().closeDatabase()
            return `val`

        }*/

    val consumerAddress: String
        get() {
            val selectQuery = "SELECT  o.address FROM login o limit 1"
            var `val` = ""
            val database = DatabaseManager.getInstance().openReadDatabase()
            val cursor = database!!.rawQuery(selectQuery, arrayOf())
            try {
                while (cursor!!.moveToNext()) {
                    `val` = cursor.getString(0)
                }
            } finally {
                cursor?.close()
            }
            DatabaseManager.getInstance().closeDatabase()
            return `val`

        }

    val consumerPin: String
        get() {
            val selectQuery = "SELECT  o.pin FROM login o limit 1"
            var `val` = ""
            val database = DatabaseManager.getInstance().openReadDatabase()
            val cursor = database!!.rawQuery(selectQuery, arrayOf())
            try {
                while (cursor!!.moveToNext()) {
                    `val` = cursor.getString(0)
                }
            } finally {
                cursor?.close()
            }
            DatabaseManager.getInstance().closeDatabase()
            return `val`

        }

    val consumerMobile: String
        get() {
            val selectQuery = "SELECT  o.mobile FROM login o limit 1"
            var `val` = ""
            val database = DatabaseManager.getInstance().openReadDatabase()
            val cursor = database!!.rawQuery(selectQuery, arrayOf())
            try {
                while (cursor!!.moveToNext()) {
                    `val` = cursor.getString(0)
                }
            } finally {
                cursor?.close()
            }
            DatabaseManager.getInstance().closeDatabase()
            return `val`

        }

    /* public ArrayList<ProductBo> getCartList() {
        String selectQuery = "SELECT sku, qty, itemName, price, count, imgurl, subcatid,department FROM cart";
        ArrayList<ProductBo> val = new ArrayList<ProductBo>();
        SQLiteDatabase db = DatabaseManager.getInstance().openReadDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                ProductBo bo = new ProductBo();
                bo.setProid(c.getString(0));
                bo.setProqty(c.getString(1));
                bo.setProname(c.getString(2));
                bo.setProamt(c.getString(3));
                bo.setProcount(c.getInt(4));
                bo.setProimg(c.getString(5));
                bo.setSubcatid(c.getString(6));
                bo.setDepartment(c.getString(7));

                val.add(bo);
            } while (c.moveToNext());
        }
        c.close();
        DatabaseManager.getInstance().closeDatabase();
        return val;
    }*/

   /* val cartList1: ArrayList<ProductBo>
        get() {
            val selectQuery =
                "SELECT sku, qty, itemName, price, count, imgurl, subcatid,department,uom FROM cart"
            val `val` = ArrayList<ProductBo>()
            val db = DatabaseManager.getInstance().openReadDatabase()
            val c = db.rawQuery(selectQuery, null)
            if (c.moveToFirst()) {
                do {
                    val bo = ProductBo()
                    bo.setProid(c.getString(0))
                    bo.setProqty(c.getString(1))
                    bo.setProname(c.getString(2))
                    bo.setProamt(c.getString(3))
                    bo.setProcount(c.getInt(4))
                    bo.setProimg(c.getString(5))
                    bo.setSubcatid(c.getString(6))
                    bo.setDepartment(c.getString(7))
                    bo.setUom(c.getString(8))

                    `val`.add(bo)
                } while (c.moveToNext())
            }
            c.close()
            DatabaseManager.getInstance().closeDatabase()
            return `val`
        }*/

    /* public ArrayList<ProductBo> getCartList() {
            String selectQuery = "SELECT sku, qty, itemName, price, count, imgurl FROM cart";
		    ArrayList<ProductBo> val = new ArrayList<ProductBo>();
		    SQLiteDatabase db = DatabaseManager.getInstance().openReadDatabase();
			Cursor c = db.rawQuery(selectQuery, null);
			if (c.moveToFirst()) {
				do {
					ProductBo bo = new ProductBo();
					bo.setProid(c.getString(0));
					bo.setProqty(c.getString(1));
					bo.setProname(c.getString(2));
					bo.setProamt(c.getString(3));
					bo.setProcount(c.getInt(4));
					bo.setProimg(c.getString(5));

					val.add(bo);
				} while (c.moveToNext());
			}
		    c.close();
		    DatabaseManager.getInstance().closeDatabase();
		    return val;
	  }*/

    /*val categoryList: ArrayList<CategoryBo>
        get() {
            val selectQuery = "SELECT catId, catName, media FROM categoryL1"
            val `val` = ArrayList<CategoryBo>()
            val db = DatabaseManager.getInstance().openReadDatabase()
            val c = db.rawQuery(selectQuery, null)
            if (c.moveToFirst()) {
                do {
                    val bo = CategoryBo()
                    bo.setCatid(c.getString(0))
                    bo.setCatname(c.getString(1))
                    bo.setCatimg(c.getString(2))
                    `val`.add(bo)
                } while (c.moveToNext())
            }
            c.close()
            DatabaseManager.getInstance().closeDatabase()
            return `val`
        }*/



    val cateLevelCount: Int
        get() {
            val database = DatabaseManager.getInstance().openReadDatabase()
            val mCount = database!!.rawQuery("select count(*) from categoryL1", null)
            mCount.moveToFirst()
            val count = mCount.getInt(0)
            mCount.close()
            DatabaseManager.getInstance().closeDatabase()
            return count
        }

    /*public void dropCart() {
        SQLiteDatabase database = DatabaseManager.getInstance().openDatabase();
        String query = "DROP TABLE IF EXISTS cart";
        database!!.execSQL(query);
        String query1 = "CREATE TABLE IF NOT EXISTS cart ( id INTEGER PRIMARY KEY, sku TEXT, qty TEXT, itemName TEXT, price INT,  count INT, imgurl TEXT, subcatid TEXT, department TEXT)";
        database!!.execSQL(query1);
        DatabaseManager.getInstance().closeDatabase();
    }*/

    val count: Int
        get() {
            val database = DatabaseManager.getInstance().openReadDatabase()
            val mCount = database!!.rawQuery("select count(*) from cart", null)
            mCount.moveToFirst()
            val count = mCount.getInt(0)
            mCount.close()
            DatabaseManager.getInstance().closeDatabase()
            return count
        }

    val consumerCount: Int
        get() {
            val database = DatabaseManager.getInstance().openReadDatabase()
            val mCount = database!!.rawQuery("select count(*) from login", null)
            mCount.moveToFirst()
            val count = mCount.getInt(0)
            mCount.close()
            DatabaseManager.getInstance().closeDatabase()
            return count
        }

    val consumerSession: String?
        get() {
            val selectQuery = "SELECT  o.token, o.secret FROM login o limit 1"
            var `val`: String? = null
            val database = DatabaseManager.getInstance().openReadDatabase()
            val cursor = database!!.rawQuery(selectQuery, arrayOf())
            try {
                while (cursor!!.moveToNext()) {
                    `val` = cursor.getString(0) + "|" + cursor.getString(1)
                }
            } finally {
                cursor?.close()
            }
            DatabaseManager.getInstance().closeDatabase()
            return `val`

        }

    val usermobile: String?
        get() {
            val selectQuery = "SELECT  o.mobile FROM login o limit 1"
            var `val`: String? = null
            val database = DatabaseManager.getInstance().openReadDatabase()
            val cursor = database!!.rawQuery(selectQuery, arrayOf())
            try {
                while (cursor!!.moveToNext()) {
                    `val` = cursor.getString(0)
                }
            } finally {
                cursor?.close()
            }
            DatabaseManager.getInstance().closeDatabase()
            return `val`

        }


    val consumerSecret: String?
        get() {
            val selectQuery = "SELECT  o.secret FROM login o limit 1"
            var `val`: String? = null
            val database = DatabaseManager.getInstance().openReadDatabase()
            val cursor = database!!.rawQuery(selectQuery, arrayOf())
            try {
                while (cursor!!.moveToNext()) {
                    `val` = cursor.getString(0)
                }
            } finally {
                cursor?.close()
            }
            DatabaseManager.getInstance().closeDatabase()
            return `val`

        }

    val consumerCart: Int
        get() {
            val selectQuery = "SELECT  o.cart FROM login o limit 1"
            var `val` = 0
            val database = DatabaseManager.getInstance().openReadDatabase()
            val cursor = database!!.rawQuery(selectQuery, arrayOf())
            try {
                while (cursor!!.moveToNext()) {
                    `val` = cursor.getInt(0)
                }
            } finally {
                cursor?.close()
            }
            DatabaseManager.getInstance().closeDatabase()
            return `val`

        }

    val consumerId: Int
        get() {
            val selectQuery = "SELECT  o.consumerId FROM login o limit 1"
            var `val` = 0
            val database = DatabaseManager.getInstance().openReadDatabase()
            val cursor = database!!.rawQuery(selectQuery, arrayOf())
            try {
                while (cursor!!.moveToNext()) {
                    `val` = cursor.getInt(0)
                }
            } finally {
                cursor?.close()
            }
            DatabaseManager.getInstance().closeDatabase()
            return `val`

        }

    val mode: Int
        get() {
            val selectQuery = "SELECT  o.mode FROM login o limit 1"
            var `val` = 0
            val database = DatabaseManager.getInstance().openReadDatabase()
            val cursor = database!!.rawQuery(selectQuery, arrayOf())
            try {
                while (cursor!!.moveToNext()) {
                    `val` = cursor.getInt(0)
                }
            } finally {
                cursor?.close()
            }
            DatabaseManager.getInstance().closeDatabase()
            return `val`

        }


    val consumerName: String
        get() {
            val selectQuery = "SELECT  o.name FROM login o limit 1"
            var `val` = ""
            val database = DatabaseManager.getInstance().openReadDatabase()
            val cursor = database!!.rawQuery(selectQuery, arrayOf())
            try {
                while (cursor!!.moveToNext()) {
                    `val` = cursor.getString(0)
                }
            } finally {
                cursor?.close()
            }
            DatabaseManager.getInstance().closeDatabase()
            return `val`

        }

    val storeTime: Boolean
        get() {
            val selectQuery = "SELECT o.datereg FROM login o limit 1"
            var `val` = false
            val database = DatabaseManager.getInstance().openReadDatabase()
            val cursor = database!!.rawQuery(selectQuery, arrayOf())
            try {
                while (cursor!!.moveToNext()) {
                    val val1 = cursor.getString(0)
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:MM:SS")
                    val date = sdf.parse(val1)
                    var secs = (Date().time - date.time) / 1000
                    val hours = (secs / 3600).toInt()
                    secs = secs % 3600
                    val mins = (secs / 60).toInt()
                    if (mins > 2)
                        `val` = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor?.close()
            }
            DatabaseManager.getInstance().closeDatabase()
            return `val`

        }



    /* public boolean checkItem(String sku) {
        SQLiteDatabase database = DatabaseManager.getInstance().openDatabase();
        Cursor mCount = database!!.rawQuery("select count(*) from cart where sku=\"" + sku + "\"", null);
        Log.i("Query", "select count(*) from cart where sku=\"" + sku + "\"AND firstname`=\"" + sku + "\"");
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        mCount.close();
        DatabaseManager.getInstance().closeDatabase();
        if (count <= 0)
            return false;
        else
            return true;
    }*/

    /*public int updateCart(int count, String sku) {
        if (count == 0) {
            delCart(sku);
            return 0;
        } else {
            SQLiteDatabase database = DatabaseManager.getInstance().openDatabase();
            ContentValues values = new ContentValues();
            values.put("count", count);
            Log.i("update database ", " ended");
            int resp = database!!.update("cart", values, "sku=\"" + sku + "\"", null);
            Log.i("Qurey", database!!.update("cart", values, "sku=\"" + sku + "\"", null) + "");
            DatabaseManager.getInstance().closeDatabase();
            return resp;
        }
    }*/


    fun getcartid(id: Int): Int {
        val db = this.readableDatabase
        val cursor = db.query("cart", null, " prodid=?", arrayOf(id.toString()), null, null, null)
        if (cursor.count == 0) {
            cursor.close()
            return 0
        }
        cursor.moveToFirst()
        val id1 = cursor.getInt(cursor.getColumnIndex("prodid"))
        cursor.close()
        return id1
    }

    /*public void delCart(String sku) {
        Log.i("delete cart", " started");
        SQLiteDatabase database = DatabaseManager.getInstance().openDatabase();
        database!!.delete("cart", "sku=\"" + sku + "\"", null);
        DatabaseManager.getInstance().closeDatabase();
        Log.i("delete cart", " ended");
    }*/


    /* public void insertCart(String sku, String qty, String itemName, String price, int count, String url, String subcatid, String department) {

        Log.i("insert cart", " started");
        SQLiteDatabase database = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put("sku", sku);
        values.put("qty", qty);
        values.put("itemName", itemName);
        values.put("price", price);
        values.put("count", count);
        values.put("imgurl", url);
        values.put("subcatid", subcatid);
        values.put("department", department);
        database!!.insert("cart", null, values);
        DatabaseManager.getInstance().closeDatabase();
        Log.i("insert cart", " ended");

    }*/

    fun insertCart1(
        sku: String,
        qty: String,
        itemName: String,
        price: String,
        count: Int,
        url: String,
        subcatid: String,
        department: String,
        uom: String
    ) {

        Log.i("insert cart", " started")
        val database = DatabaseManager.getInstance().openDatabase()
        val values = ContentValues()
        values.put("sku", sku)
        values.put("qty", qty)
        values.put("itemName", itemName)
        values.put("price", price)
        values.put("count", count)
        values.put("imgurl", url)
        values.put("subcatid", subcatid)
        values.put("department", department)
        values.put("uom", uom)
        database!!.insert("cart", null, values)
        DatabaseManager.getInstance().closeDatabase()
        Log.i("insert cart", " ended")

    }

    fun insertFavourite(pid: String, pname: String, image: String, fv: String) {

        Log.i("insert Favourite", " started")
        val database = DatabaseManager.getInstance().openDatabase()
        val values = ContentValues()
        values.put("pid", pid)
        values.put("pname", pname)
        values.put("image", image)
        values.put("fv", fv)
        database!!.insert("favourite", null, values)
        DatabaseManager.getInstance().closeDatabase()
        Log.i("insert Favourite", " ended")

    }

    fun checkIfRecordExist(COL_2: String): Boolean {
        try {
            val db = this.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM favourite WHERE pid ='$COL_2'", null)
            if (cursor.moveToFirst()) {
                db.close()
                Log.d("Record  Already Exists", "Table is:favourite ColumnName:$COL_2")
                return true//record Exists

            }
            Log.d("New Record  ", "Table is:favourite ColumnName:$COL_2 Column Value:$COL_2")
            db.close()
        } catch (errorException: Exception) {
            Log.d("Exception occured", "Exception occured $errorException")
            // db.close();
        }

        return false
    }


    fun updateFavourite(pid: String, pname: String, image: String, fv: String): Boolean {
        val database = DatabaseManager.getInstance().openDatabase()
        val contentValues = ContentValues()
        contentValues.put("pid", pid)
        contentValues.put("pname", pname)
        contentValues.put("image", image)
        contentValues.put("fv", fv)
        database!!.update("favourite", contentValues, "pid = ?", arrayOf(pid))
        return true
    }

    fun getFav(id: String): String {
        val db = this.readableDatabase
        val cursor = db.query("favourite", null, " pid=?", arrayOf(id), null, null, null)
        if (cursor.count == 0) {
            cursor.close()
            return ""
        }
        cursor.moveToFirst()
        val id1 = cursor.getString(cursor.getColumnIndex("fv"))
        cursor.close()
        return id1
    }

    fun insertConsumer(
        id: Int,
        name: String,
        mail: String,
        mobile: String,
        address: String,
        pin: String
    ) {
        Log.i("insert login", " started$address     $pin")
        val database = DatabaseManager.getInstance().openDatabase()
        val values = ContentValues()
        values.put("consumerId", id)
        values.put("name", name)
        values.put("mail", mail)
        values.put("mobile", mobile)
        values.put("address", address)
        values.put("pin", pin)

        database!!.insert("login", null, values)
        DatabaseManager.getInstance().closeDatabase()
        Log.i("insert login", " ended")

    }

    fun insertL1category(id: String, name: String, media: String) {

        Log.i("insert categoryL1", " started")
        val database = DatabaseManager.getInstance().openDatabase()
        val values = ContentValues()
        values.put("catId", id)
        values.put("catName", name)
        values.put("media", media)
        database!!.insert("categoryL1", null, values)
        DatabaseManager.getInstance().closeDatabase()
        Log.i("insert categoryL1", " ended")

    }

    fun getProductCount(proid: String): String {
        val selectQuery = "SELECT count FROM cart where sku=\'$proid\'"
        val database = DatabaseManager.getInstance().openReadDatabase()
        var total = "0"
        val c = database!!.rawQuery(selectQuery, null)
        if (c.moveToFirst()) {
            do {
                total += c.getInt(0).toString() + ""
            } while (c.moveToNext())
        }
        c.close()
        DatabaseManager.getInstance().closeDatabase()
        return total
    }

    fun getCateCartTotal(cateId: String): Double {
        val selectQuery = "SELECT price, count FROM cart where outletId=$cateId"
        val database = DatabaseManager.getInstance().openReadDatabase()
        var total = 0.0
        val c = database!!.rawQuery(selectQuery, null)
        if (c.moveToFirst()) {
            do {
                total += java.lang.Double.parseDouble(c.getString(0)) * c.getInt(1)
            } while (c.moveToNext())
        }
        c.close()
        DatabaseManager.getInstance().closeDatabase()
        return total
    }

    /*public int getCateCount(String cateId) {
        int count = 0;
        String selectQuery = "SELECT count FROM cart WHERE sku=\"" + cateId + "\"";
        SQLiteDatabase database = DatabaseManager.getInstance().openReadDatabase();
        Cursor mCount = database!!.rawQuery(selectQuery, null);

        if (mCount.moveToFirst()) {
            count = mCount.getInt(0);

        }

        mCount.close();
        DatabaseManager.getInstance().closeDatabase();
        return count;
    }*/

    fun getCateCount1(cateId: String, uom: String): Int {
        var count = 0
        val selectQuery = "SELECT count FROM cart WHERE sku=\"$cateId\"AND uom=\"$uom\""
        val database = DatabaseManager.getInstance().openReadDatabase()
        val mCount = database!!.rawQuery(selectQuery, null)

        if (mCount.moveToFirst()) {
            count = mCount.getInt(0)

        }

        mCount.close()
        DatabaseManager.getInstance().closeDatabase()
        return count
    }

    fun dropcategory() {
        val database = this.writableDatabase
        val query3 = "DROP TABLE IF EXISTS categoryL1"
        database!!.execSQL(query3)

        val query4 = "CREATE TABLE categoryL1 ( catId TEXT,catName TEXT,media TEXT)"
        database!!.execSQL(query4)
    }

    fun dropTable1() {
        val database = this.writableDatabase

        val query3 = "DROP TABLE IF EXISTS categoryL1"
        database!!.execSQL(query3)

        val query4 =
            "CREATE TABLE categoryL1 ( id INTEGER PRIMARY KEY, catId INT,catName TEXT, catDesc TEXT, outletId TEXT,parentId TEXT, level INT, rank INT,media TEXT, count INT, image TEXT)"
        database!!.execSQL(query4)
    }

    fun dropCart1() {
        val database = DatabaseManager.getInstance().openDatabase()
        val query = "DROP TABLE IF EXISTS cart"
        database!!.execSQL(query)
        val query1 =
            "CREATE TABLE IF NOT EXISTS cart ( id INTEGER PRIMARY KEY, sku TEXT, qty TEXT, itemName TEXT, price INT,  count INT, imgurl TEXT, subcatid TEXT, department TEXT, uom TEXT)"
        database!!.execSQL(query1)
        DatabaseManager.getInstance().closeDatabase()
    }

    fun getsessionCount(): Int {
        val database = DatabaseManager.getInstance().openReadDatabase()
        val mCount = database!!.rawQuery("select count(*) from login", null)
        mCount.moveToFirst()
        val count = mCount.getInt(0)
        mCount.close()
        DatabaseManager.getInstance().closeDatabase()
        return count
    }

    fun updateConsumer(store: String, name: String, regionId: String, regionName: String): Int {
        val database = DatabaseManager.getInstance().openDatabase()
        val values = ContentValues()
        values.put("storecode", store)
        values.put("storename", name)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:MM:SS")
        val date = sdf.format(Date())
        values.put("datereg", date)
        values.put("regioncode", regionId)
        values.put("regionname", regionName)
        Log.i("update database ", " ended")
        return database!!.update("login", values, null, null)
    }

    fun updateConsumerData(
        id: Int,
        name: String,
        mail: String,
        token: String,
        secret: String
    ): Int {
        val database = DatabaseManager.getInstance().openDatabase()
        val values = ContentValues()
        values.put("consumerId", id)
        values.put("name", name)
        values.put("mail", mail)
        values.put("token", token)
        values.put("secret", secret)
        values.put("mode", 2)
        Log.i("update database ", " ended")
        return database!!.update("login", values, null, null)
    }

    fun updateGuest(): Int {
        val database = DatabaseManager.getInstance().openDatabase()
        val values = ContentValues()
        values.put("mode", 1)
        Log.i("update database ", " ended")
        return database!!.update("login", values, null, null)
    }

    fun updateNothing(): Int {
        val database = DatabaseManager.getInstance().openDatabase()
        val values = ContentValues()
        values.put("mode", 0)
        Log.i("update database ", " ended")
        return database!!.update("login", values, null, null)
    }

    fun updateCartId(cart: Int): Int {
        val database = DatabaseManager.getInstance().openDatabase()
        val values = ContentValues()
        values.put("cart", cart)
        Log.i("update database ", " ended")
        return database!!.update("login", values, null, null)
    }

    fun dropConsumer() {
        val database = DatabaseManager.getInstance().openDatabase()
        val query = "DROP TABLE IF EXISTS login"
        database!!.execSQL(query)
        val query5 =
            "CREATE TABLE IF NOT EXISTS login ( id INTEGER PRIMARY KEY, consumerId INT, name TEXT, mail TEXT, mobile TEXT,address TEXT,pin TEXT)"
        database!!.execSQL(query5)
    }

    fun getConsumerRegion(need: Int): String? {
        val selectQuery = "SELECT  o.regioncode, o.regionname FROM login o limit 1"
        var `val`: String? = null
        val database = DatabaseManager.getInstance().openReadDatabase()
        val cursor = database!!.rawQuery(selectQuery, arrayOf())
        try {
            if (need == 0) {
                while (cursor!!.moveToNext()) {
                    `val` = cursor.getString(0)
                }
            } else {
                while (cursor!!.moveToNext()) {
                    `val` = cursor.getString(1)
                }
            }
        } finally {
            cursor?.close()
        }
        DatabaseManager.getInstance().closeDatabase()
        return `val`
    }

    fun getConsumerStore(need: Int): String? {
        val selectQuery = "SELECT  o.storecode, o.storename FROM login o limit 1"
        var `val`: String? = null
        val database = DatabaseManager.getInstance().openReadDatabase()
        val cursor = database!!.rawQuery(selectQuery, arrayOf())
        try {
            if (need == 0) {
                while (cursor!!.moveToNext()) {
                    `val` = cursor.getString(0)
                }
            } else {
                while (cursor!!.moveToNext()) {
                    `val` = cursor.getString(1)
                }
            }
        } finally {
            cursor?.close()
        }
        DatabaseManager.getInstance().closeDatabase()
        return `val`
    }


    fun getCategoryName(cateid: Int): String {
        val selectQuery = "SELECT  o.catName FROM categoryL1 o where o.catId =$cateid limit 1"
        var `val` = ""
        val database = DatabaseManager.getInstance().openReadDatabase()
        val cursor = database!!.rawQuery(selectQuery, arrayOf())
        try {
            while (cursor!!.moveToNext()) {
                `val` = cursor.getString(0)
            }
        } finally {
            cursor?.close()
        }
        DatabaseManager.getInstance().closeDatabase()
        return `val`

    }


    fun getLevel2CategoryName(cateid: Int): String {
        var cateid = cateid
        var level = 0
        var catename: String? = null
        while (level != 2) {
            val selectQuery =
                "SELECT  o.parentId, o.level, o.catName FROM categoryL1 o where o.catId =$cateid limit 1"
            val database = DatabaseManager.getInstance().openReadDatabase()
            val cursor = database!!.rawQuery(selectQuery, arrayOf())
            try {
                while (cursor!!.moveToNext()) {
                    level = cursor.getInt(1)
                    if (level == 2) {
                        catename = cursor.getString(2)
                    } else {
                        cateid = cursor.getInt(0)
                    }
                }
            } finally {
                cursor?.close()
            }
            DatabaseManager.getInstance().closeDatabase()
        }
        Log.i("prent data", "$cateid~~$catename")
        return "$cateid~~$catename"
    }

    /*public ArrayList<DetailBo> getbookmarklist() {
			String selectQuery = "SELECT bid,name, desc,price,weight,media, sku FROM bookmark";
			ArrayList<DetailBo> list=new ArrayList<DetailBo>();
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor c = db.rawQuery(selectQuery, null);
			if (c.moveToFirst()) {
				do {
					DetailBo bo=new DetailBo();
					bo.setId(c.getString(0));
					bo.setName(c.getString(1));
					bo.setDesc(c.getString(2));.
					bo.setPrice(c.getDouble(3));
					bo.setWeight(c.getString(4));
					bo.setImgpath(c.getString(5));
					bo.setSku(c.getString(6));
					list.add(bo);
				}while (c.moveToNext());
				db.close();
			}
			return list;
	}*/

    fun removebookmark() {
        val database = this.writableDatabase
        database!!.execSQL("delete from " + "favourite")
        database!!.close()
    }

    fun addNewLocationObject(
        currentTimeMillis: Long,
        latitudeValue: Double,
        longitudeValue: Double
    ) : Int {
        try {
            val values = ContentValues()
            values.put("currentTimeMillis",currentTimeMillis)
            values.put("latitudeValue",latitudeValue)
            values.put("longitudeValue",longitudeValue)
            val database = DatabaseManager.getInstance().openDatabase()
            val d= (database!!.insert("Location", null, values)).toInt()
            Log.d("New Record  ", "Table is:Location : "+d)
            return d
        } catch (errorException: Exception) {
            Log.d("Exception occured", "Exception occured $errorException")
            return 0
        }
    }

    fun getAllLocationObjects(): List<LocationObject> {
        val selectQuery = "SELECT * FROM Location"//AND opid='${opid}'
        val database = DatabaseManager.getInstance().openReadDatabase()
        val loclist = ArrayList<LocationObject>()
        val c = database!!.rawQuery(selectQuery, null)
        if (c.moveToFirst()) {
            do {
                val loca = LocationObject()
                loca.currentTimeM = c.getString(c.getColumnIndex("currentTimeMillis"))
                loca.Latitude = c.getDouble(c.getColumnIndex("latitudeValue"))
                loca.Longitude = c.getDouble(c.getColumnIndex("longitudeValue"))

                loclist.add(loca)
            } while (c.moveToNext())
        }
        c.close()
        DatabaseManager.getInstance().closeDatabase()
        return loclist
    }

    companion object {

        private val LOGCAT: String? = null
        private val Centre_NAME = "Centre"
    }
}