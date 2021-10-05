package org.bic.newsapp.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import org.bic.newsapp.util.Resource
import org.bic.newsapp.util.networkBoundResource
import javax.inject.Inject
import javax.inject.Singleton


class NewsRepository @Inject constructor(
    private val newsApi : NewsApi,
    private val newsArticleDb : NewsArticleDatabase
) {
    private val newsArticleDao = newsArticleDb.newsArticleDao()
     fun getBreakingNews() : Flow<Resource<List<NewsArticle>>>  =
        networkBoundResource(
            query = {
                newsArticleDao.getAllBreakingNewsArticles()
            },
            fetch = {
                val response = newsApi.getBreakingNews()
                response.articles
            },

            saveFetchResult = { serverBreakingNewsArticles ->
                val breakingNewsArticles =
                    serverBreakingNewsArticles.map{
                        NewsArticle(
                            title = it.title,
                            url = it.url,
                            thumbnailUrl = it.urlToImage,
                            isBookmarked = false
                        )
                    }
                val breakingNews = breakingNewsArticles.map{
                    article ->
                    BreakingNews(
                        articleUrl = article.url
                    )
                }
                newsArticleDb.withTransaction {
                    newsArticleDao.deleteAllBreakingNews()
                    newsArticleDao.insertArticles(breakingNewsArticles)
                    newsArticleDao.insertBreakingNews(breakingNews)
                }


            }

        )



}