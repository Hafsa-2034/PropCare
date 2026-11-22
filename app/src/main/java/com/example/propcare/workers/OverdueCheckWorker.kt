package com.example.propcare.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.propcare.PropCareApplication
import com.example.propcare.domain.model.ComplaintStatus
import com.example.propcare.domain.model.PriorityLevel
import com.example.propcare.utils.DateUtils
import kotlinx.coroutines.flow.first

class OverdueCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val container = (applicationContext as PropCareApplication).container
            // ASSUMPTION: repository exposes allComplaints: Flow<List<ComplaintEntity>>
            val complaints = container.repository.allComplaints.first()

            val unresolved = complaints.filter { it.status != ComplaintStatus.RESOLVED }
            val urgent = unresolved.count {
                it.priority == PriorityLevel.HIGH || it.priority == PriorityLevel.URGENT
            }
            val overdue = unresolved.count { DateUtils.isOverdue(it.createdAt, it.status) }
            val flagged = unresolved.count {
                it.priority == PriorityLevel.HIGH ||
                    it.priority == PriorityLevel.URGENT ||
                    DateUtils.isOverdue(it.createdAt, it.status)
            }

            if (flagged > 0) postNotification(flagged, urgent, overdue)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun postNotification(flagged: Int, urgent: Int, overdue: Int) {
        createChannel()

        // Android 13+ needs runtime permission; skip silently if not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                applicationContext, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("PropCare: $flagged complaint(s) need attention")
            .setContentText("$urgent high/urgent, $overdue overdue (48h+)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(applicationContext).notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) { /* permission revoked mid-flight */ }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "PropCare Alerts", NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Alerts for urgent and overdue maintenance complaints" }
            applicationContext.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "propcare_alerts"
        const val WORK_NAME = "propcare_overdue_check"
        private const val NOTIFICATION_ID = 1001
    }
}
