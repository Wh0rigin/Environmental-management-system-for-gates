package com.example.newland.c344;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataActivity extends Activity {

    Button homebtn;
    String sqlcmd = "";
    SQLiteDatabase db;
    ListView listView;
    boolean isOpen = false;

    Auto auto;

    EditText et_name,et_startTime,et_stopTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        homebtn = findViewById(R.id.gohome);
        listView = findViewById(R.id.datalist);
        et_name = findViewById(R.id.editText);
        et_startTime = findViewById(R.id.editText2);
        et_stopTime = findViewById(R.id.editText3);
        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DataActivity.this,MainActivity.class));
                finish();
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        isOpen = true;
        db = SQLiteDatabase.openOrCreateDatabase(getFilesDir()+"/save.db",null);
        auto = new Auto();
        auto.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOpen = false;
        auto.interrupt();
    }

    class Auto extends Thread{
        @Override
        public void run() {
            super.run();
            while(isOpen){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                runOnUiThread(()->{
                    try {
                        sqlcmd = "select * from save where name ='"+et_name.getText().toString()+"' and time >= "+simpleDateFormat.parse(et_startTime.getText().toString()).getTime() +" and time <= "+simpleDateFormat.parse(et_stopTime.getText().toString()).getTime() ;

                    Cursor cursor = db.rawQuery(sqlcmd,null);
                    SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(
                            DataActivity.this,R.layout.datalinearout,cursor,new String[]{
                            "_id","name","value","time"
                    },
                            new int[]{
                                    R.id.dataId,R.id.dataName,R.id.dataValue,R.id.dataTime
                            }
                            ,SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
                    listView.setAdapter(simpleCursorAdapter);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                });

            }
        }
    }
}
