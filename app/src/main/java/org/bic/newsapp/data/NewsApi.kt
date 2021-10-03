package org.bic.newsapp.data



import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NewsApi {

    companion object{
        private const val  BASE_URL = "https://newsapi.org/v2/"
        private const val API_KEY = "c06d89477a5d4026a6afef92bcfc1332"

    }

    @Headers("X-Api-Key: $API_KEY")
    @GET("top-headlines?country=us&pageSize=100")
    suspend fun getBreakingNews(): NewsResponse

    @Headers("X-Api-Key: $API_KEY")
    @GET("everything")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("page") page:Int,
        @Query("pageSize") pageSize: Int
    ) : NewsResponse
}