package com.azbouki.supporttool.sdk.live.controller

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.Button
import com.azbouki.supporttool.sdk.SdkState
import com.faendir.rhino_android.RhinoAndroidHelper
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.ScriptableObject


class JSExecutor(private val activity: Activity) {
//    val testCode = """
//    var findViewById = viewFinder.findViewById.bind(viewFinder);
//    const view = findViewById('startSupportToolBtn');
//    view.setText('pesho');
//    """.trimIndent()

    val presets = """
        var findViewById = viewFinder.findViewById.bind(viewFinder);
        
    """.trimIndent()

    fun executeJSCode(jsCode: String): ExecutionResult {
        val rhinoAndroidHelper = RhinoAndroidHelper(activity)

        val jsContext: Context = rhinoAndroidHelper.enterContext().apply {
            optimizationLevel = -1 // just interpret JS
            languageVersion = Context.VERSION_ES6
        }

        val scope: Scriptable = jsContext.initStandardObjects()

        ViewFinder(activity).let {
            ScriptableObject.putProperty(scope, "viewFinder", Context.javaToJS(it, scope))
        }

        return try {
            val result: Any = jsContext.evaluateString(
                scope,
                presets + jsCode,
                "CodeSnippet",
                1,
                null
            )

            if (result is Throwable) ExecutionResult(true, result.toString())
            else ExecutionResult(false, result.toString())
        } catch (throwable: Throwable) {
            ExecutionResult(true, throwable.toString())
        }
    }

    data class ExecutionResult(val hasError: Boolean, val result: String)
}

class ViewFinder(private val activity: Activity) {
    fun findViewById(viewId: String): View? {
        val id = activity.resources.getIdentifier(viewId, "id", activity.packageName)
        return activity.findViewById<Button>(id)
    }
}