package com.example.finance.categoryUtilities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finance.MyConstants
import com.example.finance.R
import com.example.finance.database.MyDbManager
import com.example.finance.items.MyCategory

class CategoryFragment : Fragment(), MyCategoryAdapter.Listener {

    private lateinit var radioButtonCategoryCost : RadioButton

    private val adapter = MyCategoryAdapter(this)

    private lateinit var myDbManager : MyDbManager

    private lateinit var currentContext: Context
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        myDbManager = MyDbManager(inflater.context)
        currentContext = inflater.context

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycleviewCategories)
        recyclerView.layoutManager = GridLayoutManager(view.context, 3)
        recyclerView.adapter = adapter
        radioButtonCategoryCost  = view.findViewById(R.id.radioButtonShowCategoryCost)

        view.findViewById<Button>(R.id.buttonGoToEditCategory).setOnClickListener {
            val intent = Intent(view.context, CategoryEditActivity::class.java)
            intent.putExtra(MyConstants.STATE, MyConstants.STATE_ADD)
            startActivity(intent)
        }

        radioButtonCategoryCost.setOnCheckedChangeListener { _, isChecked ->
            // Если выбрана радиокнопка "расход"
            // Иначе выбрана кнопка "доход"
            if (isChecked){
                myDbManager.openDatabase()
                val list : List<MyCategory> = myDbManager.fromCategories(MyConstants.CATEGORY_TYPE_COST)
                adapter.addAllCategoryList(list)
                myDbManager.closeDatabase()
            }
            else{
                myDbManager.openDatabase()
                val list : List<MyCategory> = myDbManager.fromCategories(MyConstants.CATEGORY_TYPE_INCOME)
                adapter.addAllCategoryList(list)
                myDbManager.closeDatabase()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        myDbManager.openDatabase()

        val list : List<MyCategory> = myDbManager.fromCategories(MyConstants.CATEGORY_TYPE_COST)
        adapter.addAllCategoryList(list)
        radioButtonCategoryCost.isChecked = true

        myDbManager.closeDatabase()
    }

    companion object{
        @JvmStatic
        fun newInstance() = CategoryFragment()
    }
    override fun onCategoryClick(myCategory: MyCategory) {
        val intent = Intent(currentContext, CategoryEditActivity::class.java)
        intent.putExtra(MyConstants.STATE, MyConstants.STATE_CHANGE_OR_DELETE)
        intent.putExtra(MyConstants.KEY_MY_CATEGORY, myCategory)
        startActivity(intent)
    }
}