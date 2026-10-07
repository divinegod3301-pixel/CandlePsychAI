package com.tyro.candlepsych

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.*

class MainActivity : Activity() {
    private val captureCode = 2001
    private lateinit var status: TextView
    private lateinit var start: Button

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        val pad = (20 * resources.displayMetrics.density).toInt()
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad,pad,pad,pad)
        }
        root.addView(TextView(this).apply {
            text="CandlePsych AI"
            textSize=28f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        })
        root.addView(TextView(this).apply {
            text="Real-time chart structure analyzer"
            textSize=15f
            alpha=.75f
        })
        status=TextView(this).apply { text="● READY"; textSize=16f; setPadding(0,pad,0,pad) }
        root.addView(status)
        root.addView(TextView(this).apply {
            text="The app observes visible chart structure and estimates directional probability. It does not know future prices or hidden trader intent."
            textSize=14f
        })
        root.addView(Button(this).apply {
            text="ALLOW FLOATING OVERLAY"
            setOnClickListener { startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))) }
        })
        start=Button(this).apply {
            text="START CHART ANALYSIS"
            setOnClickListener { requestCapture() }
        }
        root.addView(start)
        root.addView(Button(this).apply {
            text="STOP ANALYSIS"
            setOnClickListener {
                startService(Intent(this@MainActivity,CaptureService::class.java).setAction(CaptureService.ACTION_STOP))
                status.text="● READY"; start.isEnabled=true; start.text="START CHART ANALYSIS"
            }
        })
        root.addView(TextView(this).apply {
            text="\nPrediction display\n🟢 Bullish / 🔴 Bearish\nSignal: BULLISH / BEARISH / NO SIGNAL\n\nExperimental classifier — not financial advice."
            textSize=14f
        })
        setContentView(ScrollView(this).apply { addView(root) })
    }

    private fun requestCapture() {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this,"Allow the floating overlay first.",Toast.LENGTH_LONG).show()
            return
        }
        val mgr=getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mgr.createScreenCaptureIntent(),captureCode)
    }

    override fun onActivityResult(requestCode:Int,resultCode:Int,data:Intent?) {
        super.onActivityResult(requestCode,resultCode,data)
        if(requestCode!=captureCode || data==null) return
        startForegroundService(Intent(this,CaptureService::class.java).apply {
            putExtra("resultCode",resultCode)
            putExtra("captureData",data)
        })
        status.text="● ANALYZING"
        start.text="ANALYSIS RUNNING"
        start.isEnabled=false
    }
}