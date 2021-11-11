package com.dialler.ct.activity

import android.Manifest
import android.app.role.RoleManager
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.telecom.TelecomManager
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.dialler.ct.R
import com.dialler.ct.ads.AdsManager
import com.dialler.ct.ads.AdsManager.Companion.context
import com.dialler.ct.broadcastReceiver.MyReceiver
import com.dialler.ct.fragments.Contact_Fragment
import com.dialler.ct.fragments.Favorites_Fragment
import com.dialler.ct.fragments.Keypad_Fragment
import com.dialler.ct.fragments.Recents_Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(){

    var runCount: Int = 0
    var app_run_count:Int = 0
    var checkFragmentLoaded:Int = 0
    var isAlreadyDefaultDialer: Boolean = false
    var sharedpreferences     : SharedPreferences? = null
    var bottomNavigationView  : BottomNavigationView? = null

    companion object { const val REQUEST_CODE_SET_DEFAULT_DIALER = 200
        var isScreen = false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storelang()
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
       // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        bannerAds()
        checkDefaultDialer()
        isNumberExist()
        registerBroadcastReceiver()


    }
    private fun registerBroadcastReceiver(){
        val receiver = MyReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction("my.android.Broadcast")
        registerReceiver(receiver,intentFilter)
    }
    private fun storelang() {
        val prefs = getSharedPreferences("Settings", MODE_PRIVATE)
        val lang = prefs.getString("My_lang", "")
        if (lang != null) {
            setLocale1(lang)
        }
    }

    private fun setLocale1(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        this.baseContext?.resources?.updateConfiguration(config,   this.baseContext?.resources!!.displayMetrics)

    }

    private fun initView() {


        if (sharedpreferences!!.contains("app_run_count")) {
            app_run_count = sharedpreferences!!.getInt("app_run_count", 0)
            app_run_count++
            SavePreferenceInt("app_run_count", app_run_count)
        }else{
            SavePreferenceInt("app_run_count", app_run_count)
        }
        if (sharedpreferences!!.contains("theme_check")) {
            if (sharedpreferences!!.getInt("theme_check",0)==1){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }


    }

   private fun bannerAds() {
        if (AdsManager.adView.parent != null) {
            (AdsManager.adView.parent as ViewGroup).removeView(AdsManager.adView)
        }
        relativeLayout.addView(AdsManager.adView)
        relativeLayout.viewTreeObserver.addOnGlobalLayoutListener {
        }
    }

    override fun onResume() {
        super.onResume()
        storelang()
        if (isScreen){
            //setBottomNavigation()
            isScreen=false
            recreate()
        }

        if (sharedpreferences!!.contains("checkFragmentLoaded")) {
            if (checkFragmentLoaded!=4){
                if (sharedpreferences!!.getInt("checkFragmentLoaded", 0) ==1 )
                    bottomNavigationView!!.selectedItemId = R.id.mifavourites
                else if (sharedpreferences!!.getInt("checkFragmentLoaded", 0) ==2 )
                    bottomNavigationView!!.selectedItemId = R.id.miRecents
                else if (sharedpreferences!!.getInt("checkFragmentLoaded", 0) ==3 )
                    bottomNavigationView!!.selectedItemId = R.id.miContacts
                else if (sharedpreferences!!.getInt("checkFragmentLoaded", 0) ==4 )
                    bottomNavigationView!!.selectedItemId = R.id.midialler
            }
        }
    }

    private fun isNumberExist(){
        val number = intent?.data?.schemeSpecificPart.toString()
        Log.e("TAG", "number: "+number )

        if (number.isNotEmpty() && number != "null"){

            checkFragmentLoaded = 4

            bottomNavigationView!!.selectedItemId = R.id.midialler

            val bundle = Bundle()
            bundle.putString("defaultNumber", number)
            val trans = this.supportFragmentManager.beginTransaction()
            val keypadFragment = Keypad_Fragment()
            keypadFragment.arguments = bundle
            trans.replace(R.id.flFragment, keypadFragment).commit()
        }
    }

    private fun SavePreferenceInt(key: String?, value: Int?) {
        val editor = sharedpreferences!!.edit()
        editor.putInt(key, value!!)
        editor.apply()
    }

//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        super.onWindowFocusChanged(hasFocus)
//        if(hasFocus){
//            recreate()
//        }
//    }
    private fun setBottomNavigation(){
        bottomNavigationView?.menu?.clear();
        bottomNavigationView?.inflateMenu(R.menu.bottom_nav_menu)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flFragment, Favorites_Fragment())
        transaction.commit()

        bottomNavigationView!!.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item -> //Fragment fragment = null;

            //menuItem = item;
            when (item.itemId) {
                R.id.mifavourites -> {
                    checkFragmentLoaded = 1
                    SavePreferenceInt("checkFragmentLoaded", checkFragmentLoaded)

                    val favoritesFragment = Favorites_Fragment()
                    val fragmentManager = supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.flFragment, favoritesFragment)
                    fragmentTransaction.commit()
                    storelang()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.miRecents -> {
                    checkFragmentLoaded = 2
                    SavePreferenceInt("checkFragmentLoaded", checkFragmentLoaded)

                    val recentsFragment = Recents_Fragment()
                    val fragmentManager = supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.flFragment, recentsFragment)
                    fragmentTransaction.commit()
                    storelang()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.miContacts -> {
                    checkFragmentLoaded = 3
                    SavePreferenceInt("checkFragmentLoaded", checkFragmentLoaded)

                    val contactFragment = Contact_Fragment()
                    val fragmentManager = supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.flFragment, contactFragment)
                    fragmentTransaction.commit()
                    storelang()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.midialler -> {
                    checkFragmentLoaded = 4
                    SavePreferenceInt("checkFragmentLoaded", checkFragmentLoaded)

                  //  if (isInstalledfromPlaystore==true) {
                        AdsManager.showInterstitial(this, object : AdsManager.Companion.Listener {
                            override fun intersitialAdClosedCallback() {

                                val keypadFragment = Keypad_Fragment()
                                val fragmentManager = supportFragmentManager
                                val fragmentTransaction = fragmentManager.beginTransaction()
                                fragmentTransaction.replace(R.id.flFragment, keypadFragment)
                                fragmentTransaction.commit()
                            }
                        })
//                    }else{
//                        val keypadFragment = Keypad_Fragment()
//                        val fragmentManager = supportFragmentManager
//                        val fragmentTransaction = fragmentManager.beginTransaction()
//                        fragmentTransaction.replace(R.id.flFragment, keypadFragment)
//                        fragmentTransaction.commit()
//                    }


                    storelang()
                    return@OnNavigationItemSelectedListener true
                    //Log.e("TAG", "show ads: ", )
                    //AdsManager.instance.showInterstitial()
                    //val intentfavouriteActivity = Intent(this@MainActivity, MainActivity_Dialer::class.java)
                    //startActivity(intentfavouriteActivity)
                }
            }
            true
        })
    }

    private fun checkDefaultDialer() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return

        val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
        isAlreadyDefaultDialer = packageName == telecomManager.defaultDialerPackage

        if (isAlreadyDefaultDialer){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                // Do something ...
                initView()
                setBottomNavigation()
                //AdsManager.instance.loadBannerAd(relativeLayout,this@MainActivity)
            }else{
                requestContact()
            }
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
            //For setting dialer
            val isRoleAvailable = roleManager.isRoleAvailable(RoleManager.ROLE_DIALER)
            if (isRoleAvailable) {
                // check whether your app is already holding the default Dialer app role.
                val isRoleHeld = roleManager.isRoleHeld(RoleManager.ROLE_DIALER)
                if (!isRoleHeld) {
                    val roleRequestIntent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
                    startActivityForResult(roleRequestIntent, REQUEST_CODE_SET_DEFAULT_DIALER)
                }
            }
        } else{
            val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).putExtra(
                TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
                "com.dialler.ct"
            )
            startActivityForResult(intent, REQUEST_CODE_SET_DEFAULT_DIALER)
        }

    }

    private fun requestContact() {
        Permissions.check(this@MainActivity, Manifest.permission.READ_CONTACTS, null,
            object : PermissionHandler() {
                override fun onGranted() {
                    requestREADCALLLOG()
                }

                override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                    super.onDenied(context, deniedPermissions)
                    //do nothing
                    finish()
                }
            })
    }

    private fun requestREADCALLLOG() {
        Permissions.check(this@MainActivity, Manifest.permission.READ_CALL_LOG, null,
            object : PermissionHandler() {
                override fun onGranted() {
                    requestWriteContacts()
                }

                override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                    super.onDenied(context, deniedPermissions)
                    //do nothing
                    finish()
                }
            })
    }

    private fun requestWriteContacts() {
        Permissions.check(this@MainActivity, Manifest.permission.WRITE_CONTACTS, null,
            object : PermissionHandler() {
                override fun onGranted() {
                    requestPhoneState()
                }

                override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                    super.onDenied(context, deniedPermissions)
                    //do nothing
                    finish()
                }
            })
    }

    private fun requestPhoneState() {
        Permissions.check(this@MainActivity, Manifest.permission.READ_PHONE_STATE, null,
            object : PermissionHandler() {
                override fun onGranted() {
                    requestMakePhoneCall()
                }

                override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                    super.onDenied(context, deniedPermissions)
                    //do nothing
                    finish()
                }
            })
    }

    private fun requestMakePhoneCall() {
        Permissions.check(this@MainActivity, Manifest.permission.CALL_PHONE, null,
            object : PermissionHandler() {
                override fun onGranted() {
                    initView()
                    setBottomNavigation()
                    //AdsManager.instance.loadBannerAd(relativeLayout,this@MainActivity)
                }

                override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                    super.onDenied(context, deniedPermissions)
                    //do nothing
                    finish()
                }
            })
    }

    private fun checkSetDefaultDialerResult(resultCode: Int) {
//        val message = when (resultCode) {
//            RESULT_OK       -> "User accepted request to become default dialer"
//            RESULT_CANCELED -> "User declined request to become default dialer"
//            else            -> "Unexpected result code $resultCode"
//        }

        if (resultCode == REQUEST_CODE_SET_DEFAULT_DIALER){
            initView()
            setBottomNavigation()
            Toast.makeText(this, getString(R.string.idialer_become_default), Toast.LENGTH_LONG).show()

        }else{
            Toast.makeText(this,  getString(R.string.idialer_become_default), Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.e("TAG", "requestCode: " + requestCode)
        Log.e("TAG", "resultCode: " + resultCode)
        if(requestCode ==1 ){
            val test = 0;
//            setadsinterstitial()
//            showinsadd()

        } else if (resultCode==-1){
            checkSetDefaultDialerResult(requestCode)
        } else{
            finish()
        }
    }



    private fun showDialogBox() {
        AlertDialog.Builder(this@MainActivity)
                .setTitle(getString(R.string.alert))
                .setMessage(getString(R.string.alert_massage)) // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        // Continue with delete operation
                        //requestContact()
                        checkDefaultDialer()
                    }
                }) // A null listener allows the button to dismiss the dialog and take no further action.
                //.setNegativeButton(android.R.string.no, null)
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .show()
    }



}