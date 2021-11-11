package com.dialler.ct.ads


import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.multidex.BuildConfig
import com.dialler.ct.activity.MainActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.*

class AdsManager : Application() {

    private var sApplication: Application? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this) { }
        sApplication = this
        context = getContext()
        playstoreInstalledVerification()
        loadBanner()

//        loadInterstitialAd()
//        loadBanner()

    }

    private fun getApplication(): Application? {
        return sApplication
    }

    private fun getContext(): Context {
        return getApplication()!!.applicationContext
    }

    companion object {


        var mInterstitialAd: InterstitialAd? = null
        var adRequest: AdRequest? = null
        lateinit var context: Context
        lateinit var adView: AdView
        private val AD_UNIT_ID = AddId.BANNER //Change Here
        private var isInstalledfromPlaystore: Boolean? = null


        private fun loadBanner() {
            adView = AdView(context)

            if(isInstalledfromPlaystore==true) {
                adView.adUnitId = AD_UNIT_ID
                adView.adSize = adSize
                val adRequest = AdRequest.Builder().build()
                adView.loadAd(adRequest)
            }

        }

        private val adSize: AdSize
            get() {
                val windowManager: WindowManager =
                    context.getSystemService(WINDOW_SERVICE) as WindowManager
                val display: Display = windowManager.defaultDisplay
                val outMetrics = DisplayMetrics()
                display.getMetrics(outMetrics)
                val widthPixels = outMetrics.widthPixels.toFloat()
                val density = outMetrics.density
                val adWidth = (widthPixels / density).toInt()
                return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
            }

        fun loadInterstitialAd() {
            adRequest = AdRequest.Builder().build()
            InterstitialAd.load(context, AddId.INTERSTITIAL/*Change Here*/, adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.e("TAG Admob", adError.message)
                        mInterstitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Log.e("TAG Admob", "Ad was loaded.")
                        mInterstitialAd = interstitialAd
                    }
                })
        }

        fun showInterstitial(context: Activity, listener: Listener) {
            if (mInterstitialAd != null) {
                mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d("TAG Admob", "Ad was dismissed.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        mInterstitialAd = null
                        loadInterstitialAd()
                        listener.intersitialAdClosedCallback()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                        Log.d("TAG Admob", "Ad failed to show.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        mInterstitialAd = null
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d("TAG Admob", "Ad showed fullscreen content.")
                        // Called when ad is dismissed.
                    }
                }
                mInterstitialAd?.show(context)
            } else {
                listener.intersitialAdClosedCallback()
            }
        }

        interface Listener {
            fun intersitialAdClosedCallback()
        }
    }


    private fun playstoreAppVerfication(context: Context): Boolean {
        // A list with valid installers package name
        val validInstallers: List<String> =
            ArrayList(Arrays.asList("com.android.vending", "com.google.android.feedback"))

        // The package name of the app that has installed your app
        val installchecker = context.packageManager.getInstallerPackageName(context.packageName)

        // true if your app has been downloaded from Play Store
        return installchecker != null && validInstallers.contains(installchecker)
    }

    private fun playstoreInstalledVerification() {
        if (playstoreAppVerfication(context) == true) {
            isInstalledfromPlaystore = true
            loadInterstitialAd()
        }
    }
}
