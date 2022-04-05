package online.dreamstudio.imtest.widget

import com.google.gson.Gson
import com.google.gson.internal.`$Gson$Types`
import java.lang.reflect.Type

inline fun <reified T> String.toMyObject(): List<T> {
    val listType: Type = `$Gson$Types`.newParameterizedTypeWithOwner(null, ArrayList::class.java, T::class.java)
    return if(!contains("[")){
        Gson().fromJson("[${this}]", listType)
    }else{
        Gson().fromJson(this, listType)
    }
}