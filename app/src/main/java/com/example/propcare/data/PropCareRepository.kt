
package com.example.propcare.data.repository

import com.example.propcare.data.local.ComplaintDao
import com.example.propcare.data.local.ComplaintEntity
import com.example.propcare.data.local.UserDao
import com.example.propcare.data.local.UserEntity
import kotlinx.coroutines.flow.Flow

class PropCareRepository(
    private val userDao: UserDao,
    private val complaintDao: ComplaintDao
) {

    // Users
    fun getAllUsers(): Flow<List<UserEntity>> =
        userDao.getAllUsers()

    fun getUsersByRole(role: String): Flow<List<UserEntity>> =
        userDao.getUsersByRole(role)

    suspend fun insertUser(user: UserEntity) =
        userDao.insertUser(user)

    suspend fun updateUser(user: UserEntity) =
        userDao.updateUser(user)

    // Complaints
    fun getAllComplaints(): Flow<List<ComplaintEntity>> =
        complaintDao.getAllComplaints()

    fun getComplaintById(id: Int): Flow<ComplaintEntity?> =
        complaintDao.getComplaintById(id)

    fun getComplaintsByStatus(status: String): Flow<List<ComplaintEntity>> =
        complaintDao.getComplaintsByStatus(status)

    fun getComplaintsByTechnician(id: Int): Flow<List<ComplaintEntity>> =
        complaintDao.getComplaintsByTechnician(id)

    fun getTotalComplaintCount(): Flow<Int> =
        complaintDao.getTotalComplaintCount()

    fun getPendingComplaintCount(): Flow<Int> =
        complaintDao.getPendingComplaintCount()

    fun getCompletedComplaintCount(): Flow<Int> =
        complaintDao.getCompletedComplaintCount()

    suspend fun insertComplaint(complaint: ComplaintEntity) =
        complaintDao.insertComplaint(complaint)

    suspend fun updateComplaint(complaint: ComplaintEntity) =
        complaintDao.updateComplaint(complaint)
}
