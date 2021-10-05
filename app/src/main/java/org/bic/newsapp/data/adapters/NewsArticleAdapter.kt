package org.bic.newsapp.data.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import org.bic.newsapp.data.NewsArticle
import org.bic.newsapp.databinding.ItemNewsArticleBinding
import org.bic.newsapp.util.NewsArticleDiffUtil

class NewsArticleAdapter: ListAdapter<NewsArticle, NewsArticleViewHolder>(NewsArticleDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsArticleViewHolder {

        val binding = ItemNewsArticleBinding.inflate(LayoutInflater.from(parent.context), parent,
        false)
        return NewsArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsArticleViewHolder, position: Int) {
       val currentItem = getItem(position)
        if (currentItem != null){
            holder.bind(currentItem)
        }
    }


}