package online.dreamstudio.imtest

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import online.dreamstudio.imtest.base.BaseActivity
import com.tencent.imsdk.v2.*
import online.dreamstudio.imtest.chat.ChatActivity
import online.dreamstudio.imtest.chat.SocketChatActivity
import online.dreamstudio.imtest.constant.MyProperty
import online.dreamstudio.imtest.databinding.ActivityMainBinding
import online.dreamstudio.imtest.tool.IMTestApplication
import online.dreamstudio.imtest.tool.startActivity
import online.dreamstudio.imtest.widget.showToast


class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private val viewModel by lazy { ViewModelProvider(this)[MainActivityViewModel::class.java] }
    private var currentUser = "me"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //1.连接云服务器
        val config = V2TIMSDKConfig()
        config.logLevel = V2TIMSDKConfig.V2TIM_LOG_INFO
        V2TIMManager.getInstance()
            .initSDK(this, MyProperty.imSdkId, config, object : V2TIMSDKListener() {
                override fun onConnecting() {
                    // 正在连接到腾讯云服务器
                    Log.e("im", "正在连接到腾讯云服务器")
                }

                override fun onConnectSuccess() {
                    // 已经成功连接到腾讯云服务器
                    Log.e("im", "已经成功连接到腾讯云服务器")
                }

                override fun onConnectFailed(code: Int, error: String) {
                    // 连接腾讯云服务器失败
                    Log.e("im", "连接腾讯云服务器失败")
                }
            })

        //创建用户并生成相应的凭据
        viewBinding.meLogin.setOnClickListener {
            //me用户登录
            currentUser = "me"
            IMTestApplication.name = currentUser
            viewModel.getSig(currentUser,86400)
        }
        viewBinding.youLogin.setOnClickListener {
            currentUser = "you"
            IMTestApplication.name = currentUser
            viewModel.getSig(currentUser,86400)
        }
        viewBinding.masterLogin.setOnClickListener {
            //master用户登录
            currentUser = "master"
            IMTestApplication.name = currentUser
            viewModel.getSig(currentUser,86400)
        }
        viewBinding.createGroup.setOnClickListener {
            //master用户创建群聊，并添加自己
            val group = V2TIMGroupInfo()
            group.groupName = "VFun"
            group.groupType = "Meeting"
            group.introduction = "its VFund's,you can share your story about fund"
            group.groupID = MyProperty.groupName
            val memberInfoList: MutableList<V2TIMCreateGroupMemberInfo> = ArrayList()
            val memberA = V2TIMCreateGroupMemberInfo()
            memberA.setUserID("master")
            memberInfoList.add(memberA)
            V2TIMManager.getGroupManager().createGroup(
                group, memberInfoList, object : V2TIMValueCallback<String?> {
                    override fun onError(code: Int, desc: String) {
                        // 创建失败
                        Log.e("im","创建失败${code},详情:${desc}")
                    }

                    override fun onSuccess(groupID: String?) {
                        // 创建成功
                        Log.e("im","创建成功,群号为${groupID}")
                    }
                })
        }
        viewBinding.socketChat1.setOnClickListener {
            IMTestApplication.name = "1234"
            startActivity<SocketChatActivity>(IMTestApplication.context){}
        }
        viewBinding.socketChat2.setOnClickListener {
            IMTestApplication.name = "12345"
            startActivity<SocketChatActivity>(IMTestApplication.context){}
        }
        viewModel.sigLiveData.observe(this) { result ->
            val sig = result.getOrNull()
            if (sig != null ){
                V2TIMManager.getInstance().login(currentUser,sig, object : V2TIMCallback {
                    override fun onSuccess() {
                        Log.e("im", "${currentUser}登录成功")
                        V2TIMManager.getInstance().joinGroup("meeting1","join",object :V2TIMCallback{
                            override fun onSuccess() {
                                Log.e("im","加群成功")
                            }
                            override fun onError(p0: Int, p1: String?) {
                                Log.e("im","加群失败")
                            }
                        })
                        startActivity<ChatActivity>(IMTestApplication.context){}
                    }

                    override fun onError(code: Int, desc: String?) {
                        Log.e("im", "${currentUser}登录失败，错误码为:${code},具体错误:${desc}")
                    }
                })
            }else{
                "登录凭据失效".showToast()
            }
        }
    }
}

