package com.azbouki.supporttool.sdk.utils

import android.content.res.Resources
import android.view.View

fun getResourceId(view: View): String? {
    val viewId = view.id
    val resources = view.context.resources
    var resourceId: String? = ""
    try {
        if (resources != null) {
            resourceId = resources.getResourceEntryName(viewId)
        }
    } catch (e: Resources.NotFoundException) {
        // fall back to hex representation of the id
        resourceId = "0x" + viewId.toString(16)
    }
    return resourceId
}