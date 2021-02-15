package com.rivas.testgapsi.ui.activities

import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.isVisible
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.rivas.testgapsi.R
import com.rivas.testgapsi.core.models.SearchResultModel
import com.rivas.testgapsi.core.retrofit.APIClient
import com.rivas.testgapsi.core.retrofit.APIInterface
import com.rivas.testgapsi.ui.adapters.AdapterProducts
import com.rivas.testgapsi.utils.SuggestionProvider
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread


class MainActivity : AppCompatActivity() {

    private lateinit var apiInterface: APIInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        apiInterface = APIClient.client!!.create(APIInterface::class.java)
        getData()
    }

    private fun getData(search: String = "") {
        progressBar.visibility = View.VISIBLE
        doAsync {
            val call = apiInterface.getSearch(search).execute()
            if (call.code() == 200) {
                val response = call.body() as SearchResultModel
                uiThread {
                    showData(response)
                }
            } else {
                uiThread {
                    toast("Error al consultar el servicio")
                }
            }
        }

    }

    private fun showData(response: SearchResultModel) {
        rvProducts.layoutManager = LinearLayoutManager(this)
        rvProducts.adapter = AdapterProducts(response.items, this)
        progressBar.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val searchItem: MenuItem? = menu?.findItem(R.id.action_search)
        val searchView: SearchView = searchItem?.actionView as SearchView
        searchView.suggestionsAdapter = SimpleCursorAdapter(
                this, android.R.layout.simple_list_item_1, null, arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1), intArrayOf(android.R.id.text1))
        val queryTextListener: OnQueryTextListener = object : OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                //doFilterAsync(mSearchString);
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                //mSearchString = query
                getData(query);
                SearchRecentSuggestions(this@MainActivity, SuggestionProvider.AUTHORITY, SuggestionProvider.MODE)
                        .saveRecentQuery(query, null)
                hideKeyboard(searchView)
                return true
            }
        }

        val suggestionListener: SearchView.OnSuggestionListener = object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            override fun onSuggestionClick(position: Int): Boolean {
                hideKeyboard(searchView)
                val cursor: Cursor = searchView.suggestionsAdapter.getItem(position) as Cursor
                val selection = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                searchView.setQuery(selection, true)
                return true
            }
        }

        searchView.setOnQueryTextListener(queryTextListener)
        searchView.setOnSuggestionListener(suggestionListener)
        return super.onCreateOptionsMenu(menu)
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
                getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}