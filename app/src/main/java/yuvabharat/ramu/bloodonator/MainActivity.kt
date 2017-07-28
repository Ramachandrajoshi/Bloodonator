package yuvabharat.ramu.bloodonator

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import yuvabharat.ramu.bloodonator.firebaseDataHolders.BloodUserInfo

class MainActivity : BaseActivity(), GoogleApiClient.OnConnectionFailedListener {
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    private val TAG = "MainActivity"

    lateinit var Application: Bloodonator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Application = application as Bloodonator
        setContentView(R.layout.activity_main)
        signin.setOnClickListener { signIn() }
        DonateText.visibility = View.INVISIBLE
        slogun.visibility = View.INVISIBLE
        signin.visibility = View.INVISIBLE
        startAnimation()
    }

    override fun onResume() {
        super.onResume()
    }

    fun startAnimation() {
        val dropDownAnimation = AnimationUtils.loadAnimation(this, R.anim.drop_down)
        logo.startAnimation(dropDownAnimation)
        Handler().postDelayed({
            val pushUpIn = AnimationUtils.loadAnimation(this, R.anim.push_up_in)
            DonateText.visibility = View.VISIBLE
            slogun.visibility = View.VISIBLE

            DonateText.startAnimation(pushUpIn)
            slogun.startAnimation(pushUpIn)
//            signin.startAnimation(pushUpIn)
            pushUpIn.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationEnd(animation: Animation?) {
                    configGoogleSign()
                }

                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationRepeat(animation: Animation?) {

                }

            })
        }, 1800)


    }

    fun configGoogleSign() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        Application.mGoogleApiClient = GoogleApiClient.Builder(this@MainActivity).enableAutoManage(this@MainActivity /* FragmentActivity */, this@MainActivity /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
        Application.mGoogleApiClient!!.connect()
        Application.mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                updateUI(user)

                Application.mAuth!!.removeAuthStateListener(mAuthListener)

            } else {
                signin.visibility = View.VISIBLE
                val fadeIn = AnimationUtils.loadAnimation(this@MainActivity, R.anim.fadein)
                signin.startAnimation(fadeIn)
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out")
            }
            // ...
        }
        Application.mAuth!!.addAuthStateListener(mAuthListener)
    }

    fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(Application.mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                // Google Sign In was successful, authenticate with Firebase
                val account = result.signInAccount
                showProgressDialog()
                firebaseAuthWithGoogle(account!!)
            } else {
                Log.d(TAG, "onActivityResult: " + result.status.statusMessage!!)
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        Application.mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful)
                    hideProgressDialog()
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful) {
                        Log.w(TAG, "signInWithCredential", task.exception)
                        Toast.makeText(this@MainActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        hideProgressDialog()
    }

    var authState = false

    private fun updateUI(user: FirebaseUser?) {
        if (user != null && !authState) {
            authState = true
            val dbId = user.uid

            val rootref: DatabaseReference = Application.mainDb.reference

            rootref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onDataChange(p0: DataSnapshot?) {
                    p0?.children!!.forEach { child -> Log.d("tag", child.key) }
                }

            })

            rootref.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(p0: DataSnapshot?) {


                    if (p0!!.hasChild(dbId)) {
                        Application.databaseReference = p0.child(dbId).ref
                    } else {
                        val userinfo = BloodUserInfo()
                        userinfo.email = user.email!!
                        userinfo.name = user.displayName!!
                        userinfo.photo = user.photoUrl?.toString()!!

                        rootref.child(dbId).child("user_info").setValue(userinfo)
                        Application.databaseReference = rootref.child(dbId).ref
                    }
                    Bloodonator.prefrence.dbObject = dbId
                    //start Home activity
                    startActivity(Intent(this@MainActivity,HomeActivity::class.java))
                    finish()
                }

                override fun onCancelled(p0: DatabaseError?) {

                }
            })


        }
    }

    companion object {

        private val RC_SIGN_IN = 20017
    }
}
