package org.bic.newsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import org.bic.newsapp.databinding.ActivityMainBinding
import org.bic.newsapp.fragments.bookmarks.BookmarksFragment
import org.bic.newsapp.fragments.breakingnews.BreakingnewsFragment
import org.bic.newsapp.fragments.searchnews.SearchnewsFragment
import org.bic.newsapp.util.Constants

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var breakingNewsFragment: BreakingnewsFragment
    private lateinit var searchNewsFragment: SearchnewsFragment
    private lateinit var bookmarkedNewsFragment: BookmarksFragment


    //This fragment array stores the fragments after they are initialized.
    private val fragment: Array<Fragment>
        get() = arrayOf(
            breakingNewsFragment,
            searchNewsFragment,
            bookmarkedNewsFragment
        )

    private var selectedIndex = 0

    private val selectedFragment get() = fragment[selectedIndex]

    private fun selectFragment(selectedFragment: Fragment) {
        var transaction = supportFragmentManager.beginTransaction()
        fragment.forEachIndexed { index, fragment ->
            if (selectedFragment == fragment) {
                transaction = transaction.attach(fragment)
                selectedIndex = index
            } else {
                transaction = transaction.detach(fragment)
            }

        }
        transaction.commit()

        title = when (selectedFragment) {
            is BreakingnewsFragment -> getString(R.string.breaking_news)
            is SearchnewsFragment -> getString(R.string.search_news)
            is BookmarksFragment -> getString(R.string.bookmarks)
            else -> ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {


            breakingNewsFragment = BreakingnewsFragment()
            searchNewsFragment = SearchnewsFragment()
            bookmarkedNewsFragment = BookmarksFragment()

            supportFragmentManager.beginTransaction().add(
                R.id.Fragment_Container,
                breakingNewsFragment,
                Constants.TAG_BREAKING_NEWS_FRAGMENT
            ).add(
                R.id.Fragment_Container,
                searchNewsFragment,
                Constants.TAG_SEARCH_NEWS_FRAGMENT
            ).add(
                R.id.Fragment_Container,
                bookmarkedNewsFragment,
                Constants.TAG_BOOKMARKS_NEWS_FRAGMENT
            ).commit()
        } else {
            breakingNewsFragment = supportFragmentManager.findFragmentByTag(Constants.TAG_BREAKING_NEWS_FRAGMENT)
            as BreakingnewsFragment
            searchNewsFragment = supportFragmentManager.findFragmentByTag(Constants.TAG_SEARCH_NEWS_FRAGMENT)
                    as SearchnewsFragment
            bookmarkedNewsFragment = supportFragmentManager.findFragmentByTag(Constants.TAG_BOOKMARKS_NEWS_FRAGMENT)
                    as BookmarksFragment

            selectedIndex = savedInstanceState.getInt(Constants.KEY_SELECTED,0)
        }
        selectFragment(selectedFragment)

        binding.bottomNavigation.setOnItemSelectedListener {
            val fragment = when (it.itemId){
                R.id.breaking_news ->  breakingNewsFragment
                R.id.search_news -> searchNewsFragment
                R.id.bookmarked_news -> bookmarkedNewsFragment
                else -> throw IllegalArgumentException("Unexpected itemId")
            }

            selectFragment(fragment)
            true
        }
    }

    override fun onBackPressed() {

        if (selectedIndex != 0){
            binding.bottomNavigation.selectedItemId = R.id.breaking_news
        }
        else {
            super.onBackPressed()
        }
    }
    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt(Constants.KEY_SELECTED,selectedIndex)
    }
}