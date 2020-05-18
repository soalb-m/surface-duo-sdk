/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.device.surfaceduo.sample_surfaceduo_layout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Remove this
//        setContentView(R.layout.empty_activity)
//        findViewById<FrameLayout>(R.id.parent).addView(
//            SurfaceDuoLayout(this, SurfaceDuoLayout.Config().apply {
//                singleScreenLayoutId = R.layout.single_screen
//                dualScreenStartLayoutId = R.layout.dual_screen_start
//                dualScreenEndLayoutId = R.layout.dual_screen_end
//                dualLandscapeSingleLayoutId = R.layout.single_screen
//            })
//        )

        setContentView(R.layout.activity_main)
        // Replace configuration
//        findViewById<SurfaceDuoLayout>(R.id.surface_duo_layout)
//            .newConfigCreator()
//            .dualScreenStartLayoutId(R.layout.dual_screen_start)
//            .dualScreenEndLayoutId(R.layout.dual_screen_end)
//            .reInflate()

        // Update configuration
//        findViewById<SurfaceDuoLayout>(R.id.surface_duo_layout)
//            .updateConfigCreator()
//            .dualScreenStartLayoutId(R.layout.dual_screen_start)
//            .dualScreenEndLayoutId(R.layout.dual_screen_end)
//            .reInflate()
    }
}
