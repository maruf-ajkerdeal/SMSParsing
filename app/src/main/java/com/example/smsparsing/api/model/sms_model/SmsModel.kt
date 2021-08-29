package com.example.smsparsing.api.model.sms_model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SmsModel(
    @SerializedName("repayment")
    var address: String? = "",
    @SerializedName("body")
    var body: String? = "",
    @SerializedName("serviceCenter")
    var serviceCenter: String? = ""
): Parcelable