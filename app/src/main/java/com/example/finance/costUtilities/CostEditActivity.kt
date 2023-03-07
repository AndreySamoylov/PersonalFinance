package com.example.finance.costUtilities

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.text.format.DateUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View.OnClickListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finance.MyConstants
import com.example.finance.MyFunction
import com.example.finance.MySpinnerImageWithTextArrayAdapter
import com.example.finance.R
import com.example.finance.database.MyDbManager
import com.example.finance.items.MyAccount
import com.example.finance.items.MyCategory
import com.example.finance.items.MyCost
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import java.util.*
import kotlin.time.Duration.Companion.milliseconds


class CostEditActivity : AppCompatActivity() {

    private lateinit var editTextCostSum : EditText
    private lateinit var editTextDateCost : EditText
    private lateinit var dateInSqliteformat : String // Дата в формате для добавления в базу данных SQLite
    private lateinit var editTextCostComment : EditText
    private lateinit var buttonTodayCost : Button
    private lateinit var buttonYesterdayCost : Button
    private lateinit var buttonDayBeforeYesterdayCost : Button
    private lateinit var buttonChangeDateCost : Button
    private lateinit var buttonAddOrChangeCost : Button
    private lateinit var buttonDeleteCostOrBack : Button
    private lateinit var spinnerAccounts : Spinner
    private lateinit var spinnerCategory : Spinner

    private lateinit var myDbManager : MyDbManager

    private lateinit var receivedCost : MyCost

    private var state : String = MyConstants.STATE_ADD

    private var dateAndTime = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cost_edit)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        editTextCostSum = findViewById(R.id.editTextCostSum)
        editTextDateCost = findViewById(R.id.editTextDateCost)
        editTextCostComment = findViewById(R.id.editTextCostComment)
        buttonTodayCost = findViewById(R.id.buttonTodayCost)
        buttonYesterdayCost = findViewById(R.id.buttonYesterdayCost)
        buttonDayBeforeYesterdayCost = findViewById(R.id.buttonDayBeforeYesterdayCost)
        buttonChangeDateCost = findViewById(R.id.buttonChangeDateCost)
        buttonAddOrChangeCost = findViewById(R.id.buttonAddOrChangeCost)
        buttonDeleteCostOrBack = findViewById(R.id.buttonDeleteCostOrBack)
        spinnerAccounts = findViewById(R.id.spinnerEditAccountCost)
        spinnerCategory = findViewById(R.id.spinnerEditCategoryCost)

        myDbManager = MyDbManager(this)

        //Установить сегодняшную дату в текстовое поле
        val currentDate = Date()
        setDateTime(currentDate.time.milliseconds.inWholeMilliseconds)

        // Создание слушателя нажатий для кнопок:
        // сегодня, вчера, послезавтра, изменить дату, добавить или изменить, удалить или отмена
        val onButtonClickListener = OnClickListener { view ->
            val today = Date().time.milliseconds.inWholeMilliseconds
            when (view.id) {
                R.id.buttonTodayCost -> {
                    setDateTime(today)
                }
                R.id.buttonYesterdayCost -> {
                    val yesterday = today - 86400000 //86400000 - 1 день в миллисекундах
                    setDateTime(yesterday)
                }
                R.id.buttonDayBeforeYesterdayCost -> {
                    val dayBeforeYesterday = today - (86400000 * 2) //86400000 - 1 день в миллисекундах
                    setDateTime(dayBeforeYesterday)
                }
                R.id.buttonChangeDateCost ->{
                    DatePickerDialog(
                        this@CostEditActivity, dateListener,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
                R.id.buttonAddOrChangeCost ->{
                    if (state == MyConstants.STATE_ADD) {
                        val sum: Double = editTextCostSum.text.toString().toDouble()
                        val comment: String = editTextCostComment.text.toString()
                        val myAccount: MyAccount = spinnerAccounts.selectedItem as MyAccount
                        val accountID = myAccount._id
                        val myCategory: MyCategory = spinnerCategory.selectedItem as MyCategory
                        val categoryID = myCategory._id

                        val addCost = MyCost(0, sum, dateInSqliteformat, comment, accountID, categoryID)
                        myDbManager.insertToCosts(addCost)

                        refreshFields()

                        Toast.makeText(this, R.string.recordHaveAdded, Toast.LENGTH_SHORT).show()
                    }
                    else{
                        val sum: Double = editTextCostSum.text.toString().toDouble()
                        val comment: String = editTextCostComment.text.toString()
                        val myAccount: MyAccount = spinnerAccounts.selectedItem as MyAccount
                        val accountID = myAccount._id
                        val myCategory: MyCategory = spinnerCategory.selectedItem as MyCategory
                        val categoryID = myCategory._id

                        val changeCost = MyCost(receivedCost._id, sum, dateInSqliteformat, comment, accountID, categoryID)
                        myDbManager.updateInCosts(changeCost)

                        finish()

                        Toast.makeText(this, R.string.recordHaveChanged, Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.buttonDeleteCostOrBack ->{
                    if (state == MyConstants.STATE_CHANGE_OR_DELETE){
                        myDbManager.deleteFromCosts(receivedCost._id)
                    }
                    finish()
                }
            }
        }
        // Назначение слушателя нажатий для кнопок
        buttonTodayCost.setOnClickListener(onButtonClickListener)
        buttonYesterdayCost.setOnClickListener(onButtonClickListener)
        buttonDayBeforeYesterdayCost.setOnClickListener(onButtonClickListener)
        buttonChangeDateCost.setOnClickListener(onButtonClickListener)
        buttonAddOrChangeCost.setOnClickListener(onButtonClickListener)
        buttonDeleteCostOrBack.setOnClickListener(onButtonClickListener)
    }

    override fun onStart() {
        super.onStart()

        state = intent.getStringExtra(MyConstants.STATE).toString()
        if(state == MyConstants.STATE_CHANGE_OR_DELETE){
            receivedCost = MyFunction.getSerializable(
                this,
                MyConstants.KEY_MY_COST,
                MyCost::class.java
            )

            // Получение даты
            val calendar: Calendar = Calendar.getInstance()
            val year  = receivedCost._date_cost.subSequence(0, 4).toString().toInt()
            val month = receivedCost._date_cost.subSequence(5, 7).toString().toInt()
            val day = receivedCost._date_cost.subSequence(8, 10).toString().toInt()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.DAY_OF_MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            setDateTime(calendar.timeInMillis)

            editTextCostSum.setText(receivedCost._sum.toString())
            editTextCostComment.setText(receivedCost._comment)
            buttonAddOrChangeCost.setText(R.string.change)
            buttonDeleteCostOrBack.setText(R.string.remove)
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
        val listCategory = myDbManager.fromCategories(MyConstants.CATEGORY_TYPE_COST) //as List<MyCategory>
        val spinnerAdapter = MySpinnerImageWithTextArrayAdapter(this, listCategory)
        spinnerCategory.adapter = spinnerAdapter
    }

    override fun onPause() {
        super.onPause()
        myDbManager.closeDatabase()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cost_edit_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> finish()
            R.id.costEditMenuScanner -> barcodeLauncher.launch(ScanOptions())
        }
        return super.onOptionsItemSelected(item)
    }

    // Обработчик события для выбора даты
    private var dateListener = OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        dateAndTime[Calendar.YEAR] = year
        dateAndTime[Calendar.MONTH] = monthOfYear
        dateAndTime[Calendar.DAY_OF_MONTH] = dayOfMonth
        setDateTime(dateAndTime.timeInMillis)
    }

    // Метод, устанавливат в текстовое поле дату
    // И устанавливает дату в переменную с нужным форматом,
    // где milliseconds - время в миллисекундах
    private fun setDateTime(milliseconds : Long = 0) {
        editTextDateCost.setText(
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

    // Обновить текстовые поля
    private fun refreshFields(){
        editTextCostComment.setText("")
        editTextCostSum.setText("")
    }

    // Сканер
    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            Toast.makeText( this@CostEditActivity, R.string.canceled, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(
                this@CostEditActivity,
                R.string.scanned,
                Toast.LENGTH_LONG
            ).show()

            //Обработка строки

            //Получение даты
            val calendar: Calendar = Calendar.getInstance()
            val year = result.contents.substring(2, 6).toInt()
            val month = result.contents.substring(6, 8).toInt()
            val dayOfMonth = result.contents.substring(8, 10).toInt()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.DAY_OF_MONTH, month - 1)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            setDateTime(calendar.timeInMillis)

            // Получение суммы

            var sIndex = 0 // Индекс буквы 's'
            var pointIndex = 0 //Индекс точки '.'
            for (i in 0 until result.contents.length){
                if (result.contents.elementAt(i) == 's') {
                    sIndex = i
                }
                if (result.contents.elementAt(i) == '.'){
                    pointIndex = i
                }
            }

            val sum = result.contents.substring(sIndex + 2, pointIndex + 3)
            editTextCostSum.setText(sum)
        }
    }
}