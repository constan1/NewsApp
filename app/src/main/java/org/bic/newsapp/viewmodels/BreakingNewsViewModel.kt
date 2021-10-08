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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class BreakingNewsViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    private val refreshTriggerChannel = Channel<Refresh>()
    private val refreshTrigger = refreshTriggerChannel.receiveAsFlow()

    var pendingScrollToTopAfterRefresh = false
    val breakingNews = refreshTrigger.flatMapLatest {
        refresh ->
        repository.getBreakingNews(
            refresh == Refresh.FOCE,
            onFetchSuccess= {
                pendingScrollToTopAfterRefresh = true
            },
            onFetchFailed = {
                //Does not keep the flow lingering in the backround.
                t-> viewModelScope.launch {
                    eventChannel.send(Event.ShowErrorMessage(t))
            }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        viewModelScope.launch {
            repository.deleteNonBookmarkedArticlesOlderThan(
                System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
            )
        }
    }
    fun onManualRefresh(){

        if(breakingNews.value !is Resource.Loading){
            viewModelScope.launch {
                refreshTriggerChannel.send(Refresh.FOCE)
            }
        }

    }

    // a limited set of constants it can only take the values that are defined In the body.

    enum class  Refresh {
        FOCE, NORMAL
    }

    fun onStart(){
        if(breakingNews.value !is Resource.Loading){
            viewModelScope.launch {
                refreshTriggerChannel.send(Refresh.NORMAL)
            }
        }
    }

    sealed class Event{
        data class ShowErrorMessage(val error: Throwable):Event()
    }
}