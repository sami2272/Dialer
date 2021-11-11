package com.dialler.ct.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.dialler.ct.R
import com.dialler.ct.activity.ContactDetailActivity
import com.dialler.ct.model.ContactModelContacts
import java.util.*

class MainAdapterContacts(//Initialize variable
    var activity: FragmentActivity?,

    var arrayList: ArrayList<ContactModelContacts>) : RecyclerView.Adapter<MainAdapterContacts.Viewholder>() {
    var backup = arrayList

    var clallcontacts: ConstraintLayout? = null

    var favouritestar:ImageView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        //Initialize view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_list_item_view, parent, false)
        //Return view
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {

       holder.setIsRecyclable(false)

        //Initialize contact model
        val model = arrayList[position]

        //set name
        holder.tvName.text = model.name

        //set image
        if (model.image==null){
            //Log.e("photo", "photoNull: $model.image")
            // img_dummy?.setImageURI(Uri.parse(uri))
        }else{
            //Log.e("photo", "photo: $model.image")
            holder.profile?.setImageURI(Uri.parse(model.image))
        }


        clallcontacts?.setOnClickListener{

            val intentfavouriteActivity = Intent(activity, ContactDetailActivity::class.java)

            intentfavouriteActivity.putExtra("number",model.number ) // pass your values and retrieve them in the other Activity using keyName
            intentfavouriteActivity.putExtra("image", model.image) // pass your values and retrieve them in the other Activity using keyName
            intentfavouriteActivity.putExtra("name", model.name) // pass your values and retrieve them in the other Activity using keyName
            intentfavouriteActivity.putExtra("id", model.id)

            activity?.startActivity(intentfavouriteActivity)

        }

       //set number
      //  holder.tvNumber.text = model.number
    }

    override fun getItemCount(): Int {
        //Return array list size
        return arrayList.size
    }


    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //Initialize variable
        var tvName: TextView
        var profile:ImageView
     //   var favouritestar:ImageView

        //var tvNumber: TextView

        init {
            //Assign variable
            tvName = itemView.findViewById(R.id.tv_name_favorite)

            profile = itemView.findViewById(R.id.iv_image)

            clallcontacts = itemView.findViewById(R.id.cl_all_contacts)

            favouritestar = itemView.findViewById(R.id.iv_favouritestar)

            //tvNumber = itemView.findViewById(R.id.tv_number)
        }
    }

}