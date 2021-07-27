package com.atdev.githubproject.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.atdev.githubproject.helpers.MainRepository
import com.atdev.githubproject.model.RepositoryJsonObject
import com.atdev.githubproject.retrofit.NoConnectivityException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val mainRepository: MainRepository,
) : ViewModel() {

    var networkConnected  = MutableLiveData<Boolean>()

    var repositoryList = MutableLiveData<List<RepositoryJsonObject>>(ArrayList())
    private var job: Job? = null //в каких случаях его закрывать? см ниже в else
    private var responseEmpty = MutableLiveData(false)

    var foundByField = MutableLiveData("")

    private fun getSearchResult(value: String) {
        job = viewModelScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                try {
                    _progressBarVisibility.postValue(true)
                    val response = mainRepository.getSearchUser(value)
                    if (response.isSuccessful) {
                        Log.i("TEST!", response.message())
                        response.body()?.let {
                            repositoryList.postValue(it)
                            if (it.isNotEmpty()) {
                                responseEmpty.postValue(false)
                                foundByField.postValue(value)
                            } else {
                                responseEmpty.postValue(true)
                                foundByField.postValue("")
                            }
                            _progressBarVisibility.postValue(false)
                        }
                    } else {
                        job?.cancel()
                        _progressBarVisibility.postValue(false)
                    }
                }catch (e: NoConnectivityException){
                    _progressBarVisibility.postValue(false)
                    Log.i("TEST11", e.message.toString())
                    networkConnected.postValue(false)
                }
            }
        }
    }

    private val _progressBarVisibility = MutableLiveData<Boolean>(false)
    val progressBarVisibility: LiveData<Boolean> = _progressBarVisibility.map { it }

    val groupEmptyListVisibility: LiveData<Boolean> = repositoryList.map { it.isEmpty() }
    val groupNotFoundVisibility: LiveData<Boolean> = responseEmpty.map { it == true }
    val recyclerVisibility: LiveData<Boolean> = repositoryList.map { !it.isNullOrEmpty() }

    fun addItemInDao(itemId: String) {
        val item = repositoryList.value?.find { item -> item.id == itemId }
        viewModelScope.launch(Dispatchers.Main) {
            mainRepository.addItemInDao(mainRepository.transformItemInDao(item))
        }
    }

    fun searchByName(value: String) {
        getSearchResult(value)
    }

    fun resetStatusAdded(itemId: String) {
        val item = repositoryList.value?.find { item -> item.id == itemId }
        item?.added = false
    }

    fun clearFoundList() {
        repositoryList.postValue(ArrayList())
        responseEmpty.postValue(false)
        notifyDataSetChanged?.invoke()
    }

    var notifyDataSetChanged: (() -> Unit)? = null
    var changeEmptyViews: (() -> Unit)? = null

}
