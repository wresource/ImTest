package online.dreamstudio.imtest.widget


import com.google.gson.Gson

fun Any.toMyJson():String{
    return Gson().toJson(this)
}