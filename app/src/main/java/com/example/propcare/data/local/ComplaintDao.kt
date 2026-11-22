
package com.example.propcare.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ComplaintDao {

    @Query("SELECT * FROM complaints ORDER BY createdAt DESC")
    fun getAllComplaints(): Flow<List<ComplaintEntity>>

    @Query("SELECT * FROM complaints WHERE id = :complaintId")
    fun getComplaintById(complaintId: Int): Flow<ComplaintEntity?>

    @Query("SELECT * FROM complaints WHERE status = :status")
    fun getComplaintsByStatus(status: String): Flow<List<ComplaintEntity>>

    @Query("SELECT * FROM complaints WHERE assignedTechnicianId = :technicianId")
    fun getComplaintsByTechnician(technicianId: Int): Flow<List<ComplaintEntity>>

    @Query("SELECT COUNT(*) FROM complaints")
    fun getTotalComplaintCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM complaints WHERE status != 'RESOLVED'")
    fun getPendingComplaintCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM complaints WHERE status = 'RESOLVED'")
    fun getCompletedComplaintCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaint(complaint: ComplaintEntity)

    @Update
    suspend fun updateComplaint(complaint: ComplaintEntity)
}
