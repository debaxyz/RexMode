package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM license_keys WHERE status = 'ACTIVE' LIMIT 1")
    fun getActiveLicense(): Flow<LicenseKeyEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLicense(license: LicenseKeyEntity)

    @Query("DELETE FROM license_keys")
    suspend fun clearLicenses()

    @Query("SELECT * FROM system_logs ORDER BY id DESC LIMIT 50")
    fun getLogs(): Flow<List<SystemLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: SystemLogEntity)

    @Query("DELETE FROM system_logs")
    suspend fun clearLogs()

    @Query("SELECT * FROM control_modules ORDER BY code ASC")
    fun getControlModules(): Flow<List<ControlModuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModules(modules: List<ControlModuleEntity>)

    @Query("UPDATE control_modules SET isEnabled = :enabled WHERE code = :code")
    suspend fun updateModuleStatus(code: String, enabled: Boolean)
}
