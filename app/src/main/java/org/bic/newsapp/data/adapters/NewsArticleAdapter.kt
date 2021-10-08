package org.bic.newsapp.data.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import org.bic.newsapp.data.NewsArticle
import org.bic.newsapp.databinding.ItemNewsArticleBinding
import org.bic.newsapp.util.NewsArticleDiffUtil

class NewsArticleAdapter(
    private val onItemClick: (NewsArticle) ->Unit,
    private val onBookmarkClick: (NewsArticle) ->Unit
): ListAdapter<NewsArticle, NewsArticleViewHolder>(NewsArticleDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsArticleViewHolder {

        val binding = ItemNewsArticleBinding.inflate(LayoutInflater.from(parent.context), parent,
        false)
        return NewsArticleViewHolder(binding,
        onItemClick =  {
            position ->
            val article = getItem(position)
            if(article!=null){
            onItemClick(article)
            }
        },
        onBookmarkCick = {
                position ->
            val article = getItem(position)
            if(article!=null){
            onBookmarkClick(article)
            }
        })
    }

    override fun onBindViewHolder(holder: NewsArticleViewHolder, position: Int) {
       val currentItem = getItem(position)
        if (currentItem != null){
            holder.bind(currentItem)
        }
    }


}