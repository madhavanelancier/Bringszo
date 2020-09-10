package com.elanciers.bringszo.retrofit

import com.elanciers.bringszo.DataClass.PovaData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Resp {

    @SerializedName("Status")
    @Expose
    var status: String? = null
    @SerializedName("Message")
    @Expose
    var message: String? = null
    @SerializedName("Response")
    @Expose
    var response: List<PovaData>? = null

    inner  class Resp2 {

        @SerializedName("Status")
        @Expose
        var status: String? = null
        @SerializedName("Response")
        @Expose
        var response: List<PovaData>? = null

    }
    inner  class FailResp {

        @SerializedName("Status")
        @Expose
        var status: String? = null
        @SerializedName("Response")
        @Expose
        var response: String? = null

    }
}
