package org.bic.newsapp.fragments.breakingnews

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.bic.newsapp.R
import org.bic.newsapp.viewmodels.BreakingNewsViewModel

@AndroidEntryPoint
class BreakingnewsFragment : Fragment(R.layout.fragment_breaking_news) {

    private val viewModel :  BreakingNewsViewModel by viewModels()

}