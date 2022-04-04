package com.example.newland.c344;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nle.mylibrary.forUse.mdbus4150.Modbus4150;
import com.nle.mylibrary.forUse.zigbee.FourChannelValConvert;
import com.nle.mylibrary.forUse.zigbee.Zigbee;
import com.nle.mylibrary.transfer.DataBus;
import com.nle.mylibrary.transfer.DataBusFactory;

import java.io.File;
import java.util.Date;

import nledu.com.ipcamera.CameraManager;
import nledu.com.ipcamera.PTZ;

public class MainActivity extends Activity {

    Modbus4150 modbus4150;
    Zigbee zigbee;

    static final int push = 1;
    static final int pull = 0;

    TextView tv_tem,tv_hum,tv_co2,tv_noise;

    boolean isOpen = false;
    Save save;
    Auto auto;

    SQLiteDatabase db;

    Button speed,pushbtn,pullbtn,upbtn,downbtn,leftbtn,rightbtn,catchbtn,databtn,picbtn;
    TextToSpeech textToSpeech;

    TextureView textureView;

    boolean pull_F = false;
    boolean push_F = false;


    int length = 0;

    static final int pushed = 0;
    static final int pulled = 1;

    CameraManager cameraManager;

    String sqlcmd = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = SQLiteDatabase.openOrCreateDatabase(getFilesDir()+"/save.db",null);
//        try{
//            sqlcmd = "drop table save";
//            db.execSQL(sqlcmd);
//        }catch(Exception e){}
//        sqlcmd = "create table save(_id integer primary key,name text,value text,time integer)";
//        db.execSQL(sqlcmd);
        speed = findViewById(R.id.button);
        pullbtn = findViewById(R.id.button2);
        pushbtn = findViewById(R.id.button3);
        textureView = findViewById(R.id.textureView);
        upbtn = findViewById(R.id.up);
        downbtn = findViewById(R.id.down);
        leftbtn = findViewById(R.id.left);
        rightbtn = findViewById(R.id.right);
        new File(getFilesDir()+"/capture").mkdir();
        tv_tem = findViewById(R.id.textView4);
        tv_hum = findViewById(R.id.textView5);
        tv_co2 = findViewById(R.id.textView6);
        tv_noise = findViewById(R.id.textView7);

        catchbtn = findViewById(R.id.catchpic);
        databtn = findViewById(R.id.dataed);
        cameraManager = CameraManager.getInstance();
        cameraManager.setupInfo(textureView,"admin","admin","172.16.25.13","1");

        picbtn = findViewById(R.id.picbtn);

        speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double[] dat = null;
                try {
                     dat  = zigbee.getFourEnter();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(dat != null){
                    textToSpeech.speak("温度:"+FourChannelValConvert.getTemperature(dat[0]),TextToSpeech.QUEUE_ADD,null);
                    textToSpeech.speak("湿度:"+FourChannelValConvert.getHumidity(dat[1]),TextToSpeech.QUEUE_ADD,null);
                    textToSpeech.speak("二氧化碳:"+FourChannelValConvert.getCO2(dat[2]),TextToSpeech.QUEUE_ADD,null);
                    textToSpeech.speak("噪音:"+FourChannelValConvert.getNoice(dat[3]),TextToSpeech.QUEUE_ADD,null);
                }
            }
        });


        picbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ImgActivity.class));
            }
        });


        catchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraManager.capture(getFilesDir()+"/capture",System.currentTimeMillis()+"");
            }
        });
//
//
        pushbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    modbus4150.openRelay(push,null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new Handler().postDelayed(()->{
                    try {
                        modbus4150.closeRelay(pull,null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },100);
                new Handler().postDelayed(()->{
                    try {
                        modbus4150.closeRelay(push,null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },2000);
            }
        });

        pullbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    modbus4150.openRelay(pull,null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new Handler().postDelayed(()->{
                    try {
                        modbus4150.closeRelay(push,null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },100);
                new Handler().postDelayed(()->{
                    try {
                        modbus4150.closeRelay(pull,null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },2000);
            }
        });
//
        upbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraManager.controlDir(PTZ.Up);
            }
        });
        downbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraManager.controlDir(PTZ.Down);
            }
        });
        rightbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraManager.controlDir(PTZ.Right);
            }
        });
        leftbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraManager.controlDir(PTZ.Left);
            }
        });
//
        databtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,DataActivity.class));
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
//

    }


    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(()->{
            runOnUiThread(()->{
                cameraManager.openCamera();
            });
        },1000);
        modbus4150 = new Modbus4150(DataBusFactory.newSocketDataBus("172.16.25.15",6001));
        zigbee = new Zigbee(DataBusFactory.newSocketDataBus("172.16.25.15",6003));
        db = SQLiteDatabase.openOrCreateDatabase(getFilesDir()+"/save.db",null);
        textToSpeech = new TextToSpeech(MainActivity.this,null);
        isOpen = true;
        auto = new Auto();
        auto.start();
        save = new Save();
        save.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //db.close();
        zigbee.stopConnect();
        modbus4150.stopConnect();
        cameraManager.releaseCamera();
        isOpen =false;
        auto.interrupt();
        save.interrupt();
    }
//


    class Auto extends Thread{
        @Override
        public void run() {
            super.run();
            while(isOpen){
                try {
                    Thread.sleep(501);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(()->{
                    double[] dataa = null;
                    try {
                        dataa  = zigbee.getFourEnter();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(dataa!=null){
                        tv_tem.setText("温度:"+FourChannelValConvert.getTemperature(dataa[0])+"");
                        tv_hum.setText("湿度"+FourChannelValConvert.getHumidity(dataa[1])+"");
                        tv_co2.setText("二氧化碳"+FourChannelValConvert.getCO2(dataa[2])+"");
                        tv_noise.setText("噪音"+FourChannelValConvert.getNoice(dataa[3])+"");
                    }
                });
            }
        }
    }

    class Save extends Thread{
        @Override
        public void run() {
            super.run();
            while(isOpen){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //save
                runOnUiThread(()->{
                    sqlcmd = "select * from save";
                    length = db.rawQuery(sqlcmd,null).getCount();
                    double[] dat = null;
                    try {
                        dat  = zigbee.getFourEnter();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(dat != null){
                        sqlcmd = "insert into save(_id,name,value,time)values("+length+",'temp','"+FourChannelValConvert.getTemperature(dat[0])+"',"+new Date().getTime()+")";
                        db.execSQL(sqlcmd);
                        sqlcmd = "insert into save(_id,name,value,time)values("+(length+1)+",'humi','"+FourChannelValConvert.getTemperature(dat[1])+"',"+new Date().getTime()+")";
                        db.execSQL(sqlcmd);
                        sqlcmd = "insert into save(_id,name,value,time)values("+(length+2)+",'co2','"+FourChannelValConvert.getTemperature(dat[2])+"',"+new Date().getTime()+")";
                        db.execSQL(sqlcmd);
                        sqlcmd = "insert into save(_id,name,value,time)values("+(length+3)+",'noise','"+FourChannelValConvert.getTemperature(dat[3])+"',"+new Date().getTime()+")";
                        db.execSQL(sqlcmd);

                    }


                });


            }
        }
    }
}
