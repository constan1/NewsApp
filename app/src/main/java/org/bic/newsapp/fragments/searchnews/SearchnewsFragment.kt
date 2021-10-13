package org.bic.newsapp.fragments.searchnews

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import org.bic.newsapp.R
import org.bic.newsapp.data.NewsArticlePagingAdapter
import org.bic.newsapp.databinding.FragmentSearchNewsBinding
import org.bic.newsapp.viewmodels.SearchNewsViewModel

@AndroidEntryPoint
class SearchnewsFragment : Fragment(R.layout.fragment_search_news) {

    private val viewmodel: SearchNewsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSearchNewsBinding.bind(view)

        val newsArticleAdapter = NewsArticlePagingAdapter(
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
        }
    }

    override fun onStart() {
        super.onStart()
        viewmodel.onSearchQuerySubmit("germany")
    }
}
