package com.example.krsch_volkhontsev;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

public class Main2Activity extends AppCompatActivity {
    public Context context = this;
    public String file_path = "";
    public TextView textView_for_path;
    public int height_layout, width_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewGroup.LayoutParams params = findViewById(R.id.linearLayout).getLayoutParams();
        height_layout = params.height;
        width_layout = params.width;

        final Intent intent = getIntent();
        final int success = intent.getIntExtra("success", -1);
        final int count = intent.getIntExtra("count", -1);
        TextView textView = findViewById(R.id.textView);
        String str_out = "Успех: " + Integer.toString(success) + " из " + Integer.toString(count);
        textView.setText(str_out);

        Infographic infographic = new Infographic(this, success, count);
        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        linearLayout.addView(infographic);

        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy -- HH:mm:ss", Locale.getDefault());
        final SharedPreferences memory = getSharedPreferences("best", Context.MODE_PRIVATE);
        int best = memory.getInt("best_result", -1);
        if (best == -1)
        {
            SharedPreferences.Editor editor = memory.edit();
            editor.putInt("best_result", success);
            editor.putString("data", dateFormat.format(currentDate));
            editor.apply();
        }
        else
        {
            if (success > best)
            {
                Toast.makeText(getApplicationContext(), "ЛУЧШИЙ РЕЗУЛЬТАТ!", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = memory.edit();
                editor.putInt("best_result", success);
                editor.putString("data", dateFormat.format(currentDate));
                editor.apply();
            }
        }

        Button button_best = findViewById(R.id.button);
        button_best.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Лучший результат равен " +
                        Integer.toString(memory.getInt("best_result", -1)) +
                        ". Был получен " + memory.getString("data", "никогда")
                );
                alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                alert.setNegativeButton("Сбросить лучший результат", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        SharedPreferences.Editor editor = memory.edit();
                        editor.putInt("best_result", -1);
                        editor.putString("data", "никогда");
                        editor.apply();
                    }
                });
                alert.show();
            }
        });

        Button button = findViewById(R.id.button5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        //DBHelper dbHelper = new DBHelper(context);
        //dbHelper.delete_bd(context);
        Button button_add = findViewById(R.id.button6);
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder alert1 = new AlertDialog.Builder(context);
                alert1.setTitle("Введите имя и выберите файл");
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                final EditText input = new EditText(context);
                linearLayout.addView(input);
                final Button button1 = new Button(context);
                button1.setText("Нажмите для выбора файла.");
                linearLayout.addView(button1);
                textView_for_path = new TextView(context);
                textView_for_path.setText(file_path);
                linearLayout.addView(textView_for_path);

                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, 1);
                    }
                });
                alert1.setView(linearLayout);

                alert1.setPositiveButton("Добавить слово", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String name = input.getText().toString();
                        if ((name.equals("")) || file_path.equals(""))
                        {
                            Toast.makeText(getApplicationContext(),
                                    "Надо ввести слово и выбрать файл", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        DBHelper dbHelper = new DBHelper(context);
                        SQLiteDatabase db =  dbHelper.getReadableDatabase();
                        ContentValues cv = new ContentValues();
                        cv.put("name", name);
                        cv.put("path", file_path);
                        long rowID = db.insert("files", null, cv);
                        if (rowID == -1) Toast.makeText(context, "Такое слово уже есть.", Toast.LENGTH_SHORT).show();
                        cv.clear();
                        file_path = "";
                    }
                });
                alert1.setNegativeButton("Оставить всё как было", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DBHelper dbHelper = new DBHelper(context);
                        List<String> names = dbHelper.get_names();
                        Toast.makeText(context, Integer.toString(names.size()), Toast.LENGTH_SHORT).show();
                        file_path = "";
                    }
                });

                alert1.show();
            }
        });
    }
    @Override
    public void onBackPressed()
    {
        Toast.makeText(getApplicationContext(),
                "Чтобы выйти из приложения нажмите кнопку повтора викторины, а затем кнопку \"назад\"."
                ,Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if ((resultCode == RESULT_OK) && (requestCode == 1))
        {
            Uri chosenImageUri = data.getData();
            final Cursor cursor = getContentResolver().query( chosenImageUri, null, null, null, null );
            cursor.moveToFirst();
            file_path = "Не получилось найти путь к файлу.";
            do {
                for (int q = 0; q < 1000; q++)
                {
                    try
                    {
                        File imgFile = new File(cursor.getString(q));
                        if (imgFile.exists())
                        {
                            file_path = cursor.getString(q);
                            break;
                        }
                    }
                    catch (Exception e) { }
                }
            } while (cursor.moveToNext());
            cursor.close();
            String str = "Выбран файл: " + file_path;
            textView_for_path.setText(str);
        }
    }

    private class Infographic extends View {
        public Infographic(Context context, Integer success, Integer count)
        {
            super(context);
            this.success = success;
            this.count = count;
        }
        private Paint mPaint = new Paint();
        private int success;
        private int count;
        @Override
        protected void onDraw(Canvas canvas){
            super.onDraw(canvas);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

            mPaint.setColor(Color.GREEN);
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            int radius = Math.min(height_layout, width_layout) / 2;
            int angle = Math.round(((float) this.success / this.count)*360);
            final RectF oval = new RectF();
            oval.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
            canvas.drawArc(oval, 0,angle, true, mPaint);
            mPaint.setColor(Color.RED);
            canvas.drawArc(oval,angle,360-angle, true, mPaint);
        }
    }
}


class DBHelper extends SQLiteOpenHelper
{
    public DBHelper(Context context)
    {
        super(context, "db_images", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE files (name TEXT PRIMARY KEY, path TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public int delete_bd(Context context)
    {
        if (context.deleteDatabase("db_images")) return 0;
        else return -1;

    }

    public String get_path(String name)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query_string = "SELECT path FROM files WHERE name = ?";
        String[] args = new String[] { name };
        Cursor c = db.rawQuery(query_string, args);
        if (c.moveToFirst())
        {
            String path = c.getString(c.getColumnIndex("path"));
            c.close();
            return path;
        }
        else
        {
            c.close();
            return "";
        }
    }

    public List<String> get_names()
    {
        List<String> names = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("files", null, null, null, null, null, null);
        if (c.moveToFirst())
        {
            int nameIndex = c.getColumnIndex("name");
            int pathIndex = c.getColumnIndex("path");

            do {
                File file = new File(c.getString(pathIndex));
                if (file.exists())
                {
                    String name = c.getString(nameIndex);
                    names.add(name);

                }
            }
            while (c.moveToNext());
        }
        c.close();
        return names;
    }
}
