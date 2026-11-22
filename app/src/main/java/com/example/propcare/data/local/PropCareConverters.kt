
package com.example.propcare.data.local

import androidx.room.TypeConverter
import com.example.propcare.domain.model.UserRole
import com.example.propcare.domain.model.IssueCategory
import com.example.propcare.domain.model.PriorityLevel
import com.example.propcare.domain.model.ComplaintStatus

class PropCareConverters {

    @TypeConverter
    fun fromUserRole(value: UserRole): String {
        return value.name
    }

    @TypeConverter
    fun toUserRole(value: String): UserRole {
        return UserRole.valueOf(value)
    }

    @TypeConverter
    fun fromIssueCategory(value: IssueCategory): String {
        return value.name
    }

    @TypeConverter
    fun toIssueCategory(value: String): IssueCategory {
        return IssueCategory.valueOf(value)
    }

    @TypeConverter
    fun fromPriorityLevel(value: PriorityLevel): String {
        return value.name
    }

    @TypeConverter
    fun toPriorityLevel(value: String): PriorityLevel {
        return PriorityLevel.valueOf(value)
    }

    @TypeConverter
    fun fromComplaintStatus(value: ComplaintStatus): String {
        return value.name
    }

    @TypeConverter
    fun toComplaintStatus(value: String): ComplaintStatus {
        return ComplaintStatus.valueOf(value)
    }
}
