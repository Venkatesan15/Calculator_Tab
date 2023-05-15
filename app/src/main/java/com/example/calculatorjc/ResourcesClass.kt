package com.example.calculatorjc

import android.content.Context
import android.content.res.Configuration

class ResourcesClass {

    companion object {

        const val calculator = "Calculator"

        const val numberOne = "Number One"
        const val numberTwo = "Number Two"

        const val reset = "Reset"

        fun isTablet(context: Context): Boolean {
            return if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                context.resources.configuration.screenWidthDp > 900
            } else {
                context.resources.configuration.screenWidthDp > 600
            }
        }
    }
}
enum class Actions
{
    Add,
    Subtract,
    Multiply,
    Division,
}