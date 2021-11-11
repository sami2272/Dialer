package com.dialler.ct.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.provider.ContactsContract
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.dialler.ct.R
import com.dialler.ct.activity.MainActivity
import com.dialler.ct.activity.Setting_Activity
import com.dialler.ct.adapter.MainAdapterRecents
import com.dialler.ct.model.ContactModelRecents
import com.example.stickeyheaderrcv.RecentCallLogsListStickeyHeaderAdapter
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import java.util.*


class Recents_Fragment : Fragment(), View.OnClickListener {

    lateinit var mLayoutManager: LinearLayoutManager
    lateinit var recyclerview_recent_logs_list: RecyclerView

    lateinit var tv_select_all: TextView
    lateinit var tv_select_missed: TextView

    lateinit var img_setting: ImageView

    var arrayList = ArrayList<ContactModelRecents>()

    lateinit var recentCallLogsListStickeyHeaderAdapter       : RecentCallLogsListStickeyHeaderAdapter
    lateinit var recentCallLogsListStickeyHeaderAdapterMissed : RecentCallLogsListStickeyHeaderAdapter

    lateinit var mainAdapterRecents: MainAdapterRecents

    lateinit var ed_search_view_recent_call_logs: EditText

    var networkNameList: MutableList<String> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView: View =  inflater.inflate(R.layout.fragment_recents_, container, false)

        initView(rootView)
        setOnClickListener()
        getNetworkName()

        ed_search_view_recent_call_logs.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filter(s.toString());
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
        //Log.e("TAG", "onCreateView: "+GetCountryZipCode() )

        return rootView
    }

    private fun getNetworkName(): String? {

        networkNameList.clear()
        if (Build.VERSION.SDK_INT > 22) {
            var localSubscriptionManager: SubscriptionManager = SubscriptionManager.from(context);

//            Log.e("TAG", "getSubscriptionIds: " + localSubscriptionManager.activeSubscriptionInfoList[0].subscriptionId)
//            Log.e("TAG", "getSubscriptionIds: " + localSubscriptionManager.activeSubscriptionInfoList[1])

            if (context?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.READ_PHONE_STATE) } != PackageManager.PERMISSION_GRANTED) {
                return null
            }
            if (localSubscriptionManager.activeSubscriptionInfoCount > 1) {
                //if there are two sims in dual sim mobile
                var localList = localSubscriptionManager.activeSubscriptionInfoList


                var simInfo0: SubscriptionInfo = localList[0]
                var simInfo1: SubscriptionInfo = localList[1]

                networkNameList.add(simInfo0.displayName.toString())
                networkNameList.add(simInfo1.displayName.toString())

//                Log.e("TAG", "simInfo0: " + localSubscriptionManager.activeSubscriptionInfoList[0].subscriptionId)
//                Log.e("TAG", "simInfo1: " + simInfo1.subscriptionId)

            } else {
                //if there is 1 sim in dual sim mobile
                var tManager = context?.getSystemService(Context.TELEPHONY_SERVICE) as (TelephonyManager)

                var sim1 = tManager.networkOperatorName
                networkNameList.add(sim1)
                return sim1
                Toast.makeText(context, sim1.toString(), Toast.LENGTH_LONG).show()
            }

        } else {
            //below android version 22
            var tManager: TelephonyManager =
                    context?.getSystemService(Context.TELEPHONY_SERVICE) as (TelephonyManager)

            var sim1 = tManager?.networkOperatorName
            networkNameList.add(sim1)
            return sim1
            Toast.makeText(context, sim1.toString(), Toast.LENGTH_LONG).show()
        }
        return null
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(activity as MainActivity, Manifest.permission.READ_CALL_LOG)
                == PackageManager.PERMISSION_GRANTED) {
            // Do something ...
            getRecentCallLogs()
        }else{
            requestREADCALLLOG()
        }
    }

    private fun initView(rootView: View) {

        recyclerview_recent_logs_list = rootView.findViewById(R.id.recyclerview_recent_logs_list)

        tv_select_all    = rootView.findViewById(R.id.tv_select_all)
        tv_select_missed = rootView.findViewById(R.id.tv_select_missed)
        img_setting      = rootView.findViewById(R.id.img_setting)

        ed_search_view_recent_call_logs = rootView.findViewById(R.id.ed_search_view_recent_call_logs)

        mLayoutManager   = LinearLayoutManager(activity)
        recyclerview_recent_logs_list.layoutManager = mLayoutManager

        recentCallLogsListStickeyHeaderAdapter      = RecentCallLogsListStickeyHeaderAdapter()
        recentCallLogsListStickeyHeaderAdapterMissed= RecentCallLogsListStickeyHeaderAdapter()
    }

    private fun setOnClickListener() {
        tv_select_all.setOnClickListener(this)
        tv_select_missed.setOnClickListener(this)
        img_setting.setOnClickListener(this)

    }

    private fun filter(text: String) {
        val newList: ArrayList<ContactModelRecents> = ArrayList()
        for (item in arrayList) {
            if (item.name?.trim()?.toLowerCase()?.contains(text.toLowerCase().trim()) == true) {
                newList.add(item)
            }
            if (item.number?.trim()?.toLowerCase()?.contains(text.toLowerCase().trim()) == true) {
                newList.add(item)
            }
        }

//        recyclerview_recent_logs_list!!.layoutManager = LinearLayoutManager(activity)
//        //Initialize adapter
//        mainAdapterRecents = MainAdapterRecents(activity, newList)
//        //set adapter
//        recyclerview_recent_logs_list!!.adapter = mainAdapterRecents

        val linearLayoutManager = LinearLayoutManager(activity)
        recyclerview_recent_logs_list?.layoutManager = linearLayoutManager

        recentCallLogsListStickeyHeaderAdapter.setData(newList, activity as MainActivity,networkNameList)
        recyclerview_recent_logs_list?.adapter = recentCallLogsListStickeyHeaderAdapter

        //recyclerview_recent_logs_list?.addItemDecoration(StickyRecyclerHeadersDecoration(recentCallLogsListStickeyHeaderAdapter))
    }

    private fun getRecentCallLogs() {

        arrayList.clear()

        val projection = arrayOf(
                CallLog.Calls._ID,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.NUMBER,
                CallLog.Calls.DURATION,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.PHONE_ACCOUNT_ID,
                ContactsContract.CommonDataKinds.Photo.PHOTO_URI
        )

        val recentscallContentResolver = activity!!.contentResolver
        val cursorCallLogs = recentscallContentResolver!!.query(CallLog.Calls.CONTENT_URI, projection, null, null, CallLog.Calls.DATE + " DESC  limit 100")
        if (cursorCallLogs != null) {
            try {
                cursorCallLogs.moveToFirst()
                if (cursorCallLogs!!.moveToFirst()){
                    do{

                        val stringId  = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls._ID))
                        val stringDate= cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.DATE))
                        val stringType= cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.TYPE))
                        val stringNumber= cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.NUMBER))
                        val stringName = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.CACHED_NAME))
                        val stringDuration= cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.DURATION))

                        //Initialize contact model
                        val model = ContactModelRecents()

//                        if (index=="8992040411112389607F"){
//                            model.simName = networkNameList[0]
//                        }else{
//                            model.simName = networkNameList[1]
//                        }


                        val duration = stringDuration?.toInt()
                        val seconds  = duration?.rem(60)
                        var hours    = duration?.div(60)
                        val minutes  = hours?.rem(60)
                        if (hours != null) {
                            hours /= 60
                        }

                        if (hours == 0) {
                            if (minutes == 0) {
                                model.duration = "00:"+seconds.toString() + "s"

                            } else {
                                model.duration =  minutes.toString() + "m:" + seconds + "s"
                            }
                        } else{
                            model.duration = hours.toString() + "h:" + minutes + "m:" + seconds + "s"
                        }

                        model.name = stringName
                        model.number = stringNumber



                        if (cursorCallLogs.getString(cursorCallLogs.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_URI))!=null)
                            model.photouri = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_URI))

                        //set type
                        if (cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.TYPE)).toString() == "1") {
                            model.type  = "Incoming"
                            model.image = R.drawable.ic_incomingcall

                        }
                        else if (cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.TYPE)).toString() == "2") {
                            model.type  = "Outgoing"
                            model.image =(R.drawable.ic_outgoing)

                        }
                        else if (cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.TYPE)).toString() == "3") {
                            model.type  = "Missed"
                            model.image =(R.drawable.ic_missedcall)

                        }
                        else if (cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.TYPE)).toString() == "5") {
                            model.type = "Missed"
                            model.image =(R.drawable.ic_missedcall)
                        }
                        else if (cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.TYPE)).toString() == "6") {
                            model.type  = "Blocked"
                            model.image =(R.drawable.ic_baseline_block_24)
                        }

                        model.id = stringId
                        model.date = stringDate

                        arrayList.add(model)

                    }while (cursorCallLogs.moveToNext())

                }
                else{
                    Toast.makeText(activity, getString(R.string.str_no_miss_call), Toast.LENGTH_SHORT).show()
                }
                cursorCallLogs.close()

            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                Toast.makeText(activity, getString(R.string.went_worng), Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(activity, getString(R.string.went_worng), Toast.LENGTH_SHORT).show()
        }

//      val linearLayoutManager = LinearLayoutManager(activity)
//      recyclerview_recent_logs_list?.layoutManager = linearLayoutManager

        recentCallLogsListStickeyHeaderAdapter.setData(arrayList, activity as MainActivity,networkNameList)
        //recyclerview_recent_logs_list.setHasFixedSize(true)
        recyclerview_recent_logs_list.setItemViewCacheSize(20);
        recyclerview_recent_logs_list.isDrawingCacheEnabled = true;
        recyclerview_recent_logs_list?.isNestedScrollingEnabled = false
        recyclerview_recent_logs_list.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

//        if (recentCallLogsListStickeyHeaderAdapter.hasObservers())
//            recentCallLogsListStickeyHeaderAdapter.setHasStableIds(true);

        recyclerview_recent_logs_list?.adapter = recentCallLogsListStickeyHeaderAdapter
    //      recyclerview_recent_logs_list?.addItemDecoration(StickyRecyclerHeadersDecoration(recentCallLogsListStickeyHeaderAdapter))

//      Initialize adapter
//      recyclerview_recent_logs_list!!.layoutManager = LinearLayoutManager(activity)
//      mainAdapterRecents = MainAdapterRecents(activity, arrayList)
//      set adapter
//      recyclerview_recent_logs_list!!.adapter = mainAdapterRecents
    }

    private fun getrecentMissCallLogs() {

        arrayList.clear()

        val projection = arrayOf(
                CallLog.Calls._ID,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.NUMBER,
                CallLog.Calls.DURATION,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.PHONE_ACCOUNT_ID,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        )

        val misscallContentResolver = activity!!.contentResolver
        val cursorCallLogs = misscallContentResolver?.query(CallLog.Calls.CONTENT_URI, projection, null, null, CallLog.Calls.DATE + " DESC  limit 100")

        if (cursorCallLogs != null) {
            cursorCallLogs!!.moveToFirst()
            if (cursorCallLogs!!.moveToFirst()){
                do{

//                    val index =cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID))
//                    val simName = networkNameList[index.toInt()]

                    val stringId      = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls._ID))
                    val stringDate    = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.DATE))
                    val stringType    = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.TYPE))
                    val stringNumber  = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.NUMBER))
                    val stringDuration= cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.DURATION))
                    val stringName    = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.CACHED_NAME))

                    //set type
                    if (cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.TYPE)).toString() == "3"||
                            cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.TYPE)).toString() == "5") {

                        val model = ContactModelRecents()

                        //model.simName = simName
                        model.type  = "Missed"
                        model.image =(R.drawable.ic_missedcall)

                        if (stringName==""){
                            model.name = stringNumber
                        }else{
                            model.name = stringName
                        }

                        model.number= stringNumber
                        model.id    = stringId
                        model.date  = stringDate
                        arrayList.add(model)

                    }


                }while (cursorCallLogs.moveToNext())
            }
            else{
                Toast.makeText(activity, getString(R.string.str_no_miss_call), Toast.LENGTH_SHORT).show()
            }
            cursorCallLogs.close();
        }else{
            Toast.makeText(activity, getString(R.string.went_worng), Toast.LENGTH_SHORT).show()
        }

//      val linearLayoutManager = LinearLayoutManager(activity)
//      recyclerview_recent_logs_list?.layoutManager = linearLayoutManager

        recentCallLogsListStickeyHeaderAdapterMissed.setData(arrayList, activity as MainActivity,networkNameList)
        //recyclerview_recent_logs_list.setHasFixedSize(true)
        recyclerview_recent_logs_list.setItemViewCacheSize(20);
        recyclerview_recent_logs_list.isDrawingCacheEnabled = true;
        recyclerview_recent_logs_list?.isNestedScrollingEnabled = false
        recyclerview_recent_logs_list.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

//        if (recentCallLogsListStickeyHeaderAdapterMissed.hasObservers())
//            recentCallLogsListStickeyHeaderAdapterMissed.setHasStableIds(true);

        recyclerview_recent_logs_list?.adapter = recentCallLogsListStickeyHeaderAdapterMissed
//      recyclerview_recent_logs_list?.addItemDecoration(StickyRecyclerHeadersDecoration(recentCallLogsListStickeyHeaderAdapterMissed))

//      recyclerview_recent_logs_list!!.layoutManager = LinearLayoutManager(activity)
//      Initialize adapter
//      mainAdapterRecents = MainAdapterRecents(activity, arrayList)
//      set adapter
//      recyclerview_recent_logs_list!!.adapter = mainAdapterRecents
    }

    override fun onClick(view: View?) {
        if (view?.id== R.id.tv_select_all){
            tv_select_missed.setBackgroundDrawable(null)
            tv_select_all.setBackgroundResource(R.drawable.roundbackgroundoffwhite)

            if (ContextCompat.checkSelfPermission(activity as MainActivity, Manifest.permission.READ_CALL_LOG)
                    == PackageManager.PERMISSION_GRANTED) {
                // Do something ...
                getRecentCallLogs()
            }else{
                requestREADCALLLOG()
            }

        }
        if (view?.id== R.id.tv_select_missed){
            tv_select_all.setBackgroundDrawable(null)
            tv_select_missed.setBackgroundResource(R.drawable.roundbackgroundoffwhite)
            if (ContextCompat.checkSelfPermission(activity as MainActivity, Manifest.permission.READ_CALL_LOG)
                    == PackageManager.PERMISSION_GRANTED) {
                // Do something ...
                getrecentMissCallLogs()
            }else{
                requestREADCALLLOG()
            }
        }
        if (view?.id== R.id.img_setting){
            startActivity(Intent(activity,Setting_Activity::class.java))
        }
    }

    private fun requestREADCALLLOG() {
        Permissions.check(activity as MainActivity, Manifest.permission.READ_CALL_LOG, null,
                object : PermissionHandler() {
                    override fun onGranted() {
                        getRecentCallLogs()
                    }

                    override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                        super.onDenied(context, deniedPermissions)
                        //do nothing
                        (activity as MainActivity).finish()
                    }
                })
    }
}