package com.rivas.testgapsi.utils

import android.content.SearchRecentSuggestionsProvider

class SuggestionProvider : SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {
        const val AUTHORITY = "com.rivas.testgapsi.utils.SuggestionProvider"
        const val MODE: Int = DATABASE_MODE_QUERIES
    }
}