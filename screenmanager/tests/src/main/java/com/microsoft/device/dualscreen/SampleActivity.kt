/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.device.dualscreen

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Simple activity used for testing purpose.
 */
class SampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        ScreenManagerProvider.getScreenManager().onConfigurationChanged()
    }
}