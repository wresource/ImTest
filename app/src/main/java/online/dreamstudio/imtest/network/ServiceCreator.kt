package online.dreamstudio.imtest.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import online.dreamstudio.imtest.constant.MyProperty
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ServiceCreator {
    private const val BASE_URL = MyProperty.myUrl

    //这里是服务端生成字符本地的gson不能接受，所以需要做一些处理
    var gson: Gson = GsonBuilder()
        .setLenient()
        .create()
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun <T> create (serviceClass:Class<T>):T {
        return retrofit.create(serviceClass)
    }
    inline fun <reified T>create():T = create(T::class.java)
}