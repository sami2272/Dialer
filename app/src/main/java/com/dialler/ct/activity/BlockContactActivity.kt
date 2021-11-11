package com.dialler.ct.activity

import android.Manifest
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.BlockedNumberContract
import android.telecom.TelecomManager
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dialler.ct.R
import com.example.blockedcontactsdemo.Adapter.CustomAdapter
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions


class BlockContactActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var linearLayout_back: LinearLayout
    private lateinit var img_back : ImageView
    var isAlreadyDefaultDialer: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_block_contact)
        initView()

        setOnClickListener()



    }


    private fun loadList(){
        if (ContextCompat.checkSelfPermission(this@BlockContactActivity, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this@BlockContactActivity, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED
        ) {
            // Do something ...

            fetchBlockedContact()
        }else{
            requestReadContact()
        }

    }
    private fun requestReadContact() {
        Permissions.check(this@BlockContactActivity, Manifest.permission.READ_CONTACTS, null,
            object : PermissionHandler() {
                override fun onGranted() {
                    requestWriteContact()
                }

                override fun onDenied(context: Context, deniedPermissions: java.util.ArrayList<String>) {
                    super.onDenied(context, deniedPermissions)
                    //do nothing
                    (this@BlockContactActivity).finish()
                }
            })
    }
    private fun requestWriteContact() {
        Permissions.check(this@BlockContactActivity, Manifest.permission.WRITE_CONTACTS, null,
            object : PermissionHandler() {
                override fun onGranted() {

                    fetchBlockedContact()
                }

                override fun onDenied(context: Context, deniedPermissions: java.util.ArrayList<String>) {
                    super.onDenied(context, deniedPermissions)
                    //do nothing
                    (this@BlockContactActivity).finish()
                }
            })
    }


    private fun initView() {
        linearLayout_back = findViewById(R.id.linearLayout_back)
        img_back = findViewById(R.id.img_back)
    }

    private fun setOnClickListener() {
        linearLayout_back.setOnClickListener(this)
    }

    private fun fetchBlockedContact() {

        var c: Cursor? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            contentResolver.query(
                    BlockedNumberContract.BlockedNumbers.CONTENT_URI, arrayOf(
                    BlockedNumberContract.BlockedNumbers.COLUMN_ID,
                    BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER,
                    BlockedNumberContract.BlockedNumbers.COLUMN_E164_NUMBER
            ), null, null, null
            )
        } else {
            TODO("VERSION.SDK_INT < N")
        }


        val name: ArrayList<String> = ArrayList()
//        var incre: Int = 0

        if (c != null) {
            Log.i("Blocked List: ", c.moveToFirst().toString())
            c.moveToFirst()
            if (c!!.moveToFirst()){
                do {
                    name.add(c.getString(1))

//                Log.i("Names", name.get(incre))
//                incre++
                } while (c.moveToNext())

            }
        }

        val customAdapter = CustomAdapter(name,this@BlockContactActivity)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)

        recyclerView.layoutManager = LinearLayoutManager(this)
        //recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = customAdapter

    }

    override fun onClick(view: View?) {
        if (view?.id == R.id.linearLayout_back) {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        checkDefaultDialer()
        setBackIcon()
        loadList()

    }


    private fun setBackIcon() {
        val prefs = getSharedPreferences("Settings", MODE_PRIVATE)
        val lang = prefs.getString("My_lang", "")
        if (lang != null) {
            when (lang) {
                "En" -> {

                }
                "fr" -> {

                }
                "ur" -> {
                    img_back.setImageDrawable(getDrawable(R.drawable.ic_baseline_chevron_right_24))
                }
                "hi" -> {

                }
                "ar" -> {
                    img_back.setImageDrawable(getDrawable(R.drawable.ic_baseline_chevron_right_24))
                }
                "zh" -> {

                }
            }

        }
    }
    private fun checkDefaultDialer() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return

        val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
        isAlreadyDefaultDialer = packageName == telecomManager.defaultDialerPackage



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
            //For setting dialer
            val isRoleAvailable = roleManager.isRoleAvailable(RoleManager.ROLE_DIALER)
            if (isRoleAvailable) {
                // check whether your app is already holding the default Dialer app role.
                val isRoleHeld = roleManager.isRoleHeld(RoleManager.ROLE_DIALER)
                if (!isRoleHeld) {
                    val roleRequestIntent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
                    startActivityForResult(roleRequestIntent,
                        MainActivity.REQUEST_CODE_SET_DEFAULT_DIALER
                    )
                }
            }
        } else{
            val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).putExtra(
                TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
                "com.dialler.ct"
            )
            startActivityForResult(intent, MainActivity.REQUEST_CODE_SET_DEFAULT_DIALER)
        }

    }
   /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            -1 -> {
                finish()
            }
        }
    }*/

}