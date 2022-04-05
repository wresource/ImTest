package online.dreamstudio.imtest.tool

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context


class IMTestApplication : Application() {
    companion object{
        /**
         * 用于全局获取上下文
         */
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var name:String
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        name = "me"
    }
}