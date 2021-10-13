package org.bic.newsapp.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
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
                val bookmarkArticles = newsArticleDao.bookMarkArticles().first()
                val breakingNewsArticles =
                    serverBreakingNewsArticles.map{
                        val isBookmarked = bookmarkArticles.any {
                            bookmarkedArticle ->
                            bookmarkedArticle.url == it.url
                        }
                        NewsArticle(
                            title = it.title,
                            url = it.url,
                            thumbnailUrl = it.urlToImage,
                            isBookmarked = isBookmarked
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

    fun getAllBarkedArticles() : Flow<List<NewsArticle>>{
        return newsArticleDao.bookMarkArticles()


    }
    suspend fun updateArticle(article:NewsArticle){
        newsArticleDao.updateArticle(article)
    }


    suspend fun deleteNonBookmarkedArticlesOlderThan(timestampInMillis: Long){
        newsArticleDao.deleteNonBookmarkedArticlesOlderThan(timestampInMillis)
    }

    suspend fun resetAllBookmarks(){
        newsArticleDao.resetAllBookmarks()
    }

    fun getSearchResult(query:String): Flow<PagingData<NewsArticle>> =
        Pager(
            config = PagingConfig(pageSize = 20, maxSize = 200),
            remoteMediator = SearchNewsRemoteMediator(query,newsApi,newsArticleDb),
            pagingSourceFactory = {
                newsArticleDao.getSearchResult(query) }
        ).flow




}