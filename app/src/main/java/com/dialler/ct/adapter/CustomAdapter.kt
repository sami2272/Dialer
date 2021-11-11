package com.example.blockedcontactsdemo.Adapter

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.BlockedNumberContract.unblock
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.dialler.ct.R

class CustomAdapter(private val dataSet: ArrayList<String>,val context: Context) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textView)
        val tv_block_contant_name: TextView = view.findViewById(R.id.tv_block_contant_name)
        val img_cross: ImageView = view.findViewById(R.id.img_cross)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.single_view, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textView.text = dataSet[position]
        viewHolder.tv_block_contant_name.text = retrieveContactName(getContactID(context.contentResolver,dataSet[position]))

        viewHolder.img_cross.setOnClickListener{
            showUnBlockDialogBox(dataSet[position],position)
        }
    }
    // Return the size of your dataset (invoked by the layout manager)

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


    private  fun retrieveContactName(contactId: Long): String? {
        var contactName: String? = null
        val cursor: Cursor = context.contentResolver.query(ContactsContract.Contacts.CONTENT_URI, arrayOf(ContactsContract.Contacts.DISPLAY_NAME),
                ContactsContract.Contacts._ID + " = ?", arrayOf(contactId.toString()), null)!!
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
        }
        cursor.close()

        Log.e("TAG", "contactName: $contactName")
        //textView.text = contactName
        return contactName
    }


    override fun getItemCount(): Int {
        return dataSet.size
    }

    private  fun showUnBlockDialogBox(blocknumber: String, position: Int) {
        val mAlertDialog = AlertDialog.Builder(context)
        //    mAlertDialog.setIcon(R.mipmap.ic_launcher)
        mAlertDialog.setTitle("Unblock this contact?")
        mAlertDialog.setMessage("$blocknumber ${context.getString(R.string.str_unblock_this_contact)}")
        mAlertDialog.setPositiveButton(context.getString(R.string.str_unblock)) { _, _ ->
            unblock(context, blocknumber)
            dataSet.removeAt(position)
            notifyDataSetChanged()
            Toast.makeText(context, context.getString(R.string.str_unblock_massage), Toast.LENGTH_LONG).show()
        }
        mAlertDialog.setNegativeButton(context.getString(R.string.cancel)) { dialog, id ->
        }
        mAlertDialog.show()
    }
}