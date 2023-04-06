package com.example.pokedex.data.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.pokedex.R
import com.example.pokedex.ui.activity.MainActivity
import com.example.pokedex.utils.getDistanceCovered
import com.example.pokedex.utils.message
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.math.roundToInt

@AndroidEntryPoint
class TrackingService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var counterSensor: Sensor? = null

    private var initialStepCount = -1
    private var steps = 0

    override fun onBind(intent: Intent): IBinder? { return null }

    override fun onCreate() {
        super.onCreate()
        setupNotification()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        counterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (counterSensor != null) {
            sensorManager.registerListener(this, counterSensor, 60000)
        } else {
            message("Este dispositivo no cuenta con sensores de movimiento...")
        }
        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val sensorSteps = event!!.values[0].roundToInt()

        sensorSteps.let {
            if (initialStepCount == -1) {
                initialStepCount = sensorSteps
            }

            if (event.sensor == counterSensor) {
                steps = sensorSteps - initialStepCount
            }

            if (getDistanceCovered(steps) >= 10.0) {
                steps = 0
                initialStepCount = -1
                notifyFragment("YES")
            }

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Timber.e("sensor -> $sensor")
        Timber.e("accuracy -> $accuracy")
    }

    private fun setupNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.putExtra(EXTRA_SERVICE_WORKING, true)

        val pendingFlags = if (Build.VERSION.SDK_INT >= 23) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, pendingFlags)

        val builder = NotificationCompat.Builder(this, getNotificationChannelId())
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.search_pokemon))
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher_round)

        startForeground(NOTIFICATION_ID, builder.build())
    }

    private fun getNotificationChannelId(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(channelId = getString(R.string.search_pokemon), channelName = getString(R.string.search_pokemon))
        } else { "" }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        service.createNotificationChannel(channel)
        return channelId
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    private fun notifyFragment(json:String){
        val intent = Intent("nameOfTheAction");
        val bundle = Bundle().apply { putString("json", json)}
        intent.putExtras(bundle)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    companion object {
        private const val Initial_Count_Key = "FootStepInitialCount"
        private const val REQUEST_LOCATION_INTERVAL = 10000L
        private const val MIN_ACCURACY = 25
        private const val DISTANCE = 100
        private const val NOTIFICATION_ID = 838
        const val EXTRA_SERVICE_WORKING = "extra_service_working"
    }

}