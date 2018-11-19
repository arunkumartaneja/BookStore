package com.abnd.android.bookstore;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.abnd.android.bookstore.data.BookContract.BookEntry;

import java.util.Locale;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentBookUri;
    private EditText mBookTitleEditText;
    private EditText mBookPriceEditText;
    private EditText mBookQuantityText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneNumberEditText;
    private EditText mSupplierAddressEditText;


    private static final int BOOK_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mBookTitleEditText = findViewById(R.id.edit_book_title);
        mBookPriceEditText = findViewById(R.id.edit_book_price);
        mBookQuantityText = findViewById(R.id.edit_book_quantity);
        mSupplierNameEditText = findViewById(R.id.edit_supplier_name);
        mSupplierPhoneNumberEditText = findViewById(R.id.edit_supplier_phone_number);
        mSupplierAddressEditText = findViewById(R.id.edit_supplier_address);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        if (mCurrentBookUri == null) {
            setTitle(getString(R.string.editor_activity_label_add_book));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_label_edit_book));
            getLoaderManager().initLoader(BOOK_LOADER, null, this);
        }

        Button decreaseQuantity = findViewById(R.id.decrease_quantity);
        decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseQuantity();
            }
        });

        Button increaseQuantity = findViewById(R.id.increase_quantity);
        increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseQuantity();
            }
        });

        Button order = findViewById(R.id.order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mSupplierPhoneNumberEditText.getText())) {
                    Toast.makeText(view.getContext(), R.string.phone_numbet_not_available, Toast.LENGTH_SHORT).show();
                    return;
                }

                String supplierPhoneNumber = mSupplierPhoneNumberEditText.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + supplierPhoneNumber));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(view.getContext(), R.string.feature_not_available, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_save:
                saveBook();
                return true;
            case R.id.action_delete:
                deleteBook();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new book, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void decreaseQuantity() {
        if (TextUtils.isEmpty(mBookQuantityText.getText())) {
            mBookQuantityText.setText("0");
        }

        Integer quantity = Integer.parseInt(mBookQuantityText.getText().toString());
        if (quantity <= 0) {
            Toast.makeText(this, getString(R.string.minimum_quantity_message), Toast.LENGTH_LONG).show();
            return;
        }

        quantity--;
        mBookQuantityText.setText(quantity.toString());
    }

    private void increaseQuantity() {
        if (TextUtils.isEmpty(mBookQuantityText.getText())) {
            mBookQuantityText.setText("0");
        }

        Integer quantity = Integer.parseInt(mBookQuantityText.getText().toString());
        quantity++;
        mBookQuantityText.setText(quantity.toString());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_ADDRESS,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        return new CursorLoader(this, mCurrentBookUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }

        if (data.moveToFirst()) {
            int nameColumnIndex = data.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = data.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantityColumnIndex = data.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = data.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = data.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            int supplierAddressColumnIndex = data.getColumnIndex(BookEntry.COLUMN_SUPPLIER_ADDRESS);

            String name = data.getString(nameColumnIndex);
            double price = data.getDouble(priceColumnIndex);
            Integer quantity = data.getInt(quantityColumnIndex);
            String supplierName = data.getString(supplierNameColumnIndex);
            String supplierPhone = data.getString(supplierPhoneColumnIndex);
            String supplierAddress = data.getString(supplierAddressColumnIndex);

            mBookTitleEditText.setText(name);
            mBookPriceEditText.setText(Double.toString(price));
            mBookQuantityText.setText(Integer.toString(quantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneNumberEditText.setText(supplierPhone);
            mSupplierAddressEditText.setText(supplierAddress);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBookTitleEditText.setText("");
        mBookPriceEditText.setText("");
        mBookQuantityText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneNumberEditText.setText("");
        mSupplierAddressEditText.setText("");
    }

    private void deleteBook() {
        int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
        if (rowsDeleted == 0) {
            Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                    Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void saveBook() {

        String title = mBookTitleEditText.getText().toString().trim();
        String priceString = mBookPriceEditText.getText().toString().trim();
        String quantityString = mBookQuantityText.getText().toString().trim();
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String supplierPhone = mSupplierPhoneNumberEditText.getText().toString().trim();
        String supplierAddress = mSupplierAddressEditText.getText().toString().trim();

        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierName) &&
                TextUtils.isEmpty(supplierPhone) && TextUtils.isEmpty(supplierAddress)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, title);

        double price = 0.00;
        if (!TextUtils.isEmpty(priceString)) {
            price = Double.parseDouble(String.format(priceString, "%.2f"));
        }
        values.put(BookEntry.COLUMN_PRICE, String.format(Locale.US, "%.2f", price));

        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(BookEntry.COLUMN_QUANTITY, quantity);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhone);
        values.put(BookEntry.COLUMN_SUPPLIER_ADDRESS, supplierAddress);

        boolean isBookSaved = false;

        if (mCurrentBookUri == null) {
            // Insert a new book into the provider, returning the content URI for the new book.
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
            if (newUri != null) {
                isBookSaved = true;
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);
            if (rowsAffected > 0) {
                isBookSaved = true;
            }
        }

        if (isBookSaved) {
            Toast.makeText(this, getString(R.string.editor_save_book_successful),
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, getString(R.string.editor_save_book_failed),
                    Toast.LENGTH_SHORT).show();
        }

    }
}
