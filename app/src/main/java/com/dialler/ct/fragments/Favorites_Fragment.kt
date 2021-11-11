package com.dialler.ct.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dialler.ct.model.ContactModel_Favorites
import com.dialler.ct.adapter.MainAdapterFavorites
import com.dialler.ct.R
import com.dialler.ct.activity.MainActivity
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import java.util.ArrayList


class Favorites_Fragment : Fragment() {


    private var recyclerView: RecyclerView ? = null
    private var arrayList = ArrayList<ContactModel_Favorites>()
    private  var adapter: MainAdapterFavorites? = null
    private var imgProfileFavorite: ImageView? = null


    override fun onResume() {
        super.onResume()

        if (ContextCompat.checkSelfPermission(activity as MainActivity, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED)
                {
            favouriteContactList
        }
        else{
            requestContact()
        }
    }

    private fun requestContact() {
        Permissions.check(activity as MainActivity, Manifest.permission.READ_CONTACTS, null,
                object : PermissionHandler() {
                    override fun onGranted() {
                        favouriteContactList
                    }

                    override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                        super.onDenied(context, deniedPermissions)
                        //do nothing
                        (activity as MainActivity).finish()
                    }
                })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val favouriteView : View =  inflater.inflate(R.layout.fragment_favorites_, container, false)
        recyclerView = favouriteView.findViewById(R.id.recyclerview_contact_list)
        return favouriteView

    }

    private val favouriteContactList: Unit
        //Sort by ascending
        //Initialize cursor
        //Check condition

        get() {
            //Initialize uri
//            val uri = ContactsContract.Contacts.CONTENT_URI
            val uri = ContactsContract.Contacts.CONTENT_URI
            //Sort by ascending
            val sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            //Initialize cursor
            val cursor = activity?.contentResolver?.query(
                uri, null, "starred=?", arrayOf("1"), sort
            )
            //Check condition

            arrayList.clear()

            if(cursor!=null){
                if (cursor.count > 0) {
                    //When count is greater than 0
                    //Use while loop
                    while (cursor.moveToNext()) {
                        //Cursor move to next
                        //Get contact id
                        val id = cursor.getString(cursor.getColumnIndex(
                                ContactsContract.Contacts._ID
                        ))

                        //Get Contact name
                        val name = cursor.getString(cursor.getColumnIndex(
                                ContactsContract.Contacts.DISPLAY_NAME
                        ))


                        //get contact photo
                        val photoUri: String? = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI))

                        if (photoUri!=null){
                            imgProfileFavorite?.setImageURI(Uri.parse(photoUri))
                        }


                        //Check condition

                            //When phone cursor move to next
                            val number = getPhoneNumberById(activity?.baseContext,id)
                            //Initialize contact model
                            val model =
                                    ContactModel_Favorites()
                            //set id
                            model.id = null
                            //Set name
                            model.name = name
                            //Set number
                            model.number = number
                            //set image
                            model.image = photoUri
                            //Add model in array list
                            arrayList.add(model)
                            //Close phone cursor

                    }
                    //Close cursor
                    cursor.close()
                }
            }else{
                Toast.makeText(activity, "Something went wrong.Please try again", Toast.LENGTH_SHORT).show()
            }

            //Set layout manager
            recyclerView!!.layoutManager = LinearLayoutManager(activity)
            //Initialize adapter
            adapter = MainAdapterFavorites(activity, arrayList,requireContext())
            //set adapter
            recyclerView!!.adapter = adapter
        }

    private fun getPhoneNumberById(activity: Context?,id:String):String{
        //Initialize phone uri
        val uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        //Initialize selection
        val selection = (ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?")
        //Initialize phone cursor
        val phoneCursor =  activity?.contentResolver?.query(
            uriPhone, null, selection, arrayOf(id), null
        )
        if (phoneCursor!!.moveToNext()) {
            return phoneCursor.getString(phoneCursor.getColumnIndex(
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ))

    }

        phoneCursor.close()
        return null.toString()
    }

}