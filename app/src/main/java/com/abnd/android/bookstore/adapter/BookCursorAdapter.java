package com.abnd.android.bookstore.adapter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.abnd.android.bookstore.R;
import com.abnd.android.bookstore.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.product_name);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        Button saleButton = view.findViewById(R.id.sale);

        // Find the columns of book attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
        int breedColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);

        // Read the book attributes from the Cursor for the current book
        final String productName = cursor.getString(nameColumnIndex);
        final String price = cursor.getString(breedColumnIndex);
        String quantity = cursor.getString(quantityColumnIndex);

        // Update the TextViews with the attributes for the current book
        nameTextView.setText(productName);
        priceTextView.setText(price);
        quantityTextView.setText(quantity);

        final Integer currentQuantity = Integer.valueOf(quantity);
        final Long id = cursor.getLong(idColumnIndex);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentQuantity > 0) {
                    int updatedQuantity = currentQuantity - 1;
                    Uri saleUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_QUANTITY, updatedQuantity);
                    int rowsUpdated = context.getContentResolver().update(saleUri, values, null, null);
                    if (rowsUpdated > 0) {
                        Toast.makeText(context, R.string.book_sold, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, R.string.book_sold_failed, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, R.string.no_book_for_sell, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
