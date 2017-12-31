package com.smartysoft.foodservice.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.smartysoft.foodservice.alertbanner.AlertDialogForAnything;
import com.smartysoft.foodservice.appdata.GlobalAppAccess;
import com.smartysoft.foodservice.appdata.MydApplication;
import com.smartysoft.foodservice.service.GpsServiceUpdate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by comsol on 30-Dec-17.
 */

public class HomeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG_REQUEST_HOME_PAGE = "tag_volley_request_in_home_page";

    private Button btn_start_petroling, btn_stop_patrolling;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container,
                false);


        init(view);

        if (!MydApplication.getInstance().getPrefManger().getPathId().isEmpty()) {


            //RESTART SERVICE
            getActivity().stopService(new Intent(getActivity(), GpsServiceUpdate.class));
            getActivity().startService(new Intent(getActivity(), GpsServiceUpdate.class));

            btn_stop_patrolling.setVisibility(View.VISIBLE);
            btn_start_petroling.setVisibility(View.GONE);
        }

        return view;

    }

    public void onActivityCreated(Bundle SavedInstanceState) {
        super.onActivityCreated(SavedInstanceState);

    }

    private void init(View view) {

        btn_start_petroling = (Button) view.findViewById(R.id.btn_startpetroling);
        btn_start_petroling.setOnClickListener(this);
        btn_stop_patrolling = (Button) view.findViewById(R.id.btn_stoppatrolling);
        btn_stop_patrolling.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {

        int id = view.getId();

        LastLocationOnly lastLocationOnly = new LastLocationOnly(getActivity());

        if (!lastLocationOnly.canGetLocation()) {
            GpsEnableTool gpsEnableTool = new GpsEnableTool(getActivity());
            gpsEnableTool.enableGPs();
            return;
        }

        if (id == R.id.btn_startpetroling) {

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
                            loc_lat, loc_lng);
                }
            };
            UserLastKnownLocation myLocation = new UserLastKnownLocation();
            myLocation.getLocation(getActivity(), locationResult);
        }


        if (id == R.id.btn_stoppatrolling) {
            if (!DeviceInfoUtils.isConnectingToInternet(getActivity())) {
                AlertDialogForAnything.showAlertDialogWhenComplte(getActivity(), "Error", "Please connect to working internet connection!", false);
                return;
            }

            if(MydApplication.getInstance().getPrefManger().getPathId().isEmpty()){
                stopPatrollingSuccessUpdateUi();
                return;
            }

            showStopPatrolingAlertDialog();
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

    private void hitUrlForStartGps(String url, final String id, final double lat, final double lng) {
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

                                MydApplication.getInstance().getPrefManger().setPathId(jsonObject.getString("pathId"));

                                startPatrollingSuccessUpdateUi();

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

                                stopPatrollingSuccessUpdateUi();
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

    private void startPatrollingSuccessUpdateUi() {

        //RESTART SERVICE
        getActivity().stopService(new Intent(getActivity(), GpsServiceUpdate.class));
        getActivity().startService(new Intent(getActivity(), GpsServiceUpdate.class));

        btn_stop_patrolling.setVisibility(View.VISIBLE);
        btn_start_petroling.setVisibility(View.GONE);
        // Log.d("DEBUG",String.valueOf(error));
    }

    private void stopPatrollingSuccessUpdateUi() {
        getActivity().stopService(new Intent(getActivity(), GpsServiceUpdate.class));

        btn_start_petroling.setVisibility(View.VISIBLE);
        btn_stop_patrolling.setVisibility(View.GONE);

        MydApplication.getInstance().getPrefManger().setPathId("");
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
