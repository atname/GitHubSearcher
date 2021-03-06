package com.atdev.githubproject.search.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.atdev.githubproject.MainRepository
import com.atdev.githubproject.search.model.RepositoryObjectDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val mainRepository: MainRepository,
) : ViewModel() {

    var networkConnected = MutableLiveData<Boolean>()

    var repositoryFlow: Flow<PagingData<RepositoryObjectDto>>? = null

    fun searchByName(value: String) {
        repositoryFlow = mainRepository.getPagingDataByName(value).cachedIn(viewModelScope)
    }

    fun addItemInDao(item: RepositoryObjectDto) {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.addItemInDao(item.transformItemInDao())
        }
    }
}
