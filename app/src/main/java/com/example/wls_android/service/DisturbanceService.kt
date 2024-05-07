package com.example.wls_android.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.wls_android.MainActivity
import com.example.wls_android.R
import com.example.wls_android.data.Data
import com.example.wls_android.data.Disturbance
import com.example.wls_android.data.getKtorClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DisturbanceService : Service() {
    private var newDisturbances = mutableListOf<Disturbance>()
    private lateinit var client: HttpClient
    private var job: Job? = null

    override fun onCreate() {
        super.onCreate()
        client = getKtorClient("/api/disturbances")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                checkForNewDisturbances()
                delay(300000) // Warte 5 Minuten
            }
        }
        return START_REDELIVER_INTENT
    }

    private suspend fun checkForNewDisturbances() {
        val response = client.get {
            url {
                parameters.append(
                    "from",
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                )
                parameters.append(
                    "to",
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                )
            }
        }
        val body = response.body<Data>()

        // if one of the disturbances is new, send a notification
        for (disturbance in body.data) {
            if (isNewDisturbance(disturbance)) {
                sendNotification(disturbance)
            }
        }
    }

    private fun isNewDisturbance(disturbance: Disturbance): Boolean {
        if (newDisturbances.none { it.id == disturbance.id }) {
            newDisturbances.add(disturbance)
            return true
        }
        return false
    }

    private fun sendNotification(disturbance: Disturbance) {
        val channelId = "disturbance_notification_channel"
        val notificationId = disturbance.id.hashCode()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background) // TODO: Icon anpassen
            .setContentTitle("Neue Störung")
            .setContentText(disturbance.title)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(notificationId, builder.build())
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel() // Stellen Sie sicher, dass der Job beendet wird, wenn der Service zerstört wird
    }
}