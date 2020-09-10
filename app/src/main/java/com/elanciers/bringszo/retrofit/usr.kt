package com.elanciers.bringszo.retrofit

import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.POST



interface usr {
    @GET()
    fun getCentre(@Url st : String): Call<Resp>

    @GET()
    fun getMem(@Url st : String): Call<Resp>

    @GET()
    fun getMem2(@Url st : String): Call<Resp.FailResp>

    @FormUrlEncoded
    @POST("customers/home_slider")
    fun getSlider(@Field("api")api : String): Call<Resp>

    @FormUrlEncoded
    @POST("appPopup")
    fun getpop(@Field("api")api : String): Call<Respval>

    @FormUrlEncoded
    @POST("customers/category")
    fun getCategory(@Field("api")api : String): Call<Resp>

    @FormUrlEncoded
    @POST("product")
    fun getProduct(@Field("id")id : String): Call<Resp>

    @FormUrlEncoded
    @POST("addAddress")
    fun AddAddress(@Field("title")title : String,
                   @Field("save_us")save_us : String,
                   @Field("flat_no")flat_no : String,
                   @Field("area")area : String,
                   @Field("location")location : String,
                   @Field("lat")lat : String,
                   @Field("lng")lng : String,
                   @Field("city")city : String,
                   @Field("reg_id")reg_id : String): Call<Resp>

    @FormUrlEncoded
    @POST("editAddress/1")
    fun EditAddress(@Field("title")title : String,
                   @Field("save_us")save_us : String,
                   @Field("flat_no")flat_no : String,
                   @Field("area")area : String,
                   @Field("location")location : String,
                   @Field("lat")lat : String,
                   @Field("lng")lng : String,
                   @Field("reg_id")reg_id : String,
                   @Field("city")city : String,
                   @Field("id")id : String): Call<Resp>

    @FormUrlEncoded
    @POST("deleteAddress/1")
    fun DeleteAddress(@Field("reg_id")reg_id : String,@Field("id")id : String): Call<Resp>

    @FormUrlEncoded
    @POST("order_details")
    fun order_details(@Field("id")id : String): Call<Resp>

    @FormUrlEncoded
    @POST("order")
    fun Order(@Field("vid")vid : String,
              @Field("user")user : String,
              @Field("subtotal")subtotal : String,
              @Field("coupon_id")coupon_id : String,
              @Field("discount")discount : String,
              @Field("delivery")delivery : String,
              @Field("total")total : String,
              @Field("adrs_id")adrs_id : String,
              @Field("order_details")order_details : JSONArray): Call<Resp>

    @FormUrlEncoded
    @POST("customers/vendors/1")
    fun getVendors(@Field("lat")lat : String,
                   @Field("lng")lng : String,
                   @Field("id")id : String,
                   @Field("start")start : String): Call<Resp>

    @FormUrlEncoded
    @POST("delivery_charge")
    fun getDeliverycharge(@Field("amount")uid : String): Call<Resp>

    @FormUrlEncoded
    @POST("customers/vendors/1")
    fun Search(@Field("lat")lat : String,
                   @Field("lng")lng : String,
                   @Field("id")id : String,
                   @Field("search")search : String,
                   @Field("start")start : String): Call<Resp>

    @FormUrlEncoded
    @POST("search")
    fun MainSearch(@Field("lat")lat : String,
                   @Field("lng")lng : String,
                   @Field("search")search : String,
                   @Field("start")start : String): Call<Resp>

    @FormUrlEncoded
    @POST("offer_product/0")
    fun Offer(@Field("search")search : String,
              @Field("start")start : String): Call<Resp>

    @FormUrlEncoded
    @POST("category")
    fun getProducts(@Field("type")type : String,
                   @Field("vendor_id")vendor_id : String): Call<Resp>

    @FormUrlEncoded
    @POST("customers/mpinFetch")
    fun getmpin(@Field("mobile")type : String): Call<Resp>

    @FormUrlEncoded
    @POST("customers/mpinUpdate")
    fun setmpin(@Field("mobile")type : String,
                @Field("mpin")mpin : String): Call<Resp>

    @FormUrlEncoded
    @POST("category")
    fun getSearchProducts(@Field("vendor_id")vendor_id : String,
                   @Field("search")search : String,
                   @Field("start")start : String): Call<Resp>

    @FormUrlEncoded
    @POST("productSearch")
    fun getSearchProduct(@Field("vendor_id")vendor_id : String,
                          @Field("search")search : String,
                          @Field("start")start : String): Call<Resp>

    @FormUrlEncoded
    @POST("customers/store")
    fun Login(@Field("api")api : String,
                 @Field("mobile")mobile : String,
                 @Field("token")token : String): Call<Resp>

    @FormUrlEncoded
    @POST("sponsorCheck")
    fun spons(@Field("mobile")api : String): Call<Respval>

    @FormUrlEncoded
    @POST("customers/register")
    fun Register(@Field("api")api : String,
                 @Field("name")name : String,
                 @Field("mobile")mobile : String,
                 @Field("email")email : String,
                 @Field("sponsor")sponsor : String,
                 @Field("token")token : String): Call<Resp>

    @FormUrlEncoded
    @POST("profile_update")
    fun profile_update(@Field("uid")uid : String,
                 @Field("name")name : String,
                 @Field("mobile")mobile : String,
                 @Field("email")email : String): Call<Resp>

    @FormUrlEncoded
    @POST("customers/otp_check")
    fun Otp(@Field("api")api : String,
                 @Field("mobile")mobile : String,
                 @Field("res_otp")res_otp : String,
                 @Field("enter_otp")enter_otp : String,
                 @Field("type")type : String): Call<Resp>

    @FormUrlEncoded
    @POST("customers/otp_check")
    fun SignupOtp(@Field("api")api : String,
                 @Field("name")name : String,
                 @Field("mobile")mobile : String,
                 @Field("email")email : String,
                 @Field("res_otp")res_otp : String,
                 @Field("enter_otp")enter_otp : String): Call<Resp>

    //{"centre_id":"67","amount":"2800","paid_amount":"0","status":"Whole OD","staff_id":"177"}
    @FormUrlEncoded
    @POST("order_details")
    fun Orders(@Field("id")id : String,
               @Field("start")start : String): Call<Resp>

    @FormUrlEncoded
    @POST("product")
    fun Product(@Field("id")id : String): Call<Resp>

    @FormUrlEncoded
    @POST("single_order")
    fun getsts(@Field("id")id : String): Call<Resp>

    @POST("version")
    fun Version(): Call<Resp>

    @FormUrlEncoded
    @POST("coupon")
    fun Coupon(@Field("id")id : String): Call<Resp>

    @FormUrlEncoded
    @POST("coupon_check")
    fun Coupon_check(@Field("code")code : String): Call<Resp>

    //{"staff_id":"17","centre_id":"1","reason":"Reason"}

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("order")
    fun postRawJSON(@Body locationPost: JSONObject): Call<Resp>

    /*staff_id
area_code
centre_code
member_position
member_name
member_mobile
dob_age
member_rationcard
member_voterid
member_aadhar
religion
community
loan_amt
income
nominee_name
nominee_relationship
nominee_dob_age
nominee_voterid
nominee_aadhar
business
business_year
member_placement
member_birthplace
member_address
member_communication_address
member_image
member_nominee_image
member_signature
nominee_signature*/

    @FormUrlEncoded
    @POST("addfamily")
    fun Family(@Body fam : JSONObject): Call<Resp>


    @FormUrlEncoded
    @POST("leave")
    fun LeaveApply(@Field("staff_id")staff_id : String,
                 @Field("d_date")d_date : String,
                 @Field("leave_for")leave_for : String): Call<Resp>
/*{"staff_id":"17","d_date":"Today","leave_for":"Reason"}*/

    @GET("notification")
    fun getNotification(): Call<Resp>

    @POST("maintenance")
    fun getMaintenance(): Call<Resp>

    @FormUrlEncoded
    @POST("denomination")
    fun Pay_denom(
        @Field("staff_id")staff_id : String,
        @Field("centre_id")centre_id : String,
        @Field("group_image")group_image : String,
        @Field("grand_total")grand_total : String,
        @Field("denomination")denom : JSONArray,
        @Field("members") pay : JSONArray
                  ): Call<Resp>

    /*{"centre_id": "1","group_image": "","grand_total": "4000","denomination": [{"denominator_id": "1","denominator": "2000","count": "1","total": "2000"},{"denominator_id": "2","denominator": "2000","count": "1","total": "2000"}]}*/

    @FormUrlEncoded
    @POST("feedback")
    fun Feedbacks(@Field("staff_id")staff_id : String,
                 @Field("centre_id")centre_id : String,
                 @Field("member_id")member_id : String,
                 @Field("reason")reason : String): Call<Resp>
}
