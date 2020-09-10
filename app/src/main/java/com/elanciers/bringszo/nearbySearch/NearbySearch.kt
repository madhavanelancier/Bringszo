package com.elanciers.bringszo.nearbySearch

import com.google.gson.annotations.SerializedName

data class NearbySearch(@SerializedName("next_page_token")
                        val nextPageToken: String = "",
                        @SerializedName("results")
                        val results: List<ResultsItem>?,
                        @SerializedName("status")
                        val status: String = "")