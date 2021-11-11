package com.dialler.ct.adapter

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.dialler.ct.R
import com.dialler.ct.activity.ContactDetailActivity
import com.dialler.ct.model.ContactModelRecents
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*

class MainAdapterRecents(//Initialize Variable

    var activity: FragmentActivity?,
    var arrayList: ArrayList<ContactModelRecents>,var context: Context) : RecyclerView.Adapter<MainAdapterRecents.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_calllog, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        //Initialize contact model
        val model = arrayList[position]

        //setname
        if (model.name=="")
            holder.tvname.text =context.getString(R.string.str_unknown)
        else
            holder.tvname.text = model.name

        //set number
        holder.tvnumber.text = model.number

        val duration = model.duration?.toInt()
        val seconds  = duration?.rem(60)
        var hours    = duration?.div(60)
        val minutes  = hours?.rem(60)
        if (hours != null) {
            hours /= 60
        }

//        Log.e("Tagduration", "$hours:$minutes:$seconds")
//        holder.tv_date.text = "$hours:$minutes:$seconds"
//        holder.tvduration.setText(model.getDuration());

        //set duration
        if (model.type.toString() == "1" || model.type.toString() == "2") {
            if (hours == 0) {
                if (minutes == 0) {
                    holder.tvduration.text = "{$seconds s}"
                } else {
                    holder.tvduration.text = minutes.toString() + "m:" + seconds + "s"
                }
            } else
                holder.tvduration.text = hours.toString() + "h:" + minutes + "m:" + seconds + "s"

        }

        holder.linear_phoneNumber_recent?.setOnClickListener{
            val intentfavouriteActivity = Intent(activity, ContactDetailActivity::class.java)

            intentfavouriteActivity.putExtra("number",model.number ) // pass your values and retrieve them in the other Activity using keyName
            intentfavouriteActivity.putExtra("image", model.image) // pass your values and retrieve them in the other Activity using keyName
            intentfavouriteActivity.putExtra("name", model.name) // pass your values and retrieve them in the other Activity using keyName
            intentfavouriteActivity.putExtra("id", model.id)   // pass your values and retrieve them in the other Activity using keyName

            activity?.startActivity(intentfavouriteActivity)
        }

        //Log.e("TAG", "onBindViewHolder: "+openPhoto(model.id!!.toLong()))

        val contentResolver = activity!!.contentResolver

        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(model.number))

        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID)

        val cursor = contentResolver.query(
                uri,
                projection,
                null,
                null,
                null)

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME))
                val contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID))
//                Log.d("TAG", "contactMatch name: $contactName")
//                Log.d("TAG", "photo id: "+openPhoto(contactId.toLong()))

                if (openPhoto(contactId.toLong())!=null){
                    val bitmap1: Bitmap = BitmapFactory.decodeStream(openPhoto(contactId.toLong()))
                    holder.iv_image?.setImageBitmap(bitmap1)
                }
            }
            cursor.close()
        }

    }

   private fun openPhoto(contactId: Long): InputStream? {
        val contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId)
        val photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY)
        val cursor: Cursor = activity?.contentResolver?.query(photoUri, arrayOf<String>(ContactsContract.Contacts.Photo.PHOTO), null, null, null)
                ?: return null
        try {
            if (cursor.moveToFirst()) {
                val data = cursor.getBlob(0)
                if (data != null) {
                    return ByteArrayInputStream(data)
                }
            }
        } finally {
            cursor.close()
        }
        return null
    }


    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //Initialize variable
        var tvname: TextView
        var tvnumber: TextView

//          var tv_date: TextView
        var tvduration : TextView
        var imagecalltype : ImageView
        var iv_image : ImageView

        var linear_phoneNumber_recent : LinearLayout

        init {

//          tv_date   = itemView.findViewById(R.id.tv_type)         as TextView
            tvnumber  = itemView.findViewById(R.id.tv_number)       as TextView
            tvduration= itemView.findViewById(R.id.tv_duration)     as TextView
            tvname    = itemView.findViewById(R.id.tv_name_favorite)as TextView

            iv_image = itemView.findViewById(R.id.iv_image) as ImageView
            imagecalltype = itemView.findViewById(R.id.iv_calltype) as ImageView

            linear_phoneNumber_recent = itemView.findViewById(R.id.linear_phoneNumber_recent) as LinearLayout

        }
    }

}