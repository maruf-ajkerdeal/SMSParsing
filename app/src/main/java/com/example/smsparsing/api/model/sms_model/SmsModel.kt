package com.example.smsparsing.api.model.sms_model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SmsModel(
    @SerializedName("repayment")
    var address: String? = "",
    @SerializedName("balance")
    var body: String? = "",
    @SerializedName("score")
    var serviceCenter: String? = ""
): Parcelable