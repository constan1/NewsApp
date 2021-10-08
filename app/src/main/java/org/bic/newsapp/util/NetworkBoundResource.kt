package org.bic.newsapp.util

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/*
    This functions primary directive is to check whether cached data
    is stale and required to be updated with the REST api or whether
    it is up-to-date and can be shown.
 */
inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    crossinline onFetchSuccess: () -> Unit = { },
    crossinline onFetchFailed : (Throwable) -> Unit = { }
) = channelFlow {
    val data = query().first()

    /*
    This conditional statement determines whether
    we fetch the data from the api and cache it.
     */
    if(shouldFetch(data)){
        val loading = launch {
            query().collect {
                send(Resource.Loading(it))
            }
        }

        //These two blocks will be run concurently
        try {
            delay(2000)
            saveFetchResult(fetch())
            onFetchSuccess()
            loading.cancel()
            query().collect { send(Resource.Success(it))}
        } catch (t: Throwable){
            //Exceptions
            onFetchFailed(t)
            loading.cancel()
            query().collect {
                send(Resource.Error(t, it))
            }
        }
        /*
        If should fetch is false, the data in Room is not stale
        and we display data from the cache.
         */
    } else {
        query().collect { send(Resource.Success(it))}
    }
}