package com.dialler.ct.fragments

import android.Manifest
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.dialler.ct.R
import com.dialler.ct.activity.MainActivity
import com.dialler.ct.helpingclasses.ManageDualClass
import com.google.android.material.snackbar.Snackbar
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import java.lang.reflect.Method
import java.util.*


class Keypad_Fragment : Fragment(), View.OnClickListener,View.OnTouchListener,View.OnLongClickListener {

    lateinit var llbtn1: View
    lateinit var llbtn2: View
    lateinit var llbtn3: View
    lateinit var llbtn4: View
    lateinit var llbtn5: View
    lateinit var llbtn6: View
    lateinit var llbtn7: View
    lateinit var llbtn8: View
    lateinit var llbtn9: View
    lateinit var llbtn0: View
    lateinit var llbtnstar: View
    lateinit var llbtnhash: View

    lateinit var ll_send_sms: LinearLayout
    lateinit var ll_add_to_contact: LinearLayout
    lateinit var ll_create_new_contact: LinearLayout

    lateinit var btncall: ImageButton

    lateinit var ibtnBackspace: ImageView
    lateinit var tv_add_number: TextView
    lateinit var tv_contact_name: TextView

    lateinit var downdialpad: ImageView

    lateinit var ed_phone: EditText

    private lateinit var mToneGenerator: ToneGenerator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        val rootView : View = inflater.inflate(R.layout.fragment_keypad_, container, false)

        initView(rootView)
        setOnClickListener()
        setOnTouchListener()
        setOnLongClickListener()
        setDefaultValue()

        ed_phone .addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.length > 10) {
                    if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                        if (getContactName(s.toString(), activity) == "") {
                            tv_add_number.visibility = View.VISIBLE
                        } else {
                            tv_add_number.visibility = View.GONE
                        }
                        tv_contact_name.visibility = View.VISIBLE
                        tv_contact_name.text = "" + getContactName(s.toString(), activity)
                    } else {
                        requestContact(s)
                    }
                } else if (s.length <= 10) {
                    tv_contact_name.visibility = View.GONE
                    tv_add_number.visibility = View.VISIBLE
                }
                if (s.isEmpty()) {
                    ibtnBackspace.visibility=View.GONE
                    tv_contact_name.visibility = View.GONE
                    tv_add_number.visibility = View.GONE
                }else{
                    ibtnBackspace.visibility=View.VISIBLE
                }
                if (s.length > 11) {
                    tv_contact_name.visibility = View.GONE
                    tv_add_number.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
        ed_phone.setSelection(ed_phone.text.toString().length)//placing cursor at the end of the text

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ed_phone.setShowSoftInputOnFocus(false)
        }
        else {
            try {
                val method: Method = EditText::class.java.getMethod(
                        "setShowSoftInputOnFocus", *arrayOf<Class<*>?>(Boolean::class.javaPrimitiveType))
                method.setAccessible(true)
                method.invoke(ed_phone, false)
            } catch (e: Exception) {
                // ignore
            }
        }

        return rootView
    }

    private fun requestContact(s: Editable) {
        Permissions.check(activity as MainActivity, Manifest.permission.READ_CONTACTS, null,
                object : PermissionHandler() {
                    override fun onGranted() {
                        if (getContactName(s.toString(), activity) == "") {
                            tv_add_number.visibility = View.VISIBLE
                        } else {
                            tv_add_number.visibility = View.GONE
                        }
                        tv_contact_name.visibility = View.VISIBLE
                        tv_contact_name.text = "" + getContactName(s.toString(), activity)

                    }

                    override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                        super.onDenied(context, deniedPermissions)
                        //do nothing
                        (activity as MainActivity).finish()
                    }
                })
    }

    private fun initView(rootView: View) {
        //AdsManager.instance.loadBannerAd(relativeLayout,this)
        llbtn1 = rootView.findViewById(R.id.view1)
        llbtn2 = rootView.findViewById(R.id.view2)
        llbtn3 = rootView.findViewById(R.id.view3)

        llbtn4 = rootView.findViewById(R.id.view4)
        llbtn5 = rootView.findViewById(R.id.view5)
        llbtn6 = rootView.findViewById(R.id.view6)

        llbtn7 = rootView.findViewById(R.id.view7)
        llbtn8 = rootView.findViewById(R.id.view8)
        llbtn9 = rootView.findViewById(R.id.view9)

        llbtnstar = rootView.findViewById(R.id.view10)
        llbtn0    = rootView.findViewById(R.id.view0)
        llbtnhash = rootView.findViewById(R.id.view11)

        btncall   = rootView.findViewById(R.id.ibtncall)

        ibtnBackspace = rootView.findViewById(R.id.ibtnBackspace)

        ll_send_sms = rootView.findViewById(R.id.ll_send_sms)
        ll_add_to_contact = rootView.findViewById(R.id.ll_add_to_contact)
        ll_create_new_contact = rootView.findViewById(R.id.ll_create_new_contact)

        ed_phone       = rootView.findViewById(R.id.ed_phone)
        tv_add_number  = rootView.findViewById(R.id.tv_add_number)
        downdialpad    = rootView.findViewById(R.id.ib_down_dialpad)
        tv_contact_name= rootView.findViewById(R.id.tv_contact_name)

        val am = context?.getSystemService(AUDIO_SERVICE) as AudioManager
        val volumeLevel = am.getStreamVolume(AudioManager.STREAM_RING)
        mToneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, volumeLevel * 14)

    }

    private fun setDefaultValue(){
        var number :String?=""
        number=arguments?.getString("defaultNumber").toString()
        if (number != null  && number!="null") {
            ed_phone.setText(number.toString())
            Log.e("TAG", "number: " + number)
        }
    }

    private fun makephonecall() {
        var number = ed_phone?.text.toString().trim()
 //       var dial = "tel:${number}"
//        val intent = Intent(Intent.ACTION_CALL, Uri.parse(dial.replace("#", Uri.encode("#"))))
//      startActivity(intent)
        val manageDualClass = ManageDualClass(requireContext())
        manageDualClass.alertDialogShow(number)
    }

    private fun setOnClickListener() {
        llbtn1.setOnClickListener(this)
        llbtn2.setOnClickListener(this)
        llbtn3.setOnClickListener(this)
        llbtn4.setOnClickListener(this)
        llbtn5.setOnClickListener(this)
        llbtn6.setOnClickListener(this)
        llbtn7.setOnClickListener(this)
        llbtn8.setOnClickListener(this)
        llbtn9.setOnClickListener(this)
        llbtn0.setOnClickListener(this)
        btncall.setOnClickListener(this)
        llbtnstar.setOnClickListener(this)
        llbtnhash.setOnClickListener(this)
        ll_send_sms.setOnClickListener(this)
        tv_add_number.setOnClickListener(this)
        ibtnBackspace.setOnClickListener(this)
        ll_add_to_contact.setOnClickListener(this)
        ll_create_new_contact.setOnClickListener(this)
    }

    private fun setOnLongClickListener(){
        llbtn0.setOnLongClickListener(this)
        ibtnBackspace.setOnLongClickListener(this)
    }

    private fun setOnTouchListener() {
        llbtn1.setOnTouchListener(this)
        llbtn2.setOnTouchListener(this)
        llbtn3.setOnTouchListener(this)
        llbtn4.setOnTouchListener(this)
        llbtn5.setOnTouchListener(this)
        llbtn6.setOnTouchListener(this)
        llbtn7.setOnTouchListener(this)
        llbtn8.setOnTouchListener(this)
        llbtn9.setOnTouchListener(this)
        llbtn0.setOnTouchListener(this)
        btncall.setOnTouchListener(this)
        llbtnstar.setOnTouchListener(this)
        llbtnhash.setOnTouchListener(this)
        ll_send_sms.setOnTouchListener(this)
        tv_add_number.setOnTouchListener(this)
        ibtnBackspace.setOnTouchListener(this)
        ll_add_to_contact.setOnTouchListener(this)
        ll_create_new_contact.setOnTouchListener(this)
    }

    private fun requestMakePhoneCall() {
        Permissions.check(activity, Manifest.permission.CALL_PHONE, null,
                object : PermissionHandler() {
                    override fun onGranted() {
                        makephonecall()
                    }

                    override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                        super.onDenied(context, deniedPermissions)
                        (activity as MainActivity).finish()
                    }
                })
    }

    private fun getContactName(phoneNumber: String?, context: FragmentActivity?): String? {

        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
        val projection = arrayOf<String>(ContactsContract.PhoneLookup.DISPLAY_NAME)
        var contactName = ""
        val cursor: Cursor? = context?.contentResolver?.query(uri, projection, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0)
            }
            cursor.close()
        }
        return contactName
    }

    override fun onClick(view: View?) {

        if (view?.id== R.id.view1){
            val start: Int = ed_phone.getSelectionStart() //this is to get the the cursor position
            val s = "1"
            ed_phone.getText().insert(start, s) //this will get the text and insert the String s into   the current position
        }
        if (view?.id== R.id.view2){
            val start: Int = ed_phone.getSelectionStart() //this is to get the the cursor position
            val s = "2"
            ed_phone.getText().insert(start, s) //this will get the text and insert the String s into   the current position
        }
        if (view?.id== R.id.view3){
            val start: Int = ed_phone.getSelectionStart() //this is to get the the cursor position
            val s = "3"
            ed_phone.getText().insert(start, s) //this will get the text and insert the String s into   the current position
        }
        if (view?.id== R.id.view4){
            val start: Int = ed_phone.getSelectionStart() //this is to get the the cursor position
            val s = "4"
            ed_phone.getText().insert(start, s) //this will get the text and insert the String s into   the current position
        }
        if (view?.id== R.id.view5){
            val start: Int = ed_phone.getSelectionStart() //this is to get the the cursor position
            val s = "5"
            ed_phone.getText().insert(start, s) //this will get the text and insert the String s into   the current position
        }
        if (view?.id== R.id.view6){
            val start: Int = ed_phone.getSelectionStart() //this is to get the the cursor position
            val s = "6"
            ed_phone.getText().insert(start, s) //this will get the text and insert the String s into   the current position
        }
        if (view?.id== R.id.view7){
            val start: Int = ed_phone.getSelectionStart() //this is to get the the cursor position
            val s = "7"
            ed_phone.getText().insert(start, s) //this will get the text and insert the String s into   the current position
        }
        if (view?.id== R.id.view8){

            val start: Int = ed_phone.getSelectionStart() //this is to get the the cursor position
            val s = "8"
            ed_phone.getText().insert(start, s) //this will get the text and insert the String s into   the current position
        }
        if (view?.id== R.id.view9){
            val start: Int = ed_phone.getSelectionStart() //this is to get the the cursor position
            val s = "9"
            ed_phone.getText().insert(start, s) //this will get the text and insert the String s into   the current position
        }
        if (view?.id== R.id.view0){
            val start: Int = ed_phone.getSelectionStart() //this is to get the the cursor position
            val s = "0"
            ed_phone.getText().insert(start, s) //this will get the text and insert the String s into   the current position
        }
        if (view?.id== R.id.view10){
            val start: Int = ed_phone.getSelectionStart() //this is to get the the cursor position
            val s = "*"
            ed_phone.getText().insert(start, s) //this will get the text and insert the String s into   the current position
        }
        if (view?.id== R.id.view11){
            val start: Int = ed_phone.getSelectionStart() //this is to get the the cursor position
            val s = "#"
            ed_phone.getText().insert(start, s) //this will get the text and insert the String s into   the current position
        }
        if (view?.id== R.id.ibtncall){
            if (ContextCompat.checkSelfPermission(activity as MainActivity, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                if(!ed_phone.text.isEmpty()) {
                    makephonecall()
                }else{
                    val snack = Snackbar.make(view,getString(R.string.str_enter_number),Snackbar.LENGTH_LONG)
                    snack.show()
                }

            }else{
                requestMakePhoneCall()
            }
        }
        if (view?.id== R.id.ibtnBackspace){

            val editable: Editable = ed_phone.getText()
            val charCount: Int = ed_phone.getSelectionEnd()
            if (charCount > 0) {
                editable.delete(charCount - 1, charCount)
            }
        }
        if (view?.id== R.id.tv_add_number){

            val insertnumber = ed_phone.text.toString()
            // Creates a new Intent to insert a contact
            val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
                // Sets the MIME type to match the Contacts Provider
                type = ContactsContract.RawContacts.CONTENT_TYPE
            }
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, insertnumber)
            startActivity(intent)
        }
        if (view?.id== R.id.ll_add_to_contact){
            // The number which you want to save
            val insertnumber = ed_phone.text.toString()
            val i = Intent(Intent.ACTION_INSERT_OR_EDIT)
            i.type = ContactsContract.Contacts.CONTENT_ITEM_TYPE
            i.putExtra(ContactsContract.Intents.Insert.PHONE, insertnumber)
            startActivity(i)
        }
        if (view?.id== R.id.ll_send_sms){
            // The number on which you want to send SMS
            val numbersms = ed_phone.text.toString()
            //  startActivity(Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", numbersms, null)))
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + numbersms))
            intent.putExtra("sms_body", "")
            startActivity(intent)
        }
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        if (view?.id== R.id.view1){
            playTuneOnLongPress(1, event!!.action)
        }
        if (view?.id== R.id.view2){
            playTuneOnLongPress(2, event!!.action)
        }
        if (view?.id== R.id.view3){
            playTuneOnLongPress(3, event!!.action)
        }
        if (view?.id== R.id.view4){
            playTuneOnLongPress(4, event!!.action)
        }
        if (view?.id== R.id.view5){
            playTuneOnLongPress(5, event!!.action)
        }
        if (view?.id== R.id.view6){
            playTuneOnLongPress(6, event!!.action)
        }
        if (view?.id== R.id.view7){
            playTuneOnLongPress(7, event!!.action)
        }
        if (view?.id== R.id.view8){
            playTuneOnLongPress(8, event!!.action)
        }
        if (view?.id== R.id.view9){
            playTuneOnLongPress(9, event!!.action)
        }
        if (view?.id== R.id.view0){
            playTuneOnLongPress(10, event!!.action)
        }
        if (view?.id== R.id.view10){
            playTuneOnLongPress(11, event!!.action)
        }
        if (view?.id== R.id.view11){
            playTuneOnLongPress(12, event!!.action)
        }

       return false
    }


    private fun playTuneOnLongPress(num: Int, start: Int) {
        if (start==MotionEvent.ACTION_DOWN) {
            mToneGenerator.startTone(num)
        } else if (start==MotionEvent.ACTION_UP) {
            mToneGenerator.stopTone()
        }
    }

    override fun onLongClick(view: View?): Boolean {
        if (view?.id== R.id.view0){
            val start: Int = ed_phone.getSelectionStart() //this is to get the the cursor position
            val s = "+"
            ed_phone.text.insert(start, s) //this will get the text and insert the String s into   the current position
        }
        if (view?.id== R.id.ibtnBackspace){
            //ed_phone.setText("")
            val pos: Int = ed_phone.getSelectionStart()
            val myText: String = ed_phone.getText().toString()
            val subStringed = myText.substring(pos, myText.length)
            ed_phone.setText(subStringed)
        }
        return true
    }

}