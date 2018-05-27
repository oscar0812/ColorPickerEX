package com.bittle.colorpicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

@SuppressLint("Registered")
public class BaseDrawerActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private DrawerLayout fullLayout;
    private Toolbar toolbar;
    private int layoutResID;

    @SuppressLint("InflateParams")
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        this.layoutResID = layoutResID;
        fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_navigation, null);
        FrameLayout activityContainer = fullLayout.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);

        super.setContentView(fullLayout);

        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);

        if (useToolbar()) {
            setSupportActionBar(toolbar);
        } else {
            toolbar.setVisibility(View.GONE);
        }

        setUpNavView();
    }

    protected boolean useToolbar() {
        return true;
    }

    protected void setUpNavView() {
        navigationView.setNavigationItemSelectedListener(this);

        if (useDrawerToggle()) { // use the hamburger menu
            ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, fullLayout, toolbar,
                    R.string.nav_drawer_opened,
                    R.string.nav_drawer_closed);

            fullLayout.addDrawerListener(drawerToggle);
            drawerToggle.syncState();
        } else if (useToolbar() && getSupportActionBar() != null) {
            // Use home/back button instead
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(getResources()
                    .getDrawable(R.drawable.home));
        }
    }

    protected boolean useDrawerToggle() {
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        fullLayout.closeDrawer(GravityCompat.START);
        return onOptionsItemSelected(menuItem);
    }

    private boolean clickedSameActivity(int item_id) {
        return (layoutResID == R.layout.activity_color_picker_main && item_id == R.id.nav_home)
                || (layoutResID == R.layout.activity_image_picker_main && item_id == R.id.nav_camera);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if(clickedSameActivity(id)){
            // if clicked same activity don't do anything
            return true;
        }

        switch (id) {
            case R.id.nav_home:
                startActivity(new Intent(this, ColorPickerMainActivity.class));
                return true;
            case R.id.nav_camera:
                startActivity(new Intent(this, ImagePickerMainActivity.class));
                return true;
            case R.id.nav_share:
                shareApp();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            try {
                InputMethodManager input = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                assert input != null;
                input.hideSoftInputFromWindow(view.getWindowToken(), 0);
            } catch (java.lang.NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public void shareApp() {
        String text = "Check out \"Color Picker EX\" -\nhttps://play.google.com/store" +
                "/apps/details?id=com.bittle.colorpicker";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(
                sendIntent, getResources().getText(R.string.send_to)));

    }
}
