package online.dreamstudio.imtest.bean

import online.dreamstudio.imtest.tool.MyDate


data class Chat(val funderAccount:String, val message:String, val time:String = MyDate.getSimpleDate())
