package com.abnd.android.bookstore;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.abnd.android.bookstore.data.BookContract.BookEntry;

import com.abnd.android.bookstore.data.BookDbHelper;

public class MainActivity extends AppCompatActivity {

    private BookDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbHelper = new BookDbHelper(this);

        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_ADDRESS,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        // Perform a query on the book table
        Cursor cursor = db.query(
                BookEntry.TABLE_NAME,       // The table to query
                projection,                 // The columns to return
                null,           // The columns for the WHERE clause
                null,      // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        TextView displayView = findViewById(R.id.book_detail);

        try {
            // Create a header in the Text View that looks like this:
            displayView.setText("The book table contains " + cursor.getCount() + " records.\n\n");
            if (cursor.getCount() > 0) {
                displayView.append(BookEntry._ID + " - " +
                        BookEntry.COLUMN_PRODUCT_NAME + " - " +
                        BookEntry.COLUMN_PRICE + " - " +
                        BookEntry.COLUMN_QUANTITY + " - " +
                        BookEntry.COLUMN_SUPPLIER_NAME + " - " +
                        BookEntry.COLUMN_SUPPLIER_ADDRESS + " - " +
                        BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n");
            }


            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int productNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierAddressColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_ADDRESS);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);


            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int id = cursor.getInt(idColumnIndex);
                String productName = cursor.getString(productNameColumnIndex);
                Double price = cursor.getDouble(priceColumnIndex);
                int quantity = cursor.getInt(quantityColumnIndex);
                String supplierName = cursor.getString(supplierNameColumnIndex);
                String supplierAddress = cursor.getString(supplierAddressColumnIndex);
                String supplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);


                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + id + " - " +
                        productName + " - " +
                        price + " - " +
                        quantity + " - " +
                        supplierName + " - " +
                        supplierAddress + " - " +
                        supplierPhoneNumber));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    public void insertData(View view) {
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, "ABND");
        values.put(BookEntry.COLUMN_PRICE, "1600.45");
        values.put(BookEntry.COLUMN_QUANTITY, 1);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Udacity India");
        values.put(BookEntry.COLUMN_SUPPLIER_ADDRESS, "Lajpat Nagar IV, New Delhi");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "1800 121 6240");

        long newRowId = db.insert(BookEntry.TABLE_NAME, null, values);

        if (newRowId > -1) {
            Toast.makeText(this, "Book Data Added in DB", Toast.LENGTH_SHORT).show();
            displayDatabaseInfo();
        } else {
            Toast.makeText(this, "Error in saving book data", Toast.LENGTH_SHORT).show();
        }

    }
}
