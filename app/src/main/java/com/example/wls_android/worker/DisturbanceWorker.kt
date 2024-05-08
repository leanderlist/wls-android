package com.example.wls_android.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.wls_android.MainActivity
import com.example.wls_android.R
import com.example.wls_android.data.Data
import com.example.wls_android.data.Disturbance
import com.example.wls_android.data.getKtorClient
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DisturbanceWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val gson = Gson()
    private val sharedPreferences: SharedPreferences =
        appContext.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
    private val trackedDisturbances = sharedPreferences.getStringSet("trackedDisturbances", setOf())
        ?.map { gson.fromJson(it, Disturbance::class.java) }?.toMutableList() ?: mutableListOf()
    private val client = getKtorClient("/api/disturbances")

    override suspend fun doWork(): Result {
        Log.e("DisturbanceWorker", "get: $trackedDisturbances")
        // sharedPreferences.edit().clear().apply()
        return withContext(Dispatchers.IO) {
            try {
                checkForNewDisturbances()
                Result.success()
            } catch (e: Exception) {
                Result.failure()
            }
        }
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
                trackedDisturbances.filter { isActiveDisturbance(it) }.map { gson.toJson(it) }.toSet()
            )
            apply()
        }
        // sharedPreferences.edit().clear().apply()
        Log.e("DisturbanceWorker", "set: $trackedDisturbances")
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
        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.train)
            .setContentTitle("Neue Störung")
            .setContentText(disturbance.title)
            .setColor(0xFFC0000F.toInt())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

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

//    private fun getBitmapFromVectorDrawable(drawableId: Int): Bitmap {
//        val drawable = ContextCompat.getDrawable(applicationContext, drawableId)
//        return if (drawable is BitmapDrawable) {
//            drawable.bitmap
//        } else {
//            val bitmap = Bitmap.createBitmap(
//                drawable!!.intrinsicWidth,
//                drawable.intrinsicHeight,
//                Bitmap.Config.ARGB_8888
//            )
//            val canvas = Canvas(bitmap)
//            drawable.setBounds(0, 0, canvas.width, canvas.height)
//            drawable.draw(canvas)
//            bitmap
//        }
//    }
}