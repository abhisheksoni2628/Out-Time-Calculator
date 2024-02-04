package com.abhishek.outx.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResultTimeFeed(
    var breakTaken: String = "00:00",
    var outTime: String = "00:00"
) : Parcelable
