package com.udacity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class DownloadDetails(
    val name: String,
    val status: DownloadStatus
): Parcelable

enum class DownloadStatus(val text: String){
    SUCCESS("Success"),
    FAILED("Failed")
}

