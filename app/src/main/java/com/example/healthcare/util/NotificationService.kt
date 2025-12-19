package com.example.healthcare.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.healthcare.R
import com.example.healthcare.data.model.Appointment
import com.example.healthcare.data.repository.FirebaseRepository
import com.google.firebase.firestore.ListenerRegistration

class NotificationService : Service() {

    private val repo = FirebaseRepository()
    private var listener: ListenerRegistration? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("NotificationService", "Service Started")
        val userId = repo.currentUserId()

        if (userId != null) {
            // Keep track of old appointments to detect changes
            var oldAppointments: List<Appointment>? = null

            listener = repo.listenToAppointments(userId) { newAppointments ->
                if (oldAppointments != null) {
                    // Find changes
                    for (newApp in newAppointments) {
                        val oldApp = oldAppointments!!.find { it.appointmentId == newApp.appointmentId }
                        if (oldApp != null && oldApp.status != newApp.status) {
                            // Status changed! Send notification.
                            sendNotification(newApp)
                        }
                    }
                }
                oldAppointments = newAppointments
            }
        }

        return START_STICKY // Keep service running
    }

    private fun sendNotification(app: Appointment) {
        val channelId = "APPOINTMENT_STATUS_CHANNEL"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Appointment Status Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Appointment Update")
            .setContentText("Your appointment with ${app.doctorName} on ${app.date} is now ${app.status}")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .build()

        // Unique ID for each notification
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("NotificationService", "Service Destroyed")
        // Stop listening to prevent memory leaks
        listener?.remove()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
