package org.bic.newsapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_query_remote_keys")
data class searchQueryRemoteKey(
    @PrimaryKey val searchQuery : String,
    val nextPageKey:Int
)