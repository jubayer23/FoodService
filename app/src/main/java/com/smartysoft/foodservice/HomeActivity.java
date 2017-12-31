package com.smartysoft.foodservice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.smartysoft.foodservice.Utility.GpsEnableTool;
import com.smartysoft.foodservice.Utility.LastLocationOnly;
import com.smartysoft.foodservice.fragment.HomeFragment;

public class HomeActivity extends BaseActivity {

    private static final String TAG_HOME_FRAGMENT = "home_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initToolbar();


        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_layout, new HomeFragment(), TAG_HOME_FRAGMENT)
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LastLocationOnly lastLocationOnly = new LastLocationOnly(this);

        if (!lastLocationOnly.canGetLocation()) {
            GpsEnableTool gpsEnableTool = new GpsEnableTool(this);
            gpsEnableTool.enableGPs();
            return;
        }

    }
}
