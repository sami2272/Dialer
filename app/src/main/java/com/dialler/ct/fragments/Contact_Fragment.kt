package com.dialler.ct.fragments

import `in`.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dialler.ct.R
import com.dialler.ct.activity.MainActivity
import com.dialler.ct.model.AlphabetItem
import com.dialler.ct.model.ContactModelContacts
import com.example.stickeyheaderrcv.ContactListStickeyHeaderAdapter
import com.google.android.material.snackbar.Snackbar
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import java.util.*

class Contact_Fragment : Fragment(), View.OnClickListener {

    var recyclerview_contact_list: IndexFastScrollRecyclerView? = null
    var tv_add_contact: TextView? = null
    var tv_label_contact_not_found: TextView? = null
    var arrayList = ArrayList<ContactModelContacts>()
    lateinit var contactListStickeyHeaderAdapter : ContactListStickeyHeaderAdapter

    lateinit var ed_search_view_contacts:EditText

    //===============================================================================
    private val mDataArray: MutableList<String> = ArrayList()
    private var mAlphabetItems: MutableList<AlphabetItem>? = ArrayList()
    //===============================================================================

    private lateinit var contactsView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // storelang()
        contactsView =  inflater.inflate(R.layout.fragment_contact_, container, false)

        initViews(contactsView)
        setOnClicklistnerer()

        ed_search_view_contacts.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filter(s.toString());
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })

        return contactsView
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(activity as MainActivity, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity as MainActivity, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED
                ) {
            // Do something ...
            arrayList.clear()
            getContactsListForRecyclerView()
        }else{
            requestReadContact()
        }
    }

    private fun requestReadContact() {
        Permissions.check(activity as MainActivity, Manifest.permission.READ_CONTACTS, null,
            object : PermissionHandler() {
                override fun onGranted() {
                    requestWriteContact()
                }

                override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                    super.onDenied(context, deniedPermissions)
                    //do nothing
                    (activity as MainActivity).finish()
                }
            })
    }

    private fun requestWriteContact() {
        Permissions.check(activity as MainActivity, Manifest.permission.WRITE_CONTACTS, null,
                object : PermissionHandler() {
                    override fun onGranted() {
                        arrayList.clear()
                        getContactsListForRecyclerView()
                    }

                    override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                        super.onDenied(context, deniedPermissions)
                        //do nothing
                        (activity as MainActivity).finish()
                    }
                })
    }

    private fun initViews(contactsView: View) {
        recyclerview_contact_list = contactsView.findViewById(R.id.recyclerview_contact_list)
        ed_search_view_contacts   = contactsView.findViewById(R.id.ed_search_view_contacts)
        tv_add_contact   = contactsView.findViewById(R.id.tv_add_contact)
        tv_label_contact_not_found   = contactsView.findViewById(R.id.tv_label_contact_not_found)

       // contactListStickeyHeaderAdapter = ContactListStickeyHeaderAdapter()
    }

    private fun initialiseUI() {

        recyclerview_contact_list!!.setIndexTextSize(12)
        recyclerview_contact_list!!.setIndexBarColor("#FFFFFF")
        recyclerview_contact_list!!.setIndexBarCornerRadius(0)
        recyclerview_contact_list!!.setIndexBarTransparentValue(0.4.toFloat())
        recyclerview_contact_list!!.setIndexbarMargin(0f)
        recyclerview_contact_list!!.setIndexbarWidth(40f)
        recyclerview_contact_list!!.setPreviewPadding(0)
        recyclerview_contact_list!!.setIndexBarTextColor("#000000")
        recyclerview_contact_list!!.setPreviewTextSize(60)
        recyclerview_contact_list!!.setPreviewColor("#FFFFFF")
        recyclerview_contact_list!!.setPreviewTextColor("#33334c")
        recyclerview_contact_list!!.setPreviewTransparentValue(0.6f)
        recyclerview_contact_list!!.setIndexBarVisibility(true)
        recyclerview_contact_list!!.setIndexBarStrokeVisibility(true)
        recyclerview_contact_list!!.setIndexBarStrokeWidth(0)
        recyclerview_contact_list!!.setIndexBarStrokeColor("#FFFFFF")
        recyclerview_contact_list!!.setIndexbarHighLightTextColor("#33334c")
        recyclerview_contact_list!!.setIndexBarHighLightTextVisibility(true)
    }

    private fun initialiseData() {

        Log.d("checkList", (mDataArray as MutableList<String>?)?.size.toString())

        mAlphabetItems = ArrayList()
        val strAlphabets: MutableList<String> = java.util.ArrayList()
        for (i in mDataArray.indices) {
            val name = mDataArray[i]
            if (name.trim { it <= ' ' }.isEmpty()) continue
            val word = name.substring(0, 1)
            if (!strAlphabets.contains(word)) {
                strAlphabets.add(word)
                (mAlphabetItems as ArrayList<AlphabetItem>).add(AlphabetItem(i, word, false))
            }
        }
    }

    private fun setOnClicklistnerer() {
        tv_add_contact!!.setOnClickListener(this)
    }

    private fun filter(text: String) {
        val newList: ArrayList<ContactModelContacts> = ArrayList()
        if (text.isNotEmpty()){
            for (item in arrayList) {
                if (item.name?.trim()?.toLowerCase()?.contains(text.toLowerCase().trim()) == true) {
                    newList.add(item)
                }
            }

            val linearLayoutManager = LinearLayoutManager(activity)
            recyclerview_contact_list?.layoutManager = linearLayoutManager

            //contactListStickeyHeaderAdapter.setData(newList, activity as MainActivity)
            contactListStickeyHeaderAdapter = context?.let{ ContactListStickeyHeaderAdapter(newList, it as MainActivity) }!!
            recyclerview_contact_list?.adapter = contactListStickeyHeaderAdapter

            if (newList.size>0){
                tv_label_contact_not_found?.visibility = View.GONE
            }else{
                tv_label_contact_not_found?.visibility = View.VISIBLE
            }
//      recyclerview_contact_list?.addItemDecoration(StickyRecyclerHeadersDecoration(contactListStickeyHeaderAdapter))
//      adapter = MainAdapterContacts(activity, newList)
//      set adapter
//      recycler_view!!.adapter = adapter
        }else{
            if (ContextCompat.checkSelfPermission(activity as MainActivity, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity as MainActivity, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED
            ) {
                // Do something ...
                newList.clear()
                getContactsListForRecyclerView()
            }else{
                requestReadContact()
            }
        }


    }

    private fun getContactsListForRecyclerView() {

        arrayList.clear()

        val filter = "" + ContactsContract.Contacts.HAS_PHONE_NUMBER + " > 0 and " + ContactsContract.CommonDataKinds.Phone.TYPE + "=" + ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE

        val cursorPhones: Cursor? = activity?.contentResolver?.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.Contacts.PHOTO_URI,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ),
            filter,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        )

        if (cursorPhones != null) {
            while (cursorPhones.moveToNext()) {

                val model = ContactModelContacts()
                //set id
                model.id = cursorPhones.getString(cursorPhones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                //Set name
                model.name = cursorPhones.getString(cursorPhones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                //Set number
                model.number = cursorPhones.getString(cursorPhones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                //set image
                model.image = cursorPhones.getString(cursorPhones.getColumnIndex(ContactsContract.Contacts.PHOTO_URI))

                arrayList.add(model)

            }

            //closing the phone cursor
            cursorPhones.close()
        }else{
            Toast.makeText(activity, getString(R.string.went_worng), Toast.LENGTH_SHORT).show()
        }


        // Added by Hassan Raza on 06/sep/2021
        // filtering contactList
        val contactListFiltered = ArrayList<ContactModelContacts>()

        for ((a, item) in arrayList.withIndex()) {

            var b = a + 1
            if (arrayList.size != b) {
                var ab=item.number!!.replace("[()\\s-]".toRegex(),"")
                var bc =arrayList[b].number!!.replace("[\\s\\-]".toRegex(),"")
                if (ab != bc) {
                    mDataArray.add(item.name.toString())
                    contactListFiltered.add(item)
                }
            } else {
                mDataArray.add(item.name.toString())
                contactListFiltered.add(item)

            }
        }

        val linearLayoutManager = LinearLayoutManager(activity)
        recyclerview_contact_list?.layoutManager = linearLayoutManager

        //contactListStickeyHeaderAdapter.setData(contactListFiltered, activity as MainActivity)
        contactListStickeyHeaderAdapter = context?.let{ ContactListStickeyHeaderAdapter(contactListFiltered, it as MainActivity) }!!

        recyclerview_contact_list?.adapter = contactListStickeyHeaderAdapter

        if (0 == recyclerview_contact_list!!.itemDecorationCount) {
            initialiseUI()
            initialiseData()
            recyclerview_contact_list!!.addItemDecoration(StickyRecyclerHeadersDecoration(contactListStickeyHeaderAdapter))
        }

        Objects.requireNonNull(recyclerview_contact_list!!.layoutManager)?.scrollToPosition(0)

    }

    override fun onClick(view: View?) {
        if (view?.id== R.id.tv_add_contact){
           addNewContact()
        }
    }

    private fun addNewContact() {
        Log.e("TESTING","clickl")
        // Creates a new Intent to insert a contact
        val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
            // Sets the MIME type to match the Contacts Provider
            type = ContactsContract.RawContacts.CONTENT_TYPE
            Log.e("TESTING","click2")
        }
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, "")
        Log.e("TESTING","click3")

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R){
            // Do something for lollipop and above versions
            startActivity(intent)
        } else{
            // do something for phones running an SDK before lollipop
            if (intent.resolveActivity(activity!!.packageManager) != null) {
                startActivity(intent)
            }
        }


    }

    private fun storelang() {
        val prefs = context?.getSharedPreferences("Settings", AppCompatActivity.MODE_PRIVATE)
        val lang = prefs?.getString("My_lang", "")
        if (lang != null) {
            setLocale1(lang)
        }
    }

    private fun setLocale1(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        activity?.baseContext?.resources?.updateConfiguration(config,    activity?.baseContext?.resources!!.displayMetrics)
        val editor =  activity?.getSharedPreferences("Settings", AppCompatActivity.MODE_PRIVATE)?.edit()
        if (editor != null) {
            editor.putString("My_lang", lang)
            editor.apply()
        }
//        if (editor != null) {
//
//        }
    }
}