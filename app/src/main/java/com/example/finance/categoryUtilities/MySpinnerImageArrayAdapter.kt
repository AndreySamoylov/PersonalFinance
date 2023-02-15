package com.example.finance.categoryUtilities

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.example.finance.R

class MySpinnerImageArrayAdapter(
    ctx: Context,
    IDs: ArrayList<Int>
) :
    ArrayAdapter<Int>(ctx, 0, IDs) {

    override fun getView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }

    override fun getDropDownView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }
    private fun createView(position: Int, recycledView: View?, parent: ViewGroup): View {

        val id = getItem(position)

        val view = recycledView ?: LayoutInflater.from(context).inflate(
            R.layout.spinner_item_with_image,
            parent,
            false
        )

        val imageView = view.findViewById<ImageView>(R.id.imageViewItemSpinner)
        imageView.setBackgroundResource(id!!)
        return view
    }
}