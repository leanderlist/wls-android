package com.example.wls_android.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.wls_android.MainActivity
import com.example.wls_android.R
import com.example.wls_android.data.Data
import com.example.wls_android.data.Disturbance
import com.example.wls_android.data.getKtorClient
import com.google.gson.Gson
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DisturbanceWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    companion object {
        const val WORKER_TAG = "disturbanceWorker"
    }

    private val gson = Gson()
    private val sharedPreferences: SharedPreferences =
        appContext.getSharedPreferences("WLS-App", Context.MODE_PRIVATE)
    private val trackedDisturbances = sharedPreferences.getStringSet("trackedDisturbances", setOf())
        ?.map { gson.fromJson(it, Disturbance::class.java) }?.toMutableList() ?: mutableListOf()
    private val client = getKtorClient("/api/disturbances")

    override suspend fun doWork(): Result {
        // Log.e("DisturbanceWorker", "get: $trackedDisturbances")
        // sharedPreferences.edit().clear().apply()
        return withContext(Dispatchers.IO) {
            checkForNewDisturbances()
            Result.success()
        }
    }

    private suspend fun checkForNewDisturbances() {
        val selectedLines = inputData.getStringArray("selectedLines")?.toList()
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
                if (selectedLines != null) {
                    Log.e("DisturbanceWorker", "selectedLines: $selectedLines")
                    // parameters.append("selectedLines", selectedLines.toString())
                }
            }
            Log.e("DisturbanceWorker", "url: $url")
        }
        val body = response.body<Data>()
        for (disturbance in body.data) {
            if (!isTrackedDisturbance(disturbance) && isActiveDisturbance(disturbance)) {
                trackedDisturbances.add(disturbance)
                sendNotification(disturbance)
            }
            if (isTrackedDisturbance(disturbance) && !isActiveDisturbance(disturbance)) {
                trackedDisturbances.remove(disturbance)
            }
        }
        sharedPreferences.edit().apply {
            putStringSet(
                "trackedDisturbances",
                trackedDisturbances.filter { isActiveDisturbance(it) }.map { gson.toJson(it) }
                    .toSet()
            )
            apply()
        }
        // sharedPreferences.edit().clear().apply()
        // Log.e("DisturbanceWorker", "set: $trackedDisturbances")
    }

    private fun isTrackedDisturbance(disturbance: Disturbance): Boolean {
        return trackedDisturbances.any { it.id == disturbance.id }
    }

    private fun isActiveDisturbance(disturbance: Disturbance): Boolean {
        return disturbance.end_time == null
    }

    private fun sendNotification(disturbance: Disturbance) {
        val channelId = "disturbance_notification_channel"
        val notificationId = disturbance.id.hashCode()

        val name = "disturbance_notification_channel"
        val descriptionText = "Channel for disturbance notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTime = LocalDateTime.parse(disturbance.start_time, formatter)
        val whenMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val largeIcon = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.train)
        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.train)
            .setLargeIcon(largeIcon)
            .setContentTitle("Neue Störung")
            .setContentText(disturbance.title)
            .setColor(0xFFC0000F.toInt())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setWhen(whenMillis)

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            with(NotificationManagerCompat.from(applicationContext)) {
                notify(notificationId, builder.build())
            }
        } else {
            // TODO: Case wenn keine Berechtigung für Benachrichtigungen
        }
    }
}