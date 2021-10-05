package org.bic.newsapp.data

import javax.inject.Inject
import javax.inject.Singleton


class NewsRepository @Inject constructor(
    private val newsApi : NewsApi,
    private val newsArticleDb : NewsArticleDatabase
) {
    private val newsArticleDao = newsArticleDb.newsArticleDao()
    suspend fun getBreakingNews() : List<NewsArticle>{

        val response = newsApi.getBreakingNews()

        val remoteBreakingNewsArticles = response.articles
        val localBreakingNewsArticles  = remoteBreakingNewsArticles.map {
            NewsArticle(
                title = it.title,
                url = it.url,
                thumbnailUrl = it.urlToImage,
                isBookmarked = false
            )
        }
        return localBreakingNewsArticles

    }

}