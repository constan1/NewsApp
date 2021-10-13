package org.bic.newsapp.data

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import retrofit2.http.DELETE


@Dao
interface NewsArticleDao
{

    @Query("SELECT * FROM breaking_news INNER JOIN news_articles ON articleUrl=url")
    fun getAllBreakingNewsArticles(): Flow<List<NewsArticle>>

    @Query("SELECT * FROM search_results INNER JOIN news_articles ON articleUrl = url WHERE `query` = :query ORDER BY queryPosition")
    fun getSearchResult(query: String): PagingSource<Int,NewsArticle>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<NewsArticle>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreakingNews(breakingNews: List<BreakingNews>)

    //this Delete query will be called when layout is swipe refreshed.
    //Values must be deleted first from local cache then new values must be inserted
    @Query("DELETE FROM breaking_news")
    suspend fun deleteAllBreakingNews()

    @Update
    suspend fun updateArticle(article:NewsArticle)

    @Query("SELECT * FROM news_articles WHERE isBookmarked = 1")
    fun bookMarkArticles() : Flow<List<NewsArticle>>

    @Query("DELETE FROM news_articles WHERE updatedAt < :timestampInMillis AND isBookmarked =0 ")
    suspend fun deleteNonBookmarkedArticlesOlderThan(timestampInMillis: Long)

    @Query("UPDATE news_articles SET isBookmarked = 0")
    suspend fun resetAllBookmarks()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchResults(searchResults:List<SearchResult>)

    @Query("DELETE FROM search_results WHERE `query` = :query")
    suspend fun deleteSearchResultsForQuery(query:String)


    @Query("SELECT MAX(queryPosition) FROM search_results WHERE `query` =:searchQuery")
    suspend fun getLastQueryPosition(searchQuery: String): Int?
}