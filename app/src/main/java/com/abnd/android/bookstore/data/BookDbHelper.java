package com.abnd.android.bookstore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.abnd.android.bookstore.data.BookContract.BookEntry;

public class BookDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = BookDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "book.db";
    private static final int DATABASE_VERSION = 1;


    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_BOOKS_TABLE =
                "CREATE TABLE " + BookEntry.TABLE_NAME + " (" +
                        BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        BookEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL," +
                        BookEntry.COLUMN_PRICE + " REAL NOT NULL," +
                        BookEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 1," +
                        BookEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL," +
                        BookEntry.COLUMN_SUPPLIER_ADDRESS + " TEXT NOT NULL," +
                        BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL)";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
