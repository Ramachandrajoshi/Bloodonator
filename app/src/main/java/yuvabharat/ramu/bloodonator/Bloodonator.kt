package yuvabharat.ramu.bloodonator

import android.app.Application
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by ramu on 18/3/17.
 */

class Bloodonator : Application() {
    companion object {
        lateinit var prefrence: SharedPrefHelper
        lateinit var app: Bloodonator
    }
    var mAuth: FirebaseAuth? = null
    var mGoogleApiClient: GoogleApiClient? = null
    val mainDb: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    var databaseReference: DatabaseReference? = null

    fun initChildDb(childObject: String) {
        databaseReference = mainDb.getReference().child(childObject)
    }

    override fun onCreate() {
        prefrence = SharedPrefHelper(this)
        app = this
        super.onCreate()
        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
