package com.jian86_android.mysubway;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener {
   private ImageView iv;
   private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = findViewById(R.id.iv);
        et = findViewById(R.id.et);

        et.setOnEditorActionListener(this);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterOut();
            }
        });//iv Listener
    }//Main

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_SEARCH == actionId) {
            filterOut();
        } else {return false; }
        return true;
    }//onEditorAction
    private void filterOut(){
        if(et.getText().toString().equals("")){
            Toast.makeText(MainActivity.this, "역이름은 입력 하셔야돼요 !!", Toast.LENGTH_SHORT).show();
        }else{
            search();
        }
    }
    void search(){

        String s_station = et.getText().toString();
        /*String s_lastchar =  s_station.charAt(s_station.length()-2)+s_station.charAt(s_station.length()-1)+"";

        if (s_lastchar.equals("역역")){
            s_station = s_station.substring(0,s_station.length()-1);
        }else if((s_station.charAt(s_station.length()-1)+"").equals("역"))*/
       // Toast.makeText(this, s_station, Toast.LENGTH_SHORT).show();
        //마지막 글자에 "역"이 있으면 삭제하고 이름만 보내기

        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("station_name", s_station);
        startActivity(intent);
    }//search


}
