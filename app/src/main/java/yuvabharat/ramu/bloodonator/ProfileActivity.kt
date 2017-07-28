package yuvabharat.ramu.bloodonator

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.view.Menu
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.content_profile.*
import yuvabharat.ramu.bloodonator.firebaseDataHolders.BloodUserInfo

class ProfileActivity : BaseActivity(), AppBarLayout.OnOffsetChangedListener {
    private var mIsTheTitleVisible = false
    private var mIsTheTitleContainerVisible = true

    private val UserDbObject: DatabaseReference by lazy { Bloodonator.app.databaseReference!!.child("user_info").ref }

    val genders = ArrayList<String>()
    val bloodGroup = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        main_appbar.addOnOffsetChangedListener(this)

        main_toolbar.inflateMenu(R.menu.menu_main)
        startAlphaAnimation(main_toolbar, 0, View.INVISIBLE)
        main_toolbar.isEnabled = false

        genders.add("Male")
        genders.add("Female")

        val adapter = ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, genders)
        profile_gender.adapter = adapter

        bloodGroup.add("A+")
        bloodGroup.add("A-")
        bloodGroup.add("B+")
        bloodGroup.add("B-")
        bloodGroup.add("AB+")
        bloodGroup.add("AB-")
        bloodGroup.add("O+")
        bloodGroup.add("O-")

        val BGAdapter = ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, bloodGroup);
        profile_BloodGroup.adapter = BGAdapter
        FirebaseAuth.getInstance().addAuthStateListener { p0 ->
            if (p0.currentUser != null) {
                val photo = p0.currentUser?.photoUrl
                Picasso.with(this@ProfileActivity).load(photo).fit().centerCrop().into(Profile_UserImage_background)
                Picasso.with(this@ProfileActivity).load(photo).fit().centerCrop().into(Profile_UserImage)
            }
        }

        UserDbObject.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot?) {
                val user = p0!!.getValue(object : GenericTypeIndicator<BloodUserInfo>() {})
                val name = user.name
                val email = user.email
                profile_Toolbar_UserName.text = name
                profile_UserName.text = name
                Profile_Quote.text = email
                UserNameEditField.setText(name)
            }

            override fun onCancelled(p0: DatabaseError?) {

            }
        })

        profile_update.setOnClickListener {
            UserDbObject.child("name").setValue(UserNameEditField.text.toString())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, offset: Int) {
        val maxScroll = appBarLayout.totalScrollRange
        val percentage = Math.abs(offset).toFloat() / maxScroll.toFloat()

        handleAlphaOnTitle(percentage)
        handleToolbarTitleVisibility(percentage)
    }

    private fun handleToolbarTitleVisibility(percentage: Float) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(main_toolbar, ALPHA_ANIMATIONS_DURATION.toLong(), View.VISIBLE)
                main_toolbar.isEnabled = true
                mIsTheTitleVisible = true
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(main_toolbar, ALPHA_ANIMATIONS_DURATION.toLong(), View.INVISIBLE)
                main_toolbar.isEnabled = false
                mIsTheTitleVisible = false
            }
        }
    }

    private fun handleAlphaOnTitle(percentage: Float) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(main_linearlayout_title, ALPHA_ANIMATIONS_DURATION.toLong(), View.INVISIBLE)
                mIsTheTitleContainerVisible = false
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(main_linearlayout_title, ALPHA_ANIMATIONS_DURATION.toLong(), View.VISIBLE)
                mIsTheTitleContainerVisible = true
            }
        }
    }

    companion object {
        private val PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f
        private val PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f
        private val ALPHA_ANIMATIONS_DURATION = 200

        fun startAlphaAnimation(v: View, duration: Long, visibility: Int) {
            val alphaAnimation = if (visibility == View.VISIBLE)
                AlphaAnimation(0f, 1f)
            else
                AlphaAnimation(1f, 0f)

            alphaAnimation.duration = duration
            alphaAnimation.fillAfter = true
            v.startAnimation(alphaAnimation)
        }
    }
}
