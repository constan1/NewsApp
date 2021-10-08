package org.bic.newsapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface NewsArticleDao
{

    @Query("SELECT * FROM breaking_news INNER JOIN news_articles ON articleUrl=url")
    fun getAllBreakingNewsArticles(): Flow<List<NewsArticle>>

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
}