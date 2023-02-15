package com.example.finance

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.finance.database.MyDbManager
import com.example.finance.items.MyAccount
import com.example.finance.items.MyCategory
import com.example.finance.items.MyCost
import com.example.finance.items.MyIncome
import java.io.BufferedReader
import java.io.InputStream
import java.nio.charset.Charset

class ReadCvsActivity : AppCompatActivity() {

    private lateinit var spinnerAccounts : Spinner

    private lateinit var myDbManager: MyDbManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_cvs)

        spinnerAccounts = findViewById(R.id.spinnerAccountsCvsReader)

        // Активировать ActionBar - меню
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        myDbManager = MyDbManager(this)

        findViewById<Button>(R.id.buttonReadCvsFile).setOnClickListener {
            getContent.launch("*/*")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) finish()
        return super.onOptionsItemSelected(item)
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
    }

    override fun onPause() {
        super.onPause()
        myDbManager.closeDatabase()
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDatabase()
    }
    private val getContent =  registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        // Handle the returned Uri
        var inputStream: InputStream?
        try{
            inputStream = uri?.let { applicationContext.contentResolver.openInputStream(it) }
                //uri.let { applicationContext.contentResolver.openInputStream(it) }
        }catch (exception : NullPointerException){
            Log.d("ReadCvsActivity", exception.toString())
            return@registerForActivityResult
        }

        val reader = BufferedReader(inputStream?.reader(Charset.forName("Windows-1251")))
        try {
            var line: String = reader.readLine() // Первая строка содержит заголовки
            while ( line != null) {
                line = reader.readLine() // Чтение данных
                val items: ArrayList<String> = line.replace("\"", "").split(";") as java.util.ArrayList<String>
                Log.d("Readline", items.toString())
                readLineFromCvsTinkoff(items)
            }
            Toast.makeText(this, "Чтение завершено успещно", Toast.LENGTH_SHORT)
        }catch (e: java.lang.NullPointerException){
            Log.d("Error", e.toString())
        }
        finally {
            reader.close()
        }
    }

    private fun readLineFromCvsTinkoff(array: ArrayList<String>){
        myDbManager.openDatabase()
        val sum : Double = array[4].replace(",", ".").toDouble()
        val date : String = array[1]
        //DD-MM-YYYY -> YYYY-MM-DD
        val dateSQLiteFormat = "${date.subSequence(6, 10)}-${date.subSequence(3, 5)}-${date.subSequence(0,2)}"
        val comment : String = array[11]
        val account = spinnerAccounts.selectedItem as MyAccount
        var idCategory : Long = myDbManager.findCategoryByName(array[9]).toLong()
        //Если такая категория не найдена, то её надо добавить
        if (idCategory == (-1).toLong()){
            val addCategory = MyCategory(0, array[9], "#FF00FFFF", 0, 0, 0)
            myDbManager.insertToCategories(addCategory)
        }
        idCategory = myDbManager.findCategoryByName(array[9]).toLong()

        if (sum > 0){
            val addIncome =  MyIncome(0 , sum, dateSQLiteFormat, comment, account._id, idCategory)
            myDbManager.insertToIncome(addIncome)
        }else{
            val addCost = MyCost(0, -sum, dateSQLiteFormat, comment, account._id, idCategory)
            myDbManager.insertToCosts(addCost)
        }
    }
}