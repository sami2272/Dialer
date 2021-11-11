package com.dialler.ct.activity

import android.app.ActionBar
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import com.dialler.ct.R
import com.dialler.ct.ads.AdsManager.Companion.context
import java.util.*

class Setting_Activity : AppCompatActivity(), View.OnClickListener {

    lateinit var linearLayout_back: LinearLayout
    lateinit var linearLayout_block_contact: LinearLayout
    lateinit var relative_theme       : RelativeLayout
    lateinit var relative_app_language: RelativeLayout
    lateinit var tv_theme : TextView
    lateinit var img_back : ImageView

    var sharedpreferences     : SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storelang()
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        setContentView(R.layout.activity_setting)
        initView()
        setOnClickListener()
        setThemeText()
    }

    override fun onResume() {
        super.onResume()
        setBackIcon()
    }

    private fun setBackIcon() {
        val prefs = getSharedPreferences("Settings", MODE_PRIVATE)
        val lang = prefs.getString("My_lang", "")
        if (lang != null) {
            when (lang) {
                "En" -> {

                }
                "fr" -> {

                }
                "ur" -> {
                    img_back.setImageDrawable(getDrawable(R.drawable.ic_baseline_chevron_right_24))
                }
                "hi" -> {

                }
                "ar" -> {
                    img_back.setImageDrawable(getDrawable(R.drawable.ic_baseline_chevron_right_24))
                }
                "zh" -> {

                }
            }

        }
    }

    private fun setThemeText() {
        if (sharedpreferences!!.contains("theme_check")) {
            if (sharedpreferences!!.getInt("theme_check", 0) == 1) {


                    tv_theme.text = getString(R.string.str_light_theme)

            }else{
                tv_theme.text = getString(R.string.str_dark_theme)
            }
        }
    }

    private fun initView() {
        linearLayout_back = findViewById(R.id.linearLayout_back)
        linearLayout_block_contact = findViewById(R.id.linearLayout_block_contact)

        relative_theme = findViewById(R.id.relative_theme)
        relative_app_language = findViewById(R.id.relative_app_language)
        tv_theme =  findViewById(R.id.tv_theme)
        img_back = findViewById(R.id.img_back)
    }

    private fun setOnClickListener() {
        linearLayout_back.setOnClickListener(this)
        linearLayout_block_contact.setOnClickListener(this)

        relative_theme.setOnClickListener(this)
        relative_app_language.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        if (view?.id == R.id.linearLayout_back) {
            finish()
        }
        if (view?.id == R.id.relative_theme) {
            alertDialogShow_change_theme()

        }
        if (view?.id == R.id.relative_app_language) {
            alertDialogBoxChangeLanguage()
        }
        if (view?.id == R.id.linearLayout_block_contact) {
            startActivity(Intent(this,BlockContactActivity::class.java))
        }

    }

    private fun alertDialogShow_change_theme() {
        val viewGroup: ViewGroup = this@Setting_Activity.findViewById(android.R.id.content)

        val dialogView: View = LayoutInflater.from(this)
                .inflate(R.layout.custom_dialog, viewGroup, false)

        val builder = AlertDialog.Builder(this@Setting_Activity)
        builder.setView(dialogView)
        val alertDialog = builder.create()

        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window!!.setLayout(ActionBar.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        alertDialog.show()

        val radioGroup : RadioGroup =  dialogView.findViewById(R.id.radio_Group_themes)
        radioGroup.checkedRadioButtonId

        val radio_button_Light: RadioButton = dialogView.findViewById(R.id.radio_button_Light)
        val radio_button_Dark: RadioButton = dialogView.findViewById(R.id.radio_button_Dark)

        if (sharedpreferences!!.contains("theme_check")) {
            if (sharedpreferences!!.getInt("theme_check",0)==1){

                radio_button_Light.isChecked=true
                radio_button_Dark.isChecked=false

            }else{
                radio_button_Light.isChecked=false
                radio_button_Dark.isChecked=true
            }
        }

        // when Ok button is pressed
        dialogView.findViewById<TextView>(R.id.txt_themes_ok).setOnClickListener {
            val radioGroup : RadioGroup =  dialogView.findViewById(R.id.radio_Group_themes)
            val genid: Int = radioGroup.checkedRadioButtonId
            val radioButton: RadioButton = dialogView.findViewById(genid)
            val radioTxt = radioButton.text.toString()

            if(radioTxt==getString(R.string.str_light_theme)){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                SavePreferenceInt("theme_check",1)
                alertDialog.dismiss()
            }else if(radioTxt==getString(R.string.str_dark_theme)){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                SavePreferenceInt("theme_check", 2)
                alertDialog.dismiss()
            }
        }
        // when cancel button is pressed
        dialogView.findViewById<TextView>(R.id.txt_themes_cancel).setOnClickListener {
            alertDialog.dismiss();
        }
    }

    private fun alertDialogBoxChangeLanguage(){
        val builder = AlertDialog.Builder(this)
        val la = arrayOf("English", "Français", "اردو\n", "हिंदी","عربى","Chinese","Russian")

        val prefs = applicationContext.getSharedPreferences("Settings", AppCompatActivity.MODE_PRIVATE)
        val lang = prefs.getString("My_lang", "")
        var check=0
        if(lang=="En"){
            check = 0
        }else
            if(lang=="fr"){
                check = 1
            }else  if(lang=="ur"){
                check = 2
            }else  if(lang=="hi"){
                check = 3
            }
            else  if(lang=="ar"){
                check = 4
            }
            else  if(lang=="zh"){
                check = 5
            }else if (lang=="ru"){
                check =6
            }

        builder.setTitle(resources.getString(R.string.selectLanguage)).setSingleChoiceItems(la, check, null).setPositiveButton(resources.getString(
                R.string.yes
        ), DialogInterface.OnClickListener { dialog, which ->
            val selectedPosition = (dialog as AlertDialog).listView.checkedItemPosition
            if (selectedPosition==check){
                Toast.makeText(this,getString(R.string.str_langauage_already_applied),Toast.LENGTH_LONG).show()
            }else {
                MainActivity.isScreen=true
                if (selectedPosition == 0) {
                    setLocale1("En")
                    recreate()
                }
                if (selectedPosition == 1) {
                    setLocale1("fr")
                    recreate()
                }
                if (selectedPosition == 2) {
                    setLocale1("ur")
                    recreate()
                }
                if (selectedPosition == 3) {
                    setLocale1("hi")
                    recreate()
                }
                if (selectedPosition == 4) {
                    setLocale1("ar")
                    recreate()
                }
                if (selectedPosition == 5) {
                    setLocale1("zh")
                    recreate()
                }
                if(selectedPosition==6){
                    setLocale1("ru")
                    recreate()
                }
            }
        }).setNegativeButton(getString(R.string.cancel), DialogInterface.OnClickListener { _, _ ->


        })




        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun setLocale1(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLayoutDirection(locale)
        config.locale = locale
        this.baseContext?.resources?.updateConfiguration(config,   this.baseContext?.resources!!.displayMetrics)
        val editor = context?.getSharedPreferences("Settings", AppCompatActivity.MODE_PRIVATE)?.edit()
        if (editor != null) {
            editor.putString("My_lang", lang)
            editor.apply()
        }
    }

    private fun SavePreferenceInt(key: String?, value: Int?) {
        val editor = sharedpreferences!!.edit()
        editor.putInt(key, value!!)
        editor.apply()
    }

    private fun storelang() {
        val prefs = getSharedPreferences("Settings", MODE_PRIVATE)
        val lang = prefs.getString("My_lang", "")
        if (lang != null) {
            setLocale1(lang)
        }
    }


}