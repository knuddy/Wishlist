package com.example.knuddj1wishlist.helpers

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class WishlistItem (
    var id: Int = 0,
    var date: String? = null,
    var image : String? = null,
    var name : String? = null,
    var category : String? = null,
    var price : Double = 0.0,
    var countryCode: String? = null,
    var store : String? = null,
    var notes : String? = null,
    var purchased : Boolean = true
) : Parcelable