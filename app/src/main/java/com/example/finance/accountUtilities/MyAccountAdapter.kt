package com.example.finance.accountUtilities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finance.R
import com.example.finance.databinding.AccountItemBinding
import com.example.finance.items.MyAccount

class MyAccountAdapter(val listener: Listener) : RecyclerView.Adapter<MyAccountAdapter.MyAccountHolder>() {

    val accountList = ArrayList<MyAccount>()

    class  MyAccountHolder(item : View) : RecyclerView.ViewHolder(item){
        val binding = AccountItemBinding.bind(item)
        fun bind(myAccount: MyAccount, listener : Listener) = with(binding){
            textViewItemAccountName.text = myAccount.name
            itemView.setOnClickListener{
                listener.onAccountClick(myAccount)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAccountHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.account_item, parent, false)
        return MyAccountHolder(view)
    }

    override fun getItemCount(): Int {
        return accountList.size
    }

    override fun onBindViewHolder(holder: MyAccountHolder, position: Int) {
        holder.bind(accountList[position], listener)
    }

    fun addAccount(myAccount: MyAccount){
        accountList.add(myAccount)
        notifyDataSetChanged()
    }

    fun addAllAccountList(list: List<MyAccount>){
        accountList.clear()
        accountList.addAll(list)
        notifyDataSetChanged()
    }

    interface Listener{
        // Нажатие на элемент в RecycleView
        fun onAccountClick(myAccount: MyAccount)
    }
}