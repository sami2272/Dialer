package com.dialler.ct.activity

import android.Manifest
import android.app.role.RoleManager
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.BlockedNumberContract
import android.provider.ContactsContract
import android.telecom.TelecomManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dialler.ct.R
import com.dialler.ct.helpingclasses.ManageDualClass
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import kotlinx.android.synthetic.main.fragment_keypad_.*
import java.util.*


class ContactDetailActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var tv_mobile               : TextView
    lateinit var tv_label_block_User     : TextView
    lateinit var tv_phone_number_name    : TextView
    lateinit var tv_person_short_name    : TextView
    lateinit var tv_label_edit_contact   : TextView
    lateinit var tv_label_send_message   : TextView
    lateinit var tv_label_share_contact  : TextView
    lateinit var tv_label_add_favorites_2: TextView

    lateinit var img_back     : ImageView
    lateinit var img_edit     : ImageView
    lateinit var img_profile  : ImageView
    lateinit var img_favorites: ImageView
    //lateinit var img_dail_phone: ImageView

    lateinit var linearLayout_back: LinearLayout

    lateinit var cardView_mobile   : CardView
    lateinit var cardView_favorites: CardView
    lateinit var cardView_make_call: CardView
    lateinit var cardView_threedotmenu : CardView
    lateinit var cardView_send_message : CardView
    lateinit var cardView_block_contact: CardView
    lateinit var cardView_delete_contact:CardView

    var contactId      :String = ""
    var phoneLookUp    :String = ""
    var favouritesCheck:Boolean = false

    lateinit var contentResolver1: ContentResolver
    lateinit var extras:Bundle

    var isAlreadyDefaultDialer:Boolean = false

   lateinit  var manageDualClass : ManageDualClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)
        manageDualClass = ManageDualClass(this@ContactDetailActivity)
        checkDefaultDialer()
    }

    private  fun initView() {

        //AdsManager.instance.showInterstitialGetStarted()
        //AdsManager.instance.showInterstitial()

        tv_mobile= findViewById(R.id.tv_mobile)
        tv_label_block_User= findViewById(R.id.tv_label_block_User)
        tv_phone_number_name= findViewById(R.id.tv_phone_number_name)
        tv_person_short_name= findViewById(R.id.tv_person_short_name)
        tv_label_edit_contact= findViewById(R.id.tv_label_edit_contact)
        tv_label_send_message= findViewById(R.id.tv_label_send_message)
        tv_label_share_contact= findViewById(R.id.tv_label_share_contact)
        tv_label_add_favorites_2= findViewById(R.id.tv_label_add_favorites_2)

        img_back      = findViewById(R.id.img_back)
        img_profile   = findViewById(R.id.img_profile)
        img_favorites = findViewById(R.id.img_favorites)
        img_edit      = findViewById(R.id.img_threedotmenu)
        //img_dail_phone= findViewById(R.id.img_dail_phone)

        linearLayout_back = findViewById(R.id.linearLayout_back)

        cardView_mobile        =findViewById(R.id.cardView_mobile)
        cardView_make_call     =findViewById(R.id.cardView_make_call)
        cardView_favorites     =findViewById(R.id.cardView_favorites)
        cardView_threedotmenu  =findViewById(R.id.cardView_threedotmenu)
        cardView_send_message  =findViewById(R.id.cardView_send_message)
        cardView_block_contact =findViewById(R.id.cardView_block_contact)
        cardView_delete_contact=findViewById(R.id.cardView_delete_contact)

        contentResolver1 = this@ContactDetailActivity.contentResolver
        extras = intent.extras!!
    }

    private  fun checkFavourtite() {

        val uri = ContactsContract.Contacts.CONTENT_URI
        //Sort by ascending
        val sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        //Initialize cursor
        val cursor = this@ContactDetailActivity.contentResolver?.query(uri, null, "starred=?", arrayOf("1"), sort)
        //Check condition

        if (cursor != null) {
            if (cursor!!.count > 0) {
                //When count is greater than 0
                //Use while loop
                while (cursor.moveToNext()) {
                    //Cursor move to next
                    //Get contact id
                    val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    //Initialize phone u
                    val uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                    //Initialize selection
                    val selection = (ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?")
                    //Initialize phone cursor
                    val phoneCursor = this@ContactDetailActivity?.contentResolver?.query(uriPhone, null, selection, arrayOf(id), null)
                    //Check condition
                    if (phoneCursor!!.moveToNext()) {
                        //When phone cursor move to next
                        val number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        //Initialize contact model
                        if (extras!!.getString("number")?.replace(" ", "")?.trim().toString()==number.replace(" ", "")?.trim().toString()){
                            favouritesCheck = true
                            img_favorites.setImageDrawable(application.resources.getDrawable(R.drawable.ic_favorites))
                            return
                        }else{
                            favouritesCheck = false
                            img_favorites.setImageDrawable(application.resources.getDrawable(R.drawable.ic_outline_star_border_24))
                        }

                        phoneCursor.close()
                    }
                }
                //Close cursor
                cursor.close()
            }
        }else{
            Toast.makeText(this@ContactDetailActivity, getString(R.string.went_worng), Toast.LENGTH_SHORT).show()
        }
    }

    private  fun editContactInfo() {

        Log.e("TAG", "contactId: "+contactId )



        if (contactId=="-1"){
            Log.e("TAG", "contactId******: "+contactId )

            val insertnumber = tv_mobile.text.toString()
            // Creates a new Intent to insert a contact
            val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
                // Sets the MIME type to match the Contacts Provider
                type = ContactsContract.RawContacts.CONTENT_TYPE
            }
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, insertnumber)

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R){
                // Do something for lollipop and above versions
                startActivityForResult(intent, 100)
            } else{
                // do something for phones running an SDK before lollipop
                if (intent.resolveActivity(this!!.packageManager) != null) {
                    startActivityForResult(intent, 100)
                }
            }

        }else{
            val intent = Intent(Intent.ACTION_EDIT)
            val contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId.toLong())
            intent.data = contactUri
            intent.putExtra("finishActivityOnSaveCompleted", true)

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R){
                // Do something for lollipop and above versions
                startActivityForResult(intent, 100)
            } else{
                // do something for phones running an SDK before lollipop
                if (intent.resolveActivity(this!!.packageManager) != null) {
                    startActivityForResult(intent, 100)
                }
            }
        }

    }

    private  fun getPhoneLookUpID() {

        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(extras!!.getString("number")?.replace(" ", "")?.trim()))
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID)
        val cursor = contentResolver1.query(uri, projection, null, null, null)

        if (cursor != null) {
            cursor.moveToFirst()
            while (cursor.moveToNext()) {
                val contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME))
                phoneLookUp = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID))
            }
            cursor.close()
        }
    }

    private  fun makeCallToNumber() {
        var number = tv_mobile?.text.toString()
        var dial = "tel:${number}"
        manageDualClass.alertDialogShow(number)
//        val intent = Intent(Intent.ACTION_CALL, Uri.parse(dial))
//        startActivity(intent)
    }

    private  fun setDataFromExtras() {
        if (extras != null) {

            setNameAndPhoneNumberFromExtra()

            //getLogsByNumber(arrayOf("" + extras!!.getString("number")?.replace(" ", "")?.trim()))

            contactId = getContactID(contentResolver1, extras!!.getString("number")?.replace(" ", "")?.trim().toString()).toString()

            Log.e("TAG", "contactId: " + contactId)
            if (ContextCompat.checkSelfPermission(this@ContactDetailActivity, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                // Do something ...
                getPhoneLookUpID()
            }else{
                requestPhoneState()
            }

//            Log.e("TAG", "contactId: " + contactId)
//            Log.e("TAG", "phoneLookUp: " + phoneLookUp)

            //Log.e("TAG", "photoUri: " + extras!!.getString("image"))

            //phoneLookUp.toString()!="" && openDisplayPhoto(phoneLookUp.toLong())
            //if (phoneLookUp.toString()!="" && openDisplayPhoto(phoneLookUp.toLong())!= null){
            if (getContactPhotoURI(contactId) != null && contactId!="-1" ){
                //val bitmap1: Bitmap = BitmapFactory.decodeStream(openDisplayPhoto(phoneLookUp.toLong()))
                tv_person_short_name.visibility = View.GONE
                img_profile.visibility = View.VISIBLE


                //img_profile.setImageURI(parse(getContactList(contactId)))

                /*Log.e("TAG", "photoUri: " + extras!!.getString("image"))
                img_profile.visibility = View.VISIBLE
                img_profile.setImageURI(Uri.parse(extras!!.getString("image")))*/
                //img_profile?.setImageBitmap(bitmap1)

//                 Glide
//                .with(this)
//                .load(bitmap1).circleCrop()
//                .into(img_profile)

                Glide
                        .with(this@ContactDetailActivity)
                        .asBitmap()
                        .load(getContactPhotoURI(contactId))
                        .apply(RequestOptions().circleCrop())
                        .placeholder(R.drawable.circlebg)
                        .into(img_profile)

                Log.d("TAG", "contactId: " + contactId)
            }
            else{
                // img_profile.visibility = View.GONE

                if (extras!!.getString("name").toString() == "" ){
                    tv_person_short_name.text ="U"
                }else{

                    val test = extras!!.getString("name")
                    Log.d("TAG", "test: " + test)

                    if (test != null) {
                        val nameParts: List<String> = test?.split(" ")!!
                        val firstName = nameParts[0]
                        val firstNameChar = firstName[0]
                        Log.d("TAG", "firstName: " + firstName)
                        Log.d("TAG", "firstNameChar: " + firstNameChar)

                        tv_person_short_name.text = firstNameChar.toString()

                        /*if (nameParts.size > 1) {
                            val lastName = nameParts[nameParts.size - 1]
                            val lastNameChar = lastName[0]
                            Log.d("TAG", "lastName" + lastName)
                            Log.d("TAG", "lastNameChar" + lastNameChar)
                            tv_person_short_name.text = firstNameChar.toString()
                        } else {
                            tv_person_short_name.text = firstNameChar.toString()
                        }*/
                    }
                }
            }

            if (ContextCompat.checkSelfPermission(this@ContactDetailActivity, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                if (isBlocked()){
                    tv_label_block_User.text = getString(R.string.unblock_caller)
                }
            }else{
                requestContact()
            }
        }
    }

    private  fun checkDefaultDialer() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return

        val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
        isAlreadyDefaultDialer = packageName == telecomManager.defaultDialerPackage

        if (isAlreadyDefaultDialer){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                initView()
                setOnClickListener()
                setDataFromExtras()
                checkFavourtite()
            }else{
                requestContactCallAllFunction()
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
                    startActivityForResult(roleRequestIntent, MainActivity.REQUEST_CODE_SET_DEFAULT_DIALER)
                }
            }
        } else{
            val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).putExtra(
                    TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
                    "com.dialler.ct")
            startActivityForResult(intent, MainActivity.REQUEST_CODE_SET_DEFAULT_DIALER)
        }

    }

    private  fun setOnClickListener() {
        img_back?.setOnClickListener(this)
        img_edit?.setOnClickListener(this)
        img_favorites.setOnClickListener(this)

        tv_label_edit_contact.setOnClickListener(this)
        tv_label_send_message.setOnClickListener(this)
        tv_label_share_contact.setOnClickListener(this)
        tv_label_add_favorites_2.setOnClickListener(this)
        //img_dail_phone?.setOnClickListener(this)

        cardView_mobile.setOnClickListener(this)
        linearLayout_back.setOnClickListener(this)
        cardView_favorites.setOnClickListener(this)
        cardView_make_call.setOnClickListener(this)
        cardView_threedotmenu.setOnClickListener(this)
        cardView_send_message.setOnClickListener(this)
        cardView_block_contact.setOnClickListener(this)
        cardView_delete_contact.setOnClickListener(this)
    }

    private  fun isBlocked(): Boolean {

        try {

            val cursorPhones = contentResolver1?.query(
                    BlockedNumberContract.BlockedNumbers.CONTENT_URI, arrayOf<String>(
                    BlockedNumberContract.BlockedNumbers.COLUMN_ID,
                    BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER,
                    BlockedNumberContract.BlockedNumbers.COLUMN_E164_NUMBER
            ), null, null, null
            )

            if (cursorPhones != null){
                while (cursorPhones?.moveToNext()!!) {

                    //Log.e("TAG", "number: "+tv_mobile?.text.toString().trim()+" == "+cursorPhones.getString(cursorPhones.getColumnIndex(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER)))
                    if (tv_mobile?.text.toString().trim() == cursorPhones.getString(cursorPhones.getColumnIndex(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER)) ||
                            tv_mobile?.text.toString().trim() == cursorPhones.getString(cursorPhones.getColumnIndex(BlockedNumberContract.BlockedNumbers.COLUMN_E164_NUMBER))){
                        cursorPhones.close()
                        return true
                    }
                }
                cursorPhones.close()
            }else{
                Toast.makeText(this@ContactDetailActivity, getString(R.string.went_worng), Toast.LENGTH_SHORT).show()
            }

        } catch (e: SecurityException) {
            Toast.makeText(this@ContactDetailActivity,  getString(R.string.went_worng), Toast.LENGTH_SHORT).show()
            return false
        }
            return false
        }

    private  fun sendMessageToNumber() {
        // The number on which you want to send SMS
        val numbersms = extras!!.getString("number")
        //  startActivity(Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", numbersms, null)))
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + numbersms))
        intent.putExtra("sms_body", "")
        startActivity(intent)
    }

    private  fun makeFavoriteContact() {

        if (ContextCompat.checkSelfPermission(this@ContactDetailActivity, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            checkFavourtite()
        }else{
            requestContact()
        }

        if (favouritesCheck){
            favouritesCheck = false

            val name = "" + extras!!.getString("name")

            val contentValues = ContentValues()
            contentValues.put(ContactsContract.Contacts.STARRED, 0)

            this@ContactDetailActivity?.contentResolver?.update(ContactsContract.Contacts.CONTENT_URI, contentValues, ContactsContract.Contacts.DISPLAY_NAME + "= ?", arrayOf(name))

            img_favorites.setImageDrawable(application.resources.getDrawable(R.drawable.ic_outline_star_border_24))
            Toast.makeText(this@ContactDetailActivity, getString(R.string.contact_remove_from_favourites), Toast.LENGTH_SHORT).show()
        }
        else{
            favouritesCheck = true

            val contentValues = ContentValues()
            contentValues.put(ContactsContract.Contacts.STARRED, 1)

            if (ContextCompat.checkSelfPermission(this@ContactDetailActivity, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                this@ContactDetailActivity?.contentResolver1?.update(ContactsContract.Contacts.CONTENT_URI, contentValues, ContactsContract.Contacts._ID + "=" + contactId, null)
            }else{
                requestWriteContact()
            }

            img_favorites.setImageDrawable(application.resources.getDrawable(R.drawable.ic_favorites))
            Toast.makeText(this@ContactDetailActivity, getString(R.string.contact_added_to_favourites), Toast.LENGTH_SHORT).show()
        }
    }

    private  fun setNameAndPhoneNumberFromExtra() {

        if (extras!!.getString("name") == null || extras!!.getString("name").toString() == ""){
            tv_phone_number_name.text = ""+extras!!.getString("number")?.replace(" ", "")?.trim()
        }else{
            //tv_phone_number_name.text = ""+extras!!.getString("name")
            tv_phone_number_name.text = ""+extras!!.getString("name")
        }

        tv_mobile.text = ""+extras!!.getString("number")?.replace(" ", "")?.trim()

    }

    private  fun showBlockDialogBoxAlertDialogBox() {
        val mAlertDialog = AlertDialog.Builder(this@ContactDetailActivity)
        mAlertDialog.setTitle(getString(R.string.confirm_to_block_number))
        mAlertDialog.setPositiveButton("OK") { dialog, id ->

            if (ContextCompat.checkSelfPermission(this@ContactDetailActivity, Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                // Do something ...
                val values = ContentValues()
                values.put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER, tv_mobile?.text.toString())
                this@ContactDetailActivity?.contentResolver.insert(
                        BlockedNumberContract.BlockedNumbers.CONTENT_URI,
                        values
                )
                tv_mobile.visibility = View.VISIBLE
                tv_label_block_User.text = (getString(R.string.unblock_this_caller))
                Toast.makeText(this@ContactDetailActivity, getString(R.string.contact_block), Toast.LENGTH_SHORT).show()
            }else{
                requestContact()
            }

        }
        mAlertDialog.setNegativeButton(getString(R.string.cancel)) { dialog, id ->

        }
        mAlertDialog.show()
    }

    private  fun showDeleteDialogBoxAlertDialogBox() {
        val mAlertDialog = AlertDialog.Builder(this@ContactDetailActivity)
        //    mAlertDialog.setIcon(R.mipmap.ic_launcher)
        mAlertDialog.setTitle(getString(R.string.delete_contact))
        mAlertDialog.setMessage("${tv_mobile.text} ${getString(R.string.delete_contact_alert)}")
        mAlertDialog.setPositiveButton("Delete") { dialog, id ->
            deleteContact(this@ContactDetailActivity, tv_mobile.text.toString(), tv_mobile.text.toString())
//            Toast.makeText(this, "${tv_mobile.text} Deleted", Toast.LENGTH_SHORT).show()
            Toast.makeText(applicationContext, "Contact Deleted", Toast.LENGTH_LONG).show()
            finish()
        }
        mAlertDialog.setNegativeButton("Cancel") { dialog, id ->
        }
        mAlertDialog.show()
    }

    private  fun showUnBlockDialogBoxAlertDialogBox() {
        var blocknumber = tv_mobile?.text.toString()

        val mAlertDialog = AlertDialog.Builder(this@ContactDetailActivity)
        mAlertDialog.setTitle(getString(R.string.confirmation_of_unblock))
        mAlertDialog.setPositiveButton(getString(R.string.ok)) { dialog, id ->

            val values = ContentValues()
            values.put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER, blocknumber)
            val uri = this@ContactDetailActivity?.contentResolver.insert(
                    BlockedNumberContract.BlockedNumbers.CONTENT_URI,
                    values
            )
            contentResolver.delete(uri!!, null, null)

            tv_label_block_User.setText("Block this Caller")
            //img_blockcontact.visibility = View.GONE
            Toast.makeText(this@ContactDetailActivity, getString(R.string.contact_unblock), Toast.LENGTH_SHORT).show()
        }
        mAlertDialog.setNegativeButton(getString(R.string.cancel)) { dialog, id ->

            //Toast.makeText(this@ContactDetailActivity, "No", Toast.LENGTH_SHORT).show()
        }
        mAlertDialog.show()

    }

    private  fun getContactPhotoURI(contactId: String): String?{

        val contact = contentResolver?.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
        )
        var contactThumb=""
        if (contact != null) {

            while (contact.moveToNext()) {
                if (contactId== contact.getString(contact.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))){
                    if (contact.getString(contact.getColumnIndex(ContactsContract.Contacts.PHOTO_URI))!=null){
                        contactThumb = contact.getString(contact.getColumnIndex(ContactsContract.Contacts.PHOTO_URI))
                    }else{
                        return null
                    }
                }
            }
            //  contact.close()
        }
        return contactThumb
    }

    private  fun shareText(subject: String?, body: String?) {
        val txtIntent = Intent(Intent.ACTION_SEND)
        txtIntent.type= "text/plain"
        txtIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        txtIntent.putExtra(Intent.EXTRA_TEXT, "Contact Name:-"+subject+"\nContact Number:-"+body)
        startActivity(Intent.createChooser(txtIntent, "Share"))
    }

    private  fun checkSetDefaultDialerResult(resultCode: Int) {
//        val message = when (resultCode) {
//            RESULT_OK       -> "User accepted request to become default dialer"
//            RESULT_CANCELED -> "User declined request to become default dialer"
//            else            -> "Unexpected result code $resultCode"
//        }

        if (resultCode == MainActivity.REQUEST_CODE_SET_DEFAULT_DIALER){

            if (ContextCompat.checkSelfPermission(this@ContactDetailActivity, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                initView()
                setOnClickListener()
                setDataFromExtras()
                checkFavourtite()
                Toast.makeText(this, getString(R.string.idialer_become_default), Toast.LENGTH_LONG).show()
            }else{
                requestContactCallAllFunction()
            }
        }else{
                Toast.makeText(this, getString(R.string.idialer_become_default), Toast.LENGTH_LONG).show()
        }
    }

    private  fun retrieveContactName(contactId: Long): String? {
        var contactName: String? = null
        val cursor: Cursor = this@ContactDetailActivity.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, arrayOf(ContactsContract.Contacts.DISPLAY_NAME),
                ContactsContract.Contacts._ID + " = ?", arrayOf(contactId.toString()), null)!!
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
        }
        cursor.close()

        Log.e("TAG", "contactName: " + contactName)
        //textView.text = contactName
        return contactName
    }

    private  fun retrieveContactNumbers(contactId: Long): String {
        val phoneNum = ArrayList<String>()
        val cursor: Cursor = this@ContactDetailActivity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(contactId.toString() + ""), null)!!
        if (cursor.count >= 1) {
            while (cursor.moveToNext()) {
                // store the numbers in an array
                val str = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                if (str != null && str.trim { it <= ' ' }.length > 0) {
                    phoneNum.add(str)
                }
            }
        }
        cursor.close()
        if (phoneNum.size>0){
            return phoneNum[phoneNum.size - 1]
        }
        else{
            return tv_mobile.text.toString()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==100){
            Log.e("TAG", "onActivityResult: ")
            if (ContextCompat.checkSelfPermission(this@ContactDetailActivity, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this@ContactDetailActivity, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                        if (contactId=="-1"){
                            contactId=getContactID(contentResolver1,tv_mobile.text.toString()).toString()
                            if (contactId=="-1"){
                                //do nothing
                            }else{

                                tv_phone_number_name.text = ""+retrieveContactName(contactId.toLong())
                                tv_mobile.text = ""+retrieveContactNumbers(contactId.toLong())?.replace(" ", "")?.trim()

                                val test = tv_phone_number_name.text

                                if (test != null) {
                                    val nameParts: List<String> = test?.split(" ")!!
                                    val firstName = nameParts[0]
                                    val firstNameChar = firstName[0]
                                    Log.d("TAG", "firstName: " + firstName)
                                    Log.d("TAG", "firstNameChar: " + firstNameChar)

                                    tv_person_short_name.text = firstNameChar.toString()
                                }
                            }
                        }else{

                            if (getContactPhotoURI(contactId)==null){
                                Log.e("TAG", "photo: "+getContactPhotoURI(contactId))
                                img_profile.visibility=View.GONE
                                tv_person_short_name.visibility=View.VISIBLE
                                val test = tv_phone_number_name.text

                                if (test != null) {
                                    val nameParts: List<String> = test?.split(" ")!!
                                    val firstName = nameParts[0]
                                    val firstNameChar = firstName[0]
                                    Log.d("TAG", "firstName: " + firstName)
                                    Log.d("TAG", "firstNameChar: " + firstNameChar)

                                    tv_person_short_name.text = firstNameChar.toString()
                                }
                            }else{
                                Log.e("TAG", "photo: "+getContactPhotoURI(contactId) )
                            }

                            tv_phone_number_name.text = ""+retrieveContactName(contactId.toLong())
                            tv_mobile.text = ""+retrieveContactNumbers(contactId.toLong())?.replace(" ", "")?.trim()
                        }
            }else{
                requestReadContact()
            }
        }
        else if (resultCode==-1){
            checkSetDefaultDialerResult(requestCode)
        } else{
            finishAffinity()
        }
    }

    private  fun deleteContact(ctx: Context, phone: String?, name: String?): Boolean {

        try {
//            val contactHelper:ContentResolver = this@ContactDetailActivity!!.contentResolver
//            val contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId?.toLong()!!)
//            contactHelper.delete(contactUri, null, null)

            val ops: ArrayList<ContentProviderOperation> = ArrayList()
            val arg = arrayOf(contactId)
            ops.add(
                ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                    .withSelection(
                        ContactsContract.RawContacts.CONTACT_ID + "=?",
                        arg
                    ).build()
            )
            try {
                contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()

            }
        } catch (e: java.lang.UnsupportedOperationException) {
            Toast.makeText(this@ContactDetailActivity, getString(R.string.went_worng), Toast.LENGTH_SHORT).show()
        }



        return true
        /*val contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone))
        val cur = ctx.contentResolver.query(contactUri, null, null, null, null)
        try {
            if (cur!!.moveToFirst()) {
                do {
                    if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equals(name, ignoreCase = true)) {
                        val lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY))
                        val uri = Uri.withAppendedPath(
                                ContactsContract.Contacts.CONTENT_LOOKUP_URI,
                                lookupKey
                        )
                        ctx.contentResolver.delete(uri, null, null)
                        return true
                    }
                } while (cur.moveToNext())
            }
        } catch (e: java.lang.Exception) {
            println(e.stackTrace)
        }
        return false*/
    }

    private  fun getContactID(contactHelper: ContentResolver, number: String): Long {

        val contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
        val projection = arrayOf<String>(ContactsContract.PhoneLookup._ID)
        var cursor: Cursor? = null
        try {
            cursor = contactHelper.query(contactUri, projection, null, null, null)
            if (cursor!!.moveToFirst()) {
                val personID = cursor.getColumnIndex(ContactsContract.PhoneLookup._ID)
                return cursor.getLong(personID)
            }
            return -1
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null) {
                cursor.close()
                cursor = null
            }
        }
        return -1
    }

    override fun onClick(view: View?) {
        if (view?.id== R.id.cardView_make_call){
            if (ContextCompat.checkSelfPermission(this@ContactDetailActivity, Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                makeCallToNumber()
            }else{
                requestMakePhoneCall()
            }
        }
        if (view?.id== R.id.linearLayout_back     || view?.id==R.id.img_back){
            finish()
        }
        if (view?.id== R.id.cardView_threedotmenu || view?.id== R.id.img_threedotmenu ){
            setPupMenu(view)
        }
        if (view?.id== R.id.cardView_favorites    || view?.id== R.id.img_favorites){
            makeFavoriteContact()
        }
        if (view?.id== R.id.tv_label_add_favorites_2){
            makeFavoriteContact()
        }
        if (view?.id== R.id.cardView_send_message){
            sendMessageToNumber()
        }
        if (view?.id== R.id.tv_label_send_message){
            sendMessageToNumber()
        }
        if (view?.id== R.id.cardView_delete_contact){
            if (ContextCompat.checkSelfPermission(this@ContactDetailActivity, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                showDeleteDialogBoxAlertDialogBox()
            }else{
                requestContact()
            }
        }
        if (view?.id== R.id.cardView_block_contact){
            if (ContextCompat.checkSelfPermission(this@ContactDetailActivity, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                if (isBlocked()){
                    showUnBlockDialogBoxAlertDialogBox()
                }else{
                    showBlockDialogBoxAlertDialogBox()
                }
            }else{
                requestContact()
            }
        }
        if (view?.id== R.id.tv_label_edit_contact){
            editContactInfo()
        }
        if (view?.id== R.id.tv_label_share_contact){
           shareContact(contactId)

//            if (extras!!.getString("name") == null || extras!!.getString("name").toString() == ""){
//                Log.e("TAG", "name if: "+extras!!.getString("name"))
//                Log.e("TAG", "number if: "+extras!!.getString("number"))
//                shareText("", extras!!.getString("number"))
//            }else{
//                Log.e("TAG", "name else: "+extras!!.getString("name"))
//                Log.e("TAG", "number else: "+extras!!.getString("number"))
//                shareText(extras!!.getString("name"), extras!!.getString("number"))
//            }
        }
    }

    private  fun requestContact() {
        Permissions.check(this@ContactDetailActivity, Manifest.permission.READ_CONTACTS, null,
                object : PermissionHandler() {
                    override fun onGranted() {
                        showBlockDialogBoxAlertDialogBox()
                    }

                    override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                        super.onDenied(context, deniedPermissions)
                        //do nothing
                        this@ContactDetailActivity.finish()
                    }
                })
    }

    private  fun requestPhoneState() {
        Permissions.check(this@ContactDetailActivity, Manifest.permission.READ_PHONE_STATE, null,
                object : PermissionHandler() {
                    override fun onGranted() {
                        getPhoneLookUpID()
                    }

                    override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                        super.onDenied(context, deniedPermissions)
                        //do nothing
                        this@ContactDetailActivity.finish()
                    }
                })
    }

    private  fun requestReadContact() {
        Permissions.check(this@ContactDetailActivity, Manifest.permission.READ_CONTACTS, null,
                object : PermissionHandler() {
                    override fun onGranted() {
                        requestWriteContact()
                    }

                    override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                        super.onDenied(context, deniedPermissions)
                        //do nothing
                        (this@ContactDetailActivity).finish()
                    }
                })
    }

    private  fun requestWriteContact() {
        Permissions.check(this@ContactDetailActivity, Manifest.permission.WRITE_CONTACTS, null,
                object : PermissionHandler() {
                    override fun onGranted() {
                        tv_phone_number_name.text = "" + retrieveContactName(contactId.toLong())
                        tv_mobile.text = "" + retrieveContactNumbers(contactId.toLong())?.replace(" ", "")?.trim()
                    }

                    override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                        super.onDenied(context, deniedPermissions)
                        //do nothing
                        (this@ContactDetailActivity).finish()
                    }
                })
    }

    private  fun requestMakePhoneCall() {
        Permissions.check(this@ContactDetailActivity, Manifest.permission.CALL_PHONE, null,
                object : PermissionHandler() {
                    override fun onGranted() {
                        makeCallToNumber()
                    }

                    override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                        super.onDenied(context, deniedPermissions)
                        this@ContactDetailActivity.finish()
                    }
                })
    }

    private  fun requestContactCallAllFunction() {
        Permissions.check(this@ContactDetailActivity, Manifest.permission.READ_CONTACTS, null,
                object : PermissionHandler() {
                    override fun onGranted() {
                        initView()
                        setOnClickListener()
                        setDataFromExtras()
                        checkFavourtite()
                    }

                    override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                        super.onDenied(context, deniedPermissions)
                        //do nothing
                        this@ContactDetailActivity.finish()
                    }
                })
    }

    private fun shareContact(contactId:String) {
        var lookupKey: String? = null
        val cur: Cursor? = this@ContactDetailActivity.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI, arrayOf(ContactsContract.Contacts.LOOKUP_KEY),
            ContactsContract.Contacts._ID + " = " + contactId, null, null
        )
        if (cur?.moveToFirst() == true) {
            lookupKey = cur?.getString(0)
        }
        if (lookupKey != null) {
            val vcardUri =
                Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey)
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = ContactsContract.Contacts.CONTENT_VCARD_TYPE
            intent.putExtra(Intent.EXTRA_STREAM, vcardUri)
            intent.putExtra(Intent.EXTRA_SUBJECT, "Contact Name")
            startActivity(intent)
        }
    }

    private  fun showDialogBox() {
        AlertDialog.Builder(this@ContactDetailActivity)
                .setTitle(getString(R.string.alert))
                .setMessage(getString(R.string.alert_massage)) // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        // Continue with delete operation

                    }
                }) // A null listener allows the button to dismiss the dialog and take no further action.
                //.setNegativeButton(android.R.string.no, null)
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .show()
    }

    private  fun setPupMenu(view: View?) {
        val popupMenu = PopupMenu(this@ContactDetailActivity, view!!)
        if (ContextCompat.checkSelfPermission(this@ContactDetailActivity, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            if (isBlocked()){
                popupMenu.inflate(R.menu.menu_contact_detail_unblock)
                //img_blockcontact.visibility = View.VISIBLE
            } else {
                popupMenu.inflate(R.menu.menu_contact_detail)
                requestContact()
            }
        }

        popupMenu.setOnMenuItemClickListener { item ->
            when(item.itemId){
                R.id.menu_edit_contact -> {
                    editContactInfo()
                    true
                }
                R.id.menu_delete_contact -> {
                    showDeleteDialogBoxAlertDialogBox()
                    true
                }
                R.id.menu_block_contact -> {
                    showBlockDialogBoxAlertDialogBox()
                    true
                }
                R.id.menu_unblock_contact -> {
                    showUnBlockDialogBoxAlertDialogBox()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    override fun onResume() {
        super.onResume()
        setBackIcon()
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
}