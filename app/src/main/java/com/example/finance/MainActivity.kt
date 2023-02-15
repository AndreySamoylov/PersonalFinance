package com.example.finance

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.finance.accountUtilities.AccountFragment
import com.example.finance.categoryUtilities.CategoryFragment
import com.example.finance.costUtilities.CostFragment
import com.example.finance.database.MyDbManager
import com.example.finance.incomeUtilities.IncomeFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    // Массив фрагментов
    private val fragmentList = listOf(CostFragment.newInstance(), IncomeFragment.newInstance(), CategoryFragment.newInstance(), AccountFragment.newInstance())

    private lateinit var myDbManager: MyDbManager

    private lateinit var drawerLayout : DrawerLayout
    private lateinit var drawerToggle: ActionBarDrawerToggle // Кнопка "Гамбургер". Открывает и закрывает меню
    private lateinit var navigationView : NavigationView

    @SuppressLint("CommitTransaction", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        myDbManager = MyDbManager(this)

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
}