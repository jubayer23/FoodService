package com.smartysoft.foodservice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.smartysoft.foodservice.Utility.CommonMethods;
import com.smartysoft.foodservice.Utility.DeviceInfoUtils;
import com.smartysoft.foodservice.Utility.RunnTimePermissions;
import com.smartysoft.foodservice.alertbanner.AlertDialogForAnything;
import com.smartysoft.foodservice.appdata.GlobalAppAccess;
import com.smartysoft.foodservice.appdata.MydApplication;
import com.smartysoft.foodservice.model.User;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends BaseActivity implements View.OnClickListener {


    private Gson gson;
    // UI references.
    private Button btn_submit;
    private EditText ed_email, ed_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * If User is already logged-in then redirect user to the home page
         * */
        if (MydApplication.getInstance().getPrefManger().getUserProfile() != null) {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
            return;
        }



        setContentView(R.layout.activity_main);

        init();

        initializeCacheValue();
    }


    @Override
    protected void onStart() {
        super.onStart();
        /**
         * This is marshmallow runtime Permissions
         * It will ask user for grand permission in queue order[FIFO]
         * If user gave all permission then check whether user device has google play service or not!
         * NB : before adding runtime request for permission Must add manifest permission for that
         * specific request
         * */
        if (RunnTimePermissions.requestForAllRuntimePermissions(this)) {
            if (!DeviceInfoUtils.isGooglePlayServicesAvailable(MainActivity.this)) {
                AlertDialogForAnything.showAlertDialogWhenComplte(this, "Warning", "This app need google play service to work properly. Please install it!!", false);
            }
        }
    }


    private void init() {
        gson = new Gson();
        ed_email = (EditText) findViewById(R.id.ed_email);
        ed_password = (EditText) findViewById(R.id.ed_password);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
    }

    private void initializeCacheValue() {
        ed_email.setText(MydApplication.getInstance().getPrefManger().getEmailCache());
    }

    private void saveCache(String email) {
        MydApplication.getInstance().getPrefManger().setEmailCache(email);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (!DeviceInfoUtils.isConnectingToInternet(MainActivity.this)) {
            AlertDialogForAnything.showAlertDialogWhenComplte(MainActivity.this, "Error", "Please connect to a working internet connection!", false);
            return;
        }

        if (!DeviceInfoUtils.isGooglePlayServicesAvailable(MainActivity.this)) {
            AlertDialogForAnything.showAlertDialogWhenComplte(this, "Warning", "This app need google play service to work properly. Please install it!!", false);
            return;
        }

        if (!RunnTimePermissions.requestForAllRuntimePermissions(MainActivity.this)) {
            //AlertDialogForAnything.showAlertDialogWhenComplte(this, "Warning", "This app need google play service to work properly. Please install it!!", false);
            return;
        }

        if (id == R.id.btn_submit) {
            String imie = DeviceInfoUtils.getDeviceImieNumber(this);
            Log.d("DEBUG", imie);
            if (isValidCredentialsProvided() && imie != null && !imie.isEmpty()) {

                CommonMethods.hideKeyboardForcely(this, ed_email);
                CommonMethods.hideKeyboardForcely(this, ed_password);

                String fcmId = "123";

                saveCache(ed_email.getText().toString());

                sendRequestForLogin(GlobalAppAccess.URL_LOGIN, ed_email.getText().toString(), ed_password.getText().toString(), imie, fcmId);
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private boolean isValidCredentialsProvided() {

        // Store values at the time of the login attempt.
        String email = ed_email.getText().toString();
        String password = ed_password.getText().toString();

        // Reset errors.
        ed_email.setError(null);
        ed_password.setError(null);
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(email)) {
            ed_email.setError("Required");
            ed_email.requestFocus();
            return false;
        }
        /*if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ed_email.setError("Invalid");
            ed_email.requestFocus();
            return false;
        }*/
        if (TextUtils.isEmpty(password)) {
            ed_password.setError("Required");
            ed_password.requestFocus();
            return false;
        }

        return true;
    }

    public void sendRequestForLogin(String url, final String email, final String password, final String authImie, final String fcmId) {

        url = url + "?" + "email=" + email + "&password=" + password;
        // TODO Auto-generated method stub
        showProgressDialog("Loading..", true, false);

        final StringRequest req = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("DEBUG",response);

                        dismissProgressDialog();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.getString("result");

                            if (result.equals("1")) {
                                String id = jsonObject.getString("id");

                                User user = new User(id, email, fcmId, authImie);

                                MydApplication.getInstance().getPrefManger().setUserProfile(user);
                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                finish();

                            } else {
                                AlertDialogForAnything.showAlertDialogWhenComplte(MainActivity.this, "Error", "Wrong login information!", false);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dismissProgressDialog();

                AlertDialogForAnything.showAlertDialogWhenComplte(MainActivity.this, "Error", "Network problem. please try again!", false);

            }
        })// {

                // @Override
                // protected Map<String, String> getParams() throws AuthFailureError {
                //    Map<String, String> params = new HashMap<String, String>();
                //    params.put("email", email);
                //     params.put("password", password);
                ///     return params;
                //  }
                //}
                ;

        req.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // TODO Auto-generated method stub
        MydApplication.getInstance().addToRequestQueue(req);
    }
}
