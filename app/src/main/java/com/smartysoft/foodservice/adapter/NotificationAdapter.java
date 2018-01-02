package com.smartysoft.foodservice.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.smartysoft.foodservice.R;
import com.smartysoft.foodservice.appdata.MydApplication;
import com.smartysoft.foodservice.firebase.model.NotificationData;

import java.util.List;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    public static final String KEY_EVENT = "key_event";
    private List<NotificationData> Displayedplaces;
    private List<NotificationData> Originalplaces;
    private LayoutInflater inflater;
    @SuppressWarnings("unused")
    private Activity activity;
    private String call_from;

    private  PopupWindow popupwindow_obj;

    private int lastPosition = -1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_sub_title;
        TextView tv_deadline;
        TextView tv_status;


        public MyViewHolder(View view) {
            super(view);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_sub_title = (TextView) view.findViewById(R.id.tv_sub_title);
            tv_deadline = (TextView) view.findViewById(R.id.tv_deadline);
            tv_status = (TextView) view.findViewById(R.id.tv_status);
        }
    }


    public NotificationAdapter(Activity activity, List<NotificationData> attendees) {
        this.activity = activity;
        this.Displayedplaces = attendees;
        this.Originalplaces = attendees;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_notification_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final NotificationData event = Displayedplaces.get(position);
        holder.tv_title.setText(event.getData().getTitle());
        holder.tv_sub_title.setText(event.getData().getMessage());
        holder.tv_deadline.setText(event.getData().getDeadline());

        NotificationData running_notification_data = MydApplication.getInstance().getPrefManger().getCurrentlyRunningDelivery();

        if(running_notification_data !=null && running_notification_data.getData().getPathId().equals(event.getData().getPathId())){
            holder.tv_status.setText("Delivey on going!");
            holder.tv_status.setTextColor(activity.getResources().getColor(R.color.green));
        }else{
            holder.tv_status.setText("Pending!");
            holder.tv_status.setTextColor(activity.getResources().getColor(R.color.orange));
        }

        if (position > lastPosition) {
            lastPosition = position;
            animate(holder);
        }

    }

    public void animate(NotificationAdapter.MyViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(activity, R.anim.slide_from_bottom_in);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }


    @Override
    public int getItemCount() {
        return Displayedplaces.size();
    }

    private ProgressDialog progressDialog;
    public void showProgressDialog(String message, boolean isIntermidiate, boolean isCancelable) {
       /**/
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
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