package online.dreamstudio.imtest.bean

import online.dreamstudio.imtest.tool.MyDate

data class Message(val message:String,val fundName:String = "test",val imgUrl:String = "https://res.dreamstudio.online/img/default.png",val time:String = MyDate.getSimpleDate())
data class MessageResponse(val chatList:List<Message>)