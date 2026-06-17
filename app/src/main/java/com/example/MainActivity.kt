package com.example

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.FormatAlignRight
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.WorkspaceFile
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.SlideItem
import com.example.ui.viewmodel.WorkspaceViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val workspaceViewModel: WorkspaceViewModel = viewModel()
            val themeState by workspaceViewModel.themeState.collectAsState()

            NexOfficeTheme(selectedTheme = themeState) {
                val currentScreen by workspaceViewModel.currentScreen.collectAsState()

                val showBottomBar = currentScreen in listOf(
                    Screen.Dashboard,
                    Screen.FilesHub,
                    Screen.ToolsZone,
                    Screen.Settings
                )

                var showCreatePopup by remember { mutableStateOf(false) }

                if (showCreatePopup) {
                    AlertDialog(
                        onDismissRequest = { showCreatePopup = false },
                        title = { Text("Create New Offline Asset", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                        text = {
                            Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                                Button(
                                    onClick = {
                                        showCreatePopup = false
                                        workspaceViewModel.startNewDocument()
                                    },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(imageVector = Icons.Default.InsertDriveFile, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("New Document (DOC)")
                                }
                                Button(
                                    onClick = {
                                        showCreatePopup = false
                                        workspaceViewModel.startNewSpreadsheet()
                                    },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                ) {
                                    Icon(imageVector = Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("New Spreadsheet (SHEET)")
                                }
                                Button(
                                    onClick = {
                                        showCreatePopup = false
                                        workspaceViewModel.startNewSlideshow()
                                    },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
                                ) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("New Presentation (SLIDE)")
                                }
                            }
                        },
                        confirmButton = {},
                        dismissButton = {
                            OutlinedButton(onClick = { showCreatePopup = false }) {
                                Text("Close")
                            }
                        }
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            androidx.compose.material3.Surface(
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(width = 0.5.dp, color = Color(0xFFE2E8F0))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .navigationBarsPadding()
                                        .height(72.dp)
                                        .padding(horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 1. HOME
                                    IconButton(
                                        onClick = { workspaceViewModel.navigateTo(Screen.Dashboard) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Default.Home,
                                                contentDescription = "Home",
                                                tint = if (currentScreen == Screen.Dashboard) MaterialTheme.colorScheme.primary else Color(0xFF94A3B8)
                                            )
                                            Text(
                                                text = "HOME",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (currentScreen == Screen.Dashboard) MaterialTheme.colorScheme.primary else Color(0xFF94A3B8)
                                            )
                                        }
                                    }

                                    // 2. FILES
                                    IconButton(
                                        onClick = { workspaceViewModel.navigateTo(Screen.FilesHub) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Default.Folder,
                                                contentDescription = "Files",
                                                tint = if (currentScreen == Screen.FilesHub) MaterialTheme.colorScheme.primary else Color(0xFF94A3B8)
                                            )
                                            Text(
                                                text = "FILES",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (currentScreen == Screen.FilesHub) MaterialTheme.colorScheme.primary else Color(0xFF94A3B8)
                                            )
                                        }
                                    }

                                    // 3. Central FAB (+)
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                            .clickable { showCreatePopup = true },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Create New",
                                            tint = Color.White,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }

                                    // 4. TOOLS
                                    IconButton(
                                        onClick = { workspaceViewModel.navigateTo(Screen.ToolsZone) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Default.Calculate,
                                                contentDescription = "Tools",
                                                tint = if (currentScreen == Screen.ToolsZone) MaterialTheme.colorScheme.primary else Color(0xFF94A3B8)
                                            )
                                            Text(
                                                text = "TOOLS",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (currentScreen == Screen.ToolsZone) MaterialTheme.colorScheme.primary else Color(0xFF94A3B8)
                                            )
                                        }
                                    }

                                    // 5. SETTINGS
                                    IconButton(
                                        onClick = { workspaceViewModel.navigateTo(Screen.Settings) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Default.Settings,
                                                contentDescription = "Settings",
                                                tint = if (currentScreen == Screen.Settings) MaterialTheme.colorScheme.primary else Color(0xFF94A3B8)
                                            )
                                            Text(
                                                text = "SETTINGS",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (currentScreen == Screen.Settings) MaterialTheme.colorScheme.primary else Color(0xFF94A3B8)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            Screen.Splash -> SplashScreenView(viewModel = workspaceViewModel)
                            Screen.Onboarding -> OnboardingScreenView(viewModel = workspaceViewModel)
                            Screen.Dashboard -> SmartDashboardView(viewModel = workspaceViewModel)
                            Screen.Docs -> DocumentEditorView(viewModel = workspaceViewModel)
                            Screen.Sheets -> SpreadsheetEditorView(viewModel = workspaceViewModel)
                            Screen.Slides -> SlideshowEditorView(viewModel = workspaceViewModel)
                            Screen.PdfTools -> PdfToolsView(viewModel = workspaceViewModel)
                            Screen.FilesHub -> FileHubView(viewModel = workspaceViewModel)
                            Screen.ToolsZone -> ToolsZoneView(viewModel = workspaceViewModel)
                            Screen.Settings -> SettingsView(viewModel = workspaceViewModel)
                            Screen.Scanner -> DocumentScannerView(viewModel = workspaceViewModel)
                            Screen.VoiceCenter -> VoiceCenterView(viewModel = workspaceViewModel)
                            Screen.LockScreen -> SecurityLockView(viewModel = workspaceViewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NexOfficeTheme(
    selectedTheme: String,
    content: @Composable () -> Unit
) {
    val myColorScheme = when (selectedTheme) {
        "Dark" -> darkColorScheme(
            primary = Color(0xFF64B5F6),
            onPrimary = Color(0xFF0D47A1),
            surface = Color(0xFF242424),
            background = Color(0xFF181818),
            onBackground = Color(0xFFE0E0E0),
            onSurface = Color(0xFFE0E0E0),
            primaryContainer = Color(0xFF1565C0),
            surfaceVariant = Color(0xFF2C2C2C)
        )
        "AMOLED" -> darkColorScheme(
            primary = Color(0xFF90CAF9),
            onPrimary = Color(0xFF0D47A1),
            surface = Color(0xFF0C0C0C),
            background = Color(0xFF000000),
            onBackground = Color(0xFFFFFFFF),
            onSurface = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFF1E88E5),
            surfaceVariant = Color(0xFF151515)
        )
        "Minimalist" -> lightColorScheme(
            primary = Color(0xFF2563EB),
            onPrimary = Color.White,
            surface = Color(0xFFFFFFFF),
            background = Color(0xFFF8F9FA),
            onBackground = Color(0xFF0F172A),
            onSurface = Color(0xFF1E293B),
            primaryContainer = Color(0xFFDBEAFE),
            surfaceVariant = Color(0xFFF1F5F9)
        )
        "Blue" -> lightColorScheme(
            primary = Color(0xFF0d47a1),
            onPrimary = Color.White,
            surface = Color(0xFFFFFFFF),
            background = Color(0xFFEDF4FA),
            onBackground = Color(0xFF001529),
            onSurface = Color(0xFF001529),
            primaryContainer = Color(0xFFBFDBFE),
            surfaceVariant = Color(0xFFF1F5F9)
        )
        else -> lightColorScheme( // Light theme (Silver Sage)
            primary = Color(0xFF2E7D32),
            onPrimary = Color.White,
            surface = Color(0xFFFFFFFF),
            background = Color(0xFFF1F8F5),
            onBackground = Color(0xFF1B5E20),
            onSurface = Color(0xFF1B5E20),
            primaryContainer = Color(0xFFC8E6C9),
            surfaceVariant = Color(0xFFF5F5F5)
        )
    }

    MaterialTheme(
        colorScheme = myColorScheme,
        content = content
    )
}

// ----------------------------------------------------
// 1️⃣ Splash Screen
// ----------------------------------------------------
@Composable
fun SplashScreenView(viewModel: WorkspaceViewModel) {
    var skipped by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2200)
        if (!skipped) {
            viewModel.completeSplash()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(2.dp, Color.White, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.InsertDriveFile,
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "NexOffice Pro",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Fast Offline Office Suite",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// ----------------------------------------------------
// 2️⃣ Onboarding Screen (3 Slides)
// ----------------------------------------------------
@Composable
fun OnboardingScreenView(viewModel: WorkspaceViewModel) {
    var activePage by remember { mutableStateOf(0) }

    val slides = listOf(
        OnboardingSlide(
            "Create Documents Offline",
            "A fast Word Processor featuring normal, CV, student course templates, and robust export options.",
            Icons.Default.TextSnippet,
            Color(0xFF3B82F6)
        ),
        OnboardingSlide(
            "Manage Sheets & Data Easily",
            "Power builder Spreadsheet. Design layouts, sort listings, graph parameters, and compute SUM or AVERAGE.",
            Icons.Default.Calculate,
            Color(0xFF10B981)
        ),
        OnboardingSlide(
            "All Files in One Place",
            "Integrated File Hub manager. Categorize, lock, search, and transfer files instantly with zero latency.",
            Icons.Default.Folder,
            Color(0xFF8B5CF6)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Skip",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { viewModel.completeOnboarding() }
                    .padding(8.dp)
                    .testTag("onboarding_skip")
            )
        }

        val slide = slides[activePage]

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(slide.tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = slide.icon,
                    contentDescription = null,
                    tint = slide.tint,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = slide.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = slide.body,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                slides.forEachIndexed { idx, _ ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (idx == activePage) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (idx == activePage) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (activePage > 0) {
                    OutlinedButton(
                        onClick = { activePage-- },
                        modifier = Modifier.height(50.dp)
                    ) {
                        Text("Back")
                    }
                } else {
                    Spacer(modifier = Modifier.width(80.dp))
                }

                Button(
                    onClick = {
                        if (activePage < 2) {
                            activePage++
                        } else {
                            viewModel.completeOnboarding()
                        }
                    },
                    modifier = Modifier
                        .height(50.dp)
                        .testTag("onboarding_next_done"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(if (activePage == 2) "Get Started" else "Next")
                }
            }
        }
    }
}

data class OnboardingSlide(
    val title: String,
    val body: String,
    val icon: ImageVector,
    val tint: Color
)

// ----------------------------------------------------
// 3️⃣ Smart Dashboard
// ----------------------------------------------------
@Composable
fun SmartDashboardView(viewModel: WorkspaceViewModel) {
    val files by viewModel.filesState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val context = LocalContext.current

    var showQuickNoteDlg by remember { mutableStateOf(false) }
    var quickNoteTitle by remember { mutableStateOf("") }
    var quickNoteContent by remember { mutableStateOf("") }

    if (showQuickNoteDlg) {
        AlertDialog(
            onDismissRequest = { showQuickNoteDlg = false },
            title = { Text("Quick Note Setter") },
            text = {
                Column {
                    OutlinedTextField(
                        value = quickNoteTitle,
                        onValueChange = { quickNoteTitle = it },
                        label = { Text("Note Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = quickNoteContent,
                        onValueChange = { quickNoteContent = it },
                        label = { Text("Note Content") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (quickNoteTitle.isNotEmpty()) {
                            viewModel.addQuickNote(quickNoteTitle, quickNoteContent)
                            showQuickNoteDlg = false
                            quickNoteTitle = ""
                            quickNoteContent = ""
                            Toast.makeText(context, "Note Logged", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showQuickNoteDlg = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.InsertDriveFile,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "NexOffice Pro",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "All-In-One Offline Productivity Hub",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }

            IconButton(
                onClick = { viewModel.navigateTo(Screen.Settings) },
                modifier = Modifier.testTag("dashboard_settings_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Search Bar Component
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                "Search files & notes...",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dashboard_search_input")
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Quick Actions Modules Grid
            item {
                Text(
                    text = "Quick Actions Dashboard",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        QuickActionCard("New Document", "Writer app", Icons.Default.TextSnippet, Color(0xFF3b82f6)) {
                            viewModel.startNewDocument()
                        }
                        QuickActionCard("New Presentation", "Slide builder", Icons.Default.PlayArrow, Color(0xFFF59E0B)) {
                            viewModel.startNewSlideshow()
                        }
                        QuickActionCard("Scanner Hub", "Camera Scanner", Icons.Default.Camera, Color(0xFFEC4899)) {
                            viewModel.navigateTo(Screen.Scanner)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        QuickActionCard("New Spreadsheet", "Excel editor", Icons.Default.Calculate, Color(0xFF10B981)) {
                            viewModel.startNewSpreadsheet()
                        }
                        QuickActionCard("Quick Note Creator", "Instant capture", Icons.Default.Edit, Color(0xFF8B5CF6)) {
                            showQuickNoteDlg = true
                        }
                        QuickActionCard("Voice Recorder", "Audio dictate", Icons.Default.Mic, Color(0xFFEF4444)) {
                            viewModel.navigateTo(Screen.VoiceCenter)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Utilities Panel
            item {
                Text(
                    text = "Smart Utility Panel",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    UtilityButton("PDF Editor", Icons.Default.UploadFile) {
                        viewModel.navigateTo(Screen.PdfTools)
                    }
                    UtilityButton("Files Hub", Icons.Default.Folder) {
                        viewModel.navigateTo(Screen.FilesHub)
                    }
                    UtilityButton("Calc Pro", Icons.Default.Calculate) {
                        viewModel.navigateTo(Screen.ToolsZone)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Recent Workspace Files list
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Handled Files",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "View Hub",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable { viewModel.navigateTo(Screen.FilesHub) }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            val recentList = files.take(8)
            if (recentList.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.InsertDriveFile,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No active offline files yet",
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            } else {
                items(recentList) { file ->
                    WorkspaceFileRowItem(file = file, viewModel = viewModel, context = context)
                }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun UtilityButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun WorkspaceFileRowItem(
    file: WorkspaceFile,
    viewModel: WorkspaceViewModel,
    context: android.content.Context
) {
    var showActions by remember { mutableStateOf(false) }

    val fileTypeIcon = when (file.type) {
        "DOC" -> Icons.Default.TextSnippet
        "SHEET" -> Icons.Default.Calculate
        "SLIDE" -> Icons.Default.PlayArrow
        "NOTE" -> Icons.Default.Edit
        "SCAN" -> Icons.Default.Camera
        "AUDIO" -> Icons.Default.Mic
        else -> Icons.Default.InsertDriveFile
    }

    val iconColor = when (file.type) {
        "DOC" -> Color(0xFF3b82f6)
        "SHEET" -> Color(0xFF10B981)
        "SLIDE" -> Color(0xFFF59E0B)
        "NOTE" -> Color(0xFF8B5CF6)
        "SCAN" -> Color(0xFFEC4899)
        "AUDIO" -> Color(0xFFEF4444)
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable {
                when (file.type) {
                    "DOC" -> viewModel.loadDocument(file)
                    "SHEET" -> viewModel.loadSpreadsheet(file)
                    "SLIDE" -> viewModel.loadSlideshow(file)
                    else -> {
                        showActions = true
                    }
                }
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = fileTypeIcon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = file.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Modified: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).format(Date(file.timestamp))}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }

                if (file.isPrivate) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Stored in Locker",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                IconButton(onClick = { showActions = !showActions }) {
                    Icon(
                        imageVector = if (showActions) Icons.Default.Close else Icons.Default.Edit,
                        contentDescription = "Details actions",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            AnimatedVisibility(visible = showActions) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButtonWithIcon(
                        text = if (file.isPrivate) "Unlock" else "Lock file",
                        icon = if (file.isPrivate) Icons.Default.LockOpen else Icons.Default.Lock
                    ) {
                        viewModel.toggleFileLocker(file.id)
                        Toast.makeText(context, "Locker status modified", Toast.LENGTH_SHORT).show()
                    }

                    TextButtonWithIcon(text = "PDF Conv", icon = Icons.Default.UploadFile) {
                        viewModel.convertDocToPDF(file)
                        Toast.makeText(context, "Exported PDF to Files Hub", Toast.LENGTH_SHORT).show()
                        showActions = false
                    }

                    TextButtonWithIcon(text = "Share", icon = Icons.Default.Share) {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, file.name)
                            putExtra(Intent.EXTRA_TEXT, file.content)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Office Doc"))
                    }

                    TextButtonWithIcon(text = "Trash", icon = Icons.Default.Delete, tint = Color(0xFFEF4444)) {
                        viewModel.deleteFile(file.id)
                        Toast.makeText(context, "Doc moved to trash", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

@Composable
fun TextButtonWithIcon(
    text: String,
    icon: ImageVector,
    tint: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = tint)
    }
}

// ----------------------------------------------------
// 4️⃣ Document Editor (Docs Screen)
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentEditorView(viewModel: WorkspaceViewModel) {
    val context = LocalContext.current
    var showTemplateSelector by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App header
        TopAppBar(
            title = {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = viewModel.activeFileName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Rename",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(14.dp)
                                .clickable {
                                    // Set a custom rename dialog
                                }
                        )
                    }
                    Text(
                        text = "Writing: ${viewModel.docWritingMode} Mode",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = { viewModel.navigateTo(Screen.Dashboard) }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                // Formatting Layout selector
                IconButton(onClick = { showTemplateSelector = true }) {
                    Icon(imageVector = Icons.Default.TextSnippet, contentDescription = "Templates")
                }

                IconButton(
                    onClick = {
                        viewModel.saveDocument()
                        Toast.makeText(context, "Saved to File Hub", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("doc_save_btn")
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save file")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        if (showTemplateSelector) {
            AlertDialog(
                onDismissRequest = { showTemplateSelector = false },
                title = { Text("Choose Writing Template") },
                text = {
                    Column {
                        val modes = listOf("Normal", "Student", "CV", "Business Letter")
                        modes.forEach { mode ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.changeWritingMode(mode)
                                        showTemplateSelector = false
                                    }
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(mode, fontWeight = FontWeight.Bold)
                                if (viewModel.docWritingMode == mode) {
                                    Icon(imageVector = Icons.Default.Done, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                            Divider()
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showTemplateSelector = false }) {
                        Text("Dimiss")
                    }
                }
            )
        }

        // Action word statistics and Voice typing
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Dictation",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier
                        .clickable {
                            viewModel.triggerVoiceDictationWord()
                            Toast.makeText(context, "Dictated: ${viewModel.speechToTextResult}", Toast.LENGTH_SHORT).show()
                        }
                        .size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Offline Voice Dictation",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = { viewModel.autoFormatDoc() },
                    modifier = Modifier.height(28.dp),
                    contentPadding = ButtonDefaults.ContentPadding
                ) {
                    Text("Auto-Format", fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Words: ${viewModel.docText.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }

        // Formatting toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp)
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                IconButton(
                    onClick = { viewModel.docBold = !viewModel.docBold },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatBold,
                        contentDescription = "Bold",
                        tint = if (viewModel.docBold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }

                IconButton(
                    onClick = { viewModel.docItalic = !viewModel.docItalic },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatItalic,
                        contentDescription = "Italic",
                        tint = if (viewModel.docItalic) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }

                IconButton(
                    onClick = {
                        viewModel.docAlign = when (viewModel.docAlign) {
                            "Left" -> "Center"
                            "Center" -> "Right"
                            else -> "Left"
                        }
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    val alignIcon = when (viewModel.docAlign) {
                        "Right" -> Icons.Default.FormatAlignRight
                        else -> Icons.Default.FormatAlignLeft
                    }
                    Icon(imageVector = alignIcon, contentDescription = "Alignment")
                }
            }

            // FontSize slider
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FormatSize,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Slider(
                    value = viewModel.docFontSize,
                    onValueChange = { viewModel.docFontSize = it },
                    valueRange = 12f..30f,
                    modifier = Modifier.width(100.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${viewModel.docFontSize.toInt()}sp",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Active text typing field area
        TextField(
            value = viewModel.docText,
            onValueChange = {
                viewModel.docText = it
                if (viewModel.autoSaveState.value) {
                    viewModel.saveDocument()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("doc_editor_input"),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = viewModel.docFontSize.sp,
                fontWeight = if (viewModel.docBold) FontWeight.Bold else FontWeight.Normal,
                textAlign = when (viewModel.docAlign) {
                    "Center" -> TextAlign.Center
                    "Right" -> TextAlign.Right
                    else -> TextAlign.Left
                }
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}

// ----------------------------------------------------
// 5️⃣ Spreadsheet Editor (Sheets Screen)
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpreadsheetEditorView(viewModel: WorkspaceViewModel) {
    val context = LocalContext.current
    var showChartDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = viewModel.activeFileName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            navigationIcon = {
                IconButton(onClick = { viewModel.navigateTo(Screen.Dashboard) }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { showChartDialog = true }) {
                    Icon(imageVector = Icons.Default.Calculate, contentDescription = "Generate Analytics")
                }

                IconButton(
                    onClick = {
                        viewModel.saveSpreadsheet()
                        Toast.makeText(context, "Saved csv representation", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("sheet_save_btn")
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save Spreadsheet")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        // Formula Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp)
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = viewModel.activeCell,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                "fx",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                value = viewModel.sheetFormulaInput,
                onValueChange = { viewModel.updateActiveCell(it) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("sheet_formula_input")
                    .heightIn(max = 44.dp),
                placeholder = { Text("Enter cell value or equation e.g. =SUM(A1:A5)") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

        // Active Columns Row labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(30.dp)
                    .border(0.5.dp, Color.Gray.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text("")
            }

            for (colIdx in 0 until viewModel.sheetColCount) {
                val colLetter = ('A' + colIdx).toString()
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                        .border(0.5.dp, Color.Gray.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(colLetter, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Standard Spreadsheet Table Cells
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(viewModel.sheetRowCount) { rowIdx ->
                val rowNum = rowIdx + 1
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Row indicator
                    Box(
                        modifier = Modifier
                            .width(44.dp)
                            .height(44.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(0.5.dp, Color.Gray.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(rowNum.toString(), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Content grid columns
                    for (colIdx in 0 until viewModel.sheetColCount) {
                        val colLetter = ('A' + colIdx).toString()
                        val cellId = "$colLetter$rowNum"
                        val isSelected = viewModel.activeCell == cellId
                        val rawContent = viewModel.sheetCells[cellId] ?: ""
                        val showEvaluated = if (rawContent.startsWith("=")) {
                            viewModel.evaluateFormula(rawContent, viewModel.sheetCells)
                        } else {
                            rawContent
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.background)
                                .border(
                                    if (isSelected) 1.5.dp else 0.5.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f)
                                )
                                .clickable {
                                    viewModel.selectCell(cellId)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = showEvaluated,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }

        // Layout controls row: Add row/col
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { viewModel.sheetRowCount++ },
                modifier = Modifier.weight(1f)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Row")
            }
            Spacer(modifier = Modifier.width(10.dp))
            OutlinedButton(
                onClick = { viewModel.sheetColCount++ },
                modifier = Modifier.weight(1f)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Col")
            }
        }

        // Table dynamic charts dialog
        if (showChartDialog) {
            AlertDialog(
                onDismissRequest = { showChartDialog = false },
                title = { Text("Active Pro Chart Analytics") },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Dynamic graphics based on first column criteria", fontSize = 12.sp, modifier = Modifier.padding(bottom = 12.dp))

                        // We render standard bar chart using local code loops
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(16.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                // Draw mock dynamic analytics lines/bars
                                val width = size.width
                                val height = size.height
                                val barWidth = width / 6f
                                val colors = listOf(Color(0xFF3B82F6), Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFFEF4444))

                                for (i in 0 until 4) {
                                    val barHeight = height * (0.3f + i * 0.15f)
                                    drawRect(
                                        color = colors[i],
                                        topLeft = Offset(i * (barWidth + 20f) + 10f, height - barHeight),
                                        size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("A1-A4 Parameters", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Automatic update", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showChartDialog = false }) {
                        Text("Dismiss Analytics")
                    }
                }
            )
        }
    }
}

// ----------------------------------------------------
// 6️⃣ Slides Editor (Slides Screen)
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlideshowEditorView(viewModel: WorkspaceViewModel) {
    val context = LocalContext.current
    var activeTitleInput by remember { mutableStateOf("") }
    var activeDescInput by remember { mutableStateOf("") }

    val activeSlide = viewModel.slideList.getOrNull(viewModel.activeSlideIndex)

    LaunchedEffect(viewModel.activeSlideIndex, viewModel.slideList) {
        activeTitleInput = activeSlide?.title ?: ""
        activeDescInput = activeSlide?.description ?: ""
    }

    if (viewModel.isSlidePreviewMode) {
        // Immersive Player Screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "PREVIEW SCREEN: Slide ${viewModel.activeSlideIndex + 1} of ${viewModel.slideList.size}",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                    IconButton(onClick = { viewModel.isSlidePreviewMode = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close Preview", tint = Color.White)
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = activeSlide?.title ?: "Title",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = activeSlide?.description ?: "Description text content",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            if (viewModel.activeSlideIndex > 0) viewModel.activeSlideIndex--
                        },
                        enabled = viewModel.activeSlideIndex > 0
                    ) {
                        Text("Previous")
                    }

                    Button(
                        onClick = {
                            if (viewModel.activeSlideIndex < viewModel.slideList.size - 1) viewModel.activeSlideIndex++
                        },
                        enabled = viewModel.activeSlideIndex < viewModel.slideList.size - 1
                    ) {
                        Text("Next Slide")
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TopAppBar(
                title = { Text("Presentation Hub", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.Dashboard) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.isSlidePreviewMode = true }) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Run presentation", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(
                        onClick = {
                            viewModel.saveSlides()
                            Toast.makeText(context, "Presentation auto saved", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("slide_save_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = "Save file")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )

            // Dynamic Slide thumbnails bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    item {
                        Row {
                            viewModel.slideList.forEachIndexed { i, slide ->
                                val isSel = i == viewModel.activeSlideIndex
                                Card(
                                    modifier = Modifier
                                        .width(90.dp)
                                        .fillMaxHeight()
                                        .padding(end = 8.dp)
                                        .border(
                                            2.dp,
                                            if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            viewModel.activeSlideIndex = i
                                        },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Slide ${i + 1}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            IconButton(
                                onClick = { viewModel.addNewSlide() },
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Slide")
                            }
                        }
                    }
                }
            }

            // Real Slide Layout Editor (Center area)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = activeTitleInput,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = activeDescInput,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Text configuration panel
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Customize Active Slide Elements",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = activeTitleInput,
                        onValueChange = {
                            activeTitleInput = it
                            viewModel.updateActiveSlide(
                                it,
                                activeDescInput,
                                activeSlide?.backgroundColor ?: "Slate",
                                activeSlide?.textColor ?: "White"
                            )
                        },
                        label = { Text("Title Line") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = activeDescInput,
                        onValueChange = {
                            activeDescInput = it
                            viewModel.updateActiveSlide(
                                activeTitleInput,
                                it,
                                activeSlide?.backgroundColor ?: "Slate",
                                activeSlide?.textColor ?: "White"
                            )
                        },
                        label = { Text("Bullet points / Highlights text") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// 7️⃣ PDF Tools Screen
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfToolsView(viewModel: WorkspaceViewModel) {
    val context = LocalContext.current
    val files by viewModel.filesState.collectAsState()
    val pdfsList = files.filter { it.type == "PDF" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("PDF Toolkit offline", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            navigationIcon = {
                IconButton(onClick = { viewModel.navigateTo(Screen.Dashboard) }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Acrobat tool operations (No internet required)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = {
                                    Toast.makeText(context, "Merging selected PDFs offline...", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text("Merge PDFs", fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            OutlinedButton(
                                onClick = {
                                    Toast.makeText(context, "Splitting first PDF item into pages", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text("Split PDF", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Offline Generated PDFs",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            if (pdfssListIsEmpty(pdfsList)) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.UploadFile, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No PDFs created yet. You can export PDF in Docs!", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            } else {
                items(pdfsList) { file ->
                    WorkspaceFileRowItem(file = file, viewModel = viewModel, context = context)
                }
            }
        }
    }
}

private fun pdfssListIsEmpty(list: List<WorkspaceFile>): Boolean {
    return list.isEmpty()
}

// ----------------------------------------------------
// 8️⃣ Files Hub (Upgraded Files Manager)
// ----------------------------------------------------
@Composable
fun FileHubView(viewModel: WorkspaceViewModel) {
    val files by viewModel.filesState.collectAsState()
    val context = LocalContext.current
    val categorizerList = listOf("ALL", "DOC", "SHEET", "SLIDE", "NOTE", "SCAN", "AUDIO")
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo(Screen.Dashboard) }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Local File Locker Hub", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }

        // Horizontal filter bar
        // We render it dynamically
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
            ) {
                item {
                    Row {
                        categorizerList.forEach { option ->
                            val isSelected = selectedCategory == option
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surface
                                    )
                                    .clickable {
                                        viewModel.selectCategory(option)
                                    }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = option,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            val filterFiles = files.filter { !it.isPrivate || viewModel.isAppUnlocked.value }

            if (filterFiles.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.Folder, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No files matches this category", color = Color.Gray, fontSize = 13.sp)
                    }
                }
            } else {
                items(filterFiles) { file ->
                    WorkspaceFileRowItem(file = file, viewModel = viewModel, context = context)
                }
            }
        }
    }
}

// ----------------------------------------------------
// 9️⃣ Scanner Module (Simulated multi page document scan)
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentScannerView(viewModel: WorkspaceViewModel) {
    val context = LocalContext.current
    var scanClicked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Adaptive Cam Scanner Pro", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            navigationIcon = {
                IconButton(onClick = { viewModel.navigateTo(Screen.Dashboard) }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Simulated interactive OpenCV auto edge bounds detector. Tap Capture to compile scanned file.",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )

            // Scanning Border Interactive Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.DarkGray)
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (scanClicked) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Cropping & aligning auto bounds...", color = Color.White)
                    }
                } else {
                    // Simulates drawing vector paper outlines inside camera surface
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        // Draw vector safe bounds guidelines
                        drawRect(
                            color = Color.Green,
                            topLeft = Offset(w * 0.15f, h * 0.15f),
                            size = androidx.compose.ui.geometry.Size(w * 0.7f, h * 0.7f),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                        )
                    }
                    Text(
                        "[Camera Frame Activated]",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        scanClicked = true
                        // Simulate delay
                        viewModel.saveScannedDocument("PAGE_METRIC_IMAGE_BINARY")
                        Toast.makeText(context, "Scan compile successful! Placed in File Hub", Toast.LENGTH_SHORT).show()
                        viewModel.navigateTo(Screen.FilesHub)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("scanner_capture_btn")
                ) {
                    Icon(imageVector = Icons.Default.Camera, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Capture & Align document")
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = {
                        viewModel.navigateTo(Screen.Dashboard)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel Scan")
                }
            }
        }
    }
}

// ----------------------------------------------------
// 🔟 Voice Center
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceCenterView(viewModel: WorkspaceViewModel) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Voice Recorder Dictator", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            navigationIcon = {
                IconButton(onClick = { viewModel.navigateTo(Screen.Dashboard) }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            if (viewModel.isRecordingAudio) Color(0xFFEF4444).copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.primaryContainer
                        )
                        .clickable {
                            viewModel.toggleAudioRecording()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (viewModel.isRecordingAudio) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = "Mic button",
                        tint = if (viewModel.isRecordingAudio) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (viewModel.isRecordingAudio) "Recording Audio... Tap to save" else "Tap Mic to record Voice Notes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Recorded files list
            Column(modifier = Modifier.weight(1f).fillMaxWidth().padding(top = 24.dp)) {
                Text(
                    text = "Saved Audio Clips",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (viewModel.recordedAudioFiles.isEmpty()) {
                    Text("No recordings available yet", color = Color.Gray, fontSize = 12.sp)
                } else {
                    LazyColumn {
                        items(viewModel.recordedAudioFiles) { file ->
                            val isPlaying = viewModel.currentPlayingAudio == file && viewModel.isPlayingAudio

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { viewModel.playAudioFile(file) }) {
                                            Icon(
                                                imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(file.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("${file.length() / 1024} KB", fontSize = 11.sp)
                                        }
                                    }

                                    IconButton(onClick = {
                                        file.delete()
                                        viewModel.recordedAudioFiles = viewModel.recordedAudioFiles.filter { it != file }
                                    }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete recording", tint = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 1️⃣1️⃣ Tools Zone (Calculator, Timer, Junk Cleaner Converter)
// ----------------------------------------------------
@Composable
fun ToolsZoneView(viewModel: WorkspaceViewModel) {
    val context = LocalContext.current
    var activeToolState by remember { mutableStateOf("CALC") } // "CALC", "TIMER", "CLEANER"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo(Screen.Dashboard) }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Productivity Tool Belt", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
        }

        // Horizontal toggle row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { activeToolState = "CALC" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeToolState == "CALC") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Calculator", color = if (activeToolState == "CALC") Color.White else MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { activeToolState = "TIMER" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeToolState == "TIMER") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Stopwatch", color = if (activeToolState == "TIMER") Color.White else MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { activeToolState = "CLEANER" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeToolState == "CLEANER") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Cleaner", color = if (activeToolState == "CLEANER") Color.White else MaterialTheme.colorScheme.onSurface)
            }
        }

        Divider()

        when (activeToolState) {
            "CALC" -> CalculatorComponent(viewModel = viewModel)
            "TIMER" -> StopwatchComponent(viewModel = viewModel)
            else -> CleanerComponent(viewModel = viewModel, context = context)
        }
    }
}

@Composable
fun CalculatorComponent(viewModel: WorkspaceViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = viewModel.calculatorInput,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    text = viewModel.calculatorResult,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("calculator_result_text")
                )
            }
        }

        // Numeric Keypad Grid
        val keys = listOf(
            "C", "(", ")", "÷",
            "7", "8", "9", "x",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "=", ""
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.weight(1f).padding(top = 16.dp)
        ) {
            items(keys) { key ->
                if (key.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .padding(6.dp)
                            .height(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (key == "=") MaterialTheme.colorScheme.primary
                                else if (key == "C") Color(0xFFEF4444).copy(alpha = 0.12f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable {
                                viewModel.appendCalculatorToken(key)
                            }
                            .testTag("calc_key_$key"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = key,
                            color = if (key == "=") Color.White else if (key == "C") Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StopwatchComponent(viewModel: WorkspaceViewModel) {
    val formattedTime = remember(viewModel.stopwatchTime) {
        val totalSecs = viewModel.stopwatchTime / 1000
        val mins = totalSecs / 60
        val secs = totalSecs % 60
        val tenths = (viewModel.stopwatchTime % 1000) / 100
        String.format(Locale.US, "%02d:%02d.%d", mins, secs, tenths)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = formattedTime,
                fontSize = 36.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    if (viewModel.stopwatchRunning) {
                        viewModel.pauseStopwatch()
                    } else {
                        viewModel.startStopwatch()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (viewModel.stopwatchRunning) "Pause" else "Start")
            }
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedButton(
                onClick = { viewModel.resetStopwatch() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Reset")
            }
        }
    }
}

@Composable
fun CleanerComponent(viewModel: WorkspaceViewModel, context: android.content.Context) {
    var cleanedState by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(54.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (cleanedState) "Cache fully optimized" else "Junk & Storage Cache Optimizer",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Clears temporal sandbox logs, recorder segments, and compiled charts to reclaim app space.",
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                viewModel.runFileCleaner()
                cleanedState = true
                Toast.makeText(context, "Temporary Files Cleared", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(if (cleanedState) "Clean Slate Complete" else "Analyze and Clean App Space")
        }
    }
}

// ----------------------------------------------------
// 1️⃣2️⃣ Settings Screen
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(viewModel: WorkspaceViewModel) {
    val context = LocalContext.current
    val currentTheme by viewModel.themeState.collectAsState()
    val autoSaveEnabled by viewModel.autoSaveState.collectAsState()
    val appPinCode by viewModel.appPinState.collectAsState()

    var showPinDialog by remember { mutableStateOf(false) }
    var pinFieldInp by remember { mutableStateOf("") }

    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("App Locker Credentials") },
            text = {
                Column {
                    Text("Enter a 4-digit security code locks individual files and folders offline.", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = pinFieldInp,
                        onValueChange = { if (it.length <= 4) pinFieldInp = it },
                        label = { Text("App Security PIN") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pinFieldInp.length >= 4) {
                            viewModel.setAppSecurityPin(pinFieldInp)
                            showPinDialog = false
                            pinFieldInp = ""
                            Toast.makeText(context, "Security PIN applied successfully", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Enable Lock")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Settings & Privacy", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            navigationIcon = {
                IconButton(onClick = { viewModel.navigateTo(Screen.Dashboard) }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        LazyColumn(modifier = Modifier.padding(16.dp)) {
            // Theme Options Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Active UI Theme Variant", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        val themesList = listOf("Minimalist", "Light", "Dark", "AMOLED", "Blue")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            themesList.forEach { thName ->
                                val isSel = currentTheme == thName
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSel) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .clickable {
                                            viewModel.updateTheme(thName)
                                        }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = thName,
                                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Autosave panel
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Auto Save Documents", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Instantly saves edits directly to local SQLite", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = autoSaveEnabled,
                            onCheckedChange = { viewModel.toggleAutoSave() },
                            modifier = Modifier.testTag("settings_autosave_switch")
                        )
                    }
                }
            }

            // Security panel
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Passcode Lock", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                if (appPinCode.isNotEmpty()) "Locker enabled" else "Provide a passcode to lock",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        if (appPinCode.isNotEmpty()) {
                            Button(
                                onClick = {
                                    viewModel.removeSecurityPin()
                                    Toast.makeText(context, "Lock screen removed", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                modifier = Modifier.testTag("settings_disable_lock_btn")
                            ) {
                                Text("Disable Lock")
                            }
                        } else {
                            Button(
                                onClick = { showPinDialog = true },
                                modifier = Modifier.testTag("settings_enable_lock_btn")
                            ) {
                                Text("Enable PIN")
                            }
                        }
                    }
                }
            }

            // App details
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("About NexOffice Suite", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Designed as a high-performance lightweight, offline-first suite of document editors. Secure, stable, privacy-friendly.", fontSize = 12.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Developer Information", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Prince AR Abdur Rahman\nIndependent App Developer", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Published by Ofc company", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("NexVora Lab's Ofc\n© 2026 NexVora Lab's Ofc. All Rights Reserved.", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 1️⃣3️⃣ Passcode Security Locker View
// ----------------------------------------------------
@Composable
fun SecurityLockView(viewModel: WorkspaceViewModel) {
    val context = LocalContext.current
    var inputStr by remember { mutableStateOf("") }
    var showErrorMsg by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Locked Offline Folder",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "NexOffice Locker PIN required to unlock.",
            textAlign = TextAlign.Center,
            color = Color.Gray,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Visual bullet markers for PIN length
        Row(
            modifier = Modifier.padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            for (i in 0 until 4) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(
                            if (i < inputStr.length) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                )
            }
        }

        if (showErrorMsg) {
            Text("Incorrect combination", color = Color(0xFFEF4444), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Simulating numeric grid keyboard
        val padlockKeys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "Clear", "0", "Done")
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.height(260.dp).width(240.dp)
        ) {
            items(padlockKeys) { key ->
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            if (key == "Clear") {
                                inputStr = ""
                                showErrorMsg = false
                            } else if (key == "Done") {
                                val successResult = viewModel.submitPinUnlock(inputStr)
                                if (successResult) {
                                    Toast.makeText(context, "Access Granted", Toast.LENGTH_SHORT).show()
                                } else {
                                    showErrorMsg = true
                                    inputStr = ""
                                }
                            } else {
                                if (inputStr.length < 4) {
                                    inputStr += key
                                    showErrorMsg = false
                                }
                            }
                        }
                        .testTag("pin_key_$key"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = key,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

// Custom simple TextField implementation matching Compose rules without full text editor complexity
@Composable
fun BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: androidx.compose.ui.text.TextStyle = androidx.compose.ui.text.TextStyle.Default
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = textStyle
    )
}
