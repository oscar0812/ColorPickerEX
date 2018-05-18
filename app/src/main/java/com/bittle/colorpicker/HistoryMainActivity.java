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

import com.bittle.colorpicker.utils.ColorUtil;
import com.bittle.colorpicker.utils.ImageUtil;
import com.bittle.colorpicker.utils.PrefUtil;
import com.bittle.colorpicker.utils.Toaster;

import java.util.ArrayList;

public class HistoryMainActivity extends AppCompatActivity {

    Activity thisActivity;
    ImageUtil imageUtil;
    private ListView lvColorNames;
    ColorUtil colorUtil = new ColorUtil();

    private ArrayList<ColorUtil.ColorName> mColorNameArrayList = new ArrayList<ColorUtil.ColorName>();
    private HistoryMainActivity.MyAdapter adapter1;


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
        lvColorNames = (ListView) findViewById(R.id.historyList);
        TextView clear = (TextView) findViewById(R.id.clearTextViewHistory);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrefUtil.clearAll(thisActivity);
                mColorNameArrayList.clear();
                Toaster.toast("History Cleared.");
                finish();
            }
        });

        // for rounded edges
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    private void setMaxHeight() {
        // set the max height
        int newHeight = 1480 - 120;
        int num = PrefUtil.getNumberOfEntries(this);

        if (num >= 13) {
            lvColorNames.getLayoutParams().height = newHeight;
        }
    }

    private ArrayList<ColorUtil.ColorName> reverseList(ArrayList<ColorUtil.ColorName> c) {
        ArrayList<ColorUtil.ColorName> newList = new ArrayList<>();
        for (int x = c.size() - 1; x >= 0; x--) {
            //Log.d("Printing: ", c.get(x).hex);
            newList.add(c.get(x));
        }
        newList = cutList(0, PrefUtil.MIN_NUM_OF_ENTRIES, newList);
        return newList;
    }

    private ArrayList<ColorUtil.ColorName> cutList(int first,
                                                   int last, ArrayList<ColorUtil.ColorName> c) {
        if (c.size() < last) {
            last = c.size();
        }
        ArrayList<ColorUtil.ColorName> list = new ArrayList<>();

        for (int y = first; y < last; y++) {
            list.add(c.get(y));
        }
        return list;
    }

    private void addToList() {
        int x = 0;
        while (true) {
            String hex = PrefUtil.get(x++, this);
            if (!hex.equals("")) {
                int color = colorUtil.hexToColor(hex);
                String name = colorUtil.getClosestColor(color);
                mColorNameArrayList.add(new ColorUtil.ColorName(name, color));
            } else
                break;
        }
        mColorNameArrayList = reverseList(mColorNameArrayList);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();


        adapter1 = new HistoryMainActivity.MyAdapter(HistoryMainActivity.this, mColorNameArrayList);
        lvColorNames.setAdapter(adapter1);
    }


    // Adapter Class
    public class MyAdapter extends BaseAdapter implements Filterable {

        private ArrayList<ColorUtil.ColorName> mOriginalValues; // Original Values
        private ArrayList<ColorUtil.ColorName> mDisplayedValues;    // Values to be displayed
        LayoutInflater inflater;

        private MyAdapter(Context context, ArrayList<ColorUtil.ColorName> mColorNameArrayList) {
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
                holder.llContainer = (LinearLayout) convertView.findViewById(R.id.llContainer);
                holder.colorBox = (ImageView) convertView.findViewById(R.id.colorImageViewSearch);
                holder.tvName = (TextView) convertView.findViewById(R.id.colorNameTextView);
                holder.tvPrice = (TextView) convertView.findViewById(R.id.hexValueTextView);
                convertView.setTag(holder);

                setMaxHeight();
            } else {
                holder = (HistoryMainActivity.MyAdapter.ViewHolder) convertView.getTag();
            }
            Bitmap bit = imageUtil.colorToBitmap(mDisplayedValues.get(position).getColor(), 100, 100);
            bit = imageUtil.cropToCircleWithBorder(bit, Color.BLACK, 1.0f);
            //bit = imageUtil.cropToCircle(bit);
            holder.colorBox.setImageBitmap(bit);
            holder.tvName.setText(mDisplayedValues.get(position).name);
            String text = "#" + mDisplayedValues.get(position).hex;
            holder.tvPrice.setText(text);

            holder.llContainer.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Intent result = new Intent();

                    result.putExtra("HEX", mDisplayedValues.get(position).hex);
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

                    mDisplayedValues = (ArrayList<ColorUtil.ColorName>) results.values; // has the filtered values
                    try {
                        notifyDataSetChanged();  // notifies the data with new filtered values
                    } catch (Exception e) {
                        Log.e("ERROR", "HistoryMainActivity->publishResults");
                    }
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                    ArrayList<ColorUtil.ColorName> FilteredArrList = new ArrayList<>();

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
                            String data = mOriginalValues.get(i).name;
                            if (data.toLowerCase().contains(constraint.toString())
                                    || mOriginalValues.get(i).hex.toLowerCase().
                                    contains(constraint.toString())) {

                                FilteredArrList.add(new ColorUtil.ColorName(mOriginalValues.get(i)));
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
