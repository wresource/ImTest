package online.dreamstudio.imtest.network

import online.dreamstudio.imtest.bean.MessageResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ChatService {

    //获取聊天记录
    @GET("chat/refreshes/{time}/{number}")
    fun getChat(@Path("time")time:String, @Path("number")count:Int): Call<MessageResponse>

    //生成应用凭据
    @GET("imSig/{userId}/{expire}")
    fun getSig(@Path("userId")userId:String,@Path("expire")expire:Long):Call<String>
}