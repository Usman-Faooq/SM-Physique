package com.buzzwaretech.gymphysique.models

import android.os.Parcel
import android.os.Parcelable

data class CommentModel(
    var commentId: String = "",
    var content: String = "",
    var fromID: String = "",
    var timeStamp: Long = 0,
    var type: String = "",
)