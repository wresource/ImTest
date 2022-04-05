package online.dreamstudio.imtest.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import online.dreamstudio.imtest.tool.ActivityCollector
import online.dreamstudio.imtest.tool.LogUtil

abstract class BaseActivity<T: ViewBinding>(val inflate:(inflate:LayoutInflater) -> T):AppCompatActivity(){
    protected lateinit var viewBinding: T
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtil.d("CreateBaseActivity",javaClass.simpleName)
        ActivityCollector.addActivity(this)
        viewBinding  = inflate(layoutInflater)
        setContentView(viewBinding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onStart() {
        super.onStart()
        LogUtil.d("StartBaseActivity",javaClass.simpleName)
    }

    override fun onStop() {
        super.onStop()
        LogUtil.d("StopBaseActivity",javaClass.simpleName)
    }

    override fun onPause() {
        super.onPause()
        LogUtil.d("PauseBaseActivity",javaClass.simpleName)
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.d("DestroyBaseActivity",javaClass.simpleName)
        ActivityCollector.removeActivity(this)
    }
}