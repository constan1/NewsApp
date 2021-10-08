package org.bic.newsapp.data.adapters

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.bic.newsapp.R
import org.bic.newsapp.data.NewsArticle
import org.bic.newsapp.databinding.ItemNewsArticleBinding

class NewsArticleViewHolder(
    private val binding: ItemNewsArticleBinding,
    private val onItemClick: (Int) -> Unit,
    private val onBookmarkCick: (Int) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    fun bind(article: NewsArticle){
        binding.apply {
            Glide.with(itemView)
                .load(article.thumbnailUrl)
                .error(R.drawable.placeholder)
                .into(imageViewMain)
            textViewTitle.text = article.title ?: ""

            imageViewBookmark.setImageResource(
                when {
                    article.isBookmarked -> R.drawable.ic_baseline_book_24
                    else -> R.drawable.ic_unselected
                }
            )
        }
    }

    init {
        binding.apply{
            root.setOnClickListener {
                val position = bindingAdapterPosition
                if(position != RecyclerView.NO_POSITION){
                    onItemClick(position)
                }
            }
            imageViewBookmark.setOnClickListener {
                val position = bindingAdapterPosition
                if(position != RecyclerView.NO_POSITION){
                    onBookmarkCick(position)
                }
            }
        }
    }
}