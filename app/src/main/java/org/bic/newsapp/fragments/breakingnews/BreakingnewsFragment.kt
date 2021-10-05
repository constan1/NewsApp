package org.bic.newsapp.fragments.breakingnews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import org.bic.newsapp.R
import org.bic.newsapp.data.adapters.NewsArticleAdapter
import org.bic.newsapp.databinding.FragmentBreakingNewsBinding
import org.bic.newsapp.util.Resource
import org.bic.newsapp.viewmodels.BreakingNewsViewModel

@AndroidEntryPoint
class BreakingnewsFragment : Fragment(R.layout.fragment_breaking_news) {

    private lateinit var  viewModel :  BreakingNewsViewModel
    private var _binding: FragmentBreakingNewsBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(
            BreakingNewsViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val newsArticleAdapter = NewsArticleAdapter()
        _binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
        binding.root.findViewById<RecyclerView>(R.id.recycler_view).adapter = newsArticleAdapter
        binding.root.findViewById<RecyclerView>(R.id.recycler_view).layoutManager =
            LinearLayoutManager(requireContext())
        binding.root.findViewById<RecyclerView>(R.id.recycler_view).setHasFixedSize(true)


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.breakingNews.collect {
                val result = it ?: return@collect
                binding.root.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
                    .isRefreshing = result is Resource.Loading
                binding.root.findViewById<RecyclerView>(R.id.recycler_view).isVisible = !result.data.isNullOrEmpty()
                binding.textViewError.isVisible = result.error != null && result.data.isNullOrEmpty()
                binding.buttonRetry.isVisible = result.error != null && result.data.isNullOrEmpty()
                binding.textViewError.text = getString(
                    R.string.could_not_refresh,
                    result.error?.localizedMessage
                        ?:getString(R.string.unknown_eror_occured)
                )

                newsArticleAdapter.submitList(result.data)
            }
        }
        return binding.root
    }

}