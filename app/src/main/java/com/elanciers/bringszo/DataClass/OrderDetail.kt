package com.elanciers.bringszo.DataClass

import org.json.JSONArray

class OrderDetail {

    var id : String? = null
    var name : String? = null
    var loc : String? = null
    var price : String? = null
    var subtotal : String? = null
    var items : String? = null
    var delivery : String? = null
    var deliver_sts : String? = null
    var discount : String? = null
    var dateandtime : String? = null
    var details : JSONArray? = null
    var order_details : List<PovaData>? = null

    var shopnm : String? = null
    var shoploc : String? = null
    var adrs_type : String? = null
    var coupon_code : String? = null
    var adrs : String? = null
    var payment_mode : String? = null
}
