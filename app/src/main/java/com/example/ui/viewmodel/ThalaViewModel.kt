package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.DatabaseProvider
import com.example.data.database.HistoryItem
import com.example.data.database.UserSettings
import com.example.data.database.Achievement
import com.example.data.engine.MathEvaluator
import com.example.data.engine.ThalaReasonEngine
import com.example.data.engine.ThalaReasonResult
import com.example.data.repository.ThalaRepository
import com.example.ui.theme.ThalaTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThalaViewModel(application: Application) : AndroidViewModel(application) {

    private val database = DatabaseProvider.getDatabase(application)
    private val repository = ThalaRepository(database.thalaDao())

    // UI States
    val history: StateFlow<List<HistoryItem>> = repository.history
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val achievements: StateFlow<List<Achievement>> = repository.achievements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userSettings: StateFlow<UserSettings?> = repository.userSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Active Calculator UI State
    private val _calculatorInput = MutableStateFlow("")
    val calculatorInput: StateFlow<String> = _calculatorInput.asStateFlow()

    private val _calculatorResult = MutableStateFlow("0")
    val calculatorResult: StateFlow<String> = _calculatorResult.asStateFlow()

    // Cinematic Overlay Activation State
    private val _isCinematicActive = MutableStateFlow(false)
    val isCinematicActive: StateFlow<Boolean> = _isCinematicActive.asStateFlow()

    private val _activeReason = MutableStateFlow<ThalaReasonResult?>(null)
    val activeReason: StateFlow<ThalaReasonResult?> = _activeReason.asStateFlow()

    // Text detector State
    val textDetectionInput = mutableStateOf("")

    private val _isAILoading = MutableStateFlow(false)
    val isAILoading: StateFlow<Boolean> = _isAILoading.asStateFlow()

    // Key presses count since launch for achievement tracking
    private var totalTapCount = 0

    init {
        viewModelScope.launch {
            repository.initializeApplication()
        }
    }

    fun onDigitPressed(digit: String) {
        triggerHaptic()
        totalTapCount++
        checkTapAchievements()
        val current = _calculatorInput.value
        // Limit display
        if (current.length < 24) {
            _calculatorInput.value = current + digit
        }
    }

    fun onOperatorPressed(operator: String) {
        triggerHaptic()
        totalTapCount++
        checkTapAchievements()
        val current = _calculatorInput.value
        if (current.isNotEmpty() && !isOperator(current.last())) {
            _calculatorInput.value = current + operator
        } else if (current.isNotEmpty() && isOperator(current.last())) {
            _calculatorInput.value = current.dropLast(1) + operator
        }
    }

    fun onClear() {
        triggerHaptic()
        _calculatorInput.value = ""
        _calculatorResult.value = "0"
    }

    fun onDelete() {
        triggerHaptic()
        val current = _calculatorInput.value
        if (current.isNotEmpty()) {
            _calculatorInput.value = current.dropLast(1)
        }
    }

    fun onEvaluate() {
        triggerHaptic()
        val expression = _calculatorInput.value
        if (expression.isEmpty()) return

        try {
            val eval = MathEvaluator.evaluate(expression)
            val resultStr = if (eval % 1 == 0.0) eval.toInt().toString() else "%.4f".format(eval)
            _calculatorResult.value = resultStr

            // Save settings max value statistics
            viewModelScope.launch {
                val current = userSettings.value ?: UserSettings()
                if (eval > current.maxNumberReached) {
                    repository.saveUserSettings(current.copy(maxNumberReached = eval))
                }
            }

            // AI Thala Trigger Check
            val detectionResult = ThalaReasonEngine.getLocalReason(resultStr)
            if (detectionResult.isThala) {
                // Triggers full screen cinematic celebration!
                triggerEpicVibration()
                _activeReason.value = detectionResult
                _isCinematicActive.value = true

                viewModelScope.launch {
                    repository.insertHistoryItem(
                        expression = expression,
                        result = resultStr,
                        explanation = detectionResult.explanation,
                        isThala = true
                    )
                    // Unlock calculation achievements
                    if (resultStr == "7") {
                        repository.unlockAchievement("seven_jersey")
                    }
                    if (eval >= 77.0) {
                        repository.unlockAchievement("helicopter_shot")
                    }
                }
            } else {
                // Normal insert info
                viewModelScope.launch {
                    repository.insertHistoryItem(
                        expression = expression,
                        result = resultStr,
                        explanation = "Result: $resultStr (No direct Thala matches, but Thala is always watching).",
                        isThala = false
                    )
                }
            }

        } catch (e: Exception) {
            _calculatorResult.value = "Error"
        }
    }

    /**
     * Conducts deep analysis on custom user typed text.
     */
    fun onAnalyzeTextDetection(useGemini: Boolean = false) {
        val text = textDetectionInput.value.trim()
        if (text.isEmpty()) return

        _isAILoading.value = true
        triggerHaptic()

        viewModelScope.launch {
            if (useGemini) {
                try {
                    val geminiReason = ThalaReasonEngine.getGeminiReason(text)
                    val result = ThalaReasonResult(
                        isThala = true,
                        originalInput = text,
                        expression = "${text.length} Letters Context",
                        explanation = geminiReason,
                        trackingId = "bole_jo_koyal"
                    )
                    triggerEpicVibration()
                    _activeReason.value = result
                    _isCinematicActive.value = true

                    repository.insertHistoryItem(
                        expression = "AI Reason: Input '$text'",
                        result = "7",
                        explanation = geminiReason,
                        isThala = true
                    )
                    repository.unlockAchievement("bole_jo_koyal")

                } catch (e: Exception) {
                    // Fallback to local
                    performLocalTextAnalysis(text)
                } finally {
                    _isAILoading.value = false
                }
            } else {
                performLocalTextAnalysis(text)
                _isAILoading.value = false
            }
        }
    }

    private suspend fun performLocalTextAnalysis(text: String) {
        val result = ThalaReasonEngine.getLocalReason(text)
        if (result.isThala) {
            triggerEpicVibration()
            _activeReason.value = result
            _isCinematicActive.value = true

            repository.insertHistoryItem(
                expression = "Count: '$text'",
                result = "7",
                explanation = result.explanation,
                isThala = true
            )
            repository.unlockAchievement("bole_jo_koyal")
        }
    }

    fun dismissCinematic() {
        triggerHaptic()
        _isCinematicActive.value = false
    }

    // Settings adjustments
    fun updateTheme(theme: ThalaTheme) {
        viewModelScope.launch {
            val current = userSettings.value ?: UserSettings()
            repository.saveUserSettings(current.copy(currentThemeStr = theme.name))
        }
    }

    fun setHapticIntensity(intensity: Float) {
        viewModelScope.launch {
            val current = userSettings.value ?: UserSettings()
            repository.saveUserSettings(current.copy(hapticIntensity = intensity))
        }
    }

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch {
            val current = userSettings.value ?: UserSettings()
            repository.saveUserSettings(current.copy(soundEnabled = enabled))
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    private fun isOperator(c: Char): Boolean {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == 'x' || c == '÷'
    }

    private fun checkTapAchievements() {
        if (totalTapCount >= 70) {
            viewModelScope.launch {
                // If they surpassed 500 XP points, we unlock Captain Cool CSK color theme!
                val current = userSettings.value ?: UserSettings()
                if (current.totalXp >= 500) {
                    repository.unlockAchievement("captain_cool")
                }
            }
        }
    }

    // Vibrator tactile methods
    private fun triggerHaptic() {
        val intensity = userSettings.value?.hapticIntensity ?: 1.0f
        if (intensity <= 0f) return

        try {
            val vibrator = getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(35, (60 * intensity).toInt().coerceIn(1, 255)))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(35)
                }
            }
        } catch (_: Exception) {}
    }

    private fun triggerEpicVibration() {
        val intensity = userSettings.value?.hapticIntensity ?: 1.0f
        if (intensity <= 0f) return

        try {
            val vibrator = getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Pattern for epic victory vibration!
                    vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 150, 100, 150, 80, 250), -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(500)
                }
            }
        } catch (_: Exception) {}
    }
}
