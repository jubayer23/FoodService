package com.smartysoft.foodservice.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.smartysoft.foodservice.R;
import com.smartysoft.foodservice.Utility.CommonMethods;
import com.smartysoft.foodservice.Utility.DeviceInfoUtils;
import com.smartysoft.foodservice.Utility.GpsEnableTool;
import com.smartysoft.foodservice.Utility.LastLocationOnly;
import com.smartysoft.foodservice.Utility.UserLastKnownLocation;
import com.smartysoft.foodservice.adapter.NotificationAdapter;
import com.smartysoft.foodservice.alertbanner.AlertDialogForAnything;
import com.smartysoft.foodservice.appdata.GlobalAppAccess;
import com.smartysoft.foodservice.appdata.MydApplication;
import com.smartysoft.foodservice.customView.RecyclerItemClickListener;
import com.smartysoft.foodservice.firebase.model.NotificationData;
import com.smartysoft.foodservice.firebase.service.MyFirebaseMessagingService;
import com.smartysoft.foodservice.firebase.utils.NotificationUtils;
import com.smartysoft.foodservice.service.GpsServiceUpdate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by comsol on 30-Dec-17.
 */

public class HomeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG_REQUEST_HOME_PAGE = "tag_volley_request_in_home_page";

    private Button  btn_stop_patrolling;

    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<NotificationData> notificationDatas = new ArrayList<>();


    private ImageView img_more;
    private TextView tv_title, tv_name,tv_deadline, tv_no_delivery_alert;

    private LinearLayout ll_container_delivery_progress;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container,
                false);


        init(view);

        initAdapter();

        updateUiIfThereIsNoDelivery();

        if (MydApplication.getInstance().getPrefManger().getCurrentlyRunningDelivery() != null) {

            startPatrollingSuccessUpdateUi(MydApplication.getInstance().getPrefManger().getCurrentlyRunningDelivery());
        }

        return view;

    }

    public void onActivityCreated(Bundle SavedInstanceState) {
        super.onActivityCreated(SavedInstanceState);

        String  call_from = getArguments().getString(GlobalAppAccess.KEY_CALL_FROM);

        if(call_from!=null && !call_from.isEmpty() && call_from.equals(MyFirebaseMessagingService.TAG_NOTIFICATION)){
            String notification_id = getArguments().getString(GlobalAppAccess.KEY_NOTIFICATION_ID);

            List<NotificationData> notificationDatas = MydApplication.getInstance().getPrefManger().getNotificationDatas();
            for(NotificationData notificationData : notificationDatas){
                if(notificationData.getData().getPathId().equals(notification_id)){
                    showNotificationDialog(notificationData, false);
                    break;
                }
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GlobalAppAccess.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GlobalAppAccess.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getActivity());
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void init(View view) {


        btn_stop_patrolling = (Button) view.findViewById(R.id.btn_stoppatrolling);
        btn_stop_patrolling.setOnClickListener(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        img_more = (ImageView) view.findViewById(R.id.img_more);

        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_deadline = (TextView) view.findViewById(R.id.tv_deadline);


        ll_container_delivery_progress = (LinearLayout) view.findViewById(R.id.ll_container_delivery_progress);
        ll_container_delivery_progress.setVisibility(View.GONE);

        tv_no_delivery_alert = (TextView) view.findViewById(R.id.tv_no_delivery_alert);
        tv_no_delivery_alert.setVisibility(View.GONE);

    }

    private void initAdapter() {
        notificationDatas.addAll(MydApplication.getInstance().getPrefManger().getNotificationDatas());

        notificationAdapter = new NotificationAdapter(getActivity(), notificationDatas);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(notificationAdapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // TODO Handle item click

                        showNotificationDialog(notificationDatas.get(position),false);
                    }
                })
        );
    }

    private void updateUiIfThereIsNoDelivery(){
        if(notificationDatas.isEmpty()){
            tv_no_delivery_alert.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else{
            tv_no_delivery_alert.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();



       /* if (id == R.id.btn_startpetroling) {

                if(MydApplication.getInstance().getPrefManger().getNotificationData() != null){
                    showNotificationDialog(MydApplication.getInstance().getPrefManger().getNotificationData());
                }else{
                    Toast.makeText(getActivity(),"No delivery product is assigned for you!",Toast.LENGTH_LONG).show();
                }

        }*/


        if (id == R.id.btn_stoppatrolling) {
            stopDelivery();
        }

    }

    private void showStopPatrolingAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        alertDialog.setTitle("Alert!!");

        alertDialog.setMessage("Are you sure to stop?");

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                showProgressDialog("please wait..", true, false);
                UserLastKnownLocation.LocationResult locationResult = new UserLastKnownLocation.LocationResult() {
                    @Override
                    public void gotLocation(Location location) {
                        final double stop_lat = CommonMethods.roundFloatToFiveDigitAfterDecimal(location.getLatitude());
                        final double stop_lang = CommonMethods.roundFloatToFiveDigitAfterDecimal(location.getLongitude());
                        //Got the location!
                        //dismissProgressDialog();

                        hitUrlForStopGps(
                                GlobalAppAccess.URL_DELIVERY_BOY_LOCATION,
                                MydApplication.getInstance().getPrefManger().getUserProfile().getId(),
                                String.valueOf(stop_lat),
                                String.valueOf(stop_lang),
                                MydApplication.getInstance().getPrefManger().getPathId(),
                                MydApplication.getInstance().getPrefManger().getUserProfile().getAuthImie());
                    }
                };
                UserLastKnownLocation myLocation = new UserLastKnownLocation();
                myLocation.getLocation(getActivity(), locationResult);


            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        alertDialog.show();
    }

    private void hitUrlForStartGps(String url, final String id, final NotificationData notificationData, final double lat, final double lng) {
        // TODO Auto-generated method stub

        //showProgressDialog("Start Delivery....", true, false);

        final StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        dismissProgressDialog();


                        response = response.replaceAll("\\s+", "");

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.getString("result");
                            if (result.equals("1")) {

                                if(dialog_start != null && dialog_start.isShowing()){
                                    dialog_start.dismiss();
                                    dialog_start = null;
                                }


                                MydApplication.getInstance().getPrefManger().setCurrentlyRunningDelivery(notificationData);

                                MydApplication.getInstance().getPrefManger().setPathId(jsonObject.getString("pathId"));

                                startPatrollingSuccessUpdateUi(notificationData);

                                notificationAdapter.notifyDataSetChanged();

                            } else {
                                AlertDialogForAnything.showAlertDialogWhenComplte(getActivity(),
                                        "Error", response, false);
                                // AlertDialogForAnything.showAlertDialogWhenComplte(getActivity(),
                                //         "Error", "Something went wrong!", false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            AlertDialogForAnything.showAlertDialogWhenComplte(getActivity(),
                                    "Error", "Server Down!! Please contact with server person!!!", false);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dismissProgressDialog();
                AlertDialogForAnything.showAlertDialogWhenComplte(getActivity(), "Error", "Network error!", false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //userId=XXX&routeId=XXX&selected=XXX
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("latitude", String.valueOf(lat));
                params.put("longitude", String.valueOf(lng));
                params.put("startPath", "true");
                params.put("pathId", notificationData.getData().getPathId());
                params.put("authImie", MydApplication.getInstance().getPrefManger().getUserProfile().getAuthImie());
                return params;
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // TODO Auto-generated method stub
        MydApplication.getInstance().addToRequestQueue(req, TAG_REQUEST_HOME_PAGE);
    }

    private void hitUrlForStopGps(String url, final String id, final String lat, final String lng, final String pathId, final String authImie) {
        // TODO Auto-generated method stub

       // showProgressDialog("Stop Patrolling....", true, false);

        final StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        dismissProgressDialog();
                        response = response.replaceAll("\\s+", "");
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.getString("result");
                            if (result.equals("1")) {

                                stopPatrollingSuccessUpdateUi(MydApplication.getInstance().getPrefManger().getCurrentlyRunningDelivery());
                            } else {
                                AlertDialogForAnything.showAlertDialogWhenComplte(getActivity(), "Alert", "There is something wrong when stop patrolling", false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dismissProgressDialog();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //userId=XXX&routeId=XXX&selected=XXX
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("latitude", lat);
                params.put("longitude", lng);
                params.put("pathId", pathId);
                params.put("authImie", authImie);
                params.put("endPath", "true");
                return params;
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // TODO Auto-generated method stub
        MydApplication.getInstance().addToRequestQueue(req,TAG_REQUEST_HOME_PAGE);
    }

    private void startPatrollingSuccessUpdateUi(final NotificationData notificationData) {

        //RESTART SERVICE
        getActivity().stopService(new Intent(getActivity(), GpsServiceUpdate.class));
        getActivity().startService(new Intent(getActivity(), GpsServiceUpdate.class));


        ll_container_delivery_progress.setVisibility(View.VISIBLE);
        tv_title.setText(notificationData.getData().getTitle());
        tv_name.setText(notificationData.getData().getName());
        tv_deadline.setText(notificationData.getData().getDeadline());


        img_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNotificationDialog(notificationData, true);
            }
        });

        // Log.d("DEBUG",String.valueOf(error));
    }

    private void stopPatrollingSuccessUpdateUi(NotificationData notificationData) {
        getActivity().stopService(new Intent(getActivity(), GpsServiceUpdate.class));

        ll_container_delivery_progress.setVisibility(View.GONE);

        if(notificationData != null){
            int count = 0;
            for(NotificationData notificationData1: notificationDatas){
                if(notificationData.getData().getPathId().equals(notificationData1.getData().getPathId())){
                    notificationDatas.remove(count);
                    MydApplication.getInstance().getPrefManger().setNotificationDatas(notificationDatas);
                    notificationAdapter.notifyDataSetChanged();
                    break;
                }
                count++;
            }
        }

        MydApplication.getInstance().getPrefManger().setCurrentlyRunningDelivery("");
        MydApplication.getInstance().getPrefManger().setPathId("");


        if(dialog_start != null && dialog_start.isShowing()){
            dialog_start.dismiss();
        }

        updateUiIfThereIsNoDelivery();
    }

    BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // checking for type intent filter
            if (intent.getAction().equals(GlobalAppAccess.REGISTRATION_COMPLETE)) {
                // gcm successfully registered
                // now subscribe to `global` topic to receive app wide notifications
                //FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

               // displayFirebaseRegId();

                Log.d("DEBUG_fcm_id",MydApplication.getInstance().getPrefManger().getFcmRegId());

            } else if (intent.getAction().equals(GlobalAppAccess.PUSH_NOTIFICATION)) {
                // new push notification is received

               // String title = intent.getStringExtra("title");
               // String message = intent.getStringExtra("message");
               // String address = intent.getStringExtra("address");
               // String mobile = intent.getStringExtra("mobile");


               // Toast.makeText(getActivity(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                List<NotificationData> tempNotificationDataList = MydApplication.getInstance().getPrefManger().getNotificationDatas();
                NotificationData latestNotification = tempNotificationDataList.get(tempNotificationDataList.size() - 1);

                showNotificationDialog(latestNotification,false);
                notificationDatas.add(latestNotification);
                notificationAdapter.notifyDataSetChanged();
                updateUiIfThereIsNoDelivery();

                //txtMessage.setText(message);
            }
        }
    };


    private Dialog dialog_start;
    private void showNotificationDialog(final NotificationData notificationData, boolean isDeliveryAlreadyRunning) {
        if(dialog_start != null && dialog_start.isShowing()){
            dialog_start.dismiss();
        }
       dialog_start = new Dialog(getActivity(),
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog_start.setCancelable(true);
        dialog_start.setContentView(R.layout.dialog_notification);

        TextView tv_title = (TextView) dialog_start.findViewById(R.id.tv_title);
        TextView tv_message = (TextView) dialog_start.findViewById(R.id.tv_message);
        TextView tv_products = (TextView) dialog_start.findViewById(R.id.tv_products);
        TextView tv_grand_total = (TextView) dialog_start.findViewById(R.id.tv_grand_total);
        TextView tv_deadline = (TextView) dialog_start.findViewById(R.id.tv_deadline);
        TextView tv_name = (TextView) dialog_start.findViewById(R.id.tv_name);
        TextView tv_mobile = (TextView) dialog_start.findViewById(R.id.tv_mobile);
        TextView tv_address = (TextView) dialog_start.findViewById(R.id.tv_address);
        TextView tv_status = (TextView) dialog_start.findViewById(R.id.tv_status);

        Button btn_later = (Button) dialog_start.findViewById(R.id.btn_later) ;
        Button btn_start = (Button) dialog_start.findViewById(R.id.btn_start);
        Button btn_stop = (Button) dialog_start.findViewById(R.id.btn_stop);

        final ImageView img_close = (ImageView) dialog_start.findViewById(R.id.img_close_dialog);


        if(isDeliveryAlreadyRunning){
            btn_stop.setVisibility(View.VISIBLE);
            btn_later.setVisibility(View.GONE);
            btn_start.setVisibility(View.GONE);
            tv_status.setText("On progress");
            tv_status.setTextColor(getActivity().getResources().getColor(R.color.green));
        }else{
            btn_stop.setVisibility(View.GONE);
            btn_later.setVisibility(View.VISIBLE);
            btn_start.setVisibility(View.VISIBLE);
            tv_status.setText("Pending");
            tv_status.setTextColor(getActivity().getResources().getColor(R.color.orange));
        }


        tv_message.setText(notificationData.getData().getMessage());
        tv_title.setText(notificationData.getData().getTitle());
        tv_products.setText(notificationData.getData().getProducts());
        tv_grand_total.setText(notificationData.getData().getGrandTotal());
        tv_deadline.setText(notificationData.getData().getDeadline());
        tv_name.setText(notificationData.getData().getName());
        tv_mobile.setText(notificationData.getData().getMobile());
        tv_address.setText(notificationData.getData().getAddress());




        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDelivery(notificationData);
            }
        });

        btn_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_start.dismiss();
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopDelivery();
            }
        });

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_start.dismiss();
            }
        });


        dialog_start.show();

    }

    private void startDelivery(final NotificationData notificationData){
        if(MydApplication.getInstance().getPrefManger().getCurrentlyRunningDelivery() != null){
            Toast.makeText(getActivity(),"You cannot start another delivery when you have a delivery already running on.",Toast.LENGTH_LONG).show();
            return;
        }


        LastLocationOnly lastLocationOnly = new LastLocationOnly(getActivity());

        if (!lastLocationOnly.canGetLocation()) {
            GpsEnableTool gpsEnableTool = new GpsEnableTool(getActivity());
            gpsEnableTool.enableGPs();
            return;
        }

        if (!DeviceInfoUtils.isConnectingToInternet(getActivity())) {
            AlertDialogForAnything.showAlertDialogWhenComplte(getActivity(), "Error", "Please connect to working internet connection!", false);
            return;
        }

        showProgressDialog("please wait..", true, false);
        UserLastKnownLocation.LocationResult locationResult = new UserLastKnownLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                final double loc_lat = CommonMethods.roundFloatToFiveDigitAfterDecimal(location.getLatitude());
                final double loc_lng = CommonMethods.roundFloatToFiveDigitAfterDecimal(location.getLongitude());
                //Got the location!
                //dismissProgressDialog();
                hitUrlForStartGps(GlobalAppAccess.URL_DELIVERY_BOY_LOCATION,
                        MydApplication.getInstance().getPrefManger().getUserProfile().getId(),
                        notificationData,
                        loc_lat, loc_lng);
            }
        };
        UserLastKnownLocation myLocation = new UserLastKnownLocation();
        myLocation.getLocation(getActivity(), locationResult);
    }

    private void stopDelivery(){
        LastLocationOnly lastLocationOnly = new LastLocationOnly(getActivity());

        if (!lastLocationOnly.canGetLocation()) {
            GpsEnableTool gpsEnableTool = new GpsEnableTool(getActivity());
            gpsEnableTool.enableGPs();
            return;
        }

        if (!DeviceInfoUtils.isConnectingToInternet(getActivity())) {
            AlertDialogForAnything.showAlertDialogWhenComplte(getActivity(), "Error", "Please connect to working internet connection!", false);
            return;
        }

        if(MydApplication.getInstance().getPrefManger().getCurrentlyRunningDelivery() == null){
            stopPatrollingSuccessUpdateUi(null);
            return;
        }

        showStopPatrolingAlertDialog();
    }


    private ProgressDialog progressDialog;

    public void showProgressDialog(String message, boolean isIntermidiate, boolean isCancelable) {
       /**/
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
        }
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog.setIndeterminate(isIntermidiate);
        progressDialog.setCancelable(isCancelable);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog == null) {
            return;
        }
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
