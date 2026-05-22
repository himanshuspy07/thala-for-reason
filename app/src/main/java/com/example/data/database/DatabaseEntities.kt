package com.example.data.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "calculation_history")
data class HistoryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val expression: String,
    val result: String,
    val explanation: String,
    val isThalaTriggered: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val badgeIconName: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long = 0L,
    val xpReward: Int = 100
)

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey val id: Int = 1,
    val currentThemeStr: String = "NAVY_GOLD", // "NAVY_GOLD", "CS_YELLOW", "INDIA_BLUE", "NEON_MINT"
    val totalXp: Int = 0,
    val triggerCount: Int = 0,
    val currentStreak: Int = 1,
    val lastActiveTimestamp: Long = System.currentTimeMillis(),
    val soundEnabled: Boolean = true,
    val hapticIntensity: Float = 1.0f,
    val maxNumberReached: Double = 0.0,
    val captainCoolUnlocked: Boolean = false
)

@Dao
interface ThalaDao {
    @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC LIMIT 50")
    fun getAllHistory(): Flow<List<HistoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryItem(item: HistoryItem)

    @Query("DELETE FROM calculation_history")
    suspend fun clearHistory()

    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): Flow<List<Achievement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<Achievement>)

    @Query("UPDATE achievements SET isUnlocked = 1, unlockedAt = :timestamp WHERE id = :id")
    suspend fun unlockAchievement(id: String, timestamp: Long)

    @Query("SELECT * FROM user_settings WHERE id = 1")
    fun getUserSettings(): Flow<UserSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserSettings(settings: UserSettings)
}

@Database(entities = [HistoryItem::class, Achievement::class, UserSettings::class], version = 1, exportSchema = false)
abstract class ThalaDatabase : RoomDatabase() {
    abstract fun thalaDao(): ThalaDao
}
