package com.jian86_android.mysubway;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

public class SearchActivity extends AppCompatActivity {
    private String station_NM;//넘어오는 station
    private RadioGroup rg_line,rg_ul;
    private RadioButton upper_rb,lower_rb;
    private TextView tv_station;
    private Button upperBtn,lowerBtn;
    private StationData stationData;
    String station_CD;
    ArrayList<StationData> datas = new ArrayList<>(); // 호선이 여러개면 호선별 담을
    final String forCodeKey ="654a644b696f307835376272795049"; //역사 키
    final String finalKey ="4d4667676e6f30783730726e79776c"; //운행정보 키
    //final String forLineKey ="50766f55776f3078313139777a764649"; //노선라인별 역정보를 얻기위한
    private ListView listView;

    private ArrayList<StationInfo> stationDatasU =  new ArrayList<>() ; //상행선 정보
    private ArrayList<StationInfo> stationDatasL =  new ArrayList<>() ; //하행선 정보
    private ArrayList<StationInfo> stationDatas; //상하행선 정보

    private ArrayList<TimeInfo> timeInfos = new ArrayList<>();//리스트에 들어갈 시간
    private MyAdapter myadapter;//어댑터
    private boolean isrun = true;
    private boolean firsttimeRun =true;
    private Button btn_search;
    String selectTime;

    HashSet<String> setTime ;
    HashSet<String> setTimeU ;
    HashSet<String> setTimeL ;
    HashSet<String> staionsE ;
    HashSet<String> staionsEU ;
    HashSet<String> staionsEL ;
    String strStationU;//방면표기용
    String strStationL;
    String aress; //final adress
    String upperLower="1";
    String upperLowerstationline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = new Intent(this.getIntent());
        station_NM = intent.getStringExtra("station_name");
        rg_line = findViewById(R.id.rg_line);
        rg_ul =findViewById(R.id.rg_ul);
        upper_rb =findViewById(R.id.upper_rb);
        lower_rb =findViewById(R.id.lower_rb);
        btn_search= findViewById(R.id.btn_search);

        tv_station = findViewById(R.id.station_name);
        upperBtn =findViewById(R.id.upper_station);
        lowerBtn =findViewById(R.id.lower_station);
        listView =findViewById(R.id.listview);

        //spinner = findViewById(R.id.spinner);
        //setting
        tv_station.setText(station_NM);
        myadapter = new MyAdapter(this,timeInfos);
        listView.setAdapter(myadapter);
        btn_search.setEnabled(false);

//1. 역사정보 얻기
        new Thread(){
            String rb_value;
            @Override
            public void run() {
                String address = "http://openAPI.seoul.go.kr:8088"
                        + "/" + forCodeKey
                        + "/xml"
                        + "/SearchInfoBySubwayNameService"
                        + "/1"
                        + "/20"
                        + "/" + station_NM;
                URL url = null;
                try {
                    url = new URL(address);
                    InputStream inputStream = url.openStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

             //파싱 준비및 시작
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = factory.newPullParser();
                    parser.setInput(inputStreamReader);

                    int eventType = parser.getEventType();
                    String tagName = null;
                    String text = null;
                    while (eventType != XmlPullParser.END_DOCUMENT){
                        switch (eventType){
                            case XmlPullParser.START_DOCUMENT:

                                break;
                            case XmlPullParser.START_TAG:
                                tagName =parser.getName();
                                if(tagName.equals("row")){
                                    stationData = new StationData();
                                }
                                else if(tagName.equals("STATION_CD")){
                                    parser.next();
                                    text = parser.getText();
                                    stationData.setStation_CD(text);
                                }
                                else if(tagName.equals("STATION_NM")){
                                    parser.next();
                                    text = parser.getText();
                                    stationData.setStation_NM(text);
                                }else if(tagName.equals("LINE_NUM")){
                                    parser.next();
                                    text = parser.getText();
                                    stationData.setLine_num(text);
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                tagName =parser.getName();
                                if(tagName.equals("row")){
                                    datas.add(stationData);

                                }//if
                                break;

                        }//switch
                        parser.next();
                        eventType = parser.getEventType();
                    }//while
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mkRadioBtton();
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();//유알엘 연결 실패
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SearchActivity.this, "url연결실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();//스트림 실패
                } catch (XmlPullParserException e) {
                    e.printStackTrace(); //파서 공장 실패
                }

            }//run
            //라디오 버튼 만들기
            void mkRadioBtton(){

                //Toast.makeText(SearchActivity.this, datas.size()+"", Toast.LENGTH_SHORT).show();
                RadioButton[] line_rgBtns = new RadioButton[datas.size()];
                for (int i=0; i<datas.size();i++){
                    line_rgBtns[i] = new RadioButton(SearchActivity.this);
                    line_rgBtns[i].setId(i);
                    line_rgBtns[i].setText(datas.get(i).getLine_num());
                    rg_line.addView(line_rgBtns[i]);
                }
                upperLowerstationline =line_rgBtns[0].getText().toString();
                line_rgBtns[0].setChecked(true);
                //btn_search.setEnabled(true);
                //upper_rb.setChecked(true);
                if(firsttimeRun){
                    setTimeU = new HashSet<>();
                    setTimeL = new HashSet<>();
                    staionsEU = new HashSet<>();
                    staionsEL = new HashSet<>();
                    if(isrun) gettingStation(upperLowerstationline);
                    //gettingStation(upperLowerstationline,"2");
                    firsttimeRun=false;
                }


                //상행선 하행선
                rg_ul.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        timeInfos.clear();
                        myadapter.notifyDataSetChanged();
                        btn_search.setEnabled(true);
                        RadioButton rbb = (RadioButton) group.findViewById(checkedId);
                        if(rbb != null){
                                if (rbb == upper_rb) {
                                    mkAddr("1");
                                } else if (rbb == lower_rb) {
                                    mkAddr("2");
                                }

                                // Toast.makeText(SearchActivity.this,upperLower , Toast.LENGTH_SHORT).show();
                                //if (isrun) gettingStation(upperLowerstationline, upperLower);
                        }//if
                    }//onCheckedChanged


                });//라디오그룹 리스너

                //호선 라디오버튼의 값얻어오기
             rg_line.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                 @Override
                 public void onCheckedChanged(RadioGroup group, int checkedId) {
                     timeInfos.clear();
                     myadapter.notifyDataSetChanged();
                     RadioButton rb = (RadioButton) group.findViewById(checkedId);
                     rb_value = rb.getText().toString();
                     if(rb !=null){
                         // Toast.makeText(SearchActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
                         upperLowerstationline = rb_value;
                         //upper_rb.setChecked(true);
                         stationDatasU.clear();
                         stationDatasL.clear();
                         setTimeU = new HashSet<>();
                         setTimeL = new HashSet<>();
                         staionsEU = new HashSet<>();
                         staionsEL = new HashSet<>();
                         if(isrun) gettingStation(upperLowerstationline);


                         }
                     }



             });//라디오그룹 리스너

            }//mkRadioBtton

//2.라인 얻기고 두번째 파싱

            void gettingStation(String lineNM){
                isrun=false;
                timeInfos.clear();
                // myadapter.notifyDataSetChanged();
          //      Toast.makeText(SearchActivity.this, lineNM, Toast.LENGTH_SHORT).show();
                String line_NM = lineNM;
                station_CD = null;

                for(int i = 0 ; i< datas.size(); i++){
                    if(datas.get(i).getLine_num().equals(lineNM)){
                        station_CD = datas.get(i).getStation_CD();
                        Toast.makeText(SearchActivity.this, station_CD, Toast.LENGTH_SHORT).show();
                    }//if
                }//for
                //

                strStationU="";//방면표기용
                strStationL="";//방면표기용
                ThreadbaseTimeList ttl = new ThreadbaseTimeList("1");
                ttl.start();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ThreadbaseTimeList ttl2 = new ThreadbaseTimeList("2");
                ttl2.start();

            }//gettingStation

            class ThreadbaseTimeList extends Thread{
                    String linenum;
                ThreadbaseTimeList(String linenum){this.linenum = linenum; }
                    @Override
                    public void run() {

                        aress = "http://openAPI.seoul.go.kr:8088"
                                + "/" + finalKey
                                + "/xml"
                                + "/SearchSTNTimeTableByIDService"
                                + "/1"
                                + "/1000"
                                + "/" + station_CD //전철역 코드값
                                + "/" + date()//요일 평일:1, 토요일:2, 휴일/일요일:3
                                + "/" + linenum ; // 상하행선 상행,내선:1, 하행,외선:2
                        URL url = null;
                        try {
                            url = new URL(aress);
                            InputStream inputStream = url.openStream();
                            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                            XmlPullParser pullParser = factory.newPullParser();
                            pullParser.setInput(inputStreamReader);

                            int eventType = pullParser.getEventType();
                            String tagName = null;
                            String text = null;
                            StationInfo stationInfo = null;

                            while (eventType !=XmlPullParser.END_DOCUMENT){
                                //String times=null;
                                //String distan=null;

                                switch(eventType){
                                    case XmlPullParser.START_DOCUMENT:
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(SearchActivity.this, "파싱시작", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        break;
                                    case XmlPullParser.START_TAG :
                                        tagName = pullParser.getName();
                                        if(tagName.equals("row")){ //시작
                                            stationInfo = new StationInfo();

                                        }//시작
                                        else if(tagName.equals("LINE_NUM")){
                                            pullParser.next();
                                            text = pullParser.getText();
                                            stationInfo.setLine_num(text);
                                        }//호선
                                        else if(tagName.equals("STATION_CD")){
                                            pullParser.next();
                                            text = pullParser.getText();
                                            stationInfo.setStation_CD(text);
                                        }//역코드
                                        else if(tagName.equals("STATION_NM")){
                                            pullParser.next();
                                            text = pullParser.getText();
                                            stationInfo.setStation_NM(text);
                                        }//역이름
                                        else if(tagName.equals("TRAIN_NO")){
                                            pullParser.next();
                                            text = pullParser.getText();
                                            stationInfo.setTrain_N0(text);
                                        }//열차번호
                                        else if(tagName.equals("ARRIVETIME")){
                                            pullParser.next();
                                            text = pullParser.getText();
                                            stationInfo.setArrive_TIME(text);
                                            /*String t = text.substring(0,2);
                                            setTime.add(t);*/

                                        }//도착시간
                                        else if(tagName.equals("LEFTTIME")){
                                            pullParser.next();
                                            text = pullParser.getText();
                                            stationInfo.setLeft_TIME(text);
                                            String t = text.substring(0,2);
                                            if(linenum.equals("1")){setTimeU.add(t);}
                                            else if(linenum.equals("2")){setTimeL.add(t);}
                                        }//출발시간
                                        else if(tagName.equals("SUBWAYENAME")){
                                            pullParser.next();
                                            text = pullParser.getText();
                                            stationInfo.setSubwayE_NM(text);

                                            if(linenum.equals("1")){staionsEU.add(text);}
                                            else if(linenum.equals("2")){staionsEL.add(text);}
                                        }//도착역
                                        else if(tagName.equals("WEEK_TAG")){
                                            pullParser.next();
                                            text = pullParser.getText();
                                            stationInfo.setWeek_TAG(text);

                                        }//요일
                                        break;

                                    case XmlPullParser.END_TAG :
                                        tagName = pullParser.getName();
                                        if(tagName.equals("row")){ //끝
                                            if(linenum.equals("1")){stationDatasU.add(stationInfo);
                                            }
                                            else if(linenum.equals("2")){stationDatasL.add(stationInfo);}
                                        }//if

                                        break;

                                }//catch
                                pullParser.next();
                                eventType = pullParser.getEventType();
                            }//while

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }//파싱try
                        finally {

                            ArrayList<String> al=null;
                            if(linenum.equals("1")){al = new ArrayList<>(staionsEU);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            upper_rb.setChecked(true);
                                        }
                                    });

                            }
                            else if(linenum.equals("2")){al = new ArrayList<>(staionsEL);}
                            Collections.sort(al);

                            for(int i=0;i<al.size();i++){
                                if(linenum.equals("1")){strStationU+=al.get(i)+" ";}
                                else if(linenum.equals("2")){strStationL+=al.get(i)+" ";}

                            }
                            if(linenum.equals("1")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        upper_rb.setText("상행선 : "+strStationU);
                                    }
                                });
                            }else if(linenum.equals("2")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        lower_rb.setText("하행선 : "+strStationL);
                                        btn_search.setEnabled(true);
                                    }
                                });
                                isrun = true;
                            }

                        }//final



                    }//run

                    //요일 구하기
                    String date(){
                        Calendar cal = Calendar.getInstance();
                        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                        String dayofWeek; //일.공휴일 :3 ,평일 :1, 토요일 :2
                        if(dayOfWeek == 1){return dayofWeek="3";}
                        else if(dayOfWeek < 7){return dayofWeek="1";}
                        else {return dayofWeek="2";}
                    }

            }//threadbaseTimeList
            //전역 다음역 구하기
///*     class ThreadNextBeforeStation extends Thread{
//                @Override
//                public void run() {
//                    String addredss = "http://swopenapi.seoul.go.kr/api/subway" +
//                            "/"+forLineKey +
//                            "/xml" +
//                            "/realtimeStationArrival" +
//                            "/0" +
//                            "/5" +
//                            "/" + station_NM;
//                    URL url = null;
//                    try {
//                        url = new URL(addredss);
//                        InputStream inputStream =url.openStream();
//                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//                        XmlPullParser pullParser = factory.newPullParser();
//                        pullParser.setInput(inputStreamReader);
//
//                        int eventType = pullParser.getEventType();
//                        String tagName =null;
//                        String text = null;
//
//                        while(eventType !=XmlPullParser.END_DOCUMENT){
//
//                            switch (eventType){
//                                case XmlPullParser.START_DOCUMENT:
//                                    break;
//                                case XmlPullParser.START_TAG:
//                                    break;
//                                case XmlPullParser.END_TAG:
//                                    break;
//                            }//switch
//
//
//                            pullParser.next();
//                            eventType=pullParser.getEventType();
//                        }//while
//
//
//
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (XmlPullParserException e) {
//                        e.printStackTrace();
//                    }
//                }//run
//            }//ThreadNextBeforeStation
//*/
//




        }.start(); //역사 정보 얻기


    }//onCreate


    void mkAddr(String str){
        upperLower = str;
    }
    public void searchBtn(View view) {
        boolean hastime =false;
        timeInfos.clear();
        myadapter.notifyDataSetChanged();
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        String hourstr =String.format("%02d", hour);
        StringBuffer buffer = new StringBuffer();
        if(upperLower.equals("1")){stationDatas = stationDatasU; setTime = setTimeU;}
        else if(upperLower.equals("2")){stationDatas = stationDatasL; setTime = setTimeL;}

         ArrayList<String> al = new ArrayList<>(setTime);
         Collections.sort(al);

         if (setTime.size() <= 1 || setTime == null || stationDatas.size() <= 0) {
             TimeInfo tfo = new TimeInfo();
             tfo.setTime("");
             tfo.setUpper_time("운행 정보가 없습니다");
             timeInfos.add(tfo);
             hastime =true;
             buffer.append("운행 정보가 없습니다");
         } else {
             for (int z = 0; z < setTime.size(); z++) {
                 selectTime = al.get(z);
                 String s = "";
                 String t = "";
                 for (int i = 0; i < stationDatas.size(); i++) {
                     if (stationDatas.get(i).getLeft_TIME() != null && stationDatas.get(i).getLeft_TIME().length() >= 5) {
                         if (stationDatas.get(i).getLeft_TIME().substring(0, 2).equals(selectTime)) {
                             t = stationDatas.get(i).getLeft_TIME().substring(0, 2);
                             s += stationDatas.get(i).getLeft_TIME().substring(0, 5) + "(" + stationDatas.get(i).getSubwayE_NM() + ") ";
                         }//if

                     }//if

                 }//for
                 TimeInfo tfo = new TimeInfo();
                 tfo.setTime(t);
                 tfo.setUpper_time(s);
                 if(hourstr.equals(t)) {buffer.append(t+" 시\n\n"); buffer.append(s); hastime =true;}

                 timeInfos.add(tfo);
             }//for

         }
        if(!hastime){buffer.append("현재 시간의 운행 정보가 없습니다");}
        new AlertDialog.Builder(this).setMessage(buffer.toString() ).create().show();
         //  Calendar cal = Calendar.getInstance();
         //  int hour = cal.get(Calendar.HOUR_OF_DAY);
         // String hourstr =String.format("%02d", hour);
         //  Toast.makeText(this, hourstr, Toast.LENGTH_SHORT).show();
         setTime = null;
         myadapter.notifyDataSetChanged();
        btn_search.setEnabled(false);
    }//searchBtn
}//SearchActivity
