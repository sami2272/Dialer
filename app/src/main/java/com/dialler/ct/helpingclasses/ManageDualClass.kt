package com.dialler.ct.helpingclasses

import android.Manifest
import android.app.ActionBar
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.dialler.ct.R
import java.util.ArrayList

class ManageDualClass(private var context: Context) {
    var networkNameList: MutableList<String> = ArrayList()
    var viewGroup: ViewGroup?= null
    init {
      //  viewGroup= ViewGroup.findViewById(android.R.id.content)
        getNetworkName()
    }
    companion object{
        val stringName = arrayOf(
            "extra_asus_dial_use_dualsim",
            "com.android.phone.extra.slot",
            "slot",
            "simslot",
            "sim_slot",
            "subscription",
            "Subscription",
            "phone",
            "com.android.phone.DialingMode",
            "simSlot",
            "slot_id",
            "simId",
            "simnum",
            "phone_type",
            "slotId",
            "slotIdx" )
    }
    private fun getNetworkName(): String? {
        val localSubscriptionManager: SubscriptionManager = SubscriptionManager.from(context)

        if (context.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_PHONE_STATE
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {

            return null
        }
        if (localSubscriptionManager.activeSubscriptionInfoCount > 1) {
            //if there are two sims in dual sim mobile
            val localList = localSubscriptionManager.activeSubscriptionInfoList
            val simInfo: SubscriptionInfo = localList[0]
            val simInfo1: SubscriptionInfo = localList[1]

            networkNameList.add(simInfo.displayName.toString())
            networkNameList.add(simInfo1.displayName.toString())

        } else {
            //if there is 1 sim in dual sim mobile
            val tManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as (TelephonyManager)

            val sim1 = tManager.networkOperatorName
            networkNameList.add(sim1)
            return sim1
        }

        return null
    }
    fun alertDialogShow(Ph:String){
//

        when {
            networkNameList.size < 1 -> {
                Toast.makeText(context, "No Sim is Available ", Toast.LENGTH_LONG).show()
            }
            networkNameList.size == 1 -> {
                val number = Ph
                val dial = "tel:${number}"
                val intent = Intent(Intent.ACTION_CALL, Uri.parse(dial.replace("#", Uri.encode("#"))))
                intent.putExtra("com.android.phone.extra.slot", 0); //For sim 1
                context.startActivity(intent)

            }
            networkNameList.size == 2 -> {
                //  alertDialogShow()



        val dialogView: View = LayoutInflater.from(context)
            .inflate(R.layout.custom_dialog_for_sim,viewGroup,false)

        val builder = android.app.AlertDialog.Builder(context)
        builder.setView(dialogView)
        val alertDialog = builder.create()

        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window!!.setLayout(ActionBar.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        alertDialog.show()



        val  layoutSim1 = dialogView.findViewById<LinearLayout>(R.id.linearSim1)
        val  txtSim1 = dialogView.findViewById<TextView>(R.id.txtSim1)
        txtSim1.text=networkNameList[0]

        layoutSim1.setOnClickListener {
            val dial = "tel:$Ph"
            val intent = Intent(Intent.ACTION_CALL, Uri.parse(dial.replace("#", Uri.encode("#"))))
            //  intent.putExtra("com.android.phone.extra.slot", 0); //For sim 1
            intent.putExtra("simSlot", 0)
            for(s in stringName){
                intent.putExtra(s,0)
            }
            context.startActivity(intent)
            alertDialog.dismiss()
        }
        val  layoutSim2 = dialogView.findViewById<LinearLayout>(R.id.linearSim2)
        val  txtSim2 = dialogView.findViewById<TextView>(R.id.txtSim2)
        txtSim2.text=networkNameList[1]

        layoutSim2.setOnClickListener {
            val dial = "tel:$Ph"
            val intent = Intent(Intent.ACTION_CALL, Uri.parse(dial.replace("#", Uri.encode("#"))))

            for(s in stringName){
                intent.putExtra(s,1)
            }
            intent.putExtra("com.android.phone.extra.slot", 1) //For sim 1

            //   intent.putExtra("simSlot", 1);
            context.startActivity(intent)
            alertDialog.dismiss()
        }

            }
            else -> {
                Toast.makeText(context, networkNameList.size.toString(), Toast.LENGTH_LONG)
                    .show()

            }
        }
    }

}