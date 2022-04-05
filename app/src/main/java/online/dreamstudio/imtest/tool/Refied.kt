package online.dreamstudio.imtest.tool

import android.content.Context
import android.content.Intent

inline fun <reified T> startActivity(context: Context, block: Intent.() -> Unit) {
    val intent = Intent(context, T::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.block()
    context.startActivity(intent)
}

