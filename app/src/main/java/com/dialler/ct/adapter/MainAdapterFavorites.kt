package com.dialler.ct.adapter

import android.content.Context
import android.content.Intent
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
import com.dialler.ct.helpingclasses.ManageDualClass
import com.dialler.ct.model.ContactModel_Favorites
import java.util.*

class MainAdapterFavorites(
//Initialize variable
    var activity: FragmentActivity?,
    var arrayList: ArrayList<ContactModel_Favorites>,var context: Context) : RecyclerView.Adapter<MainAdapterFavorites.Viewholder>() {
    var dualSimClass = ManageDualClass(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        //Initialize view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favourite_contacts, parent, false)
        //Return view
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        holder.setIsRecyclable(false)
        //Initialize contact model
        val model = arrayList[position]

        //set name
        holder.tvName.text = model.name

        //set name
        holder.tv_name_character.text = model.name.toString().substring(0,1)

        //set id
        holder.tvid?.text  = model.id



        holder.constrain_contact_info.setOnClickListener{
            val number = model.number
            //         var dial = "tel:${number}"
    //            val intent = Intent(Intent.ACTION_CALL, Uri.parse(dial))
    //            activity!!.startActivity(intent)
            dualSimClass.alertDialogShow(number.toString())
        }
        holder.iv_favouritestar.setOnClickListener{
            val intentFavouriteActivity = Intent(activity, ContactDetailActivity::class.java)
            intentFavouriteActivity.putExtra("number",model.number ) // pass your values and retrieve them in the other Activity using keyName
            intentFavouriteActivity.putExtra("image", model.image) // pass your values and retrieve them in the other Activity using keyName
            intentFavouriteActivity.putExtra("name", model.name) // pass your values and retrieve them in the other Activity using keyName
            intentFavouriteActivity.putExtra("id", model.id)
            activity?.startActivity(intentFavouriteActivity)
        }

    }

    override fun getItemCount(): Int {

        //Return array list size
        return arrayList.size
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //Initialize variable
        var tvName: TextView
        var tv_name_character: TextView
        var tvid: TextView? = null
        var iv_favouritestar: ImageView
        var constrain_contact_info: ConstraintLayout
//        var tvNumber: TextView

        init {
            //Assign variable
            tvName = itemView.findViewById(R.id.tv_name_favorite)
            tv_name_character = itemView.findViewById(R.id.tv_name_character)
            iv_favouritestar = itemView.findViewById(R.id.iv_favouritestar)
            constrain_contact_info = itemView.findViewById(R.id.constrain_contact_info)
//            tvNumber = itemView.findViewById(R.id.tv_number)
        }
    }

    //Create constructor
//    init {
//        notifyDataSetChanged()
//    }
}