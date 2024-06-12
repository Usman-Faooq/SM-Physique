package com.buzzwaretech.gymphysique.models

import android.os.Parcel
import android.os.Parcelable

data class PostModel(
    var postId: String = "",
    var description: String = "",
    var mediaType: String = "",
    var mediaUrl: String = "",
    var postDate: Long = 0,
    var userId: String = "",
    var userName: String = "",
    var likes: HashMap<String, Any> = hashMapOf(),
    var commentCount: Long = 0
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readHashMap(ClassLoader.getSystemClassLoader()) as HashMap<String, Any>,
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(postId)
        parcel.writeString(description)
        parcel.writeString(mediaType)
        parcel.writeString(mediaUrl)
        parcel.writeLong(postDate)
        parcel.writeString(userId)
        parcel.writeString(userName)
        parcel.writeMap(likes)
        parcel.writeLong(commentCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PostModel> {
        override fun createFromParcel(parcel: Parcel): PostModel {
            return PostModel(parcel)
        }

        override fun newArray(size: Int): Array<PostModel?> {
            return arrayOfNulls(size)
        }
    }
}
