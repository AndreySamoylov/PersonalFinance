package com.example.finance.costUtilities

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.finance.R
import com.example.finance.MyConstants
import com.example.finance.database.MyDbManager
import com.example.finance.items.MyAccount
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

class CostFragment : Fragment() {

    private lateinit var buttonGoToEditCosts: Button
    private lateinit var buttonGoToAllCostOperations: Button
    private lateinit var pieChart: PieChart
    private lateinit var spinnerAccounts : Spinner
    private lateinit var editTextInitialDateCost : EditText
    private lateinit var initialDate : String
    private lateinit var editTextFinalDateCost : EditText
    private lateinit var finalDate : String

    private var dateAndTime = Calendar.getInstance()

    private lateinit var myDbManager: MyDbManager

    private lateinit var currentContext: Context
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        myDbManager = MyDbManager(inflater.context)

        currentContext = inflater.context

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cost, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonGoToEditCosts = view.findViewById(R.id.buttonGoToEditCosts)
        buttonGoToAllCostOperations = view.findViewById(R.id.buttonGoToAllCostOperations)
        pieChart = view.findViewById(R.id.pieChartCosts)
        spinnerAccounts = view.findViewById(R.id.fragmentCostSpinnerAccount)
        editTextInitialDateCost = view.findViewById(R.id.fragmentCostInitialDate)
        editTextFinalDateCost = view.findViewById(R.id.fragmentCostFinalDate)

        buttonGoToEditCosts.setOnClickListener {
            val intent = Intent(currentContext, CostEditActivity::class.java)
            intent.putExtra(MyConstants.STATE, MyConstants.STATE_ADD)
            startActivity(intent)
        }

        buttonGoToAllCostOperations.setOnClickListener {
            val intent = Intent(currentContext, ListCostActivity::class.java)
            startActivity(intent)
        }

        spinnerAccounts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                createPieChart()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        // Создание слушателя нажатий
        val onEditTextClickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.fragmentCostInitialDate -> {
                    DatePickerDialog(
                        currentContext, dateListenerDateFrom,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
                R.id.fragmentCostFinalDate -> {
                    DatePickerDialog(
                        currentContext, dateListenerDateBefore,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            }
        }
        //Назначение слушателя для текстовых полей
        editTextInitialDateCost.setOnClickListener(onEditTextClickListener)
        editTextFinalDateCost.setOnClickListener(onEditTextClickListener)
    }

    override fun onStart() {
        super.onStart()
        myDbManager.openDatabase()

        // Создание адаптера для волчка выбора счетов
        val accountList: ArrayList<MyAccount> = arrayListOf(MyAccount(0, "Все счета"))
        accountList.addAll(myDbManager.fromAccounts)

        val adapterAccounts = ArrayAdapter(
            currentContext,
            R.layout.account_item,
            R.id.textViewItemAccountName,
            accountList
        )
        spinnerAccounts.adapter = adapterAccounts

        myDbManager.closeDatabase()

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
    }

    companion object{
        @JvmStatic
        fun newInstance() = CostFragment()
    }

    private fun createPieChart(){
        myDbManager.openDatabase()

        lateinit var selectedAccount : MyAccount
        try{
            selectedAccount = spinnerAccounts.selectedItem as MyAccount // Выбранные счёт
        }catch (exception : NullPointerException){
            Log.d("CostFragment", exception.toString())
            return
        }

        val pieEntriesAll = ArrayList<PieEntry>()
        var allSum = 0f // Сумма всех расходов
        val categoryList = myDbManager.fromCategories // Список категорий
        for (category in categoryList){
            // Если выбран 1-ый элемент волчка, то есть нужно делать выборку из всех счетов
            // Иначе сделать выборку из одного выбранного счёта
            val sum = if (selectedAccount._id == (0).toLong()) myDbManager.getSumCostByCategory(category._id, initialDate, finalDate).roundToInt() // Сумма расхода по категории
            else myDbManager.getSumCostByCategory(category._id, selectedAccount._id, initialDate, finalDate).roundToInt() // Сумма расхода по категории

            if(sum > 0) { // Если сумма больше нуля добавить в список
                val pieEntry = PieEntry(sum.toFloat(), category._name)
                pieEntriesAll.add(pieEntry)
            }
            allSum += sum
        }

        myDbManager.closeDatabase()

        val pieEntriesSelective = ArrayList<PieEntry>() // Список который будет добавлен в диаграмму
        var otherSum = 0f // Сумма остальных категорий
        for (pieEntry in pieEntriesAll){
            // Процент суммы одной категории от общей суммы
            val percent : Float = (100 * pieEntry.value) / allSum
            if (percent > 4){ // Если этот процент больше Х
                pieEntriesSelective.add(pieEntry) // Добавить в список
            }else{
                otherSum += pieEntry.value //
            }
        }
        // Добавить остальную сумму в виде последней записи
        if (otherSum != 0f) pieEntriesSelective.add(PieEntry(otherSum, currentContext.resources.getString(R.string.other)))

        // Сортировка списка по убыванию, по сумме
        pieEntriesSelective.sortedByDescending { it.value}

        val pieDataSet = PieDataSet(pieEntriesSelective, currentContext.resources.getString(R.string.categories))
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS, 255)
        pieDataSet.valueTextSize = 12f
        pieDataSet.valueTextColor = Color.BLACK

        val pieData = PieData(pieDataSet)
        pieChart.data = pieData

        pieChart.setEntryLabelColor(R.color.black)

        pieChart.legend.form = Legend.LegendForm.CIRCLE
        pieChart.description.isEnabled = false
        pieChart.centerText = currentContext.resources.getString(R.string.cost)
        pieChart.animateY(500,)
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
        editTextInitialDateCost.setText(
            DateUtils.formatDateTime(
                currentContext,
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

        createPieChart()
    }

    private fun setDateTimeFinal(milliseconds : Long = 0) {
        editTextFinalDateCost.setText(
            DateUtils.formatDateTime(
                currentContext,
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

        createPieChart()
    }
}