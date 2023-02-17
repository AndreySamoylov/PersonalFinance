package com.example.finance.costUtilities

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finance.R
import com.example.finance.database.MyDbManager
import com.example.finance.databinding.CostItemBinding
import com.example.finance.items.MyCost
import java.text.DecimalFormat


class MyCostAdapter(val listener: Listener, val context: Context) : RecyclerView.Adapter<MyCostAdapter.MyCostHolder>() {

    private val costList = ArrayList<MyCost>()

    class  MyCostHolder(item : View) : RecyclerView.ViewHolder(item){
        private val binding = CostItemBinding.bind(item)
        private val myDbManager = MyDbManager(item.context)

        fun bind(myCost: MyCost, listener : Listener) = with(binding){

            myDbManager.openDatabase()

            val category = myDbManager.findCategoryByID(myCost._category)
            val accountName = myDbManager.findAccountNameByID(myCost._account)

            myDbManager.closeDatabase()

            val costDateText = "${itemView.context.resources.getText(R.string.date)} ${myCost._date_cost}"
            //Округление до 2-ух знаков полсе запятой
            val decimalFormat = DecimalFormat("##0.00")
            val strSum: String = decimalFormat.format(myCost._sum)
            val costSumText = "${itemView.context.resources.getText(R.string.sum)} $strSum"

            itemCostCategory.setImageResource(category!!._image)
            itemCostCategory.setBackgroundColor(Color.parseColor(category._color))
            itemCostDate.text = costDateText
            itemCostSum.text= costSumText
            itemCostAccount.text = accountName


            itemView.setOnClickListener{
                listener.onCostClick(myCost)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCostHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cost_item, parent, false)
        return MyCostHolder(view)
    }

    override fun getItemCount(): Int {
        return costList.size
    }

    override fun onBindViewHolder(holder: MyCostHolder, position: Int) {
        holder.bind(costList[position], listener)
    }

    fun addCost(myCost: MyCost){
        costList.add(myCost)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAllCostList(list: List<MyCost>){
        costList.clear()
        costList.addAll(list)
        notifyDataSetChanged()
    }

    interface Listener{
        // Нажатие на элемент в RecycleView
        fun onCostClick(myCost: MyCost)
    }
}