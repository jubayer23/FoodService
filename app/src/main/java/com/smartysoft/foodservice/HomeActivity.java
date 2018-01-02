package com.smartysoft.foodservice;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.smartysoft.foodservice.Utility.GpsEnableTool;
import com.smartysoft.foodservice.Utility.LastLocationOnly;
import com.smartysoft.foodservice.appdata.GlobalAppAccess;
import com.smartysoft.foodservice.appdata.MydApplication;
import com.smartysoft.foodservice.firebase.service.MyFirebaseMessagingService;
import com.smartysoft.foodservice.fragment.HomeFragment;

public class HomeActivity extends BaseActivity {

    private static final String TAG_HOME_FRAGMENT = "home_fragment";
    public static final String TAG_HOME_ACTIVITY = "home_activity";

    private HomeFragment homeFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initToolbar();

        Log.d("DEBUG_fcm_id", MydApplication.getInstance().getPrefManger().getFcmRegId());

        String call_from = getIntent().getStringExtra("call_from");

        //if(call_from != null && call_from.equalsIgnoreCase("notification")){
        //    Log.d("DEBUG",getIntent().getStringExtra("message"));
    //  }


        if (savedInstanceState == null) {
            //getSupportFragmentManager()
            //        .beginTransaction()
            //        .add(R.id.content_layout, new HomeFragment(), TAG_HOME_FRAGMENT)
            //        .commit();


            homeFragment = new HomeFragment();
            Bundle arguments = new Bundle();
            if(call_from != null && call_from.equalsIgnoreCase(MyFirebaseMessagingService.TAG_NOTIFICATION)){
                arguments.putString(GlobalAppAccess.KEY_CALL_FROM, MyFirebaseMessagingService.TAG_NOTIFICATION);
                arguments.putString(GlobalAppAccess.KEY_NOTIFICATION_ID, getIntent().getStringExtra(GlobalAppAccess.KEY_NOTIFICATION_ID));
            }else{
                arguments.putString(GlobalAppAccess.KEY_CALL_FROM, TAG_HOME_ACTIVITY);
            }
            homeFragment.setArguments(arguments);
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.content_layout, homeFragment, TAG_HOME_FRAGMENT)
                    .commit();
        }




        LastLocationOnly lastLocationOnly = new LastLocationOnly(this);

        if (!lastLocationOnly.canGetLocation()) {
            GpsEnableTool gpsEnableTool = new GpsEnableTool(this);
            gpsEnableTool.enableGPs();
            return;
        }
    }
}
