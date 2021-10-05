package org.bic.newsapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.bic.newsapp.data.NewsArticle
import org.bic.newsapp.data.NewsRepository
import javax.inject.Inject

@HiltViewModel
class BreakingNewsViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    private val breakingNewsFlow = MutableStateFlow<List<NewsArticle>>(emptyList())
    val breakingNews : Flow<List<NewsArticle>> = breakingNewsFlow
    /*
    This ensures that only the viewmodel may change the flow.
    Fragment/Activity can only observe the flow.
     */

    init{
        viewModelScope.launch {
            val news = repository.getBreakingNews()
            breakingNewsFlow.value = news

        }
    }
}