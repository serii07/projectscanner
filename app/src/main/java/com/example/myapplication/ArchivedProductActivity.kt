package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.camera.core.ExperimentalGetImage

class ArchivedProductActivity : AppCompatActivity() {

    private val productDatabase = ProductDatabase(this)
    private val listArchivedProductsListView: ListView by lazy { findViewById(R.id.activity_archived_product_listview) }

    private var editId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archived_product)

        rebindArchivedProductListView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.activity_archived_product_menu, menu)

        return true
    }

    @ExperimentalGetImage
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_item_main_activity -> {
                val MainActivityIntent = Intent(this, MainActivity::class.java)
                startActivity(MainActivityIntent)
            }
        }
        return true
    }

    fun rebindArchivedProductListView(){
        val productCursor = productDatabase.listSavedProducts
//        val fromFieldNames = arrayOf("_id", "data")
//        val toViews = intArrayOf(R.id.list_item_archived_id, R.id.list_item_archived_textview)

        val archivedProductCursorAdapter = ArchivedProductCursorAdapter(this, productCursor, 0 )
        listArchivedProductsListView.adapter = archivedProductCursorAdapter

    }
}