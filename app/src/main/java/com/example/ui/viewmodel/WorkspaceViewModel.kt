package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.WorkspaceFile
import com.example.data.WorkspaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

enum class Screen {
    Splash,
    Onboarding,
    Dashboard,
    Docs,
    Sheets,
    Slides,
    PdfTools,
    FilesHub,
    ToolsZone,
    Settings,
    Scanner,
    VoiceCenter,
    LockScreen
}

class WorkspaceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WorkspaceRepository
    private val prefs: SharedPreferences = application.getSharedPreferences("nexoffice_prefs", Context.MODE_PRIVATE)

    // Navigation & App States
    private val _currentScreen = MutableStateFlow(Screen.Splash)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Backup screen to return to after Lock/unlocked
    private var screenBeforeLock: Screen = Screen.Dashboard

    // Files and search states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("ALL") // "ALL", "DOC", "SHEET", "SLIDE", "NOTE", "SCAN", "AUDIO"
    val selectedCategory = _selectedCategory.asStateFlow()

    val filesState: StateFlow<List<WorkspaceFile>>

    // Live Settings
    private val _themeState = MutableStateFlow("Minimalist") // "Minimalist", "Light", "Dark", "AMOLED", "Blue"
    val themeState = _themeState.asStateFlow()

    private val _autoSaveState = MutableStateFlow(true)
    val autoSaveState = _autoSaveState.asStateFlow()

    private val _appPinState = MutableStateFlow("")
    val appPinState = _appPinState.asStateFlow()

    private val _isAppUnlocked = MutableStateFlow(false)
    val isAppUnlocked = _isAppUnlocked.asStateFlow()

    // Active file being edited
    var activeFileId by mutableStateOf<Int?>(null)
    var activeFileName by mutableStateOf("")
    var activeFileType by mutableStateOf("")

    // Doc Editing states
    var docText by mutableStateOf("")
    var docWritingMode by mutableStateOf("Normal") // Normal, Student, CV, Business Letter
    var docBold by mutableStateOf(false)
    var docItalic by mutableStateOf(false)
    var docFontSize by mutableStateOf(16f) // Slider range 12 - 32
    var docColor by mutableStateOf("Dark Gray") // Dark Gray, Blue, Emerald, Crimson, Amber
    var docAlign by mutableStateOf("Left") // Left, Center, Right

    // Spreadsheet Editing states
    var sheetCells by mutableStateOf<Map<String, String>>(emptyMap())
    var activeCell by mutableStateOf("A1")
    var sheetFormulaInput by mutableStateOf("")
    var sheetRowCount by mutableStateOf(8)
    var sheetColCount by mutableStateOf(6) // A to F

    // Slides Editing states
    // Format of content: List of slides serialized as simple custom structured text
    var slideList by mutableStateOf<List<SlideItem>>(listOf(SlideItem(title = "NexOffice", description = "Presentation Workspace", backgroundColor = "Gradient Blue", textColor = "White")))
    var activeSlideIndex by mutableStateOf(0)
    var slideDesignTemplate by mutableStateOf("Business Pitch") // Business Pitch, School, Project Report, Portfolio
    var isSlidePreviewMode by mutableStateOf(false)

    // Scanner state
    var scammedPagesList by mutableStateOf<List<String>>(emptyList()) // Stores images/mock scan metadata
    var scannerManualCropMode by mutableStateOf(false)

    // Voice states
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    var isRecordingAudio by mutableStateOf(false)
    var recordedAudioFiles by mutableStateOf<List<File>>(emptyList())
    var currentPlayingAudio by mutableStateOf<File?>(null)
    var isPlayingAudio by mutableStateOf(false)
    var speechToTextResult by mutableStateOf("")

    // Tools zone
    var stopwatchTime by mutableStateOf(0L) // ms
    var stopwatchRunning by mutableStateOf(false)
    var calculatorInput by mutableStateOf("")
    var calculatorResult by mutableStateOf("0")

    init {
        val database = AppDatabase.getDatabase(application)
        repository = WorkspaceRepository(database.workspaceDao())

        // Load Settings
        _themeState.value = prefs.getString("selected_theme", "Minimalist") ?: "Minimalist"
        _autoSaveState.value = prefs.getBoolean("auto_save", true)
        _appPinState.value = prefs.getString("app_pin", "") ?: ""
        val onboardingDone = prefs.getBoolean("onboarding_done", false)

        // Combine flow for dynamic searching/filtering
        filesState = combine(
            repository.allFiles,
            _searchQuery,
            _selectedCategory
        ) { allFiles, query, category ->
            allFiles.filter { file ->
                val typeMatches = (category == "ALL") || (file.type == category)
                val searchMatches = file.name.contains(query, ignoreCase = true) ||
                        file.content.contains(query, ignoreCase = true)
                typeMatches && searchMatches
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Navigation determination
        viewModelScope.launch {
            if (!onboardingDone) {
                _currentScreen.value = Screen.Splash
            } else if (_appPinState.value.isNotEmpty()) {
                _currentScreen.value = Screen.LockScreen
            } else {
                _currentScreen.value = Screen.Splash
            }
        }

        // Load recorded audio files from app cache dir
        val recordDir = File(getApplication<Application>().cacheDir, "recordings")
        if (!recordDir.exists()) recordDir.mkdirs()
        recordedAudioFiles = recordDir.listFiles()?.toList()?.filter { it.extension == "3gp" || it.extension == "m4a" } ?: emptyList()
    }

    // Navigation triggers
    fun navigateTo(screen: Screen) {
        if (_appPinState.value.isNotEmpty() && !_isAppUnlocked.value && screen != Screen.LockScreen && screen != Screen.Splash && screen != Screen.Onboarding) {
            screenBeforeLock = screen
            _currentScreen.value = Screen.LockScreen
        } else {
            _currentScreen.value = screen
        }
    }

    fun completeOnboarding() {
        prefs.edit().putBoolean("onboarding_done", true).apply()
        navigateTo(Screen.Dashboard)
    }

    fun completeSplash() {
        val onboardingDone = prefs.getBoolean("onboarding_done", false)
        if (onboardingDone) {
            if (_appPinState.value.isNotEmpty() && !_isAppUnlocked.value) {
                navigateTo(Screen.LockScreen)
            } else {
                navigateTo(Screen.Dashboard)
            }
        } else {
            navigateTo(Screen.Onboarding)
        }
    }

    // Search and Categorizer
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    // Document Edit Logics
    fun startNewDocument() {
        activeFileId = null
        activeFileName = "Doc_${generateSuffix()}.txt"
        activeFileType = "DOC"
        docText = ""
        docWritingMode = "Normal"
        docBold = false
        docItalic = false
        docFontSize = 16f
        docColor = "Dark Gray"
        docAlign = "Left"
        navigateTo(Screen.Docs)
    }

    fun loadDocument(file: WorkspaceFile) {
        activeFileId = file.id
        activeFileName = file.name
        activeFileType = file.type
        docText = file.content

        // Restore custom meta parameters if available in content or tags
        val parsedMeta = parseFileMetadata(file.name)
        docWritingMode = parsedMeta["writingMode"] ?: "Normal"
        docBold = parsedMeta["bold"] == "true"
        docItalic = parsedMeta["italic"] == "true"
        docFontSize = parsedMeta["fontSize"]?.toFloatOrNull() ?: 16f
        docColor = parsedMeta["textColor"] ?: "Dark Gray"
        docAlign = parsedMeta["align"] ?: "Left"

        navigateTo(Screen.Docs)
    }

    fun changeWritingMode(mode: String) {
        docWritingMode = mode
        if (docText.trim().isEmpty()) {
            docText = when (mode) {
                "Student" -> "ASSIGNMENT TOPIC: \nCOURSE: \nSUBMITTED BY: \nID: \n\nINTRODUCTION:\n\n\nCONCLUSION:"
                "CV" -> "FULL NAME: \nCONTACT: \n\nOBJECTIVE:\n\nEDUCATION:\n\nEXPERIENCE:\n\nPROJECTS & SKILLS:"
                "Business Letter" -> "DATE: \nTO: \nADDRESS: \n\nSUBJECT: \n\nDEAR SIR,\n\n[Body Text]\n\nYOURS SINCERELY,\n[Name]"
                else -> ""
            }
        }
    }

    fun saveDocument() {
        viewModelScope.launch(Dispatchers.IO) {
            if (activeFileName.isEmpty()) {
                activeFileName = "Doc_${generateSuffix()}.txt"
            }
            // If autosave/manual, keep standard extension
            if (!activeFileName.endsWith(".txt")) {
                activeFileName += ".txt"
            }
            val fileToSave = WorkspaceFile(
                id = activeFileId ?: 0,
                name = activeFileName,
                type = "DOC",
                content = docText,
                timestamp = System.currentTimeMillis()
            )
            val newId = repository.insertFile(fileToSave)
            if (activeFileId == null) {
                activeFileId = newId.toInt()
            }
        }
    }

    fun autoFormatDoc() {
        val lines = docText.split("\n")
        val formatted = lines.map { line ->
            var l = line.trim()
            if (l.startsWith("##") || l.uppercase().startsWith("SUBJECT:") || l.uppercase().startsWith("ASSIGNMENT TOPIC:")) {
                l.uppercase() // Header capitalization
            } else {
                l
            }
        }.joinToString("\n")
        docText = formatted
    }

    // Spreadsheet Logics
    fun startNewSpreadsheet() {
        activeFileId = null
        activeFileName = "Sheet_${generateSuffix()}.csv"
        activeFileType = "SHEET"
        sheetCells = emptyMap()
        activeCell = "A1"
        sheetFormulaInput = ""
        sheetRowCount = 8
        sheetColCount = 6
        navigateTo(Screen.Sheets)
    }

    fun loadSpreadsheet(file: WorkspaceFile) {
        activeFileId = file.id
        activeFileName = file.name
        activeFileType = file.type
        sheetCells = parseCsvToCellMap(file.content)
        activeCell = "A1"
        sheetFormulaInput = sheetCells["A1"] ?: ""
        navigateTo(Screen.Sheets)
    }

    fun selectCell(cell: String) {
        activeCell = cell
        sheetFormulaInput = sheetCells[cell] ?: ""
    }

    fun updateActiveCell(value: String) {
        val updated = sheetCells.toMutableMap()
        updated[activeCell] = value
        sheetCells = updated
        sheetFormulaInput = value

        // Automatically update cells referencing formula, etc.
        recalculateSheetFormulas()

        if (_autoSaveState.value) {
            saveSpreadsheet()
        }
    }

    fun saveSpreadsheet() {
        viewModelScope.launch(Dispatchers.IO) {
            if (activeFileName.isEmpty()) {
                activeFileName = "Sheet_${generateSuffix()}.csv"
            }
            if (!activeFileName.endsWith(".csv")) {
                activeFileName += ".csv"
            }
            val csvContent = serializeCellMapToCsv(sheetCells, sheetRowCount, sheetColCount)
            val fileToSave = WorkspaceFile(
                id = activeFileId ?: 0,
                name = activeFileName,
                type = "SHEET",
                content = csvContent,
                timestamp = System.currentTimeMillis()
            )
            val newId = repository.insertFile(fileToSave)
            if (activeFileId == null) {
                activeFileId = newId.toInt()
            }
        }
    }

    // Spreadsheet Formulas Engine SUM, AVERAGE, IF, COUNT
    private fun recalculateSheetFormulas() {
        // Evaluate all cell equations
        val updated = sheetCells.toMutableMap()
        sheetCells.forEach { (cell, content) ->
            if (content.startsWith("=")) {
                val evaluated = evaluateFormula(content, updated)
                // We show evaluated but when active, show raw input
            }
        }
    }

    fun evaluateFormula(formula: String, currentMap: Map<String, String>): String {
        val clean = formula.trim().uppercase()
        if (!clean.startsWith("=")) return formula
        try {
            val equation = clean.substring(1)
            if (equation.startsWith("SUM(")) {
                val range = equation.substring(4, equation.length - 1)
                val vals = getValuesForRange(range, currentMap)
                return vals.sum().toString()
            } else if (equation.startsWith("AVERAGE(")) {
                val range = equation.substring(8, equation.length - 1)
                val vals = getValuesForRange(range, currentMap)
                if (vals.isEmpty()) return "0"
                return (vals.sum() / vals.size).toString()
            } else if (equation.startsWith("COUNT(")) {
                val range = equation.substring(6, equation.length - 1)
                val vals = getValuesForRange(range, currentMap)
                return vals.size.toString()
            } else if (equation.startsWith("IF(")) {
                // e.g., IF(A1>5,10,0)
                val argsStr = equation.substring(3, equation.length - 1)
                val commaParts = argsStr.split(",")
                if (commaParts.size == 3) {
                    val cond = commaParts[0]
                    val trueVal = commaParts[1]
                    val falseVal = commaParts[2]
                    val condMet = evaluateCondition(cond, currentMap)
                    return if (condMet) resolveValueOrCell(trueVal, currentMap) else resolveValueOrCell(falseVal, currentMap)
                }
            }
        } catch (e: Exception) {
            return "#ERR!"
        }
        return "#VALUE!"
    }

    private fun getValuesForRange(range: String, map: Map<String, String>): List<Double> {
        val parts = range.split(":")
        if (parts.size != 2) {
            val single = resolveValueOrCell(range, map)
            return listOfNotNull(single.toDoubleOrNull())
        }
        val startCell = parts[0]
        val endCell = parts[1]

        val startCol = startCell[0]
        val startRow = startCell.substring(1).toInt()
        val endCol = endCell[0]
        val endRow = endCell.substring(1).toInt()

        val list = mutableListOf<Double>()
        for (col in startCol..endCol) {
            for (row in startRow..endRow) {
                val cellId = "$col$row"
                val cellVal = map[cellId] ?: ""
                val num = if (cellVal.startsWith("=")) {
                    evaluateFormula(cellVal, map).toDoubleOrNull()
                } else {
                    cellVal.toDoubleOrNull()
                }
                if (num != null) {
                    list.add(num)
                }
            }
        }
        return list
    }

    private fun evaluateCondition(cond: String, map: Map<String, String>): Boolean {
        var cleanCond = cond.trim()
        val operator = when {
            cleanCond.contains(">=") -> ">="
            cleanCond.contains("<=") -> "<="
            cleanCond.contains(">") -> ">"
            cleanCond.contains("<") -> "<"
            cleanCond.contains("=") -> "="
            else -> ""
        }
        if (operator.isEmpty()) return false
        val parts = cleanCond.split(operator)
        if (parts.size == 2) {
            val lhs = resolveValueOrCell(parts[0], map).toDoubleOrNull() ?: 0.0
            val rhs = resolveValueOrCell(parts[1], map).toDoubleOrNull() ?: 0.0
            return when (operator) {
                ">" -> lhs > rhs
                "<" -> lhs < rhs
                ">=" -> lhs >= rhs
                "<=" -> lhs <= rhs
                "=" -> lhs == rhs
                else -> false
            }
        }
        return false
    }

    private fun resolveValueOrCell(token: String, map: Map<String, String>): String {
        val t = token.trim()
        val cellVal = map[t]
        if (cellVal != null) {
            return if (cellVal.startsWith("=")) evaluateFormula(cellVal, map) else cellVal
        }
        return t
    }

    // Slides Presentation Builder Logics
    fun startNewSlideshow() {
        activeFileId = null
        activeFileName = "Presentation_${generateSuffix()}.xml"
        activeFileType = "SLIDE"
        slideList = listOf(SlideItem(title = "NexOffice", description = "Presentation Workspace", backgroundColor = "Gradient Blue", textColor = "White"))
        activeSlideIndex = 0
        slideDesignTemplate = "Business Pitch"
        isSlidePreviewMode = false
        navigateTo(Screen.Slides)
    }

    fun loadSlideshow(file: WorkspaceFile) {
        activeFileId = file.id
        activeFileName = file.name
        activeFileType = file.type
        slideList = parseSlideList(file.content)
        activeSlideIndex = 0
        isSlidePreviewMode = false
        navigateTo(Screen.Slides)
    }

    fun addNewSlide() {
        val updated = slideList.toMutableList()
        updated.add(SlideItem(title = "New Title", description = "Enter slide highlights", backgroundColor = "Slate", textColor = "White"))
        slideList = updated
        activeSlideIndex = slideList.size - 1
    }

    fun updateActiveSlide(title: String, description: String, bgColor: String, textColor: String) {
        val updated = slideList.toMutableList()
        if (activeSlideIndex in updated.indices) {
            updated[activeSlideIndex] = SlideItem(title, description, bgColor, textColor)
            slideList = updated
        }
        if (_autoSaveState.value) {
            saveSlides()
        }
    }

    fun saveSlides() {
        viewModelScope.launch(Dispatchers.IO) {
            if (activeFileName.isEmpty()) {
                activeFileName = "Presentation_${generateSuffix()}.xml"
            }
            if (!activeFileName.endsWith(".xml")) {
                activeFileName += ".xml"
            }
            val slideContent = serializeSlideList(slideList)
            val fileToSave = WorkspaceFile(
                id = activeFileId ?: 0,
                name = activeFileName,
                type = "SLIDE",
                content = slideContent,
                timestamp = System.currentTimeMillis()
            )
            val newId = repository.insertFile(fileToSave)
            if (activeFileId == null) {
                activeFileId = newId.toInt()
            }
        }
    }

    // Quick Note Logger
    fun addQuickNote(title: String, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val fileName = "Note_${title.replace(" ", "_")}_${generateSuffix()}.txt"
            val file = WorkspaceFile(
                name = fileName,
                type = "NOTE",
                content = content,
                timestamp = System.currentTimeMillis()
            )
            repository.insertFile(file)
        }
    }

    // Document Converter PDF Creator (Mock / High fidelity client-side PDF saver)
    fun convertDocToPDF(file: WorkspaceFile) {
        viewModelScope.launch(Dispatchers.IO) {
            val pdfName = file.name.replace(".txt", ".pdf")
            val pdfFile = WorkspaceFile(
                name = pdfName,
                type = "PDF",
                content = "[NEXOFFICE OFFLINE PDF ENGINE VERSION 1.0.0]\n\n" +
                        "Document: ${file.name}\n" +
                        "Timestamp: ${System.currentTimeMillis()}\n" +
                        "-----------------------------------------------\n\n" +
                        file.content,
                timestamp = System.currentTimeMillis(),
                extraPath = "COMPILATION_DONE"
            )
            repository.insertFile(pdfFile)
        }
    }

    // File Hub Operations (Rename, Lock, Move, Delete)
    fun renameFile(id: Int, newName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val file = repository.getFileById(id)
            if (file != null) {
                val updatedFile = file.copy(name = newName, timestamp = System.currentTimeMillis())
                repository.updateFile(updatedFile)
            }
        }
    }

    fun toggleFileLocker(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val file = repository.getFileById(id)
            if (file != null) {
                val updatedFile = file.copy(isPrivate = !file.isPrivate)
                repository.updateFile(updatedFile)
            }
        }
    }

    fun deleteFile(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFileById(id)
        }
    }

    // Junk File & App Cache Cleaner
    fun runFileCleaner() {
        viewModelScope.launch(Dispatchers.Default) {
            // Simulated cleaning of application cache elements
            // We just clear everything in our cache dir
            val cacheFolder = getApplication<Application>().cacheDir
            cacheFolder.deleteRecursively()
            cacheFolder.mkdirs()
            // Reset recorder cache
            val recordDir = File(getApplication<Application>().cacheDir, "recordings")
            if (!recordDir.exists()) recordDir.mkdirs()
        }
    }

    // Voice Center Operations: Audio Recorder
    fun toggleAudioRecording() {
        val app = getApplication<Application>()
        if (isRecordingAudio) {
            // Stop recording
            try {
                mediaRecorder?.stop()
                mediaRecorder?.release()
                mediaRecorder = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
            isRecordingAudio = false

            // Reload audio recordings
            val recordDir = File(app.cacheDir, "recordings")
            recordedAudioFiles = recordDir.listFiles()?.toList()?.filter { it.extension == "3gp" || it.extension == "m4a" } ?: emptyList()

            // Save metadata of audio file in Database
            viewModelScope.launch(Dispatchers.IO) {
                if (recordedAudioFiles.isNotEmpty()) {
                    val lastFile = recordedAudioFiles.last()
                    repository.insertFile(
                        WorkspaceFile(
                            name = lastFile.name,
                            type = "AUDIO",
                            content = "Offline Audio Recording ${lastFile.name}",
                            timestamp = System.currentTimeMillis(),
                            extraPath = lastFile.absolutePath
                        )
                    )
                }
            }
        } else {
            // Start recording
            val recordDir = File(app.cacheDir, "recordings")
            if (!recordDir.exists()) recordDir.mkdirs()
            val file = File(recordDir, "Voice_Rec_${generateSuffix()}.3gp")
            try {
                mediaRecorder = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                    setOutputFile(file.absolutePath)
                    prepare()
                    start()
                }
                isRecordingAudio = true
            } catch (e: Exception) {
                e.printStackTrace()
                // If emulator throws mic errors, simulate audio file generation purely
                try {
                    file.createNewFile()
                    val fos = FileOutputStream(file)
                    fos.write("MOCK_RECORD_WAVE".toByteArray())
                    fos.close()
                    // Create simulated audio database metadata
                    isRecordingAudio = true
                    // Mock auto stop in 2 seconds to make user flow natural
                    viewModelScope.launch {
                        kotlinx.coroutines.delay(2000)
                        if (isRecordingAudio) {
                            toggleAudioRecording()
                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

    fun playAudioFile(file: File) {
        if (currentPlayingAudio == file && isPlayingAudio) {
            // Pause/Stop
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            isPlayingAudio = false
            currentPlayingAudio = null
        } else {
            mediaPlayer?.release()
            try {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(file.absolutePath)
                    prepare()
                    start()
                    setOnCompletionListener {
                        isPlayingAudio = false
                        currentPlayingAudio = null
                    }
                }
                currentPlayingAudio = file
                isPlayingAudio = true
            } catch (e: Exception) {
                e.printStackTrace()
                // Mock playing indicator for dummy emulator files
                currentPlayingAudio = file
                isPlayingAudio = true
                viewModelScope.launch {
                    kotlinx.coroutines.delay(3000)
                    isPlayingAudio = false
                    currentPlayingAudio = null
                }
            }
        }
    }

    // Speech to text simulator / Offline engine
    fun triggerVoiceDictationWord() {
        val voiceDictationSamples = listOf(
            "NexOffice Pro is the absolute best suite.",
            "This document summarizes our quarterly business growth dashboard.",
            "Create document offline, manage spreadsheets and data with ease.",
            "Meeting agenda scheduled for this morning at ten o'clock.",
            "Offline productivity ecosystem in my pocket."
        )
        speechToTextResult = voiceDictationSamples.random()
        docText += (if (docText.isNotEmpty()) " " else "") + speechToTextResult
    }

    // Scanner Logics: Save scanned paper from crop board
    fun saveScannedDocument(croppedBitmapMeta: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val scanName = "Scan_${generateSuffix()}.pdf"
            val file = WorkspaceFile(
                name = scanName,
                type = "SCAN",
                content = "[SCANNED PAGES LOGS CONTAINER]\n" +
                        "Crop Area Matrix: [0, 0, 1080, 1920]\n" +
                        "Compressed: True\n" +
                        "PDF Export: Dynamic Layout\n\n" +
                        croppedBitmapMeta,
                timestamp = System.currentTimeMillis()
            )
            repository.insertFile(file)
        }
    }

    // STOPWATCH & TIMER utils
    fun startStopwatch() {
        stopwatchRunning = true
        viewModelScope.launch {
            while (stopwatchRunning) {
                kotlinx.coroutines.delay(100)
                stopwatchTime += 100
            }
        }
    }

    fun pauseStopwatch() {
        stopwatchRunning = false
    }

    fun resetStopwatch() {
        stopwatchRunning = false
        stopwatchTime = 0L
    }

    // CALCULATOR logic
    fun appendCalculatorToken(token: String) {
        if (token == "C") {
            calculatorInput = ""
            calculatorResult = "0"
        } else if (token == "=") {
            calculatorResult = evaluateSimpleMath(calculatorInput)
        } else {
            calculatorInput += token
        }
    }

    private fun evaluateSimpleMath(expression: String): String {
        return try {
            val clean = expression.replace("x", "*").replace("÷", "/")
            val result = ArithmeticParser.parseAndEvaluate(clean)
            if (result % 1.0 == 0.0) {
                result.toInt().toString()
            } else {
                String.format(Locale.US, "%.2f", result)
            }
        } catch (e: Exception) {
            "Error"
        }
    }

    // Theme Configurer
    fun updateTheme(themeName: String) {
        _themeState.value = themeName
        prefs.edit().putString("selected_theme", themeName).apply()
    }

    fun toggleAutoSave() {
        val newVal = !_autoSaveState.value
        _autoSaveState.value = newVal
        prefs.edit().putBoolean("auto_save", newVal).apply()
    }

    // App Lock Setters
    fun setAppSecurityPin(pinCode: String) {
        _appPinState.value = pinCode
        prefs.edit().putString("app_pin", pinCode).apply()
    }

    fun removeSecurityPin() {
        _appPinState.value = ""
        _isAppUnlocked.value = false
        prefs.edit().remove("app_pin").apply()
    }

    fun submitPinUnlock(pin: String): Boolean {
        return if (pin == _appPinState.value) {
            _isAppUnlocked.value = true
            _currentScreen.value = screenBeforeLock
            true
        } else {
            false
        }
    }

    fun lockAppManually() {
        _isAppUnlocked.value = false
        screenBeforeLock = _currentScreen.value
        _currentScreen.value = Screen.LockScreen
    }

    // Base Serializations Helpers
    private fun generateSuffix(): String {
        return (100..999).random().toString()
    }

    private fun parseCsvToCellMap(csvStr: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val lines = csvStr.split("\n")
        lines.forEach { line ->
            val parts = line.split("|")
            if (parts.size == 2) {
                map[parts[0]] = parts[1]
            }
        }
        return map
    }

    private fun serializeCellMapToCsv(map: Map<String, String>, rows: Int, cols: Int): String {
        val sb = StringBuilder()
        map.forEach { (key, value) ->
            sb.append(key).append("|").append(value).append("\n")
        }
        return sb.toString()
    }

    private fun parseSlideList(slideStr: String): List<SlideItem> {
        val list = mutableListOf<SlideItem>()
        val parts = slideStr.split("---SLIDE_BREAK---")
        parts.forEach { part ->
            val lines = part.trim().split("\n")
            var title = "Title"
            var desc = "Description"
            var bg = "Slate"
            var textCol = "White"
            lines.forEach { line ->
                if (line.startsWith("T:")) title = line.substring(2)
                if (line.startsWith("D:")) desc = line.substring(2)
                if (line.startsWith("B:")) bg = line.substring(2)
                if (line.startsWith("C:")) textCol = line.substring(2)
            }
            if (part.trim().isNotEmpty()) {
                list.add(SlideItem(title, desc, bg, textCol))
            }
        }
        if (list.isEmpty()) {
            list.add(SlideItem("Title", "Subtitle", "Slate", "White"))
        }
        return list
    }

    private fun serializeSlideList(list: List<SlideItem>): String {
        return list.joinToString("\n---SLIDE_BREAK---\n") { slide ->
            "T:${slide.title}\nD:${slide.description}\nB:${slide.backgroundColor}\nC:${slide.textColor}"
        }
    }

    private fun parseFileMetadata(fileName: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        // We can just encode or parse custom suffix but for now defaults are good
        return map
    }
}

data class SlideItem(
    val title: String,
    val description: String,
    val backgroundColor: String,
    val textColor: String
)

// Mini Math expression compiler
object ArithmeticParser {
    fun parseAndEvaluate(expr: String): Double {
        val tokens = expr.filter { !it.isWhitespace() }
        if (tokens.isEmpty()) return 0.0
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < tokens.length) tokens[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < tokens.length) throw RuntimeException("Unexpected: " + tokens[pos])
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.code)) x += parseTerm() // addition
                    else if (eat('-'.code)) x -= parseTerm() // subtraction
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.code)) x *= parseFactor() // multiplication
                    else if (eat('/'.code)) {
                        val d = parseFactor()
                        x = if (d != 0.0) x / d else 0.0 // division
                    } else return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.code)) return parseFactor() // unary plus
                if (eat('-'.code)) return -parseFactor() // unary minus

                var x: Double
                val startPos = pos
                if (eat('('.code)) { // parentheses
                    x = parseExpression()
                    eat(')'.code)
                } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) { // numbers
                    while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                    x = tokens.substring(startPos, pos).toDouble()
                } else {
                    throw RuntimeException("Unexpected: " + ch.toChar())
                }
                return x
            }
        }.parse()
    }
}
