package com.example.finance.incomeUtilities

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
import com.example.finance.items.MyIncome
import java.text.DecimalFormat

class MyIncomeAdapter(val listener: Listener, val context: Context) : RecyclerView.Adapter<MyIncomeAdapter.MyIncomeHolder>() {

    private val incomeList = ArrayList<MyIncome>()

    class  MyIncomeHolder(item : View) : RecyclerView.ViewHolder(item){
        private val binding = CostItemBinding.bind(item)
        private val myDbManager = MyDbManager(item.context)

        fun bind(myIncome: MyIncome, listener : Listener) = with(binding){

            myDbManager.openDatabase()

            val category = myDbManager.findCategoryByID(myIncome._category)
            val accountName = myDbManager.findAccountNameByID(myIncome._account)

            myDbManager.closeDatabase()

            val incomeDateText = "${itemView.context.resources.getText(R.string.date)} ${myIncome._date_income}"
            //Округление до 2-ух знаков полсе запятой
            val decimalFormat = DecimalFormat("##0.00")
            val strSum: String = decimalFormat.format(myIncome._sum)
            val incomeSumText = "${itemView.context.resources.getText(R.string.sum)} $strSum"

            itemCostCategory.setImageResource(category!!._image)
            itemCostCategory.setBackgroundColor(Color.parseColor(category._color))
            itemCostDate.text = incomeDateText
            itemCostSum.text= incomeSumText
            itemCostAccount.text = accountName


            itemView.setOnClickListener{
                listener.onIncomeClick(myIncome)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyIncomeHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cost_item, parent, false)
        return MyIncomeHolder(view)
    }

    override fun getItemCount(): Int {
        return incomeList.size
    }

    override fun onBindViewHolder(holder: MyIncomeHolder, position: Int) {
        holder.bind(incomeList[position], listener)
    }

    fun addCost(myIncome: MyIncome){
        incomeList.add(myIncome)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAllIncomeList(list: List<MyIncome>){
        incomeList.clear()
        incomeList.addAll(list)
        notifyDataSetChanged()
    }

    interface Listener{
        // Нажатие на элемент в RecycleView
        fun onIncomeClick(myIncome: MyIncome)
    }
}