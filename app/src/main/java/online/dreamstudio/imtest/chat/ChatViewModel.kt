package online.dreamstudio.imtest.chat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import online.dreamstudio.imtest.base.BaseViewModel
import online.dreamstudio.imtest.bean.Chat
import online.dreamstudio.imtest.bean.Message
import online.dreamstudio.imtest.bean.MessageRequest
import online.dreamstudio.imtest.network.Repository

class ChatViewModel: BaseViewModel() {
    val chatList = ArrayList<Message>()
    private val getChatLiveData = MutableLiveData<MessageRequest>()
    //数据对象用于观察数据是否发生改变
    private val messageLiveData = MutableLiveData<Chat>()
    val chatListData = Transformations.switchMap(getChatLiveData){ message ->
        Repository.getChat(message.time,message.count)
    }
    fun getMessage(time:String, count:Int){
        getChatLiveData.value = MessageRequest(time, count)
    }
    fun sendMessage(chat: Chat){
        messageLiveData.value = chat
    }
}