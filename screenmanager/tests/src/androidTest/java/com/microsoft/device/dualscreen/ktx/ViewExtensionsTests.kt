/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.device.dualscreen.ktx

import androidx.test.filters.MediumTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import com.google.common.truth.Truth.assertThat
import com.microsoft.device.dualscreen.SampleActivity
import com.microsoft.device.dualscreen.ScreenManagerProvider
import com.microsoft.device.dualscreen.ScreenMode.DUAL_SCREEN
import com.microsoft.device.dualscreen.ScreenMode.SINGLE_SCREEN
import com.microsoft.device.dualscreen.isPortrait
import com.microsoft.device.dualscreen.isSpannedInDualScreen
import com.microsoft.device.dualscreen.utils.ScreenInfoListenerImpl
import com.microsoft.device.dualscreen.utils.setOrientationRight
import com.microsoft.device.dualscreen.utils.switchFromSingleToDualScreen
import com.microsoft.device.dualscreen.utils.unfreezeRotation
import kotlinx.android.synthetic.main.activity_sample.*
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.reset
import org.mockito.Mockito.spy
import org.mockito.Mockito.`when` as whenz

@MediumTest
@RunWith(AndroidJUnit4ClassRunner::class)
class ViewExtensionsTests {
    @Rule
    @JvmField
    var rule: ActivityTestRule<SampleActivity> = ActivityTestRule(SampleActivity::class.java, false, false)
    private var screenInfoListener = ScreenInfoListenerImpl()

    @After
    fun tearDown() {
        val screenManager = ScreenManagerProvider.getScreenManager()
        screenManager.removeScreenInfoListener(screenInfoListener)
        screenInfoListener.resetScreenInfo()
    }

    @Test
    fun isSpannedInDualScreenTest() {
        assertThat(screenInfoListener.screenInfo).isNull()
        ScreenManagerProvider.getScreenManager().addScreenInfoListener(screenInfoListener)

        rule.launchActivity(null)
        screenInfoListener.waitForScreenInfoChanges()
        val view = rule.activity.simple_view
        val spyView = spy(view)

        assertThat(screenInfoListener.screenInfo).isNotNull()
        assertThat(spyView.isSpannedInDualScreen(SINGLE_SCREEN, screenInfoListener.screenInfo!!)).isFalse()
        assertThat(spyView.isSpannedInDualScreen(DUAL_SCREEN, screenInfoListener.screenInfo!!)).isFalse()

        whenz(spyView.isInEditMode).thenReturn(true)
        assertThat(spyView.isSpannedInDualScreen(SINGLE_SCREEN, screenInfoListener.screenInfo!!)).isFalse()
        assertThat(spyView.isSpannedInDualScreen(DUAL_SCREEN, screenInfoListener.screenInfo!!)).isTrue()

        reset(spyView)
        screenInfoListener.resetScreenInfo()
        screenInfoListener.resetScreenInfoCounter()

        switchFromSingleToDualScreen()
        screenInfoListener.waitForScreenInfoChanges()
        assertThat(spyView.isSpannedInDualScreen(SINGLE_SCREEN, screenInfoListener.screenInfo!!)).isTrue()
        assertThat(spyView.isSpannedInDualScreen(DUAL_SCREEN, screenInfoListener.screenInfo!!)).isTrue()

        whenz(spyView.isInEditMode).thenReturn(true)
        assertThat(spyView.isSpannedInDualScreen(SINGLE_SCREEN, screenInfoListener.screenInfo!!)).isFalse()
        assertThat(spyView.isSpannedInDualScreen(DUAL_SCREEN, screenInfoListener.screenInfo!!)).isTrue()

        screenInfoListener.resetScreenInfo()
        screenInfoListener.resetScreenInfoCounter()
    }

    @Test
    fun isInPortraitTest() {
        assertThat(screenInfoListener.screenInfo).isNull()
        ScreenManagerProvider.getScreenManager().addScreenInfoListener(screenInfoListener)

        rule.launchActivity(null)
        screenInfoListener.waitForScreenInfoChanges()
        val view = rule.activity.simple_view

        assertThat(view.isPortrait()).isTrue()
        screenInfoListener.resetScreenInfo()
        screenInfoListener.resetScreenInfoCounter()

        setOrientationRight()
        screenInfoListener.waitForScreenInfoChanges()
        assertThat(view.isPortrait()).isFalse()
        screenInfoListener.resetScreenInfo()
        screenInfoListener.resetScreenInfoCounter()

        unfreezeRotation()
    }
}