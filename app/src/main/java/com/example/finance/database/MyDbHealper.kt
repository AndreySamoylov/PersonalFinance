package com.example.finance.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.finance.MyConstants
import com.example.finance.R
import com.example.finance.items.MyCategory


class MyDbHelper(context: Context?) :
    SQLiteOpenHelper(context, MyDatabaseConstants.DB_NAME, null, MyDatabaseConstants.DB_VERSION) {

    private val listCategoryCostNames: Array<String>? = context?.resources?.getStringArray(R.array.default_categories_cost_names)
    private val listCategoryCostImages = context?.resources?.obtainTypedArray(R.array.default_categories_cost_images)
    private val listCategoryIncomeNames: Array<String>? = context?.resources?.getStringArray(R.array.default_categories_income_names)
    private val listCategoryIncomeImages  = context?.resources?.obtainTypedArray(R.array.default_categories_income_images)

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(MyDatabaseConstants.TABLE_COSTS_CREATE)
        sqLiteDatabase.execSQL(MyDatabaseConstants.TABLE_INCOME_CREATE)
        sqLiteDatabase.execSQL(MyDatabaseConstants.TABLE_CATEGORIES_CREATE)
        sqLiteDatabase.execSQL(MyDatabaseConstants.TABLE_ACCOUNTS_CREATE)

        // Добавить категории по умолчанию
        // расходы
        for (i in 0 until listCategoryCostNames?.size!!){
            val category = MyCategory(
                0,
                listCategoryCostNames[i],
                "#FFFFFFFF",
                listCategoryCostImages!!.getResourceId(i, 0),
                MyConstants.CATEGORY_TYPE_COST,
                MyConstants.CATEGORY_UNDELEDATBLE_TRUE)

            val contentValues = ContentValues()
            contentValues.put(MyDatabaseConstants.NAME_CATEGORY, category._name)
            contentValues.put(MyDatabaseConstants.COLOR_CATEGORY, category._color)
            contentValues.put(MyDatabaseConstants.IMAGE_CATEGORY, category._image)
            contentValues.put(MyDatabaseConstants.TYPE_CATEGORY, category._type)
            contentValues.put(MyDatabaseConstants.UNDELETEBLE_CATEGORY, category._undeletable)
            sqLiteDatabase.insert(MyDatabaseConstants.TABLE_CATEGORIES, null, contentValues)
        }

        //Доходы
        for (i in 0 until listCategoryIncomeNames?.size!!){
            val category = MyCategory(
                0,
                listCategoryIncomeNames[i],
                "#FFFFFFFF",
                listCategoryIncomeImages!!.getResourceId(i, 0),
                MyConstants.CATEGORY_TYPE_INCOME,
                MyConstants.CATEGORY_UNDELEDATBLE_TRUE)

            val contentValues = ContentValues()
            contentValues.put(MyDatabaseConstants.NAME_CATEGORY, category._name)
            contentValues.put(MyDatabaseConstants.COLOR_CATEGORY, category._color)
            contentValues.put(MyDatabaseConstants.IMAGE_CATEGORY, category._image)
            contentValues.put(MyDatabaseConstants.TYPE_CATEGORY, category._type)
            contentValues.put(MyDatabaseConstants.UNDELETEBLE_CATEGORY, category._undeletable)
            sqLiteDatabase.insert(MyDatabaseConstants.TABLE_CATEGORIES, null, contentValues)
        }

        listCategoryCostImages?.recycle()
        listCategoryIncomeImages?.recycle()
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL(MyDatabaseConstants.TABLE_COSTS_DROP)
        sqLiteDatabase.execSQL(MyDatabaseConstants.TABLE_INCOME_DROP)
        sqLiteDatabase.execSQL(MyDatabaseConstants.TABLE_ACCOUNTS_DROP)
        sqLiteDatabase.execSQL(MyDatabaseConstants.TABLE_CATEGORIES_DROP)
        onCreate(sqLiteDatabase)
    }

    override fun onConfigure(sqLiteDatabase: SQLiteDatabase) {
        super.onConfigure(sqLiteDatabase)
        sqLiteDatabase.setForeignKeyConstraintsEnabled(true) // Нужно для каскадного удаления
    }
}