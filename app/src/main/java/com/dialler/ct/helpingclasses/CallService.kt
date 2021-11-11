package com.dialler.ct.helpingclasses

import android.os.Build
import android.telecom.InCallService
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.M)
class CallService : InCallService()