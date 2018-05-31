package com.bittle.colorpicker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bittle.colorpicker.realm.ColorModel;
import com.bittle.colorpicker.realm.DBRealm;
import com.bittle.colorpicker.utils.ScreenUtil;

import java.util.ArrayList;

import io.realm.RealmResults;

public class HistoryMainActivity extends AppCompatActivity {

    Activity thisActivity;
    private ListView lvColorNames;

    private ArrayList<ColorModel> mColorNameArrayList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_main);

        thisActivity = this;

        initialize();
        addToList();
        // Add Text Change Listener to EditText
    }

    private void initialize() {
        lvColorNames = findViewById(R.id.historyList);
        TextView clear = findViewById(R.id.clearTextViewHistory);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  clear color db
                DBRealm.getInstance(thisActivity.getApplicationContext()).clearAll();
                mColorNameArrayList.clear();
                Toast.makeText(thisActivity.getApplicationContext(), "History Cleared", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        // for rounded edges
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    // populate the list from db
    private void addToList() {
        RealmResults<ColorModel> results =
                DBRealm.getInstance(thisActivity.getApplicationContext()).findAll();
        mColorNameArrayList.addAll(results);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DBRealm.getInstance(this).start();

        MyAdapter adapter1 = new MyAdapter(HistoryMainActivity.this, mColorNameArrayList);
        lvColorNames.setAdapter(adapter1);
        setMaxHeight();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // close the DB to avoid leaks
        DBRealm.getInstance(this).close();
    }

    // Adapter Class
    public class MyAdapter extends BaseAdapter {

        private ArrayList<ColorModel> mOriginalValues; // Original Values
        LayoutInflater inflater;

        private MyAdapter(Context context, ArrayList<ColorModel> mColorNameArrayList) {
            this.mOriginalValues = mColorNameArrayList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mOriginalValues.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            LinearLayout llContainer;
            ImageView colorBox;
            TextView name, hex;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            HistoryMainActivity.MyAdapter.ViewHolder holder;

            if (convertView == null) {

                holder = new HistoryMainActivity.MyAdapter.ViewHolder();
                convertView = inflater.inflate(R.layout.row, null);
                holder.llContainer = convertView.findViewById(R.id.llContainer);
                holder.colorBox = convertView.findViewById(R.id.colorImageViewSearch);
                holder.name = convertView.findViewById(R.id.colorNameTextView);
                holder.hex = convertView.findViewById(R.id.hexValueTextView);
                convertView.setTag(holder);


            } else {
                holder = (HistoryMainActivity.MyAdapter.ViewHolder) convertView.getTag();
            }
            // set color
            ((GradientDrawable)holder.colorBox.getBackground()).
                    setColor(mOriginalValues.get(position).getColor());

            holder.name.setText(mOriginalValues.get(position).getName());
            String text = "#" + mOriginalValues.get(position).getHex();
            holder.hex.setText(text);

            holder.llContainer.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Intent result = new Intent();

                    result.putExtra("HEX", mOriginalValues.get(position).getHex());
                    setResult(ColorPickerMainActivity.SEARCH_COMPLETE, result);

                    thisActivity.finish();
                }
            });

            return convertView;
        }
    }

    // the list view should only take a max of 2/3rd of the screen
    private void setMaxHeight() {
        // set the max height
        int screen = ScreenUtil.getScreenHeight();

        // new height will take 3/4th of screen
        int newHeight = screen - (screen/4);
        // get number of db entries

        View view = lvColorNames.getAdapter().getView(0, null, lvColorNames);
        view.measure(0,0);
        int height = mColorNameArrayList.size() * view.getMeasuredHeight();

        if (height >= newHeight) {
            lvColorNames.getLayoutParams().height = newHeight;
        }
    }
}
