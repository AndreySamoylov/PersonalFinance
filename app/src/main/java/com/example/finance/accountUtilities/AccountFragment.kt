package com.example.finance.accountUtilities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finance.MyConstants
import com.example.finance.R
import com.example.finance.database.MyDbManager
import com.example.finance.items.MyAccount

class AccountFragment : Fragment(), MyAccountAdapter.Listener {

    private val adapter = MyAccountAdapter(this)

    private lateinit var currentContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        currentContext = inflater.context

        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.resycleViewAccounts)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = adapter

        view.findViewById<Button>(R.id.buttonGoToAccoutEditActivity).setOnClickListener {
            val intent = Intent(view.context, AccountEditActivity::class.java)
            intent.putExtra(MyConstants.STATE, MyConstants.STATE_ADD)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val myDbManager = MyDbManager(currentContext)

        myDbManager.openDatabase()
        val list : List<MyAccount> = myDbManager.fromAccounts as List<MyAccount>
        adapter.addAllAccountList(list)
        myDbManager.closeDatabase()
    }

    companion object{
        @JvmStatic
        fun newInstance() = AccountFragment()
    }

    override fun onAccountClick(myAccount: MyAccount) {
        val intent = Intent(currentContext, AccountEditActivity::class.java)
        intent.putExtra(MyConstants.STATE, MyConstants.STATE_CHANGE_OR_DELETE)
        intent.putExtra(MyConstants.KEY_MY_ACCOUNT, myAccount)
        startActivity(intent)
    }
}