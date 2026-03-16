package com.example.alzcare.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class User(
    val id: String? = null,
    val userName: String? = null,
    val phoneNumber: String? = null,
    val email: String? = null,
    val role: String? = null
): Parcelable {
    companion object {
        const val CollectionName = "users"
    }
}
