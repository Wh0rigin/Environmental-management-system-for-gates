package com.example.newland.c344;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImgActivity extends Activity {

    Button btn_home;

    MyAdapter adapter;

    ListView imglist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img);
        btn_home = findViewById(R.id.button4);

        imglist = findViewById(R.id.piclist);

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ImgActivity.this,MainActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<String> imagePathList = new ArrayList<>();
        File fileAll = new File(getFilesDir()+"/capture");
        File[] files = fileAll.listFiles();
        if(files == null)Toast.makeText(this,"没有文件",Toast.LENGTH_SHORT).show();
        else{
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                imagePathList.add(file.getPath());
            }
//            ImageView img = findViewById(R.id.img_2);
//            img.setImageURI(Uri.fromFile(new File(getFilesDir()+"/capture/"+1)));
            adapter = new MyAdapter(this,imagePathList);
            imglist.setAdapter(adapter);
        }

    }


   class MyAdapter extends BaseAdapter{
       private List<String> imagePathList;
       private LayoutInflater layoutInflater;

       public MyAdapter(Context context,List<String> s){
           imagePathList = s;
           layoutInflater = LayoutInflater.from(context);
       }

       public void addAll(List<String> s){
           imagePathList = s;
       }

       @Override
       public int getCount() {
           return imagePathList.size();
       }

       @Override
       public Object getItem(int i) {
           return imagePathList.get(i);
       }

       @Override
       public long getItemId(int i) {
           return i;
       }

       @Override
       public View getView(int i, View view, ViewGroup viewGroup) {
           View myview = layoutInflater.inflate(R.layout.image,null);
           ImageView img = myview.findViewById(R.id.img);
           img.setImageURI(Uri.fromFile(new File(imagePathList.get(i))));
           return myview;
       }
   }


}
