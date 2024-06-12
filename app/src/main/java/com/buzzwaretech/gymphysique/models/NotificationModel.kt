package com.buzzwaretech.gymphysique.models

import android.os.Parcel
import android.os.Parcelable

data class NotificationModel(
    var id: String = "",
    var UserID: String = "",
    var content: String = "",
    var extradata: HashMap<String, Any> = hashMapOf(),
    var postid: String = "",
    var type: String = ""
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readHashMap(ClassLoader.getSystemClassLoader()) as HashMap<String, Any>,
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(UserID)
        parcel.writeString(content)
        parcel.writeMap(extradata)
        parcel.writeString(postid)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NotificationModel> {
        override fun createFromParcel(parcel: Parcel): NotificationModel {
            return NotificationModel(parcel)
        }

        override fun newArray(size: Int): Array<NotificationModel?> {
            return arrayOfNulls(size)
        }
    }
}
