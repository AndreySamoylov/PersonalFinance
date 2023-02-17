package com.example.finance.incomeUtilities

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.finance.MyConstants
import com.example.finance.MyFunction
import com.example.finance.MySpinnerImageWithTextArrayAdapter
import com.example.finance.R
import com.example.finance.database.MyDbManager
import com.example.finance.items.MyAccount
import com.example.finance.items.MyCategory
import com.example.finance.items.MyCost
import com.example.finance.items.MyIncome
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

class IncomeEditActivity : AppCompatActivity() {

    private lateinit var editTextIncomeSum : EditText
    private lateinit var editTextDateIncome : EditText
    private lateinit var dateInSqliteformat : String // Дата в формате для добавления в базу данных SQLite
    private lateinit var editTextIncomeComment : EditText
    private lateinit var buttonTodayIncome : Button
    private lateinit var buttonYesterdayIncome : Button
    private lateinit var buttonDayBeforeYesterdayIncome : Button
    private lateinit var buttonChangeDateIncome : Button
    private lateinit var buttonAddOrChangeIncome : Button
    private lateinit var buttonDeleteIncomeOrBack : Button
    private lateinit var spinnerAccounts : Spinner
    private lateinit var spinnerCategory : Spinner

    private lateinit var myDbManager : MyDbManager

    private lateinit var receivedIncome : MyIncome

    private var state : String = MyConstants.STATE_ADD

    private var dateAndTime = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income_edit)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        editTextIncomeSum = findViewById(R.id.editTextIncomeSum)
        editTextDateIncome = findViewById(R.id.editTextDateIncome)
        editTextIncomeComment = findViewById(R.id.editTextIncomeComment)
        buttonTodayIncome = findViewById(R.id.buttonTodayIncome)
        buttonYesterdayIncome = findViewById(R.id.buttonYesterdayIncome)
        buttonDayBeforeYesterdayIncome = findViewById(R.id.buttonDayBeforeYesterdayIncome)
        buttonChangeDateIncome = findViewById(R.id.buttonChangeDateIncome)
        buttonAddOrChangeIncome = findViewById(R.id.buttonAddOrChangeIncome)
        buttonDeleteIncomeOrBack = findViewById(R.id.buttonDeleteIncomeOrBack)
        spinnerAccounts = findViewById(R.id.spinnerEditAccountIncome)
        spinnerCategory = findViewById(R.id.spinnerEditCategoryIncome)

        myDbManager = MyDbManager(this)

        //Установить сегодняшную дату в текстовое поле
        val currentDate = Date()
        setDateTime(currentDate.time.milliseconds.inWholeMilliseconds)

        // Создание слушателя нажатий для кнопок:
        // сегодня, вчера, послезавтра, изменить дату, добавить или изменить, удалить или отмена
        val onButtonClickListener = View.OnClickListener { view ->
            val today = Date().time.milliseconds.inWholeMilliseconds
            when (view.id) {
                R.id.buttonTodayIncome -> {
                    setDateTime(today)
                }
                R.id.buttonYesterdayIncome -> {
                    val yesterday = today - 86400000 //86400000 - 1 день в миллисекундах
                    setDateTime(yesterday)
                }
                R.id.buttonDayBeforeYesterdayIncome -> {
                    val dayBeforeYesterday =
                        today - (86400000 * 2) //86400000 - 1 день в миллисекундах
                    setDateTime(dayBeforeYesterday)
                }
                R.id.buttonChangeDateIncome -> {
                    DatePickerDialog(
                        this@IncomeEditActivity, dateListener,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
                R.id.buttonAddOrChangeIncome -> {
                    if (state == MyConstants.STATE_ADD) {
                        val sum: Double = editTextIncomeSum.text.toString().toDouble()
                        val comment: String = editTextIncomeComment.text.toString()
                        val myAccount: MyAccount = spinnerAccounts.selectedItem as MyAccount
                        val accountID = myAccount._id
                        val myCategory: MyCategory = spinnerCategory.selectedItem as MyCategory
                        val categoryID = myCategory._id

                        val addIncome =
                            MyIncome(0, sum, dateInSqliteformat, comment, accountID, categoryID)
                        myDbManager.insertToIncome(addIncome)

                        refreshFields()

                        Toast.makeText(this, R.string.recordHaveAdded, Toast.LENGTH_SHORT).show()
                    } else {
                        val sum: Double = editTextIncomeSum.text.toString().toDouble()
                        val comment: String = editTextIncomeComment.text.toString()
                        val myAccount: MyAccount = spinnerAccounts.selectedItem as MyAccount
                        val accountID = myAccount._id
                        val myCategory: MyCategory = spinnerCategory.selectedItem as MyCategory
                        val categoryID = myCategory._id

                        val changeIncome = MyIncome(
                            receivedIncome._id,
                            sum,
                            dateInSqliteformat,
                            comment,
                            accountID,
                            categoryID
                        )
                        myDbManager.updateInIncome(changeIncome)

                        Toast.makeText(this, R.string.recordHaveChanged, Toast.LENGTH_SHORT).show()

                        finish()
                    }
                }
                R.id.buttonDeleteIncomeOrBack -> {
                    if (state == MyConstants.STATE_CHANGE_OR_DELETE) {
                        myDbManager.deleteFromIncome(receivedIncome._id)
                    }
                    finish()
                }
            }
        }
        // Назначение слушателя нажатий для кнопок
        buttonTodayIncome.setOnClickListener(onButtonClickListener)
        buttonYesterdayIncome.setOnClickListener(onButtonClickListener)
        buttonDayBeforeYesterdayIncome.setOnClickListener(onButtonClickListener)
        buttonChangeDateIncome.setOnClickListener(onButtonClickListener)
        buttonAddOrChangeIncome.setOnClickListener(onButtonClickListener)
        buttonDeleteIncomeOrBack.setOnClickListener(onButtonClickListener)
    }

    override fun onStart() {
        super.onStart()

        state = intent.getStringExtra(MyConstants.STATE).toString()
        if(state == MyConstants.STATE_CHANGE_OR_DELETE){
            receivedIncome = MyFunction.getSerializable(
                this,
                MyConstants.KEY_MY_INCOME,
                MyIncome::class.java
            )

            // Получение даты
            val calendar: Calendar = Calendar.getInstance()
            val year  = receivedIncome._date_income.subSequence(0, 4).toString().toInt()
            val month = receivedIncome._date_income.subSequence(5, 7).toString().toInt()
            val day = receivedIncome._date_income.subSequence(8, 10).toString().toInt()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.DAY_OF_MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            setDateTime(calendar.timeInMillis)

            editTextIncomeSum.setText(receivedIncome._sum.toString())
            editTextIncomeComment.setText(receivedIncome._comment)
            buttonAddOrChangeIncome.setText(R.string.change)
            buttonDeleteIncomeOrBack.setText(R.string.remove)
        }
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDatabase()

        // Создание адаптера для волчка для выбора счетов
        val adapterAccounts = ArrayAdapter(
            this,
            R.layout.account_item,
            R.id.textViewItemAccountName,
            myDbManager.fromAccounts
        )
        spinnerAccounts.adapter = adapterAccounts

        //Создание адаптера для волчка для выбора категорий
        val listCategory = myDbManager.fromCategories(MyConstants.CATEGORY_TYPE_INCOME) //as List<MyCategory>
        val spinnerAdapter = MySpinnerImageWithTextArrayAdapter(this, listCategory)
        spinnerCategory.adapter = spinnerAdapter
    }

    override fun onPause() {
        super.onPause()
        myDbManager.closeDatabase()
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDatabase()
    }

    // Обработчик события для выбора даты
    private var dateListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        dateAndTime[Calendar.YEAR] = year
        dateAndTime[Calendar.MONTH] = monthOfYear
        dateAndTime[Calendar.DAY_OF_MONTH] = dayOfMonth
        setDateTime(dateAndTime.timeInMillis)
    }

    // Метод, устанавливат в текстовое поле дату
    // И устанавливает дату в переменную с нужным форматом,
    // где milliseconds - время в миллисекундах
    private fun setDateTime(milliseconds : Long = 0) {
        editTextDateIncome.setText(
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
        dateInSqliteformat = if (month < 10){
            if (dayOfMonth < 10) "${year}-0${month}-0${dayOfMonth}"
            else "${year}-0${month}-${dayOfMonth}"
        }else{
            if (dayOfMonth < 10) "${year}-${month}-0${dayOfMonth}"
            else "${year}-${month}-${dayOfMonth}"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) finish()
        return super.onOptionsItemSelected(item)
    }

    // Обновить текстовые поля
    private fun refreshFields(){
        editTextIncomeComment.setText("")
        editTextIncomeSum.setText("")
    }
}