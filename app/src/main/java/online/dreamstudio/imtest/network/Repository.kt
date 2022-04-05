package online.dreamstudio.imtest.network


import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import online.dreamstudio.imtest.bean.Chat
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

object Repository {

    fun getChat(time:String,count:Int) = fire(Dispatchers.IO) {
        val fundChatResponse = FundTestNetwork.getChat(time, count)
        Result.success(fundChatResponse)
    }

    fun getSig(userId:String,expire:Long) = fire(Dispatchers.IO){
        val sig = FundTestNetwork.getSig(userId, expire)
        Result.success(sig)
    }

    private fun <T> fire(context: CoroutineContext,block:suspend () -> Result<T>) = liveData<Result<T>>(context) {
        val result = try {
            block()
        }catch (e:Exception){
            Result.failure<T>(e)
        }
        emit(result)
    }
}