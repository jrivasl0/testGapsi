package com.rivas.testgapsi.core.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SearchResultModel(
    @SerializedName("totalResults") @Expose val totalResults: Int,
    @SerializedName("page") @Expose val page: Int,
    @SerializedName("items") @Expose val items: List<Product>,
)

data class Product(
    @SerializedName("id") var id: String? = null,
    @SerializedName("rating") var rating: Double? = null,
    @SerializedName("price") var price: Double? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("title") var title: String? = null
)
