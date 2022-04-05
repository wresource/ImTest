package online.dreamstudio.imtest.chat

import android.view.ViewGroup
import android.widget.TextView
import online.dreamstudio.imtest.base.list.BaseAdapter
import online.dreamstudio.imtest.base.list.BaseViewHolder
import online.dreamstudio.imtest.bean.Message
import com.bumptech.glide.Glide
import online.dreamstudio.imtest.tool.IMTestApplication
import online.dreamstudio.imtest.R


class ChatAdapter(private val chatList: List<Message>): BaseAdapter(chatList,R.layout.chat_item){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        super.onCreateViewHolder(parent, viewType)
        return BaseViewHolder(view)
    }
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val chat = chatList[position]
        Glide.with(IMTestApplication.context)
            .load(chat.imgUrl)
            .into(holder.itemView.findViewById(R.id.chatIcon))
        holder.itemView.findViewById<TextView>(R.id.chatText).text = chat.message
        holder.itemView.findViewById<TextView>(R.id.chatTime).text = chat.time
        holder.itemView.findViewById<TextView>(R.id.chatName).text = chat.fundName
    }
}