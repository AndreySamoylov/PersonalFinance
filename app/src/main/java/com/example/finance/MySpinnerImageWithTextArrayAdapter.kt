package com.example.finance

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.finance.R
import com.example.finance.items.MyCategory

class MySpinnerImageWithTextArrayAdapter(
    ctx: Context,
    items: List<MyCategory>) :
    ArrayAdapter<MyCategory>(ctx, 0, items) {

    override fun getView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }

    override fun getDropDownView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }
    private fun createView(position: Int, recycledView: View?, parent: ViewGroup): View {

        val item = getItem(position)

        val view = recycledView ?: LayoutInflater.from(context).inflate(
            R.layout.spinner_item_image_with_text,
            parent,
            false
        )

        if (item != null) {
            val imageView = view.findViewById<ImageView>(R.id.iv)
            val editText = view.findViewById<TextView>(R.id.editTextSpinnerWithImageAndText)
            imageView.setBackgroundColor(Color.parseColor(item._color))
            imageView.setImageResource(item._image)
            editText.text = item._name
        }
        return view
    }
}