package com.example.finance.database

object MyDatabaseConstants{
    const val DB_NAME = "finance.db"
    const val DB_VERSION = 25

    //Таблица счета
    const val TABLE_ACCOUNTS = "accounts"
    const val ID_ACCOUNT = "_id"
    const val NAME_ACCOUNT = "name"
    const val TABLE_ACCOUNTS_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_ACCOUNTS +
            " (" + ID_ACCOUNT + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME_ACCOUNT + " TEXT UNIQUE NOT NULL);"
    const val TABLE_ACCOUNTS_DROP = "DROP TABLE IF EXISTS $TABLE_ACCOUNTS;"

    ///Таблица категории
    const val TABLE_CATEGORIES = "categories"
    const val ID_CATEGORY = "_id"
    const val NAME_CATEGORY = "name"
    const val COLOR_CATEGORY = "color"
    const val IMAGE_CATEGORY = "image"
    const val TYPE_CATEGORY = "type"
    const val UNDELETEBLE_CATEGORY = "undeletable"
    const val TABLE_CATEGORIES_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORIES +
            " (" + ID_CATEGORY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME_CATEGORY + " TEXT NOT NULL, " +
            COLOR_CATEGORY + " TEXT, " +
            IMAGE_CATEGORY + " TEXT," +
            TYPE_CATEGORY + " INTEGER," +
            UNDELETEBLE_CATEGORY + " INTEGER" +
            ");"
    const val TABLE_CATEGORIES_DROP = "DROP TABLE IF EXISTS $TABLE_CATEGORIES;"

    //Таблица расходы
    const val TABLE_COSTS = "costs"
    const val ID_COST = "_id"
    const val SUM_COST = "sum"
    const val DATE_COST = "date_cost"
    const val ID_ACCOUNT_COST = "id_account"
    const val COMMENT_COST = "comment"
    const val ID_CATEGORY_COST = "id_category"
    const val TABLE_COSTS_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_COSTS +
            " (" + ID_COST + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            SUM_COST + " REAL NOT NULL, " +
            DATE_COST + " TEXT NOT NULL, " +
            ID_ACCOUNT_COST + " INTEGER NOT NULL, " +
            COMMENT_COST + " TEXT, " +
            ID_CATEGORY_COST + " INTEGER NOT NULL, " +
            "FOREIGN KEY (" + ID_ACCOUNT_COST + ") REFERENCES " + TABLE_ACCOUNTS + " (" + ID_ACCOUNT + ") ON DELETE CASCADE," +
            "FOREIGN KEY (" + ID_CATEGORY_COST + ") REFERENCES " + TABLE_CATEGORIES + " (" + ID_CATEGORY + ") ON DELETE CASCADE" +
            ");"
    const val TABLE_COSTS_DROP = "DROP TABLE IF EXISTS $TABLE_COSTS;"

    //Таблица доходы
    const val TABLE_INCOME = "income"
    const val ID_INCOME = "_id"
    const val SUM_INCOME = "sum"
    const val DATE_INCOME = "date_income"
    const val ID_ACCOUNT_INCOME = "id_account"
    const val COMMENT_INCOME = "comment"
    const val ID_CATEGORY_INCOME = "id_category"
    const val TABLE_INCOME_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_INCOME +
            " (" + ID_INCOME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            SUM_INCOME + " REAL NOT NULL, " +
            DATE_INCOME + " TEXT NOT NULL, " +
            ID_ACCOUNT_INCOME + " INTEGER NOT NULL, " +
            COMMENT_INCOME + " TEXT, " +
            ID_CATEGORY_INCOME + " INTEGER NOT NULL, " +
            "FOREIGN KEY (" + ID_ACCOUNT_COST + ") REFERENCES " + TABLE_ACCOUNTS + " (" + ID_ACCOUNT + ") ON DELETE CASCADE," +
            "FOREIGN KEY (" + ID_CATEGORY_COST + ") REFERENCES " + TABLE_CATEGORIES + " (" + ID_CATEGORY + ") ON DELETE CASCADE" +
            ");"
    const val TABLE_INCOME_DROP = "DROP TABLE IF EXISTS $TABLE_INCOME;"
}