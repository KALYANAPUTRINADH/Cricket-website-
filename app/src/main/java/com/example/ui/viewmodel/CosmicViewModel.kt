package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.api.Content
import com.example.data.api.GeminiRequest
import com.example.data.api.Part
import com.example.data.api.RetrofitClient
import com.example.data.model.JournalEntry
import com.example.data.repository.JournalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CosmicViewModel(private val repository: JournalRepository) : ViewModel() {

    // 1. Reactive stream of local journal entries
    val journalEntries: StateFlow<List<JournalEntry>> = repository.allEntries
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 2. State for AI interaction
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _apiError = MutableStateFlow<String?>(null)
    val apiError: StateFlow<String?> = _apiError.asStateFlow()

    private val _geminiOracleResponse = MutableStateFlow<String?>(null)
    val geminiOracleResponse: StateFlow<String?> = _geminiOracleResponse.asStateFlow()

    private val _analysisResult = MutableStateFlow<String?>(null)
    val analysisResult: StateFlow<String?> = _analysisResult.asStateFlow()

    // 3. Dynamic Box Breathing state (Box Breathing is 4s Inhale, 4s Hold, 4s Exhale, 4s Hold)
    private val _breathingPhase = MutableStateFlow("Tap to Begin")
    val breathingPhase: StateFlow<String> = _breathingPhase.asStateFlow()

    private val _breathingCountdown = MutableStateFlow(4)
    val breathingCountdown: StateFlow<Int> = _breathingCountdown.asStateFlow()

    private var isBreathingRunning = false

    init {
        // Start atmospheric background breathing cycle
        startBreathingCycle()
    }

    // --- Breathing Routine Logic ---
    private fun startBreathingCycle() {
        if (isBreathingRunning) return
        isBreathingRunning = true
        viewModelScope.launch {
            while (isBreathingRunning) {
                _breathingPhase.value = "Inhale slowly"
                for (i in 4 downTo 1) {
                    _breathingCountdown.value = i
                    delay(1000)
                }
                _breathingPhase.value = "Hold your breath"
                for (i in 4 downTo 1) {
                    _breathingCountdown.value = i
                    delay(1000)
                }
                _breathingPhase.value = "Exhale peacefully"
                for (i in 4 downTo 1) {
                    _breathingCountdown.value = i
                    delay(1000)
                }
                _breathingPhase.value = "Hold with empty lungs"
                for (i in 4 downTo 1) {
                    _breathingCountdown.value = i
                    delay(1000)
                }
            }
        }
    }

    // --- Local DB CRUD Operations ---
    fun addJournalEntry(title: String, content: String, moodScore: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val entry = JournalEntry(
                title = title,
                content = content,
                moodScore = moodScore,
                timestamp = System.currentTimeMillis()
            )
            repository.insert(entry)
        }
    }

    fun deleteEntry(entry: JournalEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(entry)
        }
    }

    // --- Direct Gemini API Integrations ---

    /**
     * Decode Emotional symbols, sentiment, and psychological themes in a journal entry with AI and persistent update.
     */
    fun decodeEntryWithAI(entry: JournalEntry) {
        val apiKey = getVerifiedApiKey() ?: return
        _isLoading.value = true
        _apiError.value = null

        viewModelScope.launch {
            try {
                val prompt = """
                    You are an expert psychoanalyst, dream interpreter, and mental guide. 
                    Analyze the following journal or dream log:
                    Title: "${entry.title}"
                    Mood Score: ${entry.moodScore}/5
                    Content: "${entry.content}"
                    
                    Provide an elegant, compassionate summary breaking down:
                    1. Emotional Sentiment & Key Core Themes.
                    2. Unconscious Symbols or Dream Archetype meanings if present.
                    3. A gentle, cosmic mindful prescription (1 step you suggest).
                    Keep the response concise, beautifully structured, and highly poetic yet practical.
                """.trimIndent()

                val systemInstruction = "Analyze mental content and symbols with profound poetic insight."

                val request = GeminiRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                    systemInstruction = Content(parts = listOf(Part(text = systemInstruction)))
                )

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.service.generateContent(apiKey, request)
                }

                val resultText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (resultText != null) {
                    val updatedEntry = entry.copy(analysis = resultText)
                    withContext(Dispatchers.IO) {
                        repository.insert(updatedEntry)
                    }
                    _analysisResult.value = resultText
                } else {
                    _apiError.value = "Received empty response from Gemini oracle."
                }
            } catch (e: Exception) {
                _apiError.value = "Failed to reach Cosmic Mirror: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Ask Cosmic Oracle (Conversational AI Assistant)
     */
    fun askCosmicOracle(prompt: String) {
        val apiKey = getVerifiedApiKey() ?: return
        _isLoading.value = true
        _apiError.value = null

        viewModelScope.launch {
            try {
                val request = GeminiRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                    systemInstruction = Content(parts = listOf(Part(text = "You are a wise cosmic Zen master. Deep, brief, compassionate answers with beautiful space metaphors.")))
                )

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.service.generateContent(apiKey, request)
                }

                val reply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                _geminiOracleResponse.value = reply ?: "The stars remain silent today."
            } catch (e: Exception) {
                _apiError.value = "Failed to contact Oracle: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearOracleResponse() {
        _geminiOracleResponse.value = null
    }

    /**
     * Validates if the user entered their real api key in the AI studio secrets manager.
     */
    private fun getVerifiedApiKey(): String? {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            _apiError.value = "Gemini API key is unconfigured. Please add 'GEMINI_API_KEY' inside the Secrets panel of Google AI Studio to ignite cosmic intelligence."
            return null
        }
        return apiKey
    }
}

class CosmicViewModelFactory(private val repository: JournalRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CosmicViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CosmicViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
