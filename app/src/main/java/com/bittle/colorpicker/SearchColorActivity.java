package com.bittle.colorpicker;

//this is your activity with custom adapter and ListView

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bittle.colorpicker.realm.ColorModel;
import com.bittle.colorpicker.utils.ColorUtil;
import com.bittle.colorpicker.utils.ImageUtil;
import com.bittle.colorpicker.utils.Toaster;

import java.util.ArrayList;

public class SearchColorActivity extends Activity {
    Activity thisActivity;
    ImageUtil imageUtil;

    private EditText searchBar;
    private ListView lvColorNames;

    private ArrayList<ColorModel> mColorNameArrayList = new ArrayList<>();
    private MyAdapter adapter1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_color);

        thisActivity = this;
        imageUtil = new ImageUtil(this);

        try {
            initialize();
        }catch (Exception e){
            finish();
        }
        mColorNameArrayList = ColorUtil.getInstance().getColorList();

        //sortByColor();

        // Add Text Change Listener to EditText
        searchBar.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter
                if (adapter1 != null) {
                    adapter1.getFilter().filter(s.toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initialize() {
        searchBar = findViewById(R.id.etSearch);
        lvColorNames = findViewById(R.id.lvProducts);
        normalizeHeight();
    }

    private void normalizeHeight(){
        // for rounded edges
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        // set the max height
        WindowManager wm = (WindowManager) this.getSystemService(WINDOW_SERVICE);

        DisplayMetrics metrics = new DisplayMetrics();
        assert wm != null;
        wm.getDefaultDisplay().getMetrics(metrics);

        final int HEIGHT_WANTED = 1480;
        // height of device
        //Log.d("check", metrics.heightPixels + "");
        if (metrics.heightPixels < HEIGHT_WANTED) {
            this.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            this.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, HEIGHT_WANTED);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        adapter1 = new MyAdapter(SearchColorActivity.this, mColorNameArrayList);
        lvColorNames.setAdapter(adapter1);

        String str = searchBar.getText().toString();
        searchBar.setText("");
        searchBar.append(str);
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
            TextView colorName, colorPrice;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {

                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.row, null);
                holder.llContainer = convertView.findViewById(R.id.llContainer);
                holder.colorBox = convertView.findViewById(R.id.colorImageViewSearch);
                holder.colorName = convertView.findViewById(R.id.colorNameTextView);
                holder.colorPrice = convertView.findViewById(R.id.hexValueTextView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Bitmap bit = imageUtil.colorToBitmap(mDisplayedValues.get(position).getColor(), 100, 100);
            bit = imageUtil.cropToCircleWithBorder(bit, Color.BLACK, 1.0f);
            holder.colorBox.setImageBitmap(bit);
            holder.colorName.setText(mDisplayedValues.get(position).getName());
            String text= "#" + mDisplayedValues.get(position).getHex();
            holder.colorPrice.setText(text);

            holder.llContainer.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Intent result = new Intent();

                    result.putExtra("HEX", mDisplayedValues.get(position).getHex());
                    setResult(ColorPickerMainActivity.SEARCH_COMPLETE, result);

                    thisActivity.finish();
                }
            });

            holder.llContainer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toaster.toast("HELD", view.getContext());
                    return false;
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
                        Log.e("ERROR", "SearchColorActivity->publishResults");
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
                                FilteredArrList.add(mOriginalValues.get(i));
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
