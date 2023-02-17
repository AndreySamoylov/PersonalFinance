package com.example.finance.database

import android.annotation.SuppressLint

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.finance.items.MyAccount
import com.example.finance.items.MyCategory
import com.example.finance.items.MyCost
import com.example.finance.items.MyIncome

class MyDbManager(private val context: Context) {
    private val myDbHealper: MyDbHelper = MyDbHelper(context)
    private var sqLiteDatabase: SQLiteDatabase? = null

    fun openDatabase() {
        sqLiteDatabase = myDbHealper.writableDatabase
    }

    fun closeDatabase() {
        myDbHealper.close()
    }

    fun insertToAccounts(account: MyAccount) {
        val contentValues = ContentValues()
        contentValues.put(MyDatabaseConstants.NAME_ACCOUNT, account.name)
        sqLiteDatabase!!.insert(MyDatabaseConstants.TABLE_ACCOUNTS, null, contentValues)
    }

    fun updateInAccounts(account: MyAccount) {
        sqLiteDatabase!!.execSQL(
            "UPDATE " + MyDatabaseConstants.TABLE_ACCOUNTS +
                    " SET " + MyDatabaseConstants.NAME_ACCOUNT + "='" + account.name + "'" +
                    " WHERE " + MyDatabaseConstants.ID_ACCOUNT + "=" + account._id + ";"
        )
    }

    fun deleteFromAccounts(_id: Long) {
        sqLiteDatabase!!.execSQL(
            ("DELETE FROM " + MyDatabaseConstants.TABLE_ACCOUNTS +
                    " WHERE " + MyDatabaseConstants.ID_ACCOUNT + "=" + _id + ";")
        )
    }

    val fromAccounts: List<MyAccount>
        get() {
            val tempList: MutableList<MyAccount> = ArrayList()
            val cursor = sqLiteDatabase!!.query(
                MyDatabaseConstants.TABLE_ACCOUNTS, null, null, null,
                null, null, null
            )
            while (cursor.moveToNext()) {
                @SuppressLint("Range") val id =
                    cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_ACCOUNT))
                @SuppressLint("Range") val name =
                    cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.NAME_ACCOUNT))
                val account = MyAccount(id, name)
                tempList.add(account)
            }
            cursor.close()
            return tempList
        }

    // Метод ищет название счёта по его id
    // В случае успеха возвращает имя, в противном случае пустую строку
    fun findAccountNameByID(id: Long): String {
        val columns = arrayOf(
            MyDatabaseConstants.NAME_ACCOUNT
        )
        val selection = MyDatabaseConstants.ID_ACCOUNT + " = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_ACCOUNTS,
            columns,
            selection,
            selectionArgs,
            null,
            null,
            null,
            "1"
        )
        var nameAccount = ""
        while (cursor.moveToNext()) {
            nameAccount = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseConstants.NAME_ACCOUNT))
        }
        cursor.close()
        return nameAccount
    }


    fun insertToCategories(category: MyCategory) {
        val contentValues = ContentValues()
        contentValues.put(MyDatabaseConstants.NAME_CATEGORY, category._name)
        contentValues.put(MyDatabaseConstants.COLOR_CATEGORY, category._color)
        contentValues.put(MyDatabaseConstants.IMAGE_CATEGORY, category._image)
        contentValues.put(MyDatabaseConstants.TYPE_CATEGORY, category._type)
        contentValues.put(MyDatabaseConstants.UNDELETEBLE_CATEGORY, category._undeletable)
        sqLiteDatabase!!.insert(MyDatabaseConstants.TABLE_CATEGORIES, null, contentValues)
    }

    fun updateInCategories(category: MyCategory) {
        val values = ContentValues().apply {
            put(MyDatabaseConstants.NAME_CATEGORY, category._name)
            put(MyDatabaseConstants.COLOR_CATEGORY, category._color)
            put(MyDatabaseConstants.IMAGE_CATEGORY, category._image)
            put(MyDatabaseConstants.TYPE_CATEGORY, category._type)
            put(MyDatabaseConstants.UNDELETEBLE_CATEGORY, category._undeletable)
        }
        val selection = "${MyDatabaseConstants.ID_CATEGORY} = ?"
        val selectionArgs = arrayOf(category._id.toString())
        sqLiteDatabase?.update(MyDatabaseConstants.TABLE_CATEGORIES, values, selection, selectionArgs)
    }

    fun deleteFromCategories(_id: Long) {
        sqLiteDatabase!!.execSQL(
            ("DELETE FROM " + MyDatabaseConstants.TABLE_CATEGORIES +
                    " WHERE " + MyDatabaseConstants.ID_CATEGORY + "=" + _id + ";")
        )
    }

    val fromCategories: List<MyCategory>
        get() {
            val tempList: MutableList<MyCategory> = ArrayList()
            val cursor = sqLiteDatabase!!.query(
                MyDatabaseConstants.TABLE_CATEGORIES, null, null, null,
                null, null, null
            )
            while (cursor.moveToNext()) {
                @SuppressLint("Range") val id =
                    cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY))
                @SuppressLint("Range") val name =
                    cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.NAME_CATEGORY))
                @SuppressLint("Range") val color =
                    cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COLOR_CATEGORY))
                @SuppressLint("Range") val image =
                    cursor.getInt(cursor.getColumnIndex(MyDatabaseConstants.IMAGE_CATEGORY))
                @SuppressLint("Range") val type =
                    cursor.getInt(cursor.getColumnIndex(MyDatabaseConstants.TYPE_CATEGORY)).toByte()
                @SuppressLint("Range") val undeletable =
                    cursor.getInt(cursor.getColumnIndex(MyDatabaseConstants.UNDELETEBLE_CATEGORY)).toByte()
                val category = MyCategory(id, name, color, image, type, undeletable)
                tempList.add(category)
            }
            cursor.close()
            return tempList
        }

    // Метод возвращает список категорий
    // где typeCategory - тип категории
    // 0 - расход, 1 - доход
    fun fromCategories(typeCategory : Byte) : List<MyCategory>{
        // Filter results WHERE "TYPE_CATEGORY" = 'typeCategory'
        val selection = MyDatabaseConstants.TYPE_CATEGORY + " = ?"
        val selectionArgs = arrayOf(typeCategory.toString())

        val tempList: MutableList<MyCategory> = ArrayList()
        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_CATEGORIES,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null,
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val id =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY))
            @SuppressLint("Range") val name =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.NAME_CATEGORY))
            @SuppressLint("Range") val color =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COLOR_CATEGORY))
            @SuppressLint("Range") val image =
                cursor.getInt(cursor.getColumnIndex(MyDatabaseConstants.IMAGE_CATEGORY))
            @SuppressLint("Range") val type =
                cursor.getInt(cursor.getColumnIndex(MyDatabaseConstants.TYPE_CATEGORY)).toByte()
            @SuppressLint("Range") val undeletable =
                cursor.getInt(cursor.getColumnIndex(MyDatabaseConstants.UNDELETEBLE_CATEGORY)).toByte()
            val category = MyCategory(id, name, color, image, type, undeletable)
            tempList.add(category)
        }
        cursor.close()
        return tempList
    }

    // Метод ищет категорию по его идентификатору.
    // В случае успеха возвращает категорию, в противном случае пустоту
    fun findCategoryByID(id : Long) : MyCategory?{
        val selection = MyDatabaseConstants.ID_CATEGORY + " = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_CATEGORIES,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null,
            "1"
        )
        var category : MyCategory? = null
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val idCategory = cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY))
            @SuppressLint("Range") val name = cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.NAME_CATEGORY))
            @SuppressLint("Range") val color = cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COLOR_CATEGORY))
            @SuppressLint("Range") val image = cursor.getInt(cursor.getColumnIndex(MyDatabaseConstants.IMAGE_CATEGORY))
            @SuppressLint("Range") val type = cursor.getInt(cursor.getColumnIndex(MyDatabaseConstants.TYPE_CATEGORY)).toByte()
            @SuppressLint("Range") val undeletable = cursor.getInt(cursor.getColumnIndex(MyDatabaseConstants.UNDELETEBLE_CATEGORY)).toByte()
            category = MyCategory(idCategory, name, color, image, type, undeletable)
        }
        cursor.close()
        return category
    }

    // Метод пытается найти ID категории по его названию
    // В случает успеха возвращает ID категории из базы данных
    // В противном случае возвращает -1
    fun findCategoryByName(name: String): Long {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val columns = arrayOf(
            MyDatabaseConstants.ID_CATEGORY
        )
        // Filter results WHERE "title" = 'My Title'
        val selection = MyDatabaseConstants.NAME_CATEGORY + " = ?"
        val selectionArgs = arrayOf(name)
        // How you want the results sorted in the resulting Cursor
        //String sortOrder =
        //        MyDatabaseConstants.ID_CATEGORY + " DESC";
        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_CATEGORIES,
            columns,
            selection,
            selectionArgs,
            null,
            null,
            null,
            "1"
        )
        var id : Long = -1
        while (cursor.moveToNext()) {
            id = cursor.getLong(cursor.getColumnIndexOrThrow(MyDatabaseConstants.ID_CATEGORY))
        }
        cursor.close()
        return id
    }

    fun insertToCosts(cost: MyCost) {
        val contentValues = ContentValues()
        contentValues.put(MyDatabaseConstants.SUM_COST, cost._sum)
        contentValues.put(MyDatabaseConstants.DATE_COST, cost._date_cost)
        contentValues.put(MyDatabaseConstants.COMMENT_COST, cost._comment)
        contentValues.put(MyDatabaseConstants.ID_ACCOUNT_COST, cost._account)
        contentValues.put(MyDatabaseConstants.ID_CATEGORY_COST, cost._category)
        sqLiteDatabase!!.insert(MyDatabaseConstants.TABLE_COSTS, null, contentValues)
    }

    fun updateInCosts(cost: MyCost) {
        val values = ContentValues().apply {
            put(MyDatabaseConstants.SUM_COST, cost._sum)
            put(MyDatabaseConstants.DATE_COST, cost._date_cost)
            put(MyDatabaseConstants.COMMENT_COST, cost._comment)
            put(MyDatabaseConstants.ID_ACCOUNT_COST, cost._account)
            put(MyDatabaseConstants.ID_CATEGORY_COST, cost._category)
        }
        val selection = "${MyDatabaseConstants.ID_COST} = ?"
        val selectionArgs = arrayOf(cost._id.toString())

        sqLiteDatabase?.update(MyDatabaseConstants.TABLE_COSTS, values, selection, selectionArgs)
    }

    fun deleteFromCosts(_id: Long) {
        sqLiteDatabase!!.execSQL(
            ("DELETE FROM " + MyDatabaseConstants.TABLE_COSTS +
                    " WHERE " + MyDatabaseConstants.ID_COST + "=" + _id + ";")
        )
    }

    // Метод позвращает список покупок, который находяться в базе данных
    val fromCosts: List<MyCost>
        get() {
            val tempList: MutableList<MyCost> = ArrayList()

            val sortOrder = "${MyDatabaseConstants.DATE_COST} DESC"

            val cursor = sqLiteDatabase!!.query(
                MyDatabaseConstants.TABLE_COSTS, null, null, null,
                null, null, sortOrder
            )
            while (cursor.moveToNext()) {
                @SuppressLint("Range") val id =
                    cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_COST))
                @SuppressLint("Range") val sum =
                    cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_COST))
                @SuppressLint("Range") val dateCost =
                    cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.DATE_COST))
                @SuppressLint("Range") val comment =
                    cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COMMENT_COST))
                @SuppressLint("Range") val idAccount =
                    cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_ACCOUNT_COST))
                @SuppressLint("Range") val idCategory =
                    cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY_COST))
                val cost = MyCost(id, sum, dateCost, comment, idAccount, idCategory)
                tempList.add(cost)
            }
            cursor.close()
            return tempList
        }

    // Метод позвращает список покупок, который находяться в базе данных, где
    // category - идентификатор категория, по которой нужно делать выборку
    fun fromCosts(categoryID: Long) : List<MyCost>{
        val tempList: MutableList<MyCost> = ArrayList()

        val selection = "${MyDatabaseConstants.ID_CATEGORY_COST} = ?"
        val selectionArgs = arrayOf(categoryID.toString())

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_COSTS, null, selection, selectionArgs,
            null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val id =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_COST))
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_COST))
            @SuppressLint("Range") val dateCost =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.DATE_COST))
            @SuppressLint("Range") val comment =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COMMENT_COST))
            @SuppressLint("Range") val idAccount =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_ACCOUNT_COST))
            @SuppressLint("Range") val idCategory =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY_COST))
            val cost = MyCost(id, sum, dateCost, comment, idAccount, idCategory)
            tempList.add(cost)
        }
        cursor.close()
        return tempList
    }

    // Метод позвращает сумму покупок по определенной категории,
    // где category - идентификатор категория, по которой нужно делать выборку
    fun getSumCostByCategory(categoryID: Long) : Double{
        var allSum : Double = 0.0

        val selection = "${MyDatabaseConstants.ID_CATEGORY_COST} = ?"
        val selectionArgs = arrayOf(categoryID.toString())

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_COSTS, null, selection, selectionArgs,
            null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_COST))
            allSum += sum
        }
        cursor.close()
        return allSum
    }

    // Метод позвращает сумму покупок по определенной категории,
    // где category - идентификатор категория, по которой делается выборка,
    // а accountID - идентификатор аккаунта. по которому делается выборка
    fun getSumCostByCategory(categoryID: Long, accountID : Long) : Double{
        var allSum : Double = 0.0

        val selection = "${MyDatabaseConstants.ID_CATEGORY_COST} = ? AND ${MyDatabaseConstants.ID_ACCOUNT_COST} = ?"
        val selectionArgs = arrayOf(categoryID.toString(), accountID.toString())

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_COSTS, null, selection, selectionArgs,
            null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_COST))
            allSum += sum
        }
        cursor.close()
        return allSum
    }

    // Метод позвращает сумму покупок по определенной категории,
    // accountID - идентификатор аккаунта. по которому делается выборка
    // initialDate и finalDate промежуток дат, между которыми делается выборка
    fun getSumCostByCategory(categoryID: Long, initialDate: String, finalDate: String) : Double{
        var allSum : Double = 0.0

        val selection = "${MyDatabaseConstants.ID_CATEGORY_COST} = ? AND " +
                "${MyDatabaseConstants.DATE_COST} >= ? AND " +
                "${MyDatabaseConstants.DATE_COST} <= ?"
        val selectionArgs = arrayOf(categoryID.toString(),  initialDate, finalDate)

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_COSTS, null, selection, selectionArgs,
            null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_COST))
            allSum += sum
        }
        cursor.close()
        return allSum
    }

    // Метод позвращает сумму покупок по определенной категории,
    // где category - идентификатор категория, по которой делается выборка,
    // accountID - идентификатор аккаунта. по которому делается выборка,
    // initialDate и finalDate промежуток дат, между которыми делается выборка
    fun getSumCostByCategory(categoryID: Long, accountID : Long, initialDate: String, finalDate: String) : Double{
        var allSum : Double = 0.0

        val selection = "${MyDatabaseConstants.ID_CATEGORY_COST} = ? AND " +
                "${MyDatabaseConstants.ID_ACCOUNT_COST} = ? AND " +
                "${MyDatabaseConstants.DATE_COST} >= ? AND " +
                "${MyDatabaseConstants.DATE_COST} <= ?"
        val selectionArgs = arrayOf(categoryID.toString(), accountID.toString(), initialDate, finalDate)

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_COSTS, null, selection, selectionArgs,
            null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_COST))
            allSum += sum
        }
        cursor.close()
        return allSum
    }

    // Метод позвращает список покупок, который находяться в базе данных, где
    // initialDate - дата, от которой нужно делать выборку, а finalDate - дата, до которой нужно делать выборку
    fun fromCosts(initialDate : String, finalDate : String) : List<MyCost>{
        val tempList: MutableList<MyCost> = ArrayList()

        val selection = "${MyDatabaseConstants.DATE_COST} >= ? AND ${MyDatabaseConstants.DATE_COST} <= ?"
        val selectionArgs = arrayOf(initialDate, finalDate)
        val sortOrder = "${MyDatabaseConstants.DATE_COST} DESC"

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_COSTS, null, selection, selectionArgs,
            null, null, sortOrder
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val id =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_COST))
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_COST))
            @SuppressLint("Range") val dateCost =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.DATE_COST))
            @SuppressLint("Range") val comment =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COMMENT_COST))
            @SuppressLint("Range") val idAccount =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_ACCOUNT_COST))
            @SuppressLint("Range") val idCategory =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY_COST))
            val cost = MyCost(id, sum, dateCost, comment, idAccount, idCategory)
            tempList.add(cost)
        }
        cursor.close()
        return tempList
    }

    // Метод позвращает список покупок, который находяться в базе данных, где
    // initialDate - дата, от которой нужно делать выборку; finalDate - дата, до которой нужно делать выборку,
    // а accountID - идентификатор счета. по которому будет сделана выборка
    fun fromCosts(initialDate : String, finalDate : String, accountID : Long) : List<MyCost>{
        val tempList: MutableList<MyCost> = ArrayList()

        val selection = "${MyDatabaseConstants.DATE_COST} >= ? AND ${MyDatabaseConstants.DATE_COST} <= ? AND ${MyDatabaseConstants.ID_ACCOUNT_COST} = ?"
        val selectionArgs = arrayOf(initialDate, finalDate, accountID.toString())
        val sortOrder = "${MyDatabaseConstants.DATE_COST} DESC"

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_COSTS, null, selection, selectionArgs,
            null, null, sortOrder
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val id =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_COST))
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_COST))
            @SuppressLint("Range") val dateCost =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.DATE_COST))
            @SuppressLint("Range") val comment =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COMMENT_COST))
            @SuppressLint("Range") val idAccount =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_ACCOUNT_COST))
            @SuppressLint("Range") val idCategory =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY_COST))
            val cost = MyCost(id, sum, dateCost, comment, idAccount, idCategory)
            tempList.add(cost)
        }
        cursor.close()
        return tempList
    }

    fun insertToIncome(income: MyIncome) {
        val contentValues = ContentValues()
        contentValues.put(MyDatabaseConstants.SUM_INCOME, income._sum)
        contentValues.put(MyDatabaseConstants.DATE_INCOME, income._date_income)
        contentValues.put(MyDatabaseConstants.COMMENT_INCOME, income._comment)
        contentValues.put(MyDatabaseConstants.ID_ACCOUNT_INCOME, income._account)
        contentValues.put(MyDatabaseConstants.ID_CATEGORY_INCOME, income._category)
        sqLiteDatabase!!.insert(MyDatabaseConstants.TABLE_INCOME, null, contentValues)
    }

    fun updateInIncome(income: MyIncome) {
        val values = ContentValues().apply {
            put(MyDatabaseConstants.SUM_INCOME, income._sum)
            put(MyDatabaseConstants.DATE_INCOME, income._date_income)
            put(MyDatabaseConstants.COMMENT_INCOME, income._comment)
            put(MyDatabaseConstants.ID_ACCOUNT_INCOME, income._account)
            put(MyDatabaseConstants.ID_CATEGORY_INCOME, income._category)
        }
        val selection = "${MyDatabaseConstants.ID_INCOME} = ?"
        val selectionArgs = arrayOf(income._id.toString())

        sqLiteDatabase?.update(MyDatabaseConstants.TABLE_INCOME, values, selection, selectionArgs)
    }

    fun deleteFromIncome(_id: Long) {
        sqLiteDatabase!!.execSQL(
            ("DELETE FROM " + MyDatabaseConstants.TABLE_INCOME +
                    " WHERE " + MyDatabaseConstants.ID_INCOME + "=" + _id + ";")
        )
    }

    val fromIncome: List<MyIncome>
        get() {
            val tempList: MutableList<MyIncome> = ArrayList()
            val cursor = sqLiteDatabase!!.query(
                MyDatabaseConstants.TABLE_INCOME, null, null, null,
                null, null, null
            )
            while (cursor.moveToNext()) {
                @SuppressLint("Range") val id =
                    cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_INCOME))
                @SuppressLint("Range") val sum =
                    cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_INCOME))
                @SuppressLint("Range") val dateIncome =
                    cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.DATE_INCOME))
                @SuppressLint("Range") val comment =
                    cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COMMENT_INCOME))
                @SuppressLint("Range") val idAccount =
                    cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_ACCOUNT_INCOME))
                @SuppressLint("Range") val idCategory =
                    cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY_INCOME))
                val income = MyIncome(id, sum, dateIncome, comment, idAccount, idCategory)
                tempList.add(income)
            }
            cursor.close()
            return tempList
        }

    // Метод позвращает список поступлений, который находяться в базе данных, где
    // category - идентификатор категория, по которой нужно делать выборку
    fun fromIncome(categoryID: Long) : List<MyIncome>{
        val tempList: MutableList<MyIncome> = ArrayList()

        val selection = "${MyDatabaseConstants.ID_CATEGORY_INCOME} = ?"
        val selectionArgs = arrayOf(categoryID.toString())

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_INCOME, null, selection, selectionArgs,
            null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val id =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_INCOME))
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_INCOME))
            @SuppressLint("Range") val dateIncome =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.DATE_INCOME))
            @SuppressLint("Range") val comment =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COMMENT_INCOME))
            @SuppressLint("Range") val idAccount =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_ACCOUNT_INCOME))
            @SuppressLint("Range") val idCategory =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY_INCOME))
            val income = MyIncome(id, sum, dateIncome, comment, idAccount, idCategory)
            tempList.add(income)
        }
        cursor.close()
        return tempList
    }

    // Метод позвращает список поступлений, который находяться в базе данных, где
    // initialDate - дата, от которой нужно делать выборку, а finalDate - дата, до которой нужно делать выборку
    fun fromIncome(initialDate : String, finalDate : String) : List<MyIncome>{
        val tempList: MutableList<MyIncome> = ArrayList()

        val selection = "${MyDatabaseConstants.DATE_INCOME} >= ? AND ${MyDatabaseConstants.DATE_INCOME} <= ?"
        val selectionArgs = arrayOf(initialDate, finalDate)
        val sortOrder = "${MyDatabaseConstants.DATE_INCOME} DESC"

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_INCOME, null, selection, selectionArgs,
            null, null, sortOrder
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val id =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_INCOME))
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_INCOME))
            @SuppressLint("Range") val dateIncome =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.DATE_INCOME))
            @SuppressLint("Range") val comment =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COMMENT_INCOME))
            @SuppressLint("Range") val idAccount =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_ACCOUNT_INCOME))
            @SuppressLint("Range") val idCategory =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY_INCOME))
            val income = MyIncome(id, sum, dateIncome, comment, idAccount, idCategory)
            tempList.add(income)
        }
        cursor.close()
        return tempList
    }

    // Метод позвращает список поступлений, который находяться в базе данных, где
    // initialDate - дата, от которой нужно делать выборку; finalDate - дата, до которой нужно делать выборку,
    // а accountID - идентификатор счета. по которому будет сделана выборка
    fun fromIncome(initialDate : String, finalDate : String, accountID : Long) : List<MyIncome>{
        val tempList: MutableList<MyIncome> = ArrayList()

        val selection = "${MyDatabaseConstants.DATE_INCOME} >= ? AND ${MyDatabaseConstants.DATE_INCOME} <= ? AND ${MyDatabaseConstants.ID_ACCOUNT_INCOME} = ?"
        val selectionArgs = arrayOf(initialDate, finalDate, accountID.toString())
        val sortOrder = "${MyDatabaseConstants.DATE_INCOME} DESC"

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_INCOME, null, selection, selectionArgs,
            null, null, sortOrder
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val id =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_INCOME))
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_INCOME))
            @SuppressLint("Range") val dateIncome =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.DATE_INCOME))
            @SuppressLint("Range") val comment =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COMMENT_INCOME))
            @SuppressLint("Range") val idAccount =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_ACCOUNT_INCOME))
            @SuppressLint("Range") val idCategory =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY_INCOME))
            val income = MyIncome(id, sum, dateIncome, comment, idAccount, idCategory)
            tempList.add(income)
        }
        cursor.close()
        return tempList
    }

    // Метод позвращает сумму поступлений по определенной категории,
    // где category - идентификатор категория, по которой нужно делать выборку
    fun getSumIncomeByCategory(categoryID: Long) : Double{
        var allSum : Double = 0.0

        val selection = "${MyDatabaseConstants.ID_CATEGORY_INCOME} = ?"
        val selectionArgs = arrayOf(categoryID.toString())

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_INCOME, null, selection, selectionArgs,
            null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_INCOME))
            allSum += sum
        }
        cursor.close()
        return allSum
    }

    // Метод позвращает сумму поступлений по определенной категории,
    // где category - идентификатор категория, по которой делается выборка,
    // а accountID - идентификатор аккаунта. по которому делается выборка
    fun getSumIncomeByCategory(categoryID: Long, accountID : Long) : Double{
        var allSum : Double = 0.0

        val selection = "${MyDatabaseConstants.ID_CATEGORY_INCOME} = ? AND ${MyDatabaseConstants.ID_ACCOUNT_INCOME} = ?"
        val selectionArgs = arrayOf(categoryID.toString(), accountID.toString())

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_INCOME, null, selection, selectionArgs,
            null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_INCOME))
            allSum += sum
        }
        cursor.close()
        return allSum
    }

    // Метод позвращает сумму поступлений по определенной категории,
    // accountID - идентификатор аккаунта. по которому делается выборка
    // initialDate и finalDate промежуток дат, между которыми делается выборка
    fun getSumIncomeByCategory(categoryID: Long, initialDate: String, finalDate: String) : Double{
        var allSum : Double = 0.0

        val selection = "${MyDatabaseConstants.ID_CATEGORY_INCOME} = ? AND " +
                "${MyDatabaseConstants.DATE_INCOME} >= ? AND " +
                "${MyDatabaseConstants.DATE_INCOME} <= ?"
        val selectionArgs = arrayOf(categoryID.toString(),  initialDate, finalDate)

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_INCOME, null, selection, selectionArgs,
            null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_INCOME))
            allSum += sum
        }
        cursor.close()
        return allSum
    }

    // Метод позвращает сумму поступлений по определенной категории,
    // где category - идентификатор категория, по которой делается выборка,
    // accountID - идентификатор аккаунта. по которому делается выборка,
    // initialDate и finalDate промежуток дат, между которыми делается выборка
    fun getSumIncomeByCategory(categoryID: Long, accountID : Long, initialDate: String, finalDate: String) : Double{
        var allSum : Double = 0.0

        val selection = "${MyDatabaseConstants.ID_CATEGORY_INCOME} = ? AND " +
                "${MyDatabaseConstants.ID_ACCOUNT_INCOME} = ? AND " +
                "${MyDatabaseConstants.DATE_INCOME} >= ? AND " +
                "${MyDatabaseConstants.DATE_INCOME} <= ?"
        val selectionArgs = arrayOf(categoryID.toString(), accountID.toString(), initialDate, finalDate)

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_INCOME, null, selection, selectionArgs,
            null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_INCOME))
            allSum += sum
        }
        cursor.close()
        return allSum
    }
}