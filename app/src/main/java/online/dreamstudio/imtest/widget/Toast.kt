package online.dreamstudio.imtest.widget

import android.widget.Toast
import online.dreamstudio.imtest.tool.IMTestApplication

fun String.showToast(duration:Int = Toast.LENGTH_SHORT){
    Toast.makeText(IMTestApplication.context,this,duration).show()
}