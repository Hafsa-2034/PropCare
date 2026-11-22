
package com.example.propcare.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.propcare.domain.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val role: UserRole,
    val unitOrSpecialty: String
)
