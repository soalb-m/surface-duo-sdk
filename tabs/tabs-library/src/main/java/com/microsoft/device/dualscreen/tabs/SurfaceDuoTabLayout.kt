/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.device.dualscreen.tabs

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import com.microsoft.device.dualscreen.core.DisplayPosition
import com.microsoft.device.dualscreen.core.ScreenHelper
import com.microsoft.device.dualscreen.core.ScreenMode
import com.microsoft.device.dualscreen.core.isPortrait
import com.microsoft.device.dualscreen.core.isSpannedInDualScreen

/**
 * A sub class of the Tab layout that positions it's children on the start, end or both screens when the application is spanned on both screens.
 */
class SurfaceDuoTabLayout : TabLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int = 0) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        attributeSet?.let { initialize(it) }
        ScreenHelper.getHinge(context)?.let {
            singleScreenWidth = it.left
        }

        ScreenHelper.getWindowRect(context).let {
            totalScreenWidth = it.right
        }
        setInitialPosition()
        updateBackground()
    }

    private var singleScreenWidth = -1
    private var totalScreenWidth = -1

    private var displayPosition = DisplayPosition.DUAL
    private var screenMode = ScreenMode.DUAL_SCREEN

    private var setEmptyAreaToTransparent = false
    private var initialBackground: Drawable? = null

    var surfaceDuoDisplayPosition: DisplayPosition
        get() {
            return displayPosition
        }
        set(value) {
            displayPosition = value
            requestLayout()
            updateBackground()
        }

    var surfaceDuoTransparentBackground: Boolean
        get() {
            return setEmptyAreaToTransparent
        }
        set(value) {
            setEmptyAreaToTransparent = value
            updateBackground()
        }

    private fun initialize(attrs: AttributeSet) {
        val styledAttributes =
            context.theme.obtainStyledAttributes(attrs, R.styleable.SurfaceDuoTabLayout, 0, 0)
        try {
            displayPosition = DisplayPosition.fromId(
                styledAttributes.getInt(
                    R.styleable.SurfaceDuoTabLayout_display_position,
                    DisplayPosition.DUAL.ordinal
                )
            )
            screenMode = ScreenMode.fromId(
                styledAttributes.getResourceId(
                    R.styleable.SurfaceDuoTabLayout_tools_application_mode,
                    ScreenMode.DUAL_SCREEN.ordinal
                )
            )
            setEmptyAreaToTransparent =
                styledAttributes.getBoolean(
                    R.styleable.SurfaceDuoTabLayout_setEmptyAreaToTransparent,
                    setEmptyAreaToTransparent
                )
        } finally {
            styledAttributes.recycle()
        }
    }

    private fun setInitialPosition() {
        val view = getChildAt(0)
        val params = view.layoutParams as LayoutParams

        when (displayPosition) {
            DisplayPosition.DUAL -> {
                params.width = LayoutParams.MATCH_PARENT
            }

            DisplayPosition.START -> {
                params.width = singleScreenWidth
                params.gravity = Gravity.START
            }

            DisplayPosition.END -> {
                params.width = singleScreenWidth
                params.gravity = Gravity.END
            }
        }
        view.layoutParams = params
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (!isSpannedInDualScreen(screenMode) || isPortrait()) {
            return
        }

        if (childCount == 1) {
            adjustChildMeasurements(heightMeasureSpec)
        }
    }

    private fun adjustChildMeasurements(heightMeasureSpec: Int) {
        val desiredLength = when (displayPosition) {
            DisplayPosition.START -> singleScreenWidth
            DisplayPosition.END -> singleScreenWidth
            else -> totalScreenWidth
        }

        val gravity = when (displayPosition) {
            DisplayPosition.START -> Gravity.START
            DisplayPosition.END -> Gravity.END
            else -> Gravity.CENTER
        }

        val child = getChildAt(0)
        val remeasure = child.measuredWidth != desiredLength
        if (remeasure) {
            val childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(
                heightMeasureSpec,
                paddingTop + paddingBottom,
                child.layoutParams.height
            )
            val childWidthMeasureSpec =
                MeasureSpec.makeMeasureSpec(desiredLength, MeasureSpec.EXACTLY)
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
            val params = child.layoutParams as LayoutParams
            params.gravity = gravity
        }
    }

    override fun setBackground(background: Drawable?) {
        if (initialBackground == null) {
            initialBackground = background
        }
        super.setBackground(background)
    }

    private fun updateBackground() {
        if (!isSpannedInDualScreen(screenMode) || isPortrait() || childCount != 1 || !setEmptyAreaToTransparent) {
            if (background != initialBackground) {
                background = initialBackground
            }
        } else {
            background = createHalfTransparentBackground()
        }
    }

    private fun createHalfTransparentBackground(): LayerDrawable {
        val transparentBackground =
            ContextCompat.getDrawable(context, R.drawable.background_transparent)
        val finalBackground = LayerDrawable(arrayOf(initialBackground, transparentBackground))

        if (displayPosition == DisplayPosition.START) {
            finalBackground.setLayerInset(0, 0, 0, singleScreenWidth, 0)
            finalBackground.setLayerInset(1, singleScreenWidth, 0, 0, 0)
        }

        if (displayPosition == DisplayPosition.END) {
            finalBackground.setLayerInset(0, singleScreenWidth, 0, 0, 0)
            finalBackground.setLayerInset(1, 0, 0, singleScreenWidth, 0)
        }
        return finalBackground
    }
}