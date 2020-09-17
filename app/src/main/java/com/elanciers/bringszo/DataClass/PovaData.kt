package com.elanciers.bringszo.DataClass

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PovaData {

    @SerializedName("otp")
    @Expose
    var otp: String? = null


    @SerializedName("total")
    @Expose
    var total: String? = null
    @SerializedName("ven_lat")
    @Expose
    var ven_lat: String? = null
    @SerializedName("ven_lng")
    @Expose
    var ven_lng: String? = null
    @SerializedName("user_lat")
    @Expose
    var user_lat: String? = null
    @SerializedName("user_lng")
    @Expose
    var user_lng: String? = null
    @SerializedName("delivery_boy")
    @Expose
    var delivery_boy: String? = null
    @SerializedName("d_mobile")
    @Expose
    var d_mobile: String? = null
    @SerializedName("d_lat")
    @Expose
    var d_lat: String? = null
    @SerializedName("d_lng")
    @Expose
    var d_lng: String? = null
    @SerializedName("payment_mode")
    @Expose
    var payment_mode: String? = null
    @SerializedName("ptotal")
    @Expose
    var ptotal: String? = null
    @SerializedName("delivery")
    @Expose
    var delivery: String? = null
    @SerializedName("order_status")
    @Expose
    var order_status: String? = null
    @SerializedName("ven_address")
    @Expose
    var ven_address: String? = null
    @SerializedName("user_address")
    @Expose
    var user_address: String? = null
    @SerializedName("user_type")
    @Expose
    var user_type: String? = null
    @SerializedName("datetime")
    @Expose
    var datetime: String? = null

    @SerializedName("time")
    @Expose
    var time: String? = null

    @SerializedName("start_time")
    @Expose
    var start_time: String? = null

    @SerializedName("end_time")
    @Expose
    var end_time: String? = null

    @SerializedName("coupon_code")
    @Expose
    var coupon_code: String? = null

    @SerializedName("content")
    @Expose
    var content: String? = null

    @SerializedName("vid")
    @Expose
    var vid: String? = null

    @SerializedName("cid")
    @Expose
    var cid: String? = null

    @SerializedName("vendor_id")
    @Expose
    var vendor_id: String? = null
    @SerializedName("vendor_image")
    @Expose
    var vendor_image: String? = null
    @SerializedName("vimage")
    @Expose
    var vimage: String? = null
    @SerializedName("discount")
    @Expose
    var discount: String? = null
    @SerializedName("subtotal")
    @Expose
    var subtotal: String? = null
    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("pid")
    @Expose
    var pid: String? = null
    @SerializedName("price")
    @Expose
    var price: String? = null
    @SerializedName("vendor_price")
    @Expose
    var vendor_price: String? = null
    @SerializedName("sid")
    @Expose
    var sid: String? = null
    @SerializedName("category")
    @Expose
    var category: String? = null
    @SerializedName("type_id")
    @Expose
    var type_id: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("pname")
    @Expose
    var pname: String? = null
    @SerializedName("email")
    @Expose
    var email: String? = null
    @SerializedName("mobile")
    @Expose
    var mobile: String? = null
    @SerializedName("mail")
    @Expose
    var mail: String? = null
    @SerializedName("adrs_id")
    @Expose
    var adrs_id: String? = null
    @SerializedName("type")
    @Expose
    var type: String? = null
    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("flat_no")
    @Expose
    var flat_no: String? = null
    @SerializedName("area")
    @Expose
    var area: String? = null
    @SerializedName("location")
    @Expose
    var location: String? = null
    @SerializedName("lat")
    @Expose
    var lat: String? = null
    @SerializedName("lng")
    @Expose
    var lng: String? = null


    @SerializedName("image")
    @Expose
    var image: String? = null
    @SerializedName("distance")
    @Expose
    var distance: String? = null
    @SerializedName("duration")
    @Expose
    var duration: String? = null
    @SerializedName("offer")
    @Expose
    var offer: String? = null


    @SerializedName("mesure")
    @Expose
    var mesure: String? = null


    @SerializedName("product")
    @Expose
    var product: String? = null


    @SerializedName("qty")
    @Expose
    var qty: String? = null


    @SerializedName("code")
    @Expose
    var code: String? = null

    @SerializedName("minimum_order")
    @Expose
    var minimum_order: String? = null

    @SerializedName("address")
    @Expose
    var address: List<PovaData>? = null


    @SerializedName("details")
    @Expose
    var details: List<PovaData>? = null


    @SerializedName("pro")
    @Expose
    var pro: List<PovaData>? = null


    @SerializedName("option")
    @Expose
    var option: List<PovaData>? = null


    @SerializedName("order_details")
    @Expose
    var order_details: List<PovaData>? = null

    @SerializedName("Vendor")
    @Expose
    var Vendor: List<PovaData>? = null

    /*@SerializedName("total_amount")
    @Expose
    var total_amount: String? = null*/
}
/*  "type": "Home",
                        "flat_no": "37/20",
                        "area": "Teachers colony",
                        "location": "35-20, 2nd St, Puttuthoppu, Arappalayam, Madurai, Tamil Nadu 625016, India"
*/