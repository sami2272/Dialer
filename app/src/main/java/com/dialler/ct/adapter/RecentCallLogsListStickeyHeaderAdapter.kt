package com.example.stickeyheaderrcv

import android.app.ActionBar
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.provider.CallLog
import android.provider.ContactsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dialler.ct.R
import com.dialler.ct.activity.ContactDetailActivity
import com.dialler.ct.activity.MainActivity
import com.dialler.ct.helpingclasses.ManageDualClass
import com.dialler.ct.model.ContactModelRecents
import com.example.stickeyheaderrcv.RecentCallLogsListStickeyHeaderAdapter.AnimalViewHolder
//import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter
import java.io.ByteArrayInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.util.*

class RecentCallLogsListStickeyHeaderAdapter : RecyclerView.Adapter<AnimalViewHolder>() , StickyRecyclerHeadersAdapter<RecentCallLogsListStickeyHeaderAdapter.HeaderViewHolder> {


    lateinit var arrayList: ArrayList<ContactModelRecents>
    lateinit var mActivity: Activity
    var networkNameList: MutableList<String> = ArrayList()
    var viewGroup: ViewGroup? = null
    lateinit var manageDualClass: ManageDualClass

    //lateinit var model : ContactModelRecents

    private var mInterstitialAd: InterstitialAd? = null

    fun setData(
        list: ArrayList<ContactModelRecents>?,
        activity: MainActivity,
        networkNameList: MutableList<String>
    ) {
        arrayList = list!!
        mActivity = activity
        this.networkNameList = networkNameList
        notifyDataSetChanged()
        manageDualClass = ManageDualClass(activity)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calllog, parent, false)
        //   setadsinterstitial()
        viewGroup = view.findViewById(android.R.id.content)
        return AnimalViewHolder(view)
    }

    private val SECOND_MILLIS = 1000
    private val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private val DAY_MILLIS = 24 * HOUR_MILLIS

    private fun currentDate(): Date {
        val calendar = Calendar.getInstance()
        return calendar.time
    }

    fun getTimeAgo(date: Date): String {

        var time = date.time
        if (time < 1000000000000L) {
            time *= 1000
        }

        val now = currentDate().time
        if (time > now || time <= 0) {
            return "in the future"
        }

        val dates = SimpleDateFormat("MM/dd/yyyy")

        var strcurrnetdate = dates.format(Date())
        var strcallDate = dates.format(time)

        //Setting dates
        var currentDate: Date = dates.parse(strcurrnetdate)
        var callDate: Date = dates.parse(strcallDate)

        //Comparing dates
        val difference: Long = Math.abs(currentDate.getTime() - callDate.getTime())
        val differenceDates = difference / (24 * 60 * 60 * 1000)

        //Convert long to String
        val dayDifference = differenceDates.toString()

        //Log.e("TAG", "HERE: $dayDifference")
        if (dayDifference == "1")
            return "2"

        //Log.e("TAG", "call Date:    "+SimpleDateFormat("dd-MMM-yyyy").format(Date(time)))
        //Log.e("TAG", "current Date: "+SimpleDateFormat("dd-MMM-yyyy").format(Date(currentDate().time)))
        // var callDate: Date = SimpleDateFormat("dd-MMM-yyyy").format(Date(time)))

        val diff = now - time
        return when {
            diff < MINUTE_MILLIS -> "1"
            diff < 2 * MINUTE_MILLIS -> "1"
            diff < 60 * MINUTE_MILLIS -> "1"
            diff < 2 * HOUR_MILLIS -> "1"
            diff < 24 * HOUR_MILLIS -> "1"
            diff < 48 * HOUR_MILLIS -> "2"
            diff < 168 * HOUR_MILLIS -> "3"
            else -> "${diff / DAY_MILLIS} days ago"
        }

        /*return when {
            diff < MINUTE_MILLIS -> "moments ago"
            diff < 2 * MINUTE_MILLIS -> "a minute ago"
            diff < 60 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS} minutes ago"
            diff < 2 * HOUR_MILLIS -> "an hour ago"
            diff < 24 * HOUR_MILLIS -> "${diff / HOUR_MILLIS} hours ago"
            diff < 48 * HOUR_MILLIS -> "yesterday"
            else -> "${diff / DAY_MILLIS} days ago"
        }*/
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {

        //holder.setIsRecyclable(false)

        var model = arrayList[position]

        if (model.name == null || model.name == "") {
            holder.tvname.text = model.number
        } else {
            holder.tvname.text = model.name
        }


        holder.tvnumber.text = model.duration
/*        if (model.duration == "" || model.duration.isNullOrEmpty()) {
            holder.tvnumber.text = model.duration
        } else {

        }*/

        if (getTimeAgo(Date(model.date!!.toLong())) == "1") {
            holder.tvduration.text =
                SimpleDateFormat("hh:mm a ").format(Date(model.date!!.toLong()))
        } else if (getTimeAgo(Date(model.date!!.toLong())) == "2") {
            holder.tvduration.text = mActivity.getString(R.string.str_yesterday)
        } else if (getTimeAgo(Date(model.date!!.toLong())) == "3") {
            holder.tvduration.text = SimpleDateFormat("EEEE", Locale.ENGLISH).format(Date(model.date!!.toLong()))
        } else if (getTimeAgo(Date(model.date!!.toLong())).contains("days ago")) {
            holder.tvduration.text =
                SimpleDateFormat("dd/MM/yyyy").format(Date(model.date!!.toLong()))
        }

        Glide.with(mActivity)
            .load(model.image)
            .placeholder(R.drawable.ic_baseline_person_36) //placeholder
            .error(R.drawable.ic_baseline_person_36) //error
            .into(holder.imagecalltype)

        holder.iv_arrow?.setOnClickListener {
            val intentfavouriteActivity = Intent(mActivity, ContactDetailActivity::class.java)

            intentfavouriteActivity.putExtra(
                "number",
                model.number
            ) // pass your values and retrieve them in the other Activity using keyName
            intentfavouriteActivity.putExtra(
                "image",
                model.photouri
            ) // pass your values and retrieve them in the other Activity using keyName
            intentfavouriteActivity.putExtra(
                "name",
                model.name
            ) // pass your values and retrieve them in the other Activity using keyName
            intentfavouriteActivity.putExtra(
                "id",
                model.id
            )   // pass your values and retrieve them in the other Activity using keyName
//            setadsinterstitial()
            //           showinsadd()
            //mActivity?.startActivityForResult(intentfavouriteActivity,1)
            mActivity?.startActivity(intentfavouriteActivity)
        }

        holder.linear_phoneNumber_recent?.setOnClickListener {

            when {
                networkNameList.size < 1 -> {
                    Toast.makeText(mActivity, "No Sim is Available ", Toast.LENGTH_LONG).show()
                }
                networkNameList.size == 1 -> {
                    val number = model.number
                    val dial = "tel:${number}"
                    val intent = Intent(Intent.ACTION_CALL, Uri.parse(dial))
                    intent.putExtra("com.android.phone.extra.slot", 0); //For sim 1
                    mActivity.startActivity(intent)

                }
                networkNameList.size == 2 -> {
                  //  alertDialogShow()
                    manageDualClass.alertDialogShow(model.number.toString())
                }
                else -> {
                    Toast.makeText(mActivity, networkNameList.size.toString(), Toast.LENGTH_LONG)
                        .show()

                }
            }

        }

    }

    override fun getHeaderId(position: Int): Long {
       // Log.i("Tag" , "date" + getFormattedDate(arrayList[position].date!!.toLong())!![0]!!.toLong())
        //return  getFormattedDate(arrayList[position].date!!.toLong())!![0]!!.toLong()
        return  getFormattedDate(arrayList[position].date!!.toLong())!![0]!!.toLong()
    }

    private  fun getFormattedDate(smsTimeInMilis: Long): String? {
        val smsTime = Calendar.getInstance()
        smsTime.timeInMillis = smsTimeInMilis
        val now = Calendar.getInstance()
        val timeFormatString = "h:mm"
        val dateTimeFormatString = "d"
        val HOURS = 60 * 60 * 60.toLong()
        return if (now[Calendar.DATE] == smsTime[Calendar.DATE] && now[Calendar.MONTH] == smsTime[Calendar.MONTH] && now[Calendar.YEAR] == smsTime[Calendar.YEAR]) {
            "Today "
        } else if (now[Calendar.DATE] - smsTime[Calendar.DATE] == 1 && now[Calendar.MONTH] == smsTime[Calendar.MONTH] && now[Calendar.YEAR] == smsTime[Calendar.YEAR] ) {
            "Yesterday "
        } else if (now[Calendar.DATE] - smsTime[Calendar.DATE] == 2 ||
                    now[Calendar.MONTH] == smsTime[Calendar.MONTH] || now[Calendar.MONTH] - smsTime[Calendar.MONTH] == 1
                    || now[Calendar.YEAR] == smsTime[Calendar.YEAR] || now[Calendar.YEAR] - smsTime[Calendar.YEAR] == 1){
            "Older"
        }else{
            "Older"
        }
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_header_recent_call_logs, parent, false)
        return HeaderViewHolder(view)
    }

    override fun onBindHeaderViewHolder(headerViewHolder: HeaderViewHolder, i: Int) {
        val nameHeader = getFormattedDate(arrayList!![i].date!!.toLong())
//        Log.e("Tag" , "headerdate" + nameHeader)
        headerViewHolder.tvHeader.text = nameHeader
//        headerViewHolder.tvHeader.setBackgroundColor(randomColor)
    }

    override fun getItemCount(): Int {
        return if (arrayList != null) {
            arrayList!!.size
        } else 0
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHeader: TextView

        init {
            tvHeader = itemView.findViewById(R.id.tv_header)
        }
    }

    inner class AnimalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //Initialize variable
        var tvname: TextView
        var tvnumber: TextView
        //var tv_date: TextView
        var tvduration : TextView
        var imagecalltype : ImageView
        var iv_arrow : ImageView
//      var iv_image_recents_log : ImageView

        var linear_phoneNumber_recent : LinearLayout


        init {
//          tv_date   = itemView.findViewById(R.id.tv_type)         as TextView
            tvnumber  = itemView.findViewById(R.id.tv_number)       as TextView
            tvduration= itemView.findViewById(R.id.tv_duration)     as TextView
            tvname    = itemView.findViewById(R.id.tv_name_favorite)as TextView

//            iv_image_recents_log = itemView.findViewById(R.id.iv_image_recents_log) as ImageView
            imagecalltype = itemView.findViewById(R.id.iv_calltype) as ImageView
            iv_arrow = itemView.findViewById(R.id.iv_arrow) as ImageView

            linear_phoneNumber_recent = itemView.findViewById(R.id.linear_phoneNumber_recent) as LinearLayout

        }
    }




}