package com.rivas.testgapsi.ui.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.miguelcatalan.materialsearchview.MaterialSearchView.SearchViewListener
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
import org.json.JSONArray


class MainActivity : AppCompatActivity() {

    private lateinit var apiInterface: APIInterface
    var busquedas = JSONArray()
    var searchView: MaterialSearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        searchViewCode()
        apiInterface = APIClient.client!!.create(APIInterface::class.java)
        getData()
        getBusquedas()
        if (Intent.ACTION_SEARCH == intent.action) {
            Log.e("fasdf", "fasdfga")
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                SearchRecentSuggestions(this, SuggestionProvider.AUTHORITY, SuggestionProvider.MODE)
                    .saveRecentQuery(query, null)
            }
        }
    }

    private fun getBusquedas() {
        val preferencias = getSharedPreferences("busquedas", Context.MODE_PRIVATE)
        busquedas= JSONArray(preferencias.getString("data", "[]"))
        val array = arrayOfNulls<String>(busquedas.length())

        for (i in 0 until busquedas.length() ) {
            array[i] = busquedas.optString(i)
        }
        searchView?.setSuggestions(array);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                return  true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (searchView!!.isSearchOpen) {
            searchView!!.closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    private fun searchViewCode() {
        searchView = findViewById<MaterialSearchView>(R.id.search_view)
        searchView?.setEllipsize(true)
        searchView?.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                getData(query)
                busquedas.put(query)
                val preferencias = getSharedPreferences("busquedas", Context.MODE_PRIVATE)
                preferencias.edit().putString("data", busquedas.toString()).apply()
                getBusquedas()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        searchView?.setOnSearchViewListener(object : SearchViewListener {
            override fun onSearchViewShown() {}
            override fun onSearchViewClosed() {}
        })
    } /*click alt+insert key */

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
        searchView?.setMenuItem(searchItem);
        return super.onCreateOptionsMenu(menu)
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}