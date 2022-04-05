package online.dreamstudio.imtest

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import online.dreamstudio.imtest.base.BaseViewModel
import online.dreamstudio.imtest.bean.GetSigRequest
import online.dreamstudio.imtest.network.Repository

class MainActivityViewModel:BaseViewModel(){
    private val getSigLiveData = MutableLiveData<GetSigRequest>()
    val sigLiveData = Transformations.switchMap(getSigLiveData){ sig ->
        Repository.getSig(sig.userId,sig.expire)
    }
    fun getSig(userId:String,expire:Long){
        getSigLiveData.value = GetSigRequest(userId, expire)
    }
}