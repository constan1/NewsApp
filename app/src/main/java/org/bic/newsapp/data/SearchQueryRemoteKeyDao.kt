package org.bic.newsapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface  SearchQueryRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemoteKey(remoteKey: searchQueryRemoteKey)

    @Query("SELECT * FROM search_query_remote_keys WHERE searchQuery = :searchQuery")
    suspend fun getRemoteKey(searchQuery: String) : searchQueryRemoteKey

}