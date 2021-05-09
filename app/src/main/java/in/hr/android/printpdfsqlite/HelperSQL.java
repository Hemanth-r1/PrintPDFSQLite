package in.hr.android.printpdfsqlite;

import android.content.ContentValues;
import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
// step 2

public class HelperSQL extends SQLiteOpenHelper {

    public HelperSQL(@Nullable Context context ) {
        super(context, "PdfDatabase", null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // with open helper step 2
        String createTable = "CREATE TABLE PdfTABLE(invoiceNo INTEGER PRIMARY KEY AUTOINCREMENT, customerName TEXT, contactNo TEXT, date INTEGER, item TEXT, qty INTEGER, amount INTEGER);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    // step 3
    public void insert(String customerName, String contactNo, Long date, String item, int qty, int amount){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("customerName",customerName);
        contentValues.put("contactNo",contactNo);
        contentValues.put("date",date);
        contentValues.put("item",item);
        contentValues.put("qty",qty);
        contentValues.put("amount",amount);

        sqLiteDatabase.insert("PdfTABLE", null, contentValues);
    }
}
