package com.jian86_android.mysubway;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class MyAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<TimeInfo> mListdata = new ArrayList<>();

    public MyAdapter(Context mContext, ArrayList<TimeInfo> mListdata) {
        this.mContext = mContext;
        this.mListdata = mListdata;
    }

    @Override
    public int getCount() {
        return mListdata.size();
    }

    @Override
    public Object getItem(int position) {
        return mListdata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.time_items,null);
            TextView  m_upper= convertView.findViewById(R.id.upper_min);
         //   TextView  m_lower= convertView.findViewById(R.id.lower_min);
        TextView  time= convertView.findViewById(R.id.time);
        LinearLayout lli = convertView.findViewById(R.id.lli);
        m_upper.setText(mListdata.get(position).getUpper_time());
        time.setText(mListdata.get(position).getTime());

        lli.setBackgroundResource(R.color.item_utime);
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        String hourstr =String.format("%02d", hour);
        if(hourstr.equals( time.getText().toString()  )){
            lli.setBackgroundResource(R.color.item_ctime);

        }
        return convertView;
    }
}
