package in.hr.android.printpdfsqlite;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button saveAndPrint, printBtn;
    EditText editTextName, editTextPhone, editTextQty;
    Spinner spinner;
    //string array
    String[]itemList;
    int[] itemPrice;
    ArrayAdapter<String> arrayAdapter;
    HelperSQL helperSQL;
    SQLiteDatabase sqLiteDatabase;
    Date date = new Date();

    String datePattern = "dd-MM-YYYY";
    SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);

    String timePattern = "hh:mm a";
    SimpleDateFormat timeFormat = new SimpleDateFormat(timePattern);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callFindVewById();
        helperSQL = new HelperSQL(this);
        sqLiteDatabase = helperSQL.getWritableDatabase();
        callOnClickListener();
    }

    private void callFindVewById() {
        saveAndPrint = findViewById(R.id.btnSaveAndPrint);
        printBtn = findViewById(R.id.btnPrint);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextQty = findViewById(R.id.editTextQty);
        spinner = findViewById(R.id.spinner);
        itemList = new String[]{"hello", "World", "from", "String", "Array"};
        itemPrice = new int[]{100,200,300,400,500};
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemList);
        spinner.setAdapter(arrayAdapter);
    }

    private void callOnClickListener() {

        saveAndPrint.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String customerName = String.valueOf(editTextName.getText());
                String contactNo = String.valueOf(editTextPhone.getText());
                String item = spinner.getSelectedItem().toString();
                //int qty = Integer.parseInt(String.valueOf(editTextQty.getText()));
                int qty = Integer.parseInt(editTextQty.getText().toString());
                int amount = qty * itemPrice[ spinner.getSelectedItemPosition()];
                helperSQL.insert(customerName, contactNo, date.getTime(), item, qty, amount);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    printInvoice();
                }
                Toast.makeText(MainActivity.this,"Successfully Converted To PDF", Toast.LENGTH_LONG ).show();
            }
        });

        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PrintOldPDFActivity.class);
                startActivity(intent);
            }
        });
    }

    //Step 4

    private void printInvoice() {

        PdfDocument pdfDocument = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            pdfDocument = new PdfDocument();

            Paint paint = new Paint();

            String[] column = {"invoiceNo", "customerName", "contactNo", "date", "item", "qty", "amount"};

            Cursor cursor = sqLiteDatabase.query("PdfTABLE", column, null, null, null, null, null);
            cursor.move(cursor.getCount());


            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1000, 900, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            Canvas canvas = page.getCanvas();

            //first text
            paint.setTextSize(80);
            canvas.drawText("Custom Builds", 30, 80, paint);

            //second text
            paint.setTextSize(30);
            canvas.drawText("#21, Rajeev Gandji Nagar, Nandini Layout, bengaluru 96", 30, 120, paint);

            //
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("Invoice Number", canvas.getWidth() - 40, 80, paint);
            canvas.drawText(String.valueOf(cursor.getInt(0)), canvas.getWidth() - 40, 80, paint);
            paint.setTextAlign(Paint.Align.LEFT);

            paint.setColor(Color.rgb(150, 150, 150));
            canvas.drawRect(30, 150, canvas.getWidth() - 30, 160, paint);

            paint.setColor(Color.BLACK);
            canvas.drawText("Date", 50, 200, paint);
            canvas.drawText(dateFormat.format(cursor.getLong(3)), 250, 200, paint);

            canvas.drawText("Time", 620, 200, paint);
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(timeFormat.format(cursor.getLong(3)), canvas.getWidth() - 40, 200, paint);
            paint.setTextAlign(Paint.Align.LEFT);

            paint.setColor(Color.rgb(150, 150, 150));
            canvas.drawRect(30, 250, 250, 300, paint);

            paint.setColor(Color.WHITE);
            canvas.drawText("Bill to", 50, 285, paint);

            paint.setColor(Color.BLACK);
            canvas.drawText("Customer Name:", 30, 350, paint);
            canvas.drawText(cursor.getString(1), 280, 350, paint);

            canvas.drawText("Phone", 620, 350, paint);
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(cursor.getString(2), canvas.getWidth() - 40, 350, paint);
            paint.setTextAlign(Paint.Align.LEFT);

            // create rectangular bar
            paint.setColor(Color.rgb(150, 150, 150));
            canvas.drawRect(30, 400, canvas.getWidth() - 40, 450, paint);

            paint.setColor(Color.WHITE);
            canvas.drawText("Item", 50, 435, paint);
            canvas.drawText("Qty", 550, 435, paint);
            paint.setTextAlign(Paint.Align.RIGHT);

            canvas.drawText("Amount", canvas.getWidth() - 40, 435, paint);
            paint.setTextAlign(Paint.Align.LEFT);

            paint.setColor(Color.BLACK);
            canvas.drawText(cursor.getString(4), 50, 480, paint);
            canvas.drawText(String.valueOf(cursor.getInt(5)), 550, 480, paint);
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(String.valueOf(cursor.getInt(6)), canvas.getWidth() - 40, 480, paint);

            paint.setColor(Color.rgb(150, 150, 150));
            canvas.drawRect(30, 550, canvas.getWidth() - 30, 560, paint);

            paint.setColor(Color.BLACK);
            canvas.drawText("Sub Total", 550, 600, paint);
            canvas.drawText("Tax 4%", 550, 640, paint);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

            canvas.drawText("TOTAL", 550, 680, paint);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(String.valueOf(cursor.getInt(6)), 970, 600, paint);
            canvas.drawText(String.valueOf(cursor.getInt(6) * 4 / 100), 970, 640, paint);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText(String.valueOf(cursor.getInt(6) + (cursor.getInt(6) * 4 / 100)), 970, 680, paint);

            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("Make all check payable to \"Custom Builds\"", 30, 800, paint);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

            canvas.drawText("Thank you very much", 30, 840, paint);
            pdfDocument.finishPage(page);

            File file = new File(this.getExternalFilesDir("/"), cursor.getInt(0) + "CustomBuilds.pdf");

            try {
                pdfDocument.writeTo(new FileOutputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            pdfDocument.close();
        }
    }
}