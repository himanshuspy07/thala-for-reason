package com.example.data.repository

import com.example.data.database.Achievement
import com.example.data.database.HistoryItem
import com.example.data.database.ThalaDao
import com.example.data.database.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.concurrent.TimeUnit

class ThalaRepository(private val dao: ThalaDao) {

    val history: Flow<List<HistoryItem>> = dao.getAllHistory()
    val achievements: Flow<List<Achievement>> = dao.getAllAchievements()
    val userSettings: Flow<UserSettings?> = dao.getUserSettings()

    suspend fun insertHistoryItem(expression: String, result: String, explanation: String, isThala: Boolean) {
        val item = HistoryItem(
            expression = expression,
            result = result,
            explanation = explanation,
            isThalaTriggered = isThala
        )
        dao.insertHistoryItem(item)

        // Increment stats if Thala triggered
        if (isThala) {
            val currentSettings = userSettings.firstOrNull() ?: UserSettings()
            val newTriggers = currentSettings.triggerCount + 1
            val newXp = currentSettings.totalXp + 50
            
            // Auto unlock streak-based achievement
            if (newTriggers >= 7) {
                unlockAchievement("streak_7")
            }
            
            saveUserSettings(currentSettings.copy(
                triggerCount = newTriggers,
                totalXp = newXp
            ))
        }
    }

    suspend fun clearHistory() {
        dao.clearHistory()
    }

    suspend fun unlockAchievement(id: String) {
        val currentAchievements = achievements.firstOrNull() ?: emptyList()
        val target = currentAchievements.find { it.id == id }
        if (target != null && !target.isUnlocked) {
            dao.unlockAchievement(id, System.currentTimeMillis())
            
            // Add XP reward to user settings
            val currentSettings = userSettings.firstOrNull() ?: UserSettings()
            saveUserSettings(currentSettings.copy(
                totalXp = currentSettings.totalXp + target.xpReward
            ))
        }
    }

    suspend fun saveUserSettings(settings: UserSettings) {
        dao.saveUserSettings(settings)
    }

    // Call this on app launch to seed achievements and handle streak detection
    suspend fun initializeApplication() {
        // Seeding achievements if none exists
        val current = achievements.firstOrNull() ?: emptyList()
        if (current.isEmpty()) {
            val defaultList = listOf(
                Achievement(
                    id = "seven_jersey",
                    title = "Number #7 Jersey",
                    description = "Get an exact number 7 calculation result to reveal the golden aura.",
                    badgeIconName = "seven",
                    xpReward = 77
                ),
                Achievement(
                    id = "helicopter_shot",
                    title = "The Helicopter Shot \uD83C\uDFCF",
                    description = "Trigger Thala mode using a calculation that yields 77 or more.",
                    badgeIconName = "bat",
                    xpReward = 177
                ),
                Achievement(
                    id = "bole_jo_koyal",
                    title = "Bole Jo Koyal \uD83D\uDC26",
                    description = "Trigger Thala detection using a custom word containing exactly 7 letters.",
                    badgeIconName = "bird",
                    xpReward = 77
                ),
                Achievement(
                    id = "streak_7",
                    title = "Captain's Streak \uD83E\uDD81",
                    description = "Trigger the cinematic Thala Mode celebration 7 times total.",
                    badgeIconName = "lion",
                    xpReward = 377
                ),
                Achievement(
                    id = "captain_cool",
                    title = "Captain Cool Mode \u2744\uFE0F",
                    description = "Accumulate over 500 XP to discover the hidden bright CSK theme.",
                    badgeIconName = "crown",
                    xpReward = 777
                )
            )
            dao.insertAchievements(defaultList)
        }

        // Streak check
        val settings = userSettings.firstOrNull() ?: UserSettings()
        val now = System.currentTimeMillis()
        val diffMs = now - settings.lastActiveTimestamp
        val diffDays = TimeUnit.MILLISECONDS.toDays(diffMs)

        var finalStreak = settings.currentStreak
        if (diffDays == 1L) {
            finalStreak += 1
        } else if (diffDays > 1L) {
            finalStreak = 1 // Reset streak if user missed a day
        }

        saveUserSettings(settings.copy(
            currentStreak = finalStreak,
            lastActiveTimestamp = now
        ))
    }
}
