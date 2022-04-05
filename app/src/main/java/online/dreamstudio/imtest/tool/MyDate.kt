package online.dreamstudio.imtest.tool

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

object MyDate{
    //设置日期格式
    @SuppressLint("SimpleDateFormat")
    var dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    //返回直观的日期格式
    //2021-12-01 01:21:16
    fun getSimpleDate():String{
        return dateFormat.format(Date())
    }

    //转化成时间戳
    //即86400000为一天
    //1000为1秒
    fun timeToLong(sTime:String):Long{
        return dateFormat.parse(sTime).time
    }
}