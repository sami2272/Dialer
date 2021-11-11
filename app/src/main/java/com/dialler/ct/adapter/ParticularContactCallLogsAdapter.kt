package com.dialler.ct.adapter

import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.dialler.ct.R
import com.dialler.ct.model.ParticularContactCallLogsModel
import java.text.SimpleDateFormat
import java.util.*

class ParticularContactCallLogsAdapter(
//Initialize variable
    var activity: FragmentActivity?,

    var arrayList: ArrayList<ParticularContactCallLogsModel>) : RecyclerView.Adapter<ParticularContactCallLogsAdapter.Viewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        //Initialize view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_particular_calllog, parent, false)
        //Return view
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {

       holder.setIsRecyclable(false)

        //Initialize contact model
        val model = arrayList[position]

        holder.tv_phone_number.text = model.number
//        val timestampString = ""+ model.date!!.toLong()
//        val value = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date(timestampString*1000))



   //     val timestampString = "yourString".toLong()
        //Log.e("TAG", "timestamp: "+model.date )
        val value = SimpleDateFormat("EEEE, MMM d, YYYY HH:mm").format(Date(model.date!!.toLong()))
        holder.tv_call_details.text = value
    }


  private  fun getDate(timestamp: Long) :String {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = timestamp * 1000L
        Log.e("TAG", "getDate: ", )
        val date = DateFormat.format("EEEE, MMM d, YYYY HH:mm:ss",calendar).toString()
        //val date = DateFormat.format("EEEE d 'de' MMMM 'del' yyyy",calendar).toString()
        return date
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //Initialize variable
        var tv_phone_number: TextView
        var tv_call_details: TextView

        init {
            //Assign variable
            tv_phone_number = itemView.findViewById(R.id.tv_phone_number)
            tv_call_details = itemView.findViewById(R.id.tv_call_details)

        }
    }
}


