
package com.example.propcare.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.propcare.domain.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        ComplaintEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(PropCareConverters::class)
abstract class PropCareDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun complaintDao(): ComplaintDao

    companion object {

        @Volatile
        private var INSTANCE: PropCareDatabase? = null

        fun getDatabase(context: Context): PropCareDatabase {

            return INSTANCE ?: synchronized(this) {

                val callback = object : Callback() {

                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)

                        CoroutineScope(Dispatchers.IO).launch {

                            INSTANCE?.userDao()?.apply {

                                insertUser(
                                    UserEntity(
                                        name = "Sample Tenant",
                                        role = UserRole.TENANT,
                                        unitOrSpecialty = "A101"
                                    )
                                )

                                insertUser(
                                    UserEntity(
                                        name = "Sample Landlord",
                                        role = UserRole.LANDLORD,
                                        unitOrSpecialty = "Management"
                                    )
                                )

                                insertUser(
                                    UserEntity(
                                        name = "Technician One",
                                        role = UserRole.TECHNICIAN,
                                        unitOrSpecialty = "Plumbing"
                                    )
                                )

                                insertUser(
                                    UserEntity(
                                        name = "Technician Two",
                                        role = UserRole.TECHNICIAN,
                                        unitOrSpecialty = "Electrical"
                                    )
                                )
                            }
                        }
                    }
                }

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PropCareDatabase::class.java,
                    "propcare_database"
                )
                    .addCallback(callback)
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}
