package com.example.myapplication

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cursoradapter.widget.CursorAdapter
import com.google.android.material.snackbar.Snackbar

class ArchivedProductCursorAdapter(
    context: Context,
    cursor: Cursor,
    flags: Int
) : CursorAdapter(context, cursor, flags) {

    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        return LayoutInflater.from(context).inflate(R.layout.list_item_archived_product, parent, false)
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        val archivedProductIdTextView: TextView = view?.findViewById(R.id.list_item_archived_id)!!
        val archivedProductDataTextView: TextView = view.findViewById(R.id.list_item_archived_textview)
        val productNameTextView: TextView = view.findViewById(R.id.list_item_archived_product_name)

        val archivedProductId: Int =
            cursor?.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID)) ?: 0
        archivedProductIdTextView.text = "ID: $archivedProductId"

        val archivedProductData: String =
            cursor?.getString(cursor.getColumnIndexOrThrow(ProductDatabase.TABLE_PRODUCTS_COLUMN_DATA))
                ?: "No data found"
        archivedProductDataTextView.text = archivedProductData

        val productName: String =
            cursor?.getString(cursor.getColumnIndexOrThrow("productName")) ?: "Unknown Product"
        productNameTextView.text = productName

        // Handle delete button
        val deleteButton: Button = view.findViewById(R.id.list_item_archived_product_delete_button)
        deleteButton.setOnClickListener {
            val db = ProductDatabase(context!!)
            db.deleteListItem(archivedProductId.toLong())

            Snackbar.make(view, "Deleted entry #$archivedProductId", Snackbar.LENGTH_SHORT)
                .setAction("Undo") {
                    // Restore the deleted item
                    db.restoreListItem(archivedProductId.toLong())
                    changeCursor(db.listSavedProducts)
                    notifyDataSetChanged()
                }
                .show()

            changeCursor(db.listSavedProducts)
            notifyDataSetChanged()
        }
    }
}
