package org.bic.newsapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.bic.newsapp.data.NewsArticle
import org.bic.newsapp.data.NewsRepository
import org.bic.newsapp.util.Resource
import javax.inject.Inject

@HiltViewModel
class BreakingNewsViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    private val refreshTriggerChannel = Channel<Unit>()
    private val refreshTrigger = refreshTriggerChannel.receiveAsFlow()

    val breakingNews = refreshTrigger.flatMapLatest {
        repository.getBreakingNews(
            onFetchFailed = {
                //Does not keep the flow lingering in the backround.
                t-> viewModelScope.launch {
                    eventChannel.send(Event.ShowErrorMessage(t))
            }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun onManualRefresh(){

        if(breakingNews.value !is Resource.Loading){
            viewModelScope.launch {
                refreshTriggerChannel.send(Unit)
            }
        }

    }

    fun onStart(){
        if(breakingNews.value !is Resource.Loading){
            viewModelScope.launch {
                refreshTriggerChannel.send(Unit)
            }
        }
    }

    sealed class Event{
        data class ShowErrorMessage(val error: Throwable):Event()
    }
}