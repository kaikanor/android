package com.example.krsch_volkhontsev;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public int i = 0;
    public ImageView imageView;
    public Button[] buttons = new Button[3];
    public List<Integer> list_id = new ArrayList<>();
    public List<String> list_name = new ArrayList<>();
    public String main_name;
    public int success = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final TextToSpeech speech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(final int status) {
            }
        });

        buttons[0] = findViewById(R.id.button2);
        buttons[1] = findViewById(R.id.button3);
        buttons[2] = findViewById(R.id.button4);

        imageView = findViewById(R.id.imageView2);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speech.speak(main_name, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        final R.drawable id = new R.drawable();
        final Class<R.drawable> c = R.drawable.class;
        final java.lang.reflect.Field[] fields = c.getDeclaredFields();
        for (Field field : fields)
        {
            final int idR;
            try
            {
                idR = field.getInt(id);
                if (!(field.getName().contains("_"))) list_id.add(idR);
            }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        list_name.addAll(dbHelper.get_names());
        Collections.shuffle(list_name);

        Collections.shuffle(list_id);

        main_name =  getResources().getResourceEntryName(list_id.get(i));
        paint(null);

        buttons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paint(buttons[0]);
            }
        });
        buttons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paint(buttons[1]);
            }
        });
        buttons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paint(buttons[2]);
            }
        });
    }

    public void paint(Button button)
    {
        if (button != null)
        {
            String name_button = button.getText().toString();
            if (name_button.equals(main_name))
            {
                success++;
            }
        }
        if (i == list_id.size() + list_name.size())
        {
            for (int k = 0; k < 3; k++)
            {
                buttons[k].setText("");
                buttons[k].setEnabled(false);
            }
            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
            intent.putExtra("success", success);
            intent.putExtra("count", list_id.size() + list_name.size());
            startActivityForResult(intent, 0);
            return;
        }
        if (i < list_id.size())
        {
            main_name =  getResources().getResourceEntryName(list_id.get(i));
            int id = list_id.get(i);
            imageView.setImageResource(id);
            String name = getResources().getResourceEntryName(id);
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            List<String> list1 = new ArrayList<>(dbHelper.get_names());
            for (int id_list : list_id)
            {
                if (id_list != id) list1.add(getResources().getResourceEntryName(id_list));
            }
            Collections.shuffle(list1);

            List<Integer> indexes = new ArrayList<>();
            indexes.add(0);
            indexes.add(1);
            indexes.add(2);
            Collections.shuffle(indexes);
            buttons[indexes.get(0)].setText(name);
            buttons[indexes.get(1)].setText(list1.get(0));
            buttons[indexes.get(2)].setText(list1.get(1));
            TextView textView = findViewById(R.id.textView2);
            String text = Integer.toString(i) + " из " + Integer.toString(list_id.size() + list_name.size());
            text += "\n" + Integer.toString(success) + " успешных.";
            textView.setText(text);
        }
        else
        {
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            String name = list_name.get(i - list_id.size());
            main_name = name;
            String path = dbHelper.get_path(name);
            File imgFile = new File(path);
            if(imgFile.exists())
            {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                imageView.setMaxHeight(100);
            }
            else
            {
                String str = "Файла для " + name + " просто нет." ;
                alert(str);
            }

            List<String> list1 = new ArrayList<>(dbHelper.get_names());
            list1.remove(name);
            for (int id_list : list_id)
            {
                list1.add(getResources().getResourceEntryName(id_list));
            }
            Collections.shuffle(list1);

            List<Integer> indexes = new ArrayList<>();
            indexes.add(0);
            indexes.add(1);
            indexes.add(2);
            Collections.shuffle(indexes);
            buttons[indexes.get(0)].setText(name);
            buttons[indexes.get(1)].setText(list1.get(0));
            buttons[indexes.get(2)].setText(list1.get(1));
            TextView textView = findViewById(R.id.textView2);
            String text = Integer.toString(i) + " из " + Integer.toString(list_id.size() + list_name.size());
            text += "\n" + Integer.toString(success) + " успешных.";
            textView.setText(text);
        }
        i = i + 1;
    }

    public void alert(String str)
    {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        list_name = new ArrayList<>(dbHelper.get_names());
        Collections.shuffle(list_name);

        buttons[0].setEnabled(true);
        buttons[1].setEnabled(true);
        buttons[2].setEnabled(true);
        i = 0;
        success = 0;
        Collections.shuffle(list_id);
        main_name =  getResources().getResourceEntryName(list_id.get(i));
        paint(null);
        }
}

