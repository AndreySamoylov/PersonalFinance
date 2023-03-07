package com.example.finance

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.finance.accountUtilities.AccountFragment
import com.example.finance.categoryUtilities.CategoryFragment
import com.example.finance.costUtilities.CostFragment
import com.example.finance.database.MyDbManager
import com.example.finance.firebase.MyFirebaseUserData
import com.example.finance.incomeUtilities.IncomeFragment
import com.example.finance.items.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    // Массив фрагментов
    private val fragmentList = listOf(CostFragment.newInstance(), IncomeFragment.newInstance(), CategoryFragment.newInstance(), AccountFragment.newInstance())

    private lateinit var myDbManager: MyDbManager

    private lateinit var myDataBase: DatabaseReference // База данных firebase
    private lateinit var myAuth : FirebaseAuth

    private lateinit var drawerLayout : DrawerLayout
    private lateinit var drawerToggle: ActionBarDrawerToggle // Кнопка "Гамбургер". Открывает и закрывает меню
    private lateinit var navigationView : NavigationView

    @SuppressLint("CommitTransaction", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        myDbManager = MyDbManager(this)

        myDataBase = FirebaseDatabase.getInstance().getReference(MyConstants.USERS)
        myAuth = FirebaseAuth.getInstance()

        drawerLayout = findViewById(R.id.drawerLayoutMain)
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        navigationView = findViewById(R.id.navigationViewMenu)

        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Массив названий вкладок, полученный из ресорсов
        val fragmentTitles = resources.getStringArray(R.array.fragment_titles)

        val viewPager2 = findViewById<ViewPager2>(R.id.viewPager2PlaceHolder)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayoutMain)

        val adapter = MyViewPager2Adapter(this, fragmentList)
        // Подключение адаптера к ViewPager2
        viewPager2.adapter = adapter
        // Собрать с tabLayout
        TabLayoutMediator(tabLayout, viewPager2){ tab, position ->
            tab.text = fragmentTitles[position]
        }.attach()

        // Слушатель нажатий на элементы выдвигающегося списка
        navigationView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.item_enter_account -> {
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
                R.id.item_save_data_to_server -> {
                    val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> saveDataToServer()
                            DialogInterface.BUTTON_NEGATIVE -> {}
                        }
                    }

                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder.setMessage(R.string.doYouWantSaveDataToServer)
                        .setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener)
                        .show()
                }
                R.id.item_load_data_from_server -> {
                    val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> loadDataFromServer()
                                DialogInterface.BUTTON_NEGATIVE -> {}
                            }
                        }

                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder.setMessage(R.string.doYouWantLoadDataFromServer)
                        .setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener)
                        .show()
                }
                R.id.item_read_from_cvs -> {
                    val intent = Intent(this@MainActivity, ReadCvsActivity::class.java)
                    startActivity(intent)
                }
                R.id.item_exit ->{
                    finishAffinity()
                }
            }
            true
        }
    }

    // Нажатие на какую-либо кнопку из меню (которое вверху)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)){
            if (drawerLayout.isDrawerOpen(GravityCompat.START)){
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            else{
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Сохранение данных на сервер firebase. Действие перезаписывает все записи на сервере!!!
    private fun saveDataToServer(){
        if (myAuth.currentUser != null) {
            myDbManager.openDatabase()
            val listCost: List<MyCost> = myDbManager.fromCosts
            val listIncome: List<MyIncome> = myDbManager.fromIncome
            val listCategory: List<MyCategoryFirebase> = myDbManager.fromCategoriesFirebaseType
            val listAccount: List<MyAccount> = myDbManager.fromAccounts
            myDbManager.closeDatabase()
            val allData =
                MyFirebaseUserData(listCost, listIncome, listCategory, listAccount)
            val userName = myAuth.currentUser!!.email.toString().replace(".", "")
            myDataBase.child(userName).setValue(allData)
            Toast.makeText(this@MainActivity, R.string.dataSuccesfulSave,Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this@MainActivity, R.string.youHaveNotAuth,Toast.LENGTH_SHORT).show()
        }
    }

    // Загрузка данных с сервера firebase. Действие перезаписывает все записи!!!
    private fun loadDataFromServer(){
        if (myAuth.currentUser != null) {
            val userName = myAuth.currentUser!!.email.toString().replace(".", "")
            myDataBase.child(userName).get().addOnSuccessListener { snapshot ->
                val allData = snapshot.getValue(MyFirebaseUserData::class.java)
                val listCost = allData?.listCost
                val listIncome = allData?.listIncome
                val listCategory = allData?.listCategory
                val listAccount = allData?.listAccount
                // Полное обновление базы данных
                myDbManager.openDatabase()

                myDbManager.clearTableCategories()
                for (category in listCategory!!) {
                    // Переделывает категорию в нужный формат, т.к. она сохранялась в другом
                    val myCategory = MyCategory(
                        category._id,
                        category._name,
                        category._color,
                        category._image,
                        category._type.toByte(),
                        category._undeletable.toByte()
                    )
                    myDbManager.insertToCategoriesWithID(myCategory)
                }
                myDbManager.clearTableAccounts()
                for (account in listAccount!!) myDbManager.insertToAccountsWithID(
                    account
                )
                myDbManager.clearTableCosts()
                for (cost in listCost!!) myDbManager.insertToCostsWithID(cost)
                myDbManager.clearTableIncome()
                for (income in listIncome!!) myDbManager.insertToIncomeWithID(income)

                myDbManager.closeDatabase()

                Toast.makeText(
                    this@MainActivity,
                    R.string.dataSuccesfulLoad,
                    Toast.LENGTH_SHORT
                ).show()
                Log.i("firebase", "Got value ${snapshot.value}")
            }.addOnFailureListener { snapshot ->
                Toast.makeText(
                    this@MainActivity,
                    R.string.dataFailedLoad,
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("firebase", "Error getting data $snapshot")
            }
        } else {
            Toast.makeText(
                this@MainActivity,
                R.string.youHaveNotAuth,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}