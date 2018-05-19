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

import com.bittle.colorpicker.utils.ColorUtil;
import com.bittle.colorpicker.utils.ImageUtil;
import com.bittle.colorpicker.utils.Toaster;

import java.util.ArrayList;

public class SearchColorActivity extends Activity {
    Activity thisActivity;
    ImageUtil imageUtil;

    private EditText searchBar;
    private ListView lvColorNames;

    private ArrayList<ColorUtil.ColorName> mColorNameArrayList = new ArrayList<>();
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
        addToList();

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
        searchBar = (EditText) findViewById(R.id.etSearch);
        lvColorNames = (ListView) findViewById(R.id.lvProducts);
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

    private void addToList() {
        mColorNameArrayList.add(new ColorUtil.ColorName("AliceBlue", 0xF0, 0xF8, 0xFF));
        mColorNameArrayList.add(new ColorUtil.ColorName("AntiqueWhite", 0xFA, 0xEB, 0xD7));
        mColorNameArrayList.add(new ColorUtil.ColorName("Aqua", 0x00, 0xFF, 0xFF));
        mColorNameArrayList.add(new ColorUtil.ColorName("Aquamarine", 0x7F, 0xFF, 0xD4));
        mColorNameArrayList.add(new ColorUtil.ColorName("Azure", 0xF0, 0xFF, 0xFF));
        mColorNameArrayList.add(new ColorUtil.ColorName("Beige", 0xF5, 0xF5, 0xDC));
        mColorNameArrayList.add(new ColorUtil.ColorName("Bisque", 0xFF, 0xE4, 0xC4));
        mColorNameArrayList.add(new ColorUtil.ColorName("Black", 0x00, 0x00, 0x00));
        mColorNameArrayList.add(new ColorUtil.ColorName("BlanchedAlmond", 0xFF, 0xEB, 0xCD));
        mColorNameArrayList.add(new ColorUtil.ColorName("Blue", 0x00, 0x00, 0xFF));
        mColorNameArrayList.add(new ColorUtil.ColorName("BlueViolet", 0x8A, 0x2B, 0xE2));
        mColorNameArrayList.add(new ColorUtil.ColorName("Brown", 0xA5, 0x2A, 0x2A));
        mColorNameArrayList.add(new ColorUtil.ColorName("BurlyWood", 0xDE, 0xB8, 0x87));
        mColorNameArrayList.add(new ColorUtil.ColorName("CadetBlue", 0x5F, 0x9E, 0xA0));
        mColorNameArrayList.add(new ColorUtil.ColorName("Chartreuse", 0x7F, 0xFF, 0x00));
        mColorNameArrayList.add(new ColorUtil.ColorName("Chocolate", 0xD2, 0x69, 0x1E));
        mColorNameArrayList.add(new ColorUtil.ColorName("Coral", 0xFF, 0x7F, 0x50));
        mColorNameArrayList.add(new ColorUtil.ColorName("CornflowerBlue", 0x64, 0x95, 0xED));
        mColorNameArrayList.add(new ColorUtil.ColorName("Cornsilk", 0xFF, 0xF8, 0xDC));
        mColorNameArrayList.add(new ColorUtil.ColorName("Crimson", 0xDC, 0x14, 0x3C));
        mColorNameArrayList.add(new ColorUtil.ColorName("Cyan", 0x00, 0xFF, 0xFF));
        mColorNameArrayList.add(new ColorUtil.ColorName("DarkBlue", 0x00, 0x00, 0x8B));
        mColorNameArrayList.add(new ColorUtil.ColorName("DarkCyan", 0x00, 0x8B, 0x8B));
        mColorNameArrayList.add(new ColorUtil.ColorName("DarkGoldenRod", 0xB8, 0x86, 0x0B));
        mColorNameArrayList.add(new ColorUtil.ColorName("DarkGray", 0xA9, 0xA9, 0xA9));
        mColorNameArrayList.add(new ColorUtil.ColorName("DarkGreen", 0x00, 0x64, 0x00));
        mColorNameArrayList.add(new ColorUtil.ColorName("DarkKhaki", 0xBD, 0xB7, 0x6B));
        mColorNameArrayList.add(new ColorUtil.ColorName("DarkMagenta", 0x8B, 0x00, 0x8B));
        mColorNameArrayList.add(new ColorUtil.ColorName("DarkOliveGreen", 0x55, 0x6B, 0x2F));
        mColorNameArrayList.add(new ColorUtil.ColorName("DarkOrange", 0xFF, 0x8C, 0x00));
        mColorNameArrayList.add(new ColorUtil.ColorName("DarkOrchid", 0x99, 0x32, 0xCC));
        mColorNameArrayList.add(new ColorUtil.ColorName("DarkRed", 0x8B, 0x00, 0x00));
        mColorNameArrayList.add(new ColorUtil.ColorName("DarkSalmon", 0xE9, 0x96, 0x7A));
        mColorNameArrayList.add(new ColorUtil.ColorName("DarkSeaGreen", 0x8F, 0xBC, 0x8F));
        mColorNameArrayList.add(new ColorUtil.ColorName("DarkSlateBlue", 0x48, 0x3D, 0x8B));
        mColorNameArrayList.add(new ColorUtil.ColorName("DarkSlateGray", 0x2F, 0x4F, 0x4F));
        mColorNameArrayList.add(new ColorUtil.ColorName("DarkTurquoise", 0x00, 0xCE, 0xD1));
        mColorNameArrayList.add(new ColorUtil.ColorName("DarkViolet", 0x94, 0x00, 0xD3));
        mColorNameArrayList.add(new ColorUtil.ColorName("DeepPink", 0xFF, 0x14, 0x93));
        mColorNameArrayList.add(new ColorUtil.ColorName("DeepSkyBlue", 0x00, 0xBF, 0xFF));
        mColorNameArrayList.add(new ColorUtil.ColorName("DimGray", 0x69, 0x69, 0x69));
        mColorNameArrayList.add(new ColorUtil.ColorName("DodgerBlue", 0x1E, 0x90, 0xFF));
        mColorNameArrayList.add(new ColorUtil.ColorName("FireBrick", 0xB2, 0x22, 0x22));
        mColorNameArrayList.add(new ColorUtil.ColorName("FloralWhite", 0xFF, 0xFA, 0xF0));
        mColorNameArrayList.add(new ColorUtil.ColorName("ForestGreen", 0x22, 0x8B, 0x22));
        mColorNameArrayList.add(new ColorUtil.ColorName("Fuchsia", 0xFF, 0x00, 0x80));
        mColorNameArrayList.add(new ColorUtil.ColorName("Gainsboro", 0xDC, 0xDC, 0xDC));
        mColorNameArrayList.add(new ColorUtil.ColorName("GhostWhite", 0xF8, 0xF8, 0xFF));
        mColorNameArrayList.add(new ColorUtil.ColorName("Gold", 0xFF, 0xD7, 0x00));
        mColorNameArrayList.add(new ColorUtil.ColorName("GoldenRod", 0xDA, 0xA5, 0x20));
        mColorNameArrayList.add(new ColorUtil.ColorName("Gray", 0x80, 0x80, 0x80));
        mColorNameArrayList.add(new ColorUtil.ColorName("Green", 0x00, 0x80, 0x00));
        mColorNameArrayList.add(new ColorUtil.ColorName("GreenYellow", 0xAD, 0xFF, 0x2F));
        mColorNameArrayList.add(new ColorUtil.ColorName("HoneyDew", 0xF0, 0xFF, 0xF0));
        mColorNameArrayList.add(new ColorUtil.ColorName("HotPink", 0xFF, 0x69, 0xB4));
        mColorNameArrayList.add(new ColorUtil.ColorName("IndianRed", 0xCD, 0x5C, 0x5C));
        mColorNameArrayList.add(new ColorUtil.ColorName("Indigo", 0x4B, 0x00, 0x82));
        mColorNameArrayList.add(new ColorUtil.ColorName("Ivory", 0xFF, 0xFF, 0xF0));
        mColorNameArrayList.add(new ColorUtil.ColorName("Khaki", 0xF0, 0xE6, 0x8C));
        mColorNameArrayList.add(new ColorUtil.ColorName("Lavender", 0xE6, 0xE6, 0xFA));
        mColorNameArrayList.add(new ColorUtil.ColorName("LavenderBlush", 0xFF, 0xF0, 0xF5));
        mColorNameArrayList.add(new ColorUtil.ColorName("LawnGreen", 0x7C, 0xFC, 0x00));
        mColorNameArrayList.add(new ColorUtil.ColorName("LemonChiffon", 0xFF, 0xFA, 0xCD));
        mColorNameArrayList.add(new ColorUtil.ColorName("LightBlue", 0xAD, 0xD8, 0xE6));
        mColorNameArrayList.add(new ColorUtil.ColorName("LightCoral", 0xF0, 0x80, 0x80));
        mColorNameArrayList.add(new ColorUtil.ColorName("LightCyan", 0xE0, 0xFF, 0xFF));
        mColorNameArrayList.add(new ColorUtil.ColorName("LightGoldenRodYellow", 0xFA, 0xFA, 0xD2));
        mColorNameArrayList.add(new ColorUtil.ColorName("LightGray", 0xD3, 0xD3, 0xD3));
        mColorNameArrayList.add(new ColorUtil.ColorName("LightGreen", 0x90, 0xEE, 0x90));
        mColorNameArrayList.add(new ColorUtil.ColorName("LightPink", 0xFF, 0xB6, 0xC1));
        mColorNameArrayList.add(new ColorUtil.ColorName("LightSalmon", 0xFF, 0xA0, 0x7A));
        mColorNameArrayList.add(new ColorUtil.ColorName("LightSeaGreen", 0x20, 0xB2, 0xAA));
        mColorNameArrayList.add(new ColorUtil.ColorName("LightSkyBlue", 0x87, 0xCE, 0xFA));
        mColorNameArrayList.add(new ColorUtil.ColorName("LightSlateGray", 0x77, 0x88, 0x99));
        mColorNameArrayList.add(new ColorUtil.ColorName("LightSteelBlue", 0xB0, 0xC4, 0xDE));
        mColorNameArrayList.add(new ColorUtil.ColorName("LightYellow", 0xFF, 0xFF, 0xE0));
        mColorNameArrayList.add(new ColorUtil.ColorName("Lime", 0x00, 0xFF, 0x00));
        mColorNameArrayList.add(new ColorUtil.ColorName("LimeGreen", 0x32, 0xCD, 0x32));
        mColorNameArrayList.add(new ColorUtil.ColorName("Linen", 0xFA, 0xF0, 0xE6));
        mColorNameArrayList.add(new ColorUtil.ColorName("Magenta", 0xFF, 0x00, 0xFF));
        mColorNameArrayList.add(new ColorUtil.ColorName("Maroon", 0x80, 0x00, 0x00));
        mColorNameArrayList.add(new ColorUtil.ColorName("MediumAquaMarine", 0x66, 0xCD, 0xAA));
        mColorNameArrayList.add(new ColorUtil.ColorName("MediumBlue", 0x00, 0x00, 0xCD));
        mColorNameArrayList.add(new ColorUtil.ColorName("MediumOrchid", 0xBA, 0x55, 0xD3));
        mColorNameArrayList.add(new ColorUtil.ColorName("MediumPurple", 0x93, 0x70, 0xDB));
        mColorNameArrayList.add(new ColorUtil.ColorName("MediumSeaGreen", 0x3C, 0xB3, 0x71));
        mColorNameArrayList.add(new ColorUtil.ColorName("MediumSlateBlue", 0x7B, 0x68, 0xEE));
        mColorNameArrayList.add(new ColorUtil.ColorName("MediumSpringGreen", 0x00, 0xFA, 0x9A));
        mColorNameArrayList.add(new ColorUtil.ColorName("MediumTurquoise", 0x48, 0xD1, 0xCC));
        mColorNameArrayList.add(new ColorUtil.ColorName("MediumVioletRed", 0xC7, 0x15, 0x85));
        mColorNameArrayList.add(new ColorUtil.ColorName("MidnightBlue", 0x19, 0x19, 0x70));
        mColorNameArrayList.add(new ColorUtil.ColorName("MintCream", 0xF5, 0xFF, 0xFA));
        mColorNameArrayList.add(new ColorUtil.ColorName("MistyRose", 0xFF, 0xE4, 0xE1));
        mColorNameArrayList.add(new ColorUtil.ColorName("Moccasin", 0xFF, 0xE4, 0xB5));
        mColorNameArrayList.add(new ColorUtil.ColorName("NavajoWhite", 0xFF, 0xDE, 0xAD));
        mColorNameArrayList.add(new ColorUtil.ColorName("Navy", 0x00, 0x00, 0x80));
        mColorNameArrayList.add(new ColorUtil.ColorName("OldLace", 0xFD, 0xF5, 0xE6));
        mColorNameArrayList.add(new ColorUtil.ColorName("Olive", 0x80, 0x80, 0x00));
        mColorNameArrayList.add(new ColorUtil.ColorName("OliveDrab", 0x6B, 0x8E, 0x23));
        mColorNameArrayList.add(new ColorUtil.ColorName("Orange", 0xFF, 0xA5, 0x00));
        mColorNameArrayList.add(new ColorUtil.ColorName("OrangeRed", 0xFF, 0x45, 0x00));
        mColorNameArrayList.add(new ColorUtil.ColorName("Orchid", 0xDA, 0x70, 0xD6));
        mColorNameArrayList.add(new ColorUtil.ColorName("PaleGoldenRod", 0xEE, 0xE8, 0xAA));
        mColorNameArrayList.add(new ColorUtil.ColorName("PaleGreen", 0x98, 0xFB, 0x98));
        mColorNameArrayList.add(new ColorUtil.ColorName("PaleTurquoise", 0xAF, 0xEE, 0xEE));
        mColorNameArrayList.add(new ColorUtil.ColorName("PaleVioletRed", 0xDB, 0x70, 0x93));
        mColorNameArrayList.add(new ColorUtil.ColorName("PapayaWhip", 0xFF, 0xEF, 0xD5));
        mColorNameArrayList.add(new ColorUtil.ColorName("PeachPuff", 0xFF, 0xDA, 0xB9));
        mColorNameArrayList.add(new ColorUtil.ColorName("Peru", 0xCD, 0x85, 0x3F));
        mColorNameArrayList.add(new ColorUtil.ColorName("Pink", 0xFF, 0xC0, 0xCB));
        mColorNameArrayList.add(new ColorUtil.ColorName("Plum", 0xDD, 0xA0, 0xDD));
        mColorNameArrayList.add(new ColorUtil.ColorName("PowderBlue", 0xB0, 0xE0, 0xE6));
        mColorNameArrayList.add(new ColorUtil.ColorName("Purple", 0x80, 0x00, 0x80));
        mColorNameArrayList.add(new ColorUtil.ColorName("Red", 0xFF, 0x00, 0x00));
        mColorNameArrayList.add(new ColorUtil.ColorName("RosyBrown", 0xBC, 0x8F, 0x8F));
        mColorNameArrayList.add(new ColorUtil.ColorName("RoyalBlue", 0x41, 0x69, 0xE1));
        mColorNameArrayList.add(new ColorUtil.ColorName("SaddleBrown", 0x8B, 0x45, 0x13));
        mColorNameArrayList.add(new ColorUtil.ColorName("Salmon", 0xFA, 0x80, 0x72));
        mColorNameArrayList.add(new ColorUtil.ColorName("SandyBrown", 0xF4, 0xA4, 0x60));
        mColorNameArrayList.add(new ColorUtil.ColorName("SeaGreen", 0x2E, 0x8B, 0x57));
        mColorNameArrayList.add(new ColorUtil.ColorName("SeaShell", 0xFF, 0xF5, 0xEE));
        mColorNameArrayList.add(new ColorUtil.ColorName("Sienna", 0xA0, 0x52, 0x2D));
        mColorNameArrayList.add(new ColorUtil.ColorName("Silver", 0xC0, 0xC0, 0xC0));
        mColorNameArrayList.add(new ColorUtil.ColorName("SkyBlue", 0x87, 0xCE, 0xEB));
        mColorNameArrayList.add(new ColorUtil.ColorName("SlateBlue", 0x6A, 0x5A, 0xCD));
        mColorNameArrayList.add(new ColorUtil.ColorName("SlateGray", 0x70, 0x80, 0x90));
        mColorNameArrayList.add(new ColorUtil.ColorName("Snow", 0xFF, 0xFA, 0xFA));
        mColorNameArrayList.add(new ColorUtil.ColorName("SpringGreen", 0x00, 0xFF, 0x7F));
        mColorNameArrayList.add(new ColorUtil.ColorName("SteelBlue", 0x46, 0x82, 0xB4));
        mColorNameArrayList.add(new ColorUtil.ColorName("Tan", 0xD2, 0xB4, 0x8C));
        mColorNameArrayList.add(new ColorUtil.ColorName("Teal", 0x00, 0x80, 0x80));
        mColorNameArrayList.add(new ColorUtil.ColorName("Thistle", 0xD8, 0xBF, 0xD8));
        mColorNameArrayList.add(new ColorUtil.ColorName("Tomato", 0xFF, 0x63, 0x47));
        mColorNameArrayList.add(new ColorUtil.ColorName("Turquoise", 0x40, 0xE0, 0xD0));
        mColorNameArrayList.add(new ColorUtil.ColorName("Violet", 0xEE, 0x82, 0xEE));
        mColorNameArrayList.add(new ColorUtil.ColorName("Wheat", 0xF5, 0xDE, 0xB3));
        mColorNameArrayList.add(new ColorUtil.ColorName("White", 0xFF, 0xFF, 0xFF));
        mColorNameArrayList.add(new ColorUtil.ColorName("WhiteSmoke", 0xF5, 0xF5, 0xF5));
        mColorNameArrayList.add(new ColorUtil.ColorName("Yellow", 0xFF, 0xFF, 0x00));
        mColorNameArrayList.add(new ColorUtil.ColorName("YellowGreen", 0x9A, 0xCD, 0x32));
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();


        adapter1 = new MyAdapter(SearchColorActivity.this, mColorNameArrayList);
        lvColorNames.setAdapter(adapter1);

        String str = searchBar.getText().toString();
        searchBar.setText("");
        searchBar.append(str);
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

            ViewHolder holder;

            if (convertView == null) {

                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.row, null);
                holder.llContainer = (LinearLayout) convertView.findViewById(R.id.llContainer);
                holder.colorBox = (ImageView) convertView.findViewById(R.id.colorImageViewSearch);
                holder.tvName = (TextView) convertView.findViewById(R.id.colorNameTextView);
                holder.tvPrice = (TextView) convertView.findViewById(R.id.hexValueTextView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Bitmap bit = imageUtil.colorToBitmap(mDisplayedValues.get(position).getColor(), 100, 100);
            bit = imageUtil.cropToCircleWithBorder(bit, Color.BLACK, 1.0f);
            holder.colorBox.setImageBitmap(bit);
            holder.tvName.setText(mDisplayedValues.get(position).name);
            String text= "#" + mDisplayedValues.get(position).hex;
            holder.tvPrice.setText(text);

            holder.llContainer.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Intent result = new Intent();

                    result.putExtra("HEX", mDisplayedValues.get(position).hex);
                    setResult(ColorPickerMainActivity.SEARCH_COMPLETE, result);

                    //PrefUtil.write(mDisplayedValues.get(position).hex, thisActivity);

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

                    mDisplayedValues = (ArrayList<ColorUtil.ColorName>) results.values; // has the filtered values
                    try {
                        notifyDataSetChanged();  // notifies the data with new filtered values
                    } catch (Exception e) {
                        Log.e("ERROR", "SearchColorActivity->publishResults");
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
