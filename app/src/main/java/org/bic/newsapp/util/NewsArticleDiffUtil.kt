package org.bic.newsapp.util

import androidx.recyclerview.widget.DiffUtil
import org.bic.newsapp.data.NewsArticle

class NewsArticleDiffUtil : DiffUtil.ItemCallback<NewsArticle>() {
    override fun areItemsTheSame(oldItem: NewsArticle, newItem: NewsArticle) =
        oldItem.url == newItem.url

    override fun areContentsTheSame(oldItem: NewsArticle, newItem: NewsArticle) =
        oldItem == newItem
}