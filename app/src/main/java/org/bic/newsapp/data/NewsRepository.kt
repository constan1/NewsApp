package org.bic.newsapp.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import org.bic.newsapp.util.Resource
import org.bic.newsapp.util.networkBoundResource
import retrofit2.HttpException
import java.io.IOException
import java.sql.Timestamp
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


class NewsRepository @Inject constructor(
    private val newsApi : NewsApi,
    private val newsArticleDb : NewsArticleDatabase
) {
    private val newsArticleDao = newsArticleDb.newsArticleDao()
     fun getBreakingNews(
         forceRefresh: Boolean,
         onFetchSuccess: () -> Unit,
         onFetchFailed:(Throwable) -> Unit
     ) : Flow<Resource<List<NewsArticle>>>  =
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


            },


            shouldFetch ={
                cachedArticles ->
                if(forceRefresh){
                    true
                } else {
                    val sortedArticles = cachedArticles.sortedBy {
                            article -> article.updatedAt
                    }

                    val oldestTimeStamp = sortedArticles.firstOrNull()?.updatedAt
                    val needsRefresh = oldestTimeStamp == null ||
                            oldestTimeStamp < System.currentTimeMillis() -
                            TimeUnit.MINUTES.toMillis(5)
                    needsRefresh
                }

            },
            onFetchSuccess = onFetchSuccess,
            onFetchFailed = {

                if(it !is HttpException && it !is IOException){
                    throw it
                }
                 onFetchFailed(it)
            }

        )


    suspend fun deleteNonBookmarkedArticlesOlderThan(timestampInMillis: Long){
        newsArticleDao.deleteNonBookmarkedArticlesOlderThan(timestampInMillis)
    }

}