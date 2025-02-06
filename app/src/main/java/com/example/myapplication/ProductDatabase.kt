package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import androidx.core.content.contentValuesOf

class ProductDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "producthistory.db"
        private const val DATABASE_VERSION = 4 // Incremented version to add archived_products table

        const val TABLE_PRODUCTS_NAME = "products"
        const val TABLE_PRODUCTS_COLUMN_DATA = "data"

        const val TABLE_ARCHIVED_PRODUCTS_NAME = "archived_products"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            """
            CREATE TABLE $TABLE_PRODUCTS_NAME (
                ${BaseColumns._ID} INTEGER PRIMARY KEY, 
                $TABLE_PRODUCTS_COLUMN_DATA TEXT, 
                productName TEXT, 
                productImage TEXT
            );
            """.trimIndent()
        )

        db?.execSQL(
            """
            CREATE TABLE $TABLE_ARCHIVED_PRODUCTS_NAME (
                ${BaseColumns._ID} INTEGER PRIMARY KEY, 
                $TABLE_PRODUCTS_COLUMN_DATA TEXT, 
                productName TEXT, 
                productImage TEXT
            );
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 4) {
            db?.execSQL(
                """
                CREATE TABLE IF NOT EXISTS $TABLE_ARCHIVED_PRODUCTS_NAME (
                    ${BaseColumns._ID} INTEGER PRIMARY KEY, 
                    $TABLE_PRODUCTS_COLUMN_DATA TEXT, 
                    productName TEXT, 
                    productImage TEXT
                );
                """.trimIndent()
            )
        }
    }

    val listSavedProducts: Cursor
        get() {
            val db = readableDatabase
            val queryStatement = """
                SELECT ${BaseColumns._ID}, 
                       $TABLE_PRODUCTS_COLUMN_DATA, 
                       productName, 
                       productImage 
                FROM $TABLE_PRODUCTS_NAME 
                ORDER BY ${BaseColumns._ID} DESC
            """.trimIndent()

            return db.rawQuery(queryStatement, null)
        }

    fun insertProduct(newProductEntry: ArchivedProduct): Long {
        val db = writableDatabase
        val columnValues = contentValuesOf().apply {
            put(TABLE_PRODUCTS_COLUMN_DATA, newProductEntry.data)
            put("productName", newProductEntry.productName)
            put("productImage", newProductEntry.productImage)
        }

        return db.insert(TABLE_PRODUCTS_NAME, null, columnValues)
    }

    fun deleteListItem(id: Long): Int {
        val db = writableDatabase

        // Backup the item to archived_products before deleting it
        db.execSQL(
            """
            INSERT INTO $TABLE_ARCHIVED_PRODUCTS_NAME 
            SELECT * FROM $TABLE_PRODUCTS_NAME WHERE ${BaseColumns._ID} = ?
            """.trimIndent(),
            arrayOf(id.toString())
        )

        return db.delete(TABLE_PRODUCTS_NAME, "${BaseColumns._ID} = ?", arrayOf(id.toString()))
    }

    fun restoreListItem(id: Long): Int {
        val db = writableDatabase

        // Restore the item from archived_products to products
        db.execSQL(
            """
            INSERT INTO $TABLE_PRODUCTS_NAME 
            SELECT * FROM $TABLE_ARCHIVED_PRODUCTS_NAME WHERE ${BaseColumns._ID} = ?
            """.trimIndent(),
            arrayOf(id.toString())
        )

        // Delete the item from archived_products after restoring it
        return db.delete(TABLE_ARCHIVED_PRODUCTS_NAME, "${BaseColumns._ID} = ?", arrayOf(id.toString()))
    }
}
