package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ScanHistoryEntity
import com.example.data.local.SecurityDatabase
import com.example.localization.AppStrings
import com.example.model.AppThreatModel
import com.example.model.DeviceSecurityState
import com.example.model.Language
import com.example.model.RiskLevel
import com.example.model.UrlRiskStatus
import com.example.model.UrlScanResult
import com.example.scanner.AppScannerEngine
import com.example.scanner.DeviceAuditEngine
import com.example.scanner.PhishingScannerEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
    DASHBOARD,
    SCANNER,
    SETTINGS
}

enum class ScanFilter {
    ALL,
    THREATS,
    SUSPICIOUS,
    SAFE
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = SecurityDatabase.getDatabase(application)
    private val appScanner = AppScannerEngine(application)
    private val phishingScanner = PhishingScannerEngine()
    private val deviceAuditor = DeviceAuditEngine(application)

    private val _currentLanguage = MutableStateFlow(Language.UZBEK)
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()

    private val _currentTab = MutableStateFlow(MainTab.DASHBOARD)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _deviceState = MutableStateFlow(deviceAuditor.auditDeviceState())
    val deviceState: StateFlow<DeviceSecurityState> = _deviceState.asStateFlow()

    // Real-time Protection Toggles
    private val _isWebGuardEnabled = MutableStateFlow(true)
    val isWebGuardEnabled: StateFlow<Boolean> = _isWebGuardEnabled.asStateFlow()

    private val _isAppInstallGuardEnabled = MutableStateFlow(true)
    val isAppInstallGuardEnabled: StateFlow<Boolean> = _isAppInstallGuardEnabled.asStateFlow()

    // Scanner state
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _scanProgress = MutableStateFlow(0f)
    val scanProgress: StateFlow<Float> = _scanProgress.asStateFlow()

    private val _scannedCurrentAppName = MutableStateFlow("")
    val scannedCurrentAppName: StateFlow<String> = _scannedCurrentAppName.asStateFlow()

    private val _scannedApps = MutableStateFlow<List<AppThreatModel>>(emptyList())
    val scannedApps: StateFlow<List<AppThreatModel>> = _scannedApps.asStateFlow()

    private val _selectedFilter = MutableStateFlow(ScanFilter.ALL)
    val selectedFilter: StateFlow<ScanFilter> = _selectedFilter.asStateFlow()

    private val _selectedAppForDetail = MutableStateFlow<AppThreatModel?>(null)
    val selectedAppForDetail: StateFlow<AppThreatModel?> = _selectedAppForDetail.asStateFlow()

    // Link Interceptor State (When user clicks a link in Telegram, SMS, browser)
    private val _interceptedUrl = MutableStateFlow<String?>(null)
    val interceptedUrl: StateFlow<String?> = _interceptedUrl.asStateFlow()

    private val _interceptedResult = MutableStateFlow<UrlScanResult?>(null)
    val interceptedResult: StateFlow<UrlScanResult?> = _interceptedResult.asStateFlow()

    private val _isCheckingInterceptedLink = MutableStateFlow(false)
    val isCheckingInterceptedLink: StateFlow<Boolean> = _isCheckingInterceptedLink.asStateFlow()

    // Alert for newly installed APK
    private val _newInstalledAppAlert = MutableStateFlow<AppThreatModel?>(null)
    val newInstalledAppAlert: StateFlow<AppThreatModel?> = _newInstalledAppAlert.asStateFlow()

    // History flows from Room
    val scanHistory: StateFlow<List<ScanHistoryEntity>> = db.scanHistoryDao()
        .getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var scanJob: Job? = null

    init {
        refreshAudit()
        // Auto-run initial light scan on launch
        startScan(deepScan = false)
    }

    fun setLanguage(lang: Language) {
        _currentLanguage.value = lang
    }

    fun selectTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun toggleWebGuard() {
        _isWebGuardEnabled.value = !_isWebGuardEnabled.value
    }

    fun toggleAppInstallGuard() {
        _isAppInstallGuardEnabled.value = !_isAppInstallGuardEnabled.value
    }

    fun navigateBack(): Boolean {
        if (_interceptedUrl.value != null) {
            _interceptedUrl.value = null
            _interceptedResult.value = null
            return true
        }
        if (_newInstalledAppAlert.value != null) {
            _newInstalledAppAlert.value = null
            return true
        }
        if (_selectedAppForDetail.value != null) {
            _selectedAppForDetail.value = null
            return true
        }
        if (_currentTab.value != MainTab.DASHBOARD) {
            _currentTab.value = MainTab.DASHBOARD
            return true
        }
        return false
    }

    fun setSelectedFilter(filter: ScanFilter) {
        _selectedFilter.value = filter
    }

    fun selectAppForDetail(app: AppThreatModel?) {
        _selectedAppForDetail.value = app
    }

    fun dismissNewAppAlert() {
        _newInstalledAppAlert.value = null
    }

    fun dismissLinkIntercept() {
        _interceptedUrl.value = null
        _interceptedResult.value = null
    }

    // Intercept Link from Telegram, SMS, WhatsApp or other browsers
    fun handleIncomingUrl(url: String, context: Context) {
        if (!_isWebGuardEnabled.value) {
            openInBrowserDirectly(context, url)
            return
        }

        _interceptedUrl.value = url
        _isCheckingInterceptedLink.value = true

        viewModelScope.launch {
            val result = phishingScanner.analyzeUrl(url, _currentLanguage.value)
            _interceptedResult.value = result
            _isCheckingInterceptedLink.value = false

            // If completely SAFE, auto-redirect after brief safety notification
            if (result.status == UrlRiskStatus.SAFE) {
                delay(1200)
                // If user didn't dismiss
                if (_interceptedUrl.value == url) {
                    openInBrowserDirectly(context, url)
                    _interceptedUrl.value = null
                    _interceptedResult.value = null
                }
            }
            // If DANGEROUS / PHISHING: Keep dialog visible and BLOCK REDIRECT!
        }
    }

    fun proceedToBrowserRegardless(context: Context, url: String) {
        openInBrowserDirectly(context, url)
        dismissLinkIntercept()
    }

    private fun openInBrowserDirectly(context: Context, url: String) {
        try {
            var formatted = url.trim()
            if (!formatted.startsWith("http://") && !formatted.startsWith("https://")) {
                formatted = "https://$formatted"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formatted)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // fallback
        }
    }

    // Handle new APK installed event
    fun handleNewPackageInstalled(packageName: String) {
        viewModelScope.launch {
            val lang = _currentLanguage.value
            val results = appScanner.scanInstalledApps(lang) { _, _, _ -> }
            _scannedApps.value = results
            val app = results.find { it.packageName == packageName }
            if (app != null && (app.riskLevel == RiskLevel.THREAT_CONFIRMED || app.riskLevel == RiskLevel.SUSPICIOUS)) {
                _newInstalledAppAlert.value = app
            }
            refreshAudit()
        }
    }

    fun refreshAudit() {
        val currentThreats = _scannedApps.value.count { it.riskLevel == RiskLevel.THREAT_CONFIRMED }
        val currentSuspicious = _scannedApps.value.count { it.riskLevel == RiskLevel.SUSPICIOUS }
        val currentSensitive = _scannedApps.value.count { it.riskLevel == RiskLevel.SENSITIVE_PERMISSIONS }
        val totalApps = _scannedApps.value.size

        val newState = deviceAuditor.auditDeviceState(
            threatCount = currentThreats,
            suspiciousCount = currentSuspicious,
            sensitivePermCount = currentSensitive,
            totalAppsScanned = totalApps,
            lastScanTime = _deviceState.value.lastScanTimestamp
        )
        _deviceState.value = newState
    }

    fun startScan(deepScan: Boolean = true) {
        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            _isScanning.value = true
            _scanProgress.value = 0f
            _scannedCurrentAppName.value = "Tekshirilmoqda..."

            val lang = _currentLanguage.value
            val results = appScanner.scanInstalledApps(lang) { current, total, appName ->
                _scanProgress.value = if (total > 0) current.toFloat() / total.toFloat() else 0f
                _scannedCurrentAppName.value = appName
            }

            _scannedApps.value = results
            val threatCount = results.count { it.riskLevel == RiskLevel.THREAT_CONFIRMED }
            val suspiciousCount = results.count { it.riskLevel == RiskLevel.SUSPICIOUS }
            val sensitiveCount = results.count { it.riskLevel == RiskLevel.SENSITIVE_PERMISSIONS }

            val timestamp = System.currentTimeMillis()
            val audited = deviceAuditor.auditDeviceState(
                threatCount = threatCount,
                suspiciousCount = suspiciousCount,
                sensitivePermCount = sensitiveCount,
                totalAppsScanned = results.size,
                lastScanTime = timestamp
            )
            _deviceState.value = audited

            val scanTypeTitle = if (deepScan) AppStrings.deepScan(lang) else AppStrings.quickScan(lang)
            val summary = if (threatCount > 0) {
                "$threatCount ta xavf aniqlandi"
            } else if (suspiciousCount > 0) {
                "$suspiciousCount ta shubhali ilova"
            } else {
                "Qurilma toza"
            }

            db.scanHistoryDao().insertScan(
                ScanHistoryEntity(
                    timestamp = timestamp,
                    scanType = scanTypeTitle,
                    totalAppsScanned = results.size,
                    threatsCount = threatCount,
                    suspiciousCount = suspiciousCount,
                    securityScore = audited.overallSecurityScore,
                    summaryText = summary
                )
            )

            _isScanning.value = false
            _scanProgress.value = 1f
        }
    }

    fun cancelScan() {
        scanJob?.cancel()
        _isScanning.value = false
        _scannedCurrentAppName.value = ""
    }

    // 1-Tap Threat Purge: Launches official uninstall for all dangerous apps
    fun purgeAllThreats(context: Context) {
        val dangerousApps = _scannedApps.value.filter {
            (it.riskLevel == RiskLevel.THREAT_CONFIRMED || it.riskLevel == RiskLevel.SUSPICIOUS) && !it.isSystemApp
        }
        for (app in dangerousApps) {
            safeUninstallApp(context, app.packageName)
        }
    }

    fun testAmtsoPhishing(context: Context) {
        val testUrl = "https://www.amtso.org/check-android-phishing-page/"
        handleIncomingUrl(testUrl, context)
    }

    fun openAccessibilitySettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // fallback
        }
    }

    fun openDefaultAppsSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // fallback
        }
    }

    fun safeUninstallApp(context: Context, packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.parse("package:$packageName")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            openAppSettings(context, packageName)
        }
    }

    fun openAppSettings(context: Context, packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:$packageName")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // fallback
        }
    }
}
