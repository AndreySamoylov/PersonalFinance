package com.example.finance.incomeUtilities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finance.MyConstants
import com.example.finance.R
import com.example.finance.database.MyDbManager
import com.example.finance.items.MyAccount
import com.example.finance.items.MyIncome
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

class ListIncomeActivity : AppCompatActivity(), MyIncomeAdapter.Listener {

    private val adapter = MyIncomeAdapter(this, this)

    private lateinit var editTextInitialDateIncome : EditText
    private lateinit var initialDate : String
    private lateinit var editTextFinalDateIncome : EditText
    private lateinit var finalDate : String
    private lateinit var spinnerAccounts : Spinner

    private lateinit var myDbManager : MyDbManager

    private var dateAndTime = Calendar.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_income)

        myDbManager = MyDbManager(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        editTextInitialDateIncome = findViewById(R.id.editTextInitialDateIncome)
        editTextFinalDateIncome = findViewById(R.id.editTextFinalDateIncome)
        spinnerAccounts = findViewById(R.id.spinnerAccountOnShowIncome)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewIncome)

        // Создание слушателя нажатий
        val onEditTextClickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.editTextInitialDateIncome -> {
                    DatePickerDialog(
                        this@ListIncomeActivity, dateListenerDateFrom,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)
                    ).show()
                    setRecycleViewAdaper()
                }
                R.id.editTextFinalDateIncome -> {
                    DatePickerDialog(
                        this@ListIncomeActivity, dateListenerDateBefore,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)
                    ).show()
                    setRecycleViewAdaper()
                }
            }
        }
        //Назначение слушателя для текстовых полей
        editTextInitialDateIncome.setOnClickListener(onEditTextClickListener)
        editTextFinalDateIncome.setOnClickListener(onEditTextClickListener)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        spinnerAccounts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                setRecycleViewAdaper()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        myDbManager.openDatabase()

        // Создание адаптера для волчка выбора счетов
        val accountList : ArrayList<MyAccount> = arrayListOf(MyAccount(0, "Все счета"))
        accountList.addAll(myDbManager.fromAccounts)
        val adapterAccounts = ArrayAdapter(
            this,
            R.layout.account_item,
            R.id.textViewItemAccountName,
            accountList
        )
        spinnerAccounts.adapter = adapterAccounts

        // Инициализация календаря
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = Date().time.milliseconds.inWholeMilliseconds
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        //Приведение даты в формат YYYY-MM-DD
        // Установить сегодняшную дату в переменную
        initialDate = if (month < 10) {
            if (dayOfMonth < 10) "${year}-0${month}-0${dayOfMonth}"
            else "${year}-0${month}-${dayOfMonth}"
        } else {
            if (dayOfMonth < 10) "${year}-${month}-0${dayOfMonth}"
            else "${year}-${month}-${dayOfMonth}"
        }
        // Установить первое число текущего мемяца
        finalDate = if (month < 10) {
            if (dayOfMonth < 10) "${year}-0${month}-01"
            else "${year}-0${month}-01"
        } else {
            if (dayOfMonth < 10) "${year}-${month}-01"
            else "${year}-${month}-01"
        }

        // Установка текущей даты
        setDateTimeFinal(calendar.timeInMillis)

        // Установка в календарь первого дня текущего месяца
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        // Установка первого числа текущего месяца
        setDateTimeInitial(calendar.timeInMillis)

        myDbManager.closeDatabase()
    }

    override fun onIncomeClick(myIncome: MyIncome) {
        val intent = Intent(this@ListIncomeActivity, IncomeEditActivity::class.java)
        intent.putExtra(MyConstants.STATE, MyConstants.STATE_CHANGE_OR_DELETE)
        intent.putExtra(MyConstants.KEY_MY_INCOME, myIncome)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDatabase()

        setRecycleViewAdaper()
    }

    override fun onPause() {
        super.onPause()
        myDbManager.closeDatabase()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) finish()
        return super.onOptionsItemSelected(item)
    }

    // Обработчик события для выбора даты "от которой нужно считать"
    private var dateListenerDateFrom = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        dateAndTime[Calendar.YEAR] = year
        dateAndTime[Calendar.MONTH] = monthOfYear
        dateAndTime[Calendar.DAY_OF_MONTH] = dayOfMonth
        setDateTimeInitial(dateAndTime.timeInMillis)
    }

    // Обработчик события для выбора даты "до которой нужно считать"
    private var dateListenerDateBefore = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        dateAndTime[Calendar.YEAR] = year
        dateAndTime[Calendar.MONTH] = monthOfYear
        dateAndTime[Calendar.DAY_OF_MONTH] = dayOfMonth
        setDateTimeFinal(dateAndTime.timeInMillis)
    }

    private fun setDateTimeInitial(milliseconds : Long = 0) {
        editTextInitialDateIncome.setText(
            DateUtils.formatDateTime(
                this,
                milliseconds,
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
            )
        )

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliseconds
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        //Приведение даты в формат YYYY-MM-DD
        initialDate = if (month < 10) {
            if (dayOfMonth < 10) "${year}-0${month}-0${dayOfMonth}"
            else "${year}-0${month}-${dayOfMonth}"
        } else {
            if (dayOfMonth < 10) "${year}-${month}-0${dayOfMonth}"
            else "${year}-${month}-${dayOfMonth}"
        }

        setRecycleViewAdaper()
    }

    private fun setDateTimeFinal(milliseconds : Long = 0) {
        editTextFinalDateIncome.setText(
            DateUtils.formatDateTime(
                this,
                milliseconds,
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
            )
        )

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliseconds
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        //Приведение даты в формат YYYY-MM-DD
        finalDate = if (month < 10) {
            if (dayOfMonth < 10) "${year}-0${month}-0${dayOfMonth}"
            else "${year}-0${month}-${dayOfMonth}"
        } else {
            if (dayOfMonth < 10) "${year}-${month}-0${dayOfMonth}"
            else "${year}-${month}-${dayOfMonth}"
        }

        setRecycleViewAdaper()
    }

    private fun setRecycleViewAdaper(){
        val account : MyAccount = spinnerAccounts.selectedItem as MyAccount
        //Если выбран элемент с id ноль (т.е все счета)), то вывести данные из всех счетов, иначе из выбранного
        val list : List<MyIncome> = if (account._id  == (0).toLong())  myDbManager.fromIncome(initialDate, finalDate)
        else myDbManager.fromIncome(initialDate, finalDate, account._id)
        adapter.addAllIncomeList(list)
    }
}