package yuvabharat.ramu.bloodonator.firebaseDataHolders

import android.location.Location
import java.util.*

/**
 * Created by ramu on 21/3/17.
 */
data class BloodUserInfo (
    var name: String = "",
    var email: String = "",
    var photo: String = "",
    var bloodGroup:Int = 1,
    var age:Int = 10,
    var height:Double = 0.0,
    var weight:Double = 0.0,
    var phone:String = "",
    var lastDonationDate:Date = Date(),
    var CurrentLocation:Location? = null
    )