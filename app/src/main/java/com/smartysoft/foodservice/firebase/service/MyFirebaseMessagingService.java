package com.smartysoft.foodservice.firebase.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.smartysoft.foodservice.HomeActivity;
import com.smartysoft.foodservice.Utility.CommonMethods;
import com.smartysoft.foodservice.appdata.GlobalAppAccess;
import com.smartysoft.foodservice.appdata.MydApplication;
import com.smartysoft.foodservice.firebase.model.NotificationData;
import com.smartysoft.foodservice.firebase.utils.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by jubayer on 1/1/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "DEBUG";
    public static final String TAG_NOTIFICATION = "notification";

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());


            try {
                //JSONObject json = new JSONObject(remoteMessage.getData().toString());
                //handleDataMessage(json);
                NotificationData notificationData = MydApplication.gson.fromJson(remoteMessage.getData().toString(), NotificationData.class);
                handleDataMessage(notificationData);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(GlobalAppAccess.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(NotificationData notificationData) {
       //Log.e(TAG, "push json: " + json.toString());

        MydApplication.getInstance().getPrefManger().setNotificationData(notificationData);

        List<NotificationData> notificationDatas = MydApplication.getInstance().getPrefManger().getNotificationDatas();
        notificationDatas.add(notificationData);
        MydApplication.getInstance().getPrefManger().setNotificationDatas(notificationDatas);


        String title = notificationData.getData().getTitle();
        String message = notificationData.getData().getMessage();
        String timestamp = CommonMethods.currentDate("yyyy-MM-dd HH:mm:ss");
        String imageUrl = notificationData.getData().getImageUrl();
        try {

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(GlobalAppAccess.PUSH_NOTIFICATION);
                pushNotification.putExtra(GlobalAppAccess.KEY_NOTIFICATION_ID,notificationData.getData().getPathId());
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
                resultIntent.putExtra(GlobalAppAccess.KEY_CALL_FROM, TAG_NOTIFICATION);
                resultIntent.putExtra(GlobalAppAccess.KEY_NOTIFICATION_ID,notificationData.getData().getPathId());

                // check for image attachment
                if (imageUrl != null && TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}