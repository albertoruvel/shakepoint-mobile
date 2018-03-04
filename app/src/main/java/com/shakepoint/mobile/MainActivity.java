package com.shakepoint.mobile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.shakepoint.mobile.adapter.TabbedSectionAdapter;
import com.shakepoint.mobile.fragment.ProfileFragment;
import com.shakepoint.mobile.util.SharedUtils;

public class MainActivity extends AppCompatActivity {

    private TabbedSectionAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    private int currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new TabbedSectionAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //currentTab = tab.getPosition();
                invalidateOptionsMenu();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        int selectedTab = tabLayout.getSelectedTabPosition();
        switch (selectedTab) {
            case 0:
                //products
                getMenuInflater().inflate(R.menu.menu_main, menu);
                break;
            case 1:
                //purchases
                break;
            case 2:
                //active codes
                break;
            case 3:
                //profile
                getMenuInflater().inflate(R.menu.menu_profile, menu);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.saveProfile:
                ProfileFragment profileFragment = (ProfileFragment)mSectionsPagerAdapter.getItem(3);
                profileFragment.saveProfile();
                return true;
            case R.id.signout:
                signout();
                return true;
            /**case R.id.contact:
                Intent intent = new Intent(this, ContactActivity.class);
                startActivity(intent);
                break;**/
            case R.id.paymentInfo:
                startActivity(new Intent(this, CardActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void signout() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.exit_question))
                .setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedUtils.clear(MainActivity.this);
                        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null).create().show();
    }

    @Override
    public void onBackPressed(){
        finishAffinity();
    }

    @Override
    public void onResume(){
        super.onResume();
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
