package com.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.localization.AppStrings
import com.example.ui.MainTab
import com.example.ui.MainViewModel
import com.example.ui.components.CyberBottomNav
import com.example.ui.components.CyberTopBar
import com.example.ui.components.LinkSecurityModal
import com.example.ui.components.NewAppInstallAlertModal
import com.example.ui.components.PermissionsOnboardingDialog
import com.example.ui.screens.AppThreatDetailDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.MyApplicationTheme
import com.example.util.SecurityNotificationHelper

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create notification channel for security alerts
        SecurityNotificationHelper.createNotificationChannel(this)

        handleIntent(intent)

        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return

        // 1. Intercept Web Link clicked in any other application (Telegram, SMS, WhatsApp, etc.)
        if (intent.action == Intent.ACTION_VIEW && intent.data != null) {
            val url = intent.data.toString()
            viewModel.handleIncomingUrl(url, this)
            return
        }

        // 2. Shared link via text/plain
        if (intent.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (!sharedText.isNullOrBlank()) {
                viewModel.handleIncomingUrl(sharedText, this)
                return
            }
        }

        // 3. New package installed intercept from AppInstallReceiver
        val newPkg = intent.getStringExtra("EXTRA_NEW_PACKAGE_NAME")
        if (!newPkg.isNullOrBlank()) {
            viewModel.handleNewPackageInstalled(newPkg)
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val context = LocalContext.current
    val currentLang by viewModel.currentLanguage.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val deviceState by viewModel.deviceState.collectAsState()

    val isWebGuardEnabled by viewModel.isWebGuardEnabled.collectAsState()
    val isAppInstallGuardEnabled by viewModel.isAppInstallGuardEnabled.collectAsState()

    val isScanning by viewModel.isScanning.collectAsState()
    val scanProgress by viewModel.scanProgress.collectAsState()
    val scannedAppName by viewModel.scannedCurrentAppName.collectAsState()
    val scannedApps by viewModel.scannedApps.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val selectedAppForDetail by viewModel.selectedAppForDetail.collectAsState()

    // Link Intercept & Package Alert Modals
    val interceptedUrl by viewModel.interceptedUrl.collectAsState()
    val interceptedResult by viewModel.interceptedResult.collectAsState()
    val isCheckingLink by viewModel.isCheckingInterceptedLink.collectAsState()
    val newAppAlert by viewModel.newInstalledAppAlert.collectAsState()

    // Permissions Onboarding Dialog at the start
    var showPermissionsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("himoyachi_prefs", Context.MODE_PRIVATE)
        val hasShown = prefs.getBoolean("has_shown_permissions_setup", false)
        if (!hasShown) {
            showPermissionsDialog = true
            prefs.edit().putBoolean("has_shown_permissions_setup", true).apply()
        }
    }

    // Handle Back Press
    BackHandler(enabled = true) {
        if (showPermissionsDialog) {
            showPermissionsDialog = false
        } else if (!viewModel.navigateBack()) {
            (context as? ComponentActivity)?.finish()
        }
    }

    val topBarTitle = when (currentTab) {
        MainTab.DASHBOARD -> AppStrings.appName(currentLang)
        MainTab.SCANNER -> AppStrings.tabAntivirus(currentLang)
        MainTab.SETTINGS -> AppStrings.tabSettings(currentLang)
    }

    Scaffold(
        topBar = {
            CyberTopBar(
                title = topBarTitle,
                currentLang = currentLang,
                onLangSelect = { viewModel.setLanguage(it) },
                showBackButton = false
            )
        },
        bottomBar = {
            CyberBottomNav(
                currentTab = currentTab,
                currentLang = currentLang,
                onTabSelected = { viewModel.selectTab(it) }
            )
        },
        containerColor = CyberDarkBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CyberDarkBg)
        ) {
            when (currentTab) {
                MainTab.DASHBOARD -> {
                    DashboardScreen(
                        deviceState = deviceState,
                        lang = currentLang,
                        isWebGuardEnabled = isWebGuardEnabled,
                        isAppInstallGuardEnabled = isAppInstallGuardEnabled,
                        onToggleWebGuard = { viewModel.toggleWebGuard() },
                        onToggleAppInstallGuard = { viewModel.toggleAppInstallGuard() },
                        onStartScan = {
                            viewModel.selectTab(MainTab.SCANNER)
                            viewModel.startScan(deepScan = true)
                        },
                        onPurgeThreats = { viewModel.purgeAllThreats(context) },
                        onViewScanner = { viewModel.selectTab(MainTab.SCANNER) },
                        onOpenPermissionsOnboarding = { showPermissionsDialog = true },
                        onOpenAccessibility = { viewModel.openDefaultAppsSettings(context) }
                    )
                }
                MainTab.SCANNER -> {
                    ScannerScreen(
                        isScanning = isScanning,
                        scanProgress = scanProgress,
                        currentScannedAppName = scannedAppName,
                        apps = scannedApps,
                        selectedFilter = selectedFilter,
                        lang = currentLang,
                        onStartScan = { viewModel.startScan(deepScan = true) },
                        onCancelScan = { viewModel.cancelScan() },
                        onFilterSelect = { viewModel.setSelectedFilter(it) },
                        onAppClick = { viewModel.selectAppForDetail(it) },
                        onUninstallApp = { app ->
                            viewModel.safeUninstallApp(context, app.packageName)
                        }
                    )
                }
                MainTab.SETTINGS -> {
                    SettingsScreen(
                        currentLang = currentLang,
                        onLangSelect = { viewModel.setLanguage(it) }
                    )
                }
            }

            // Permissions Onboarding Dialog (Shown at startup or when requested)
            if (showPermissionsDialog) {
                PermissionsOnboardingDialog(
                    onDismiss = { showPermissionsDialog = false }
                )
            }

            // Link Interceptor Dialog (Blocks dangerous phishing links, forwards safe links)
            if (interceptedUrl != null) {
                LinkSecurityModal(
                    url = interceptedUrl!!,
                    result = interceptedResult,
                    isChecking = isCheckingLink,
                    lang = currentLang,
                    onProceed = {
                        viewModel.proceedToBrowserRegardless(context, interceptedUrl!!)
                    },
                    onDismiss = {
                        viewModel.dismissLinkIntercept()
                    }
                )
            }

            // Real-time Rogue APK Install Alert Dialog
            newAppAlert?.let { app ->
                NewAppInstallAlertModal(
                    app = app,
                    lang = currentLang,
                    onUninstall = {
                        viewModel.safeUninstallApp(context, app.packageName)
                        viewModel.dismissNewAppAlert()
                    },
                    onDismiss = {
                        viewModel.dismissNewAppAlert()
                    }
                )
            }

            // App Detail Dialog
            selectedAppForDetail?.let { app ->
                AppThreatDetailDialog(
                    app = app,
                    lang = currentLang,
                    onDismiss = { viewModel.selectAppForDetail(null) },
                    onSafeUninstall = {
                        viewModel.safeUninstallApp(context, app.packageName)
                    },
                    onOpenAppSettings = {
                        viewModel.openAppSettings(context, app.packageName)
                    }
                )
            }
        }
    }
}
