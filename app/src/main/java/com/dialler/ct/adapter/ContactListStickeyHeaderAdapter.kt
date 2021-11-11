package com.example.stickeyheaderrcv

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SectionIndexer
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.dialler.ct.R
import com.dialler.ct.activity.ContactDetailActivity
import com.dialler.ct.activity.MainActivity
import com.dialler.ct.model.ContactModelContacts
import com.dialler.ct.model.Helpers
import com.example.stickeyheaderrcv.ContactListStickeyHeaderAdapter.AnimalViewHolder
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter

class   ContactListStickeyHeaderAdapter(var arrayList: ArrayList<ContactModelContacts>?,var  mActivity: MainActivity) : RecyclerView.Adapter<AnimalViewHolder>(), StickyRecyclerHeadersAdapter<ContactListStickeyHeaderAdapter.HeaderViewHolder>,
        SectionIndexer {

//    lateinit var mActivity:  Activity
//    lateinit var arrayList:  ArrayList<ContactModelContacts>
    var clallcontacts: ConstraintLayout? = null

    //=========================================

    private var sectionsTranslator = HashMap<Int, Int>()
    private var mSectionPositions: MutableList<Int> = ArrayList()
    private val mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    //=========================================

    fun setData(list: ArrayList<ContactModelContacts>?,activity: MainActivity) {
        arrayList = list!!
        mActivity = activity
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_list_item_view, parent, false)
        return AnimalViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {

        holder.setIsRecyclable(false)
        //Initialize contact model
        val model = arrayList!![position]

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

            val intentfavouriteActivity = Intent(mActivity, ContactDetailActivity::class.java)

            intentfavouriteActivity.putExtra("number",model.number ) // pass your values and retrieve them in the other Activity using keyName
            intentfavouriteActivity.putExtra("image", model.image) // pass your values and retrieve them in the other Activity using keyName
            intentfavouriteActivity.putExtra("name", model.name) // pass your values and retrieve them in the other Activity using keyName
            intentfavouriteActivity.putExtra("id", model.id)

            mActivity?.startActivity(intentfavouriteActivity)

        }

    }

    override fun getHeaderId(position: Int): Long {
        if (arrayList?.get(position)!!.name?.get(0)?.toLong()!=null && position<arrayList!!.size){
//            Log.e("TAG", "array_size: "+ arrayList!!.size)
//            Log.e("TAG", "position: "+ position)
            return arrayList!!.get(position).name?.get(0)?.toLong()!!
        }
        else
            return 11
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_header_contact, parent, false)
        return HeaderViewHolder(view)
    }

    override fun onBindHeaderViewHolder(headerViewHolder: HeaderViewHolder, i: Int) {
        val nameHeader = arrayList!![i].name?.substring(0,1)
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
        val tvName: TextView
        var profile: ImageView

        init {
            profile= itemView.findViewById(R.id.iv_image)
            tvName = itemView.findViewById(R.id.tv_name_favorite)
            clallcontacts = itemView.findViewById(R.id.cl_all_contacts)

        }
    }

    @ExperimentalStdlibApi
    override fun getSections(): Array<out Any> {

        val sections: MutableList<String> = ArrayList(27)
        val alphabetFull = ArrayList<String>()
        mSectionPositions = ArrayList()
        run {
            var i = 0
            val size = arrayList!!.size
            while (i < size) {
                val section = arrayList!![i].name?.get(0)?.uppercase().toString()
                if (!sections.contains(section)) {
                    sections.add(section)
                    mSectionPositions.add(i)
                }
                i++
            }
        }
        for (element in mSections) {
            alphabetFull.add(element.toString())
        }
        sectionsTranslator = Helpers.sectionsHelper(sections, alphabetFull)
        return alphabetFull.toTypedArray()
    }

    override fun getPositionForSection(sectionIndex: Int): Int {
        return mSectionPositions[sectionsTranslator[sectionIndex]!!]
    }

    override fun getSectionForPosition(position: Int): Int {
        return 0
    }

}