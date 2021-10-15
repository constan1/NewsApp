package org.bic.newsapp.fragments.searchnews

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import org.bic.newsapp.R
import org.bic.newsapp.data.NewsArticlePagingAdapter
import org.bic.newsapp.databinding.FragmentSearchNewsBinding
import org.bic.newsapp.viewmodels.SearchNewsViewModel
import org.bic.newsapp.util.onQueryTextSubmit

@AndroidEntryPoint
class SearchnewsFragment : Fragment(R.layout.fragment_search_news) {

    private val viewmodel: SearchNewsViewModel by viewModels()

    private lateinit var newsArticleAdapter : NewsArticlePagingAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSearchNewsBinding.bind(view)

        newsArticleAdapter = NewsArticlePagingAdapter(
            onItemClick = {
                    article ->
                val uri = Uri.parse(article.url)
                val intent = Intent(Intent.ACTION_VIEW,uri)
                requireActivity().startActivity(intent)
            },

            onBookmarkClick = {
                    article ->
                viewmodel.onBookmarkClick(article)
            }
        )

        binding.apply{
            recyclerView.apply {
                adapter = newsArticleAdapter.withLoadStateFooter(
                    NewsArticleLoadStateAdapter(newsArticleAdapter::retry)
                )

                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                itemAnimator?.changeDuration =0
            }

            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewmodel.searchResults.collectLatest {
                    /*
                    Collect Latest means as soon as we get new value from this flow
                    the previous block will be canceled.
                     */
                    data->
                    textViewInstructions.isVisible = false
                    newsArticleAdapter.submitData(data)
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                newsArticleAdapter.loadStateFlow
                    .collect {
                        loadState ->
                        when ( val refresh = loadState.mediator?.refresh){
                            is LoadState.NotLoading -> {
                                textViewError.isVisible = false
                                buttonRetry.isVisible = false
                                swipeRefreshLayout.isRefreshing = false
                                recyclerView.isVisible = newsArticleAdapter.itemCount > 0


                                val noResults =
                                    newsArticleAdapter.itemCount < 1 && loadState.append.endOfPaginationReached
                                            && loadState.source.append.endOfPaginationReached
                                textViewNoResults.isVisible = noResults
                            }
                            is LoadState.Loading -> {
                                textViewError.isVisible = false
                                buttonRetry.isVisible = false
                                textViewNoResults.isVisible = false
                                swipeRefreshLayout.isRefreshing = true
                                recyclerView.isVisible = newsArticleAdapter.itemCount > 0

                            }
                            is LoadState.Error -> {

                                swipeRefreshLayout.isRefreshing = false
                                textViewNoResults.isVisible = false
                                recyclerView.isVisible = newsArticleAdapter.itemCount > 0

                                val noCachedResults =
                                    newsArticleAdapter.itemCount < 1 && loadState.source.append.endOfPaginationReached

                                textViewError.isVisible = noCachedResults
                                buttonRetry.isVisible = noCachedResults

                                val errorMessage = getString(
                                    R.string.could_not_load_search_results,
                                    refresh.error.localizedMessage
                                        ?:getString(R.string.unknown_eror_occured)
                                    )
                                textViewError.text = errorMessage
                            }
                           
                        }
                    }
            }


            swipeRefreshLayout.setOnRefreshListener {
                newsArticleAdapter.refresh()
            }

            buttonRetry.setOnClickListener {
                newsArticleAdapter.retry()
            }

        }
        setHasOptionsMenu(true)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search_news, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as androidx.appcompat.widget.SearchView

        searchView.onQueryTextSubmit { query ->
            viewmodel.onSearchQuerySubmit(query)
            searchView.clearFocus()

        }



    }

    override fun onOptionsItemSelected(item: MenuItem)  =
        when (item.itemId){
            R.id.action_refresh -> {
             newsArticleAdapter.refresh()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

}
