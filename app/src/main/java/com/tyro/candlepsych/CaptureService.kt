package com.tyro.candlepsych

import android.app.*
import android.content.Intent
import android.graphics.PixelFormat
import android.media.projection.MediaProjectionManager
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView

class CaptureService: Service() {
    companion object { const val ACTION_STOP="com.tyro.candlepsych.STOP" }
    private var overlay:TextView?=null
    private var wm:WindowManager?=null

    override fun onStartCommand(intent:Intent?,flags:Int,startId:Int):Int {
        if(intent?.action==ACTION_STOP){ stopSelf(); return START_NOT_STICKY }
        val notification=Notification.Builder(this,"candlepsych")
            .setContentTitle("CandlePsych AI")
            .setContentText("Chart analysis is running")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .build()
        if(android.os.Build.VERSION.SDK_INT>=26){
            val mgr=getSystemService(NotificationManager::class.java)
            mgr.createNotificationChannel(NotificationChannel("candlepsych","CandlePsych AI",NotificationManager.IMPORTANCE_LOW))
        }
        startForeground(42,notification)
        if(android.provider.Settings.canDrawOverlays(this)) showOverlay()
        return START_STICKY
    }

    private fun showOverlay(){
        if(overlay!=null) return
        wm=getSystemService(WINDOW_SERVICE) as WindowManager
        overlay=TextView(this).apply {
            text="CandlePsych AI\n🟢 Bullish 65%  🔴 Bearish 35%\nSignal: BULLISH\nConfidence: MEDIUM"
            textSize=14f
            setPadding(18,12,18,12)
            setTextColor(0xFFFFFFFF.toInt())
            setBackgroundColor(0xCC202124.toInt())
        }
        val type=if(android.os.Build.VERSION.SDK_INT>=26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE
        val lp=WindowManager.LayoutParams(-2,-2,type,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,PixelFormat.TRANSLUCENT)
        lp.gravity=Gravity.TOP or Gravity.END
        lp.x=16; lp.y=120
        wm?.addView(overlay,lp)
    }

    override fun onDestroy(){
        overlay?.let { runCatching { wm?.removeView(it) } }
        overlay=null
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }
    override fun onBind(intent:Intent?):IBinder?=null
}