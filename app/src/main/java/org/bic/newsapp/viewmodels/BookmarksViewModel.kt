package org.bic.newsapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.bic.newsapp.data.NewsArticle
import org.bic.newsapp.data.NewsRepository
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel()
{

    val bookmarks = repository.getAllBarkedArticles()
        .stateIn(viewModelScope, SharingStarted.Lazily,
        null)
    fun onBookmarkClick(article: NewsArticle){
        val currentlyBookmarked = article.isBookmarked
        val updatedArticle = article.copy(isBookmarked = !currentlyBookmarked)
        viewModelScope.launch {
            repository.updateArticle(updatedArticle)
        }
    }

    fun onDeleteAllBookmarks(){
        viewModelScope.launch {
            repository.resetAllBookmarks()
        }
    }
}
