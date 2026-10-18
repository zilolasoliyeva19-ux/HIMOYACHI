package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.Language
import com.example.model.UrlRiskStatus
import com.example.scanner.DeviceAuditEngine
import com.example.scanner.PhishingScannerEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app_name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("HIMOYACHI", appName)
    }

    @Test
    fun `device audit engine returns valid security state`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val engine = DeviceAuditEngine(context)
        val state = engine.auditDeviceState(threatCount = 0, suspiciousCount = 0)
        assertNotNull(state)
        assertTrue(state.overallSecurityScore in 0..100)
    }

    @Test
    fun `phishing engine detects brand spoofing and dangerous TLD`() {
        val engine = PhishingScannerEngine()
        val result = engine.analyzeUrl("https://payme-bonus-check.xyz/card", Language.UZBEK)
        assertEquals(UrlRiskStatus.PHISHING_THREAT, result.status)
        assertTrue(result.riskScore >= 60)
        assertTrue(result.detectedRedFlags.isNotEmpty())
    }

    @Test
    fun `phishing engine detects AMTSO phishing test page`() {
        val engine = PhishingScannerEngine()
        val result = engine.analyzeUrl("https://www.amtso.org/check-android-phishing-page/", Language.UZBEK)
        assertEquals(UrlRiskStatus.PHISHING_THREAT, result.status)
        assertTrue(result.riskScore >= 90)
    }

    @Test
    fun `phishing engine approves legitimate official domain`() {
        val engine = PhishingScannerEngine()
        val result = engine.analyzeUrl("https://click.uz", Language.UZBEK)
        assertEquals(UrlRiskStatus.SAFE, result.status)
    }
}
