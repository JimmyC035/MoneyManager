package com.example.liveactivty

import android.app.Notification.MediaStyle
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ForegroundService: Service() {



    private val process = ProcessCounter()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when(intent?.action){
            ForegroundAction.START.name -> start()
            ForegroundAction.STOP.name -> stop()
        }


        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        CoroutineScope(Dispatchers.Default).launch{
            process.start().collect{processValue ->
                Log.i("Jimmy","$processValue")
                notification(processValue = processValue)
            }
        }
    }

    private fun notification(processValue: Int){
        val processNotification = NotificationCompat
            .Builder(this,"delivery_status_channel")
            .setSmallIcon(R.drawable.uber)
            .setContentTitle("Pick up in 1 min")
            .setContentText("TD3065")
            .setProgress(100,processValue,false)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .build()
        startForeground(1,processNotification)
    }

    private fun stop() {
        process.stop()
        stopSelf()
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    enum class ForegroundAction{
        START, STOP
    }
}