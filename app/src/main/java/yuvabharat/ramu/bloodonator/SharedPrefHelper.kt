package yuvabharat.ramu.bloodonator

import android.content.Context

/**
 * Created by ramu on 21/3/17.
 */
class SharedPrefHelper(context: Context) {
    val preference = context.getSharedPreferences("com.bloodinator.pref", Context.MODE_PRIVATE)
    val DB_KEY = "db";



    var dbObject: String
        get() = preference.getString(DB_KEY, "")
        set(value) = preference.edit().putString(DB_KEY, value).apply()
}