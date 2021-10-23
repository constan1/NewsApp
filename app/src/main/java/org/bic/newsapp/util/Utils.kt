package org.bic.newsapp.util

import android.view.View
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.locks.Condition


fun Fragment.showSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    view: View = requireView()
) {
    Snackbar.make(view,message,duration).show()
}

//inline fun takes function argument
inline fun <T: View>T.showIfOrInvisible(condition: (T) -> Boolean){
    if(condition(this)){
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.INVISIBLE
    }
}
/*
Crossline is required when you do not call return in your function argument.
 */
inline fun SearchView.onQueryTextSubmit(crossinline listener: (String) -> Unit) {

    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
        override fun onQueryTextSubmit(query: String?): Boolean {
            if(!query.isNullOrBlank()) {
                listener(query)
            }
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return true
        }

    })

}

val <T> T.exhaustive: T
    get() = this