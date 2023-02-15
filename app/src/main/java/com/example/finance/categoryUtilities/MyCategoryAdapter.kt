package com.example.finance.categoryUtilities

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finance.R
import com.example.finance.databinding.CategoryItemBinding
import com.example.finance.items.MyCategory

class MyCategoryAdapter(val listener: Listener) : RecyclerView.Adapter<MyCategoryAdapter.MyCategoryHolder>() {

    private val categoryList = ArrayList<MyCategory>()

    class  MyCategoryHolder(item : View) : RecyclerView.ViewHolder(item){
        private val binding = CategoryItemBinding.bind(item)
        fun bind(myCategory: MyCategory, listener : Listener) = with(binding){
            textViewitemCategoryName.text = myCategory._name
            imageViewItemCategory.setBackgroundColor(Color.parseColor(myCategory._color))
            imageViewItemCategory.setImageResource(myCategory._image)
            itemView.setOnClickListener{
                listener.onCategoryClick(myCategory)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCategoryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return MyCategoryHolder(view)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: MyCategoryHolder, position: Int) {
        holder.bind(categoryList[position], listener)
    }

    fun addCategory(myCategory: MyCategory){
        categoryList.add(myCategory)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAllCategoryList(list: List<MyCategory>){
        categoryList.clear()
        categoryList.addAll(list)
        notifyDataSetChanged()
    }

    interface Listener{
        // Нажатие на элемент в RecycleView
        fun onCategoryClick(myCategory: MyCategory)
    }
}