package online.dreamstudio.imtest.network


import online.dreamstudio.imtest.bean.MessageResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object FundTestNetwork {

    //获取最新消息
    private val chatService = ServiceCreator.create<ChatService>()
    suspend fun getChat(time:String,count:Int): MessageResponse = chatService.getChat(time, count).await()
    suspend fun getSig(userId:String,expire:Long):String = chatService.getSig(userId, expire).await()
    private suspend fun <T> Call<T>.await():T{
        return suspendCoroutine { continuation ->
            enqueue(object :Callback<T>{
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null)continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }
                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}