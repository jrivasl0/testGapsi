package com.rivas.testgapsi.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rivas.testgapsi.R
import com.rivas.testgapsi.core.models.Product
import kotlinx.android.synthetic.main.product.view.*

class AdapterProducts(private var products: List<Product>, private var context: Context) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.product, parent, false))
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = products[position]
        holder.bind(item, context)
    }
}

class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    @SuppressLint("DefaultLocale", "SetTextI18n")
    fun bind(product: Product, context: Context) {
        view.titleProduct.text = product.title
        view.priceProduct.text = "\$"+"%.4f".format(product.price)
        Glide.with(context).load(product.image).into(view.imgProduct)
    }

}