package online.dreamstudio.imtest.chat

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tencent.imsdk.v2.*
import online.dreamstudio.imtest.R
import online.dreamstudio.imtest.base.BaseActivity
import online.dreamstudio.imtest.bean.Message
import online.dreamstudio.imtest.databinding.FragmentChatBinding
import online.dreamstudio.imtest.tool.IMTestApplication
import online.dreamstudio.imtest.widget.showToast
import online.dreamstudio.imtest.widget.toMyJson
import online.dreamstudio.imtest.widget.toMyObject


class ChatActivity: BaseActivity<FragmentChatBinding>(FragmentChatBinding::inflate){
    private val viewModel by lazy { ViewModelProvider(this)[ChatViewModel::class.java] }
    private lateinit var adapter: ChatAdapter
    private lateinit var lastMessage:V2TIMMessage
    private val up = 1
    private val down = 2
    private val fail = 0
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
                fail -> {
                    "刷新失败请检查网络".showToast()
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //recycleView初始化
        val layoutManager = LinearLayoutManager(this)
        viewBinding.chatRecyclerview.layoutManager = layoutManager
        adapter = ChatAdapter(viewModel.chatList)
        viewBinding.chatRecyclerview.adapter = adapter

        //初始化聊天界面
        refreshData()

        //设置新消息监听
        V2TIMManager.getMessageManager().addAdvancedMsgListener(object:V2TIMAdvancedMsgListener(){
            override fun onRecvNewMessage(msg: V2TIMMessage?) {
                Log.e("im","新消息${msg?.customElem}")

                //这里针对多种消息类型有不同的处理方法
                when(msg?.elemType){
                    V2TIMMessage.V2TIM_ELEM_TYPE_CUSTOM ->{
                        val message = msg.customElem?.data?.let { String(it).toMyObject<Message>() }?.get(0)
                        if (message != null) {
                            viewModel.chatList.add(message)
                        }
                    }
                    V2TIMMessage.V2TIM_ELEM_TYPE_TEXT ->{
                        val message = msg.textElem.text.toMyObject<Message>()[0]
                        viewModel.chatList.add(message)
                    }
                    else -> {
                        "暂时不支持此消息的接收".showToast()
                        Log.e("im","${msg?.elemType}")
                    }
                }
                adapter.notifyItemChanged(viewModel.chatList.size-1)
                viewBinding.chatRecyclerview.scrollToPosition(viewModel.chatList.size-1)
            }
        })

        //发送消息
        viewBinding.sentMessage.setOnClickListener {
            val myMessage = Message(viewBinding.chatMessage.text.toString(),IMTestApplication.name).toMyJson()
            val messageCus= V2TIMManager.getMessageManager().createCustomMessage(myMessage.toByteArray())
            V2TIMManager.getMessageManager().sendMessage(messageCus,"","meeting1",1,false,null,object:V2TIMSendCallback<V2TIMMessage>{
                override fun onSuccess(message: V2TIMMessage?) {
                    Log.e("im","发送成功，内容为:${message?.customElem}")
                    val message1 = message?.customElem?.data?.let { String(it).toMyObject<Message>() }?.get(0)
                    if (message1 != null) {
                        viewModel.chatList.add(message1)
                    }
                    adapter.notifyDataSetChanged()
                    adapter.notifyItemChanged(viewModel.chatList.size-1)
                    viewBinding.chatRecyclerview.scrollToPosition(viewModel.chatList.size-1)
                    viewBinding.chatMessage.setText("")
                    "发送成功".showToast()
                }

                override fun onError(p0: Int, p1: String?) {
                    Log.e("im","错误码为:${p0},具体错误:${p1}")
                    "发送失败".showToast()
                }

                override fun onProgress(p0: Int) {
                    Log.e("im","处理进度:${p0}")
                }
            })
        }

        //拉取历史记录
        viewBinding.swipeRefresh.setColorSchemeResources(R.color.fund_blue)
        viewBinding.swipeRefresh.setOnRefreshListener {
            refreshData(up,lastMessage)
        }
    }
    private fun refreshData(way:Int = down,last: V2TIMMessage? = null){
        val msg = android.os.Message()
        msg.what = way
        V2TIMManager.getMessageManager().getGroupHistoryMessageList("meeting1",20,last,object:V2TIMValueCallback<List<V2TIMMessage>>{
            override fun onSuccess(p0: List<V2TIMMessage>?) {
                if (p0 != null) {
                    if (p0.isEmpty()){
                        Log.e("im","没有更多消息了")
                        "没有更多消息了".showToast()
                    }else {
                        //记录最后一条消息
                        lastMessage = p0[p0.size - 1]
                        for (msgIndex in p0.indices) {
                            //解析消息
                            when(p0[msgIndex].elemType){
                                V2TIMMessage.V2TIM_ELEM_TYPE_CUSTOM ->{
                                    Log.e("im",String(p0[msgIndex].customElem.data))
                                    try{
                                        //自定义消息，这里理论上也是需要进行各种不同的消息的判断
                                        val message = String(p0[msgIndex].customElem.data).toMyObject<Message>()[0]
                                        viewModel.chatList.add(0, message)
                                    }catch (e:Exception){
                                        e.printStackTrace()
                                        //这里主要针对之前custom消息没有统一进行的一个小bug的处理
                                        val message = Message(String(p0[msgIndex].customElem.data))
                                        viewModel.chatList.add(0, message)
                                    }
                                }
                                V2TIMMessage.V2TIM_ELEM_TYPE_TEXT -> {
                                    val message = Message(p0[msgIndex].textElem.text)
                                    viewModel.chatList.add(0, message)
                                }
                                else -> {
                                    val message = Message("暂时不支持此类型的消息的查看")
                                    viewModel.chatList.add(0, message)
                                }
                            }
                            adapter.notifyItemInserted(0)
                        }
                    }
                    handler.sendMessage(msg)
                }
            }
            override fun onError(p0: Int, p1: String?) {
                Log.e("im","错误码为:${p0},具体错误:${p1}")
                msg.what = fail
                handler.sendMessage(msg)
            }
        })
    }
}