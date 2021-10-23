package org.bic.newsapp.fragments.bookmarks

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import org.bic.newsapp.MainActivity
import org.bic.newsapp.R
import org.bic.newsapp.data.adapters.NewsArticleAdapter
import org.bic.newsapp.databinding.FragmentBookmarksBinding
import org.bic.newsapp.databinding.FragmentBreakingNewsBinding
import org.bic.newsapp.viewmodels.BookmarksViewModel
import org.bic.newsapp.viewmodels.BreakingNewsViewModel

@AndroidEntryPoint
class BookmarksFragment : Fragment(R.layout.fragment_bookmarks)
,MainActivity.OnBottomNavigationFragmentReselectedListener{

    private lateinit var viewModel: BookmarksViewModel
    private var _binding: FragmentBookmarksBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(
            BookmarksViewModel::class.java
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)

        val bookmarksAdapter = NewsArticleAdapter(
            onItemClick = { article ->
                val uri = Uri.parse(article.url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                requireActivity().startActivity(intent)
            },

            onBookmarkClick = { article ->
                viewModel.onBookmarkClick(article)
            }
        )

        binding.root.findViewById<RecyclerView>(R.id.recycler_view).adapter = bookmarksAdapter
        binding.root.findViewById<RecyclerView>(R.id.recycler_view).layoutManager =
            LinearLayoutManager(requireContext())
        binding.root.findViewById<RecyclerView>(R.id.recycler_view).setHasFixedSize(true)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.bookmarks.collect {
                val bookmarks = it ?: return@collect

                bookmarksAdapter.submitList(bookmarks)
                binding.textViewNoBookmarks.isVisible = bookmarks.isEmpty()
                binding.root.findViewById<RecyclerView>(R.id.recycler_view).isVisible =
                    bookmarks.isNotEmpty()
            }
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_bookmarks, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_delete_all_bookmarks -> {
                viewModel.onDeleteAllBookmarks()
                true
            }
            else -> super.onOptionsItemSelected(item)

        }

    override fun onBottomNavigationFragmentReselected() {
        binding.recyclerView.scrollToPosition(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}