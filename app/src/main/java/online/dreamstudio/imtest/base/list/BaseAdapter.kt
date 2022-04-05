package online.dreamstudio.imtest.base.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter(private val itemList: List<*>,private val RItem:Int): RecyclerView.Adapter<BaseViewHolder>(){
    protected lateinit var view:View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        view = LayoutInflater.from(parent.context).inflate(RItem,parent,false)

        return BaseViewHolder(view)
    }
    override fun getItemCount() = itemList.size
}