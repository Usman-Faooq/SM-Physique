package com.buzzwaretech.gymphysique.models

import android.os.Parcel
import android.os.Parcelable

data class KartModel(
    var itemId : String = "",
    var name : String = "",
    var description : String = "",
    var price : String = "",
    var images : ArrayList<String> = arrayListOf()
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: arrayListOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(itemId)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(price)
        parcel.writeStringList(images)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<KartModel> {
        override fun createFromParcel(parcel: Parcel): KartModel {
            return KartModel(parcel)
        }

        override fun newArray(size: Int): Array<KartModel?> {
            return arrayOfNulls(size)
        }
    }
}
