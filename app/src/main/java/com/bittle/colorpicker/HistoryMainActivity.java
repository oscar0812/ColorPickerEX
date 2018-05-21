package com.bittle.colorpicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bittle.colorpicker.realm.ColorModel;
import com.bittle.colorpicker.realm.DBRealm;
import com.bittle.colorpicker.utils.ImageUtil;
import com.bittle.colorpicker.utils.Toaster;

import java.util.ArrayList;

import io.realm.RealmResults;

public class HistoryMainActivity extends AppCompatActivity {

    Activity thisActivity;
    ImageUtil imageUtil;
    private ListView lvColorNames;

    private ArrayList<ColorModel> mColorNameArrayList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_main);

        thisActivity = this;
        imageUtil = new ImageUtil(this);

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
                Toaster.toast("History Cleared.", thisActivity.getApplicationContext());
                finish();
            }
        });

        // for rounded edges
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    private void setMaxHeight() {
        // set the max height
        int newHeight = 1480 - 120;
        // get number of db entries
        int num = mColorNameArrayList.size();

        if (num >= 13) {
            lvColorNames.getLayoutParams().height = newHeight;
        }
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        // close the DB to avoid leaks
        DBRealm.getInstance(this).close();
    }

    // Adapter Class
    public class MyAdapter extends BaseAdapter implements Filterable {

        private ArrayList<ColorModel> mOriginalValues; // Original Values
        private ArrayList<ColorModel> mDisplayedValues;    // Values to be displayed
        LayoutInflater inflater;

        private MyAdapter(Context context, ArrayList<ColorModel> mColorNameArrayList) {
            this.mOriginalValues = mColorNameArrayList;
            this.mDisplayedValues = mColorNameArrayList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mDisplayedValues.size();
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
            TextView tvName, tvPrice;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            HistoryMainActivity.MyAdapter.ViewHolder holder;

            if (convertView == null) {

                holder = new HistoryMainActivity.MyAdapter.ViewHolder();
                convertView = inflater.inflate(R.layout.row, null);
                holder.llContainer = convertView.findViewById(R.id.llContainer);
                holder.colorBox = convertView.findViewById(R.id.colorImageViewSearch);
                holder.tvName = convertView.findViewById(R.id.colorNameTextView);
                holder.tvPrice = convertView.findViewById(R.id.hexValueTextView);
                convertView.setTag(holder);

                setMaxHeight();
            } else {
                holder = (HistoryMainActivity.MyAdapter.ViewHolder) convertView.getTag();
            }
            Bitmap bit = imageUtil.colorToBitmap(mDisplayedValues.get(position).getColor(), 100, 100);
            bit = imageUtil.cropToCircleWithBorder(bit, Color.BLACK, 1.0f);
            //bit = imageUtil.cropToCircle(bit);
            holder.colorBox.setImageBitmap(bit);
            holder.tvName.setText(mDisplayedValues.get(position).getName());
            String text = "#" + mDisplayedValues.get(position).getHex();
            holder.tvPrice.setText(text);

            holder.llContainer.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Intent result = new Intent();

                    result.putExtra("HEX", mDisplayedValues.get(position).getHex());
                    setResult(ColorPickerMainActivity.SEARCH_COMPLETE, result);

                    thisActivity.finish();
                }
            });

            return convertView;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    mDisplayedValues = (ArrayList<ColorModel>) results.values; // has the filtered values
                    try {
                        notifyDataSetChanged();  // notifies the data with new filtered values
                    } catch (Exception e) {
                        Log.e("ERROR", "HistoryMainActivity->publishResults");
                    }
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                    ArrayList<ColorModel> FilteredArrList = new ArrayList<>();

                    if (mOriginalValues == null) {
                        mOriginalValues = new ArrayList<>(mDisplayedValues); // saves the original data in mOriginalValues
                    }

                    if (constraint == null || constraint.length() == 0) {

                        // set the Original result to return
                        results.count = mOriginalValues.size();
                        results.values = mOriginalValues;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < mOriginalValues.size(); i++) {
                            String data = mOriginalValues.get(i).getName();
                            if (data.toLowerCase().contains(constraint.toString())
                                    || mOriginalValues.get(i).getHex().toLowerCase().
                                    contains(constraint.toString())) {

                                FilteredArrList.add((mOriginalValues.get(i)));
                            }
                        }
                        // set the Filtered result to return
                        results.count = FilteredArrList.size();
                        results.values = FilteredArrList;

                    }
                    return results;
                }
            };
        }
    }
}
