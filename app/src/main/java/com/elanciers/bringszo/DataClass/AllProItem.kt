package com.elanciers.bringszo.DataClass

import com.elanciers.booking.DataClass.SpinnerPojo

class AllProItem{
    var name : String? = null
    var pros : ArrayList<ProductItems>? = null


}
class AllProducts{
    var pros : ArrayList<AllProItem>? = null
}

class AllCat{
    var cid : String? = null
    var title : String? = null
    var pros : ArrayList<TitledProduct>? = null
}
class TitledProduct{
    var title : String? = null
    var sid : String? = null
    var pid : String? = null
    var name : String? = null
    var qty : String? = null
    var nm : String? = null
    var price : String? = null
    var selpos : Int? = null
    var img : String? = null
    var options : ArrayList<SpinnerPojo>? = null
}
class CartProduct{
    var vendor_id : String? = null
    var vendor_nm : String? = null
    var img : String? = null
    var cid : String? = null
    var cnm : String? = null
    var sid : String? = null
    var snm : String? = null
    var pid : String? = null
    var pnm : String? = null
    var qty : String? = null
    var opid : String? = null
    var opnm : String? = null
    var price : String? = null
    var options : ArrayList<SpinnerPojo>? = null
}