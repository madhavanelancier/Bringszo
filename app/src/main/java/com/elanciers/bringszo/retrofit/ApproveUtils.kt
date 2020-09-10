package com.elanciers.bringszo.retrofit

import com.elanciers.bringszo.Common.Appconstands

object ApproveUtils {
    val BASE_URL = Appconstands.Domin

    val Get: usr
        //get() = RetrofitClient.getClient(BASE_URL).create(usr::class.java)
        get() = RetrofitCli.instance.api
}
