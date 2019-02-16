package com.mc0239.recyclertableviewexample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.mc0239.recyclertableviewexample.database.AppDatabase;
import com.mc0239.recyclertableviewexample.samples.FragmentSampleCheckbox;
import com.mc0239.recyclertableviewexample.samples.FragmentSampleEdittext;
import com.mc0239.recyclertableviewexample.samples.FragmentSampleLivedata;
import com.mc0239.recyclertableviewexample.samples.FragmentSampleRadiobutton;
import com.mc0239.recyclertableviewexample.samples.FragmentSampleUsage;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    private Fragment
            fragmentSampleUsage,
            fragmentSampleCheckbox,
            fragmentSampleRadiobutton,
            fragmentSampleEdittext,
            fragmentSampleLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentSampleUsage = new FragmentSampleUsage();
        fragmentSampleCheckbox = new FragmentSampleCheckbox();
        fragmentSampleRadiobutton = new FragmentSampleRadiobutton();
        fragmentSampleEdittext = new FragmentSampleEdittext();
        fragmentSampleLiveData = new FragmentSampleLivedata();

        AppDatabase.getDatabase(this).generateSampleData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);
        drawerLayout.closeDrawers();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        int mid = menuItem.getItemId();

        if (mid == R.id.nav_sample) {
            transaction.replace(R.id.fragment_frame, fragmentSampleUsage);
        } else if (mid == R.id.nav_sample_checkbox) {
            transaction.replace(R.id.fragment_frame, fragmentSampleCheckbox);
        } else if (mid == R.id.nav_sample_radio) {
            transaction.replace(R.id.fragment_frame, fragmentSampleRadiobutton);
        } else if (mid == R.id.nav_sample_edittext) {
            transaction.replace(R.id.fragment_frame, fragmentSampleEdittext);
        } else if (mid == R.id.nav_sample_livedata) {
            transaction.replace(R.id.fragment_frame, fragmentSampleLiveData);
        } else {
            Log.w(getClass().getSimpleName(), "Navigation menu selection not handled.");
        }

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.disallowAddToBackStack();
        transaction.commit();

        return true;
    }
}
