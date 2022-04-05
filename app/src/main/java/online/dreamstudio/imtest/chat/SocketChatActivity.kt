package online.dreamstudio.imtest.chat

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import online.dreamstudio.imtest.R
import online.dreamstudio.imtest.base.BaseActivity
import online.dreamstudio.imtest.bean.Chat
import online.dreamstudio.imtest.bean.Message
import online.dreamstudio.imtest.constant.MyProperty
import online.dreamstudio.imtest.databinding.ActivitySocketChatBinding
import online.dreamstudio.imtest.tool.IMTestApplication
import online.dreamstudio.imtest.tool.MyDate
import online.dreamstudio.imtest.widget.showToast
import online.dreamstudio.imtest.widget.toMyJson
import online.dreamstudio.imtest.widget.toMyObject
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI


class SocketChatActivity :BaseActivity<ActivitySocketChatBinding>(ActivitySocketChatBinding::inflate) {
    private val viewModel by lazy { ViewModelProvider(this)[ChatViewModel::class.java] }
    private lateinit var adapter: ChatAdapter
    private var lastMessage:Message = Message("")
    private val fail = 0
    private val up = 1
    private val down = 2
    private val newMessage = 3
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: android.os.Message) {
            when (msg.what) {
                up -> {
                    viewBinding.chatRecyclerview.scrollToPosition(0)
                    viewBinding.swipeRefresh.isRefreshing = false
                }
                down ->{
                    viewBinding.chatRecyclerview.scrollToPosition(viewModel.chatList.size-1)
                }
                newMessage ->{
                    adapter.notifyItemChanged(viewModel.chatList.size-1)
                    viewBinding.chatRecyclerview.scrollToPosition(viewModel.chatList.size-1)
                }
                fail -> {
                    viewBinding.swipeRefresh.isRefreshing = false
                    "刷新失败请检查网络".showToast()
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutManager = LinearLayoutManager(this)
        viewBinding.chatRecyclerview.layoutManager = layoutManager
        adapter = ChatAdapter(viewModel.chatList)
        viewBinding.chatRecyclerview.adapter = adapter


        //初始化界面
        refreshData()

        //创建一个用户的 socket用于消息的发送
        val userId = IMTestApplication.name
        val userSocket = object :WebSocketClient(URI("${MyProperty.mySocketUrl}/imserver/${userId}")){
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.e("webSocket","webSocket已经打开")
                runOnUiThread {
                    "连接成功".showToast()
                    viewBinding.notice.visibility = View.GONE
                }

            }

            //监听消息
            override fun onMessage(message: String?) {
                Log.e("webSocket","新消息为${message}")
                val msg = message?.toMyObject<Message>()?.get(0)!!
                viewModel.chatList.add(msg)


                //这里由于是子线程执行的，所以需要message
                val handleMessage = android.os.Message()
                handleMessage.what = newMessage
                handler.sendMessage(handleMessage)
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.e("webSocket","断开连接,错误码为${code},理由是${reason},是否远程连接:${remote}")
                runOnUiThread {
                    "断开连接".showToast()
                    viewBinding.notice.visibility = View.VISIBLE
                }
            }

            override fun onError(ex: Exception?) {
                Log.e("webSocket","发生错误，错误为:${ex}")
            }

        }
        userSocket.connect()
        //发送消息
        viewBinding.sentMessage.setOnClickListener {
            if (viewBinding.chatMessage.text.toString().isNotBlank()){
                val message = Chat(userId,viewBinding.chatMessage.text.toString()).toMyJson()

                //在网络的情况下发送会失败，这里做个异常捕捉
                try {
                    userSocket.send(message)
                    viewBinding.chatMessage.setText("")
                }catch (e:Exception){
                    e.printStackTrace()
                    "发送失败,请确保网络是否顺畅".showToast()
                }
            }else{
                "输入为空，无法发送".showToast()
            }
        }

        //拉取历史记录
        viewBinding.swipeRefresh.setColorSchemeResources(R.color.fund_blue)
        viewBinding.swipeRefresh.setOnRefreshListener {
            refreshData(lastMessage)
        }

        //这里做一个重新连接判断
        viewBinding.netRemind.setOnClickListener {
            "正在重新连接中".showToast()
            userSocket.reconnect()
            viewBinding.chatRecyclerview.scrollToPosition(viewModel.chatList.size - 1)
        }
        viewModel.chatListData.observe(this){ result ->
            val messageResponse = result.getOrNull()
            if (messageResponse != null ){
                if(messageResponse.chatList.isEmpty()){
                    "没有更多消息了".showToast()
                    val msg = android.os.Message()
                    msg.what = up
                    handler.sendMessage(msg)
                }else{
                    for(msg in messageResponse.chatList){
                        viewModel.chatList.add(0,msg)
                        adapter.notifyItemInserted(0)
                    }

                    //这里的lastMessage其实是个标记，初始设置成空字符串
                    if (lastMessage.message != ""){
                        val msg = android.os.Message()
                        msg.what = up
                        handler.sendMessage(msg)
                    }else{
                        val msg = android.os.Message()
                        msg.what = down
                        handler.sendMessage(msg)
                    }
                    lastMessage = messageResponse.chatList[messageResponse.chatList.size-1]
                    "刷新成功".showToast()
                }
            }else{
                "刷新失败".showToast()
                val msg = android.os.Message()
                msg.what = fail
                handler.sendMessage(msg)
            }
        }
    }
    private fun refreshData(last: Message? = null){
        if (last == null){
            viewModel.getMessage(MyDate.getSimpleDate(),20)
        }else{
            viewModel.getMessage(last.time,20)
        }
    }
}