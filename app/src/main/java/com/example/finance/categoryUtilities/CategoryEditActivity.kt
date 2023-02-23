package com.example.finance.categoryUtilities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.finance.ColorPickerActivity
import com.example.finance.MyFunction.getSerializable
import com.example.finance.MyConstants
import com.example.finance.R
import com.example.finance.database.MyDbManager
import com.example.finance.items.MyCategory

class CategoryEditActivity : AppCompatActivity() {

    private lateinit var buttonSetColorCategory : Button
    private var currentColor: String = "#FFFFFF"
    private lateinit var buttonAddOrChangeCategory : Button
    private lateinit var buttonDeleteCategoryOrBack : Button
    private lateinit var editTextCategpryName: EditText
    private lateinit var imageViewCategoryImage : ImageView
    private lateinit var spinnerImage: Spinner
    private var imageID : Int = 0
    private lateinit var radioButtonCost: RadioButton
    private lateinit var radioButtonIncome: RadioButton
    private var typeCategory : Byte = MyConstants.CATEGORY_TYPE_COST // Тип категории по умолчанию "расход"

    private lateinit var state : String // Состояние: 1. добавления 2. изменения и удаления
    private lateinit var receivedCategory: MyCategory // Категория полученная из другой активити

    private lateinit var myDbManager: MyDbManager

    private var isLaunch = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_edit)

        myDbManager = MyDbManager(this)

        imageViewCategoryImage = findViewById(R.id.imageViewCategoryImage)
        buttonSetColorCategory = findViewById(R.id.buttonSetColorCategory)
        buttonAddOrChangeCategory = findViewById(R.id.buttonAddOrChangeCategory)
        buttonDeleteCategoryOrBack = findViewById(R.id.buttonDeleteCategoryOrBack)
        editTextCategpryName = findViewById(R.id.editTextCategpryName)
        spinnerImage = findViewById(R.id.spinnerChooseImageCateroty)
        radioButtonCost = findViewById(R.id.radioButtonCategoryTypeCost)
        radioButtonIncome = findViewById(R.id.radioButtonCategoryTypeIncome)

        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Активировать ActionBar - меню

        val listDrawable = resources.obtainTypedArray(R.array.category_images) // Получить список изображений
        val listIDs = ArrayList<Int>() // Список идентификаторов
        for (i in 0 until listDrawable.length()){
            val imageID = listDrawable.getResourceId(i, 0) // Получить id из списка изображений
            listIDs.add(imageID) // Добавить id в список
        }
        // Установка адаптера для волчка
        val spinnerAdapter = MySpinnerImageArrayAdapter(this, listIDs)
        spinnerImage.adapter = spinnerAdapter
        listDrawable.recycle()

        buttonSetColorCategory.setOnClickListener {
            val intent = Intent(this, ColorPickerActivity::class.java)
            launcher?.launch(intent)
        }

        buttonAddOrChangeCategory.setOnClickListener {
            if (state == MyConstants.STATE_ADD){
                val addCategory = MyCategory(0, editTextCategpryName.text.toString(), currentColor, imageID, typeCategory, MyConstants.CATEGORY_UNDELEDATBLE_FALSE)
                myDbManager.insertToCategories(addCategory)
            }
            else if (state == MyConstants.STATE_CHANGE_OR_DELETE){
                val updateCategory = MyCategory(receivedCategory._id, editTextCategpryName.text.toString(), currentColor, imageID, typeCategory, receivedCategory._undeletable)
                myDbManager.updateInCategories(updateCategory)
            }
            finish()
        }

        buttonDeleteCategoryOrBack.setOnClickListener {
            if (state == MyConstants.STATE_CHANGE_OR_DELETE){
                if (receivedCategory._undeletable == MyConstants.CATEGORY_UNDELEDATBLE_TRUE){
                    Toast.makeText(this, "Вы не можете удалить эту категорию", Toast.LENGTH_SHORT).show()
                }
                else{
                    myDbManager.deleteFromCategories(receivedCategory._id)
                }
            }
            finish()
        }

        spinnerImage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //Если страницу, уже запустилась
                if (!isItFirstLaunch()) {
                    val selectedItem = spinnerImage.selectedItem
                    imageViewCategoryImage.setImageResource(selectedItem as Int)
                    imageID = selectedItem
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        radioButtonCost.setOnCheckedChangeListener { _, isCheck ->
            typeCategory = if(isCheck) MyConstants.CATEGORY_TYPE_COST
            else MyConstants.CATEGORY_TYPE_INCOME
        }
    }

    override fun onStart() {
        super.onStart()

        // Состояние
        state = intent.getStringExtra(MyConstants.STATE).toString()
        // В зависимости от того какое состояние, выполнить соответствующее действие
        if(state == MyConstants.STATE_ADD){

            // Поставить картинку по умолчанию
            imageID = resources.getIdentifier("supermarket", "drawable", packageName)
            imageViewCategoryImage.setImageResource(imageID)

            buttonAddOrChangeCategory.text = resources.getText(R.string.add)
            buttonDeleteCategoryOrBack.text = resources.getText(R.string.cancel)
        }
        else if(state == MyConstants.STATE_CHANGE_OR_DELETE){
            receivedCategory = getSerializable(this, MyConstants.KEY_MY_CATEGORY, MyCategory::class.java)

            setCheckRadioButton()

            imageViewCategoryImage.setImageResource(receivedCategory._image)
            imageViewCategoryImage.setBackgroundColor(Color.parseColor(receivedCategory._color))
            imageID = receivedCategory._image
            currentColor = receivedCategory._color

            editTextCategpryName.setText(receivedCategory._name)
            buttonAddOrChangeCategory.text = resources.getText(R.string.change)
            buttonDeleteCategoryOrBack.text = resources.getText(R.string.remove)
        }
    }


    override fun onResume() {
        super.onResume()
        myDbManager.openDatabase()
    }

    override fun onPause() {
        super.onPause()
        myDbManager.closeDatabase()
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDatabase()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) finish()
        return super.onOptionsItemSelected(item)
    }

    // Получение результата из activity.
    // В данном случае результат - цвет
    private var launcher : ActivityResultLauncher<Intent>?  = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result : ActivityResult ->
        if (result.resultCode == RESULT_OK){
            val strColor = result.data?.getStringExtra(MyConstants.COLOR)
            if (strColor != null) {
                imageViewCategoryImage.setBackgroundColor(Color.parseColor(strColor))
                currentColor = strColor
            }
        }
    }

    private fun setCheckRadioButton(){
        if (receivedCategory._type == MyConstants.CATEGORY_TYPE_COST){
            radioButtonIncome.isChecked = false
            radioButtonCost.isChecked = true
        }
        else {
            radioButtonCost.isChecked = false
            radioButtonIncome.isChecked = true
        }
    }

    //Если страница только запустилась
    private fun isItFirstLaunch() : Boolean{
        return if (isLaunch){
            isLaunch = false
            true
        }else{
            false
        }
    }
}