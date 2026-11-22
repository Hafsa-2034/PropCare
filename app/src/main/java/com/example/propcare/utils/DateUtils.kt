package com.example.propcare.utils

import com.example.propcare.domain.model.ComplaintStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {
    val OVERDUE_THRESHOLD_MS: Long = TimeUnit.HOURS.toMillis(48)

    fun formatTimestamp(timestamp: Long, pattern: String = "dd MMM yyyy, HH:mm"): String =
        SimpleDateFormat(pattern, Locale.getDefault()).format(Date(timestamp))

    /** A ticket is overdue if it is not RESOLVED and has been open longer than 48 hours. */
    fun isOverdue(
        createdAt: Long,
        status: ComplaintStatus,
        now: Long = System.currentTimeMillis()
    ): Boolean = status != ComplaintStatus.RESOLVED && (now - createdAt) > OVERDUE_THRESHOLD_MS

    fun hoursOpen(createdAt: Long, now: Long = System.currentTimeMillis()): Long =
        TimeUnit.MILLISECONDS.toHours(now - createdAt)
}
