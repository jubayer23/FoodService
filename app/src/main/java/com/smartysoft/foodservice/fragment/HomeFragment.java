package com.smartysoft.foodservice.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.smartysoft.foodservice.R;

/**
 * Created by comsol on 30-Dec-17.
 */

public class HomeFragment extends Fragment implements View.OnClickListener {



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container,
                false);

        how_many_time_user_press_start = 0;

        init(view);

        if (!AppController.getInstance().getPrefManger().getPetrolId().isEmpty() ||
                (!AppController.getInstance().getPrefManger().getUserStartLat().equals("0")
                        && !AppController.getInstance().getPrefManger().getUserStartLang().equals("0"))) {


            //RESTART SERVICE
            getActivity().stopService(new Intent(getActivity(), GpsServiceUpdate.class));
            getActivity().startService(new Intent(getActivity(), GpsServiceUpdate.class));

            btn_stop_patrolling.setVisibility(View.VISIBLE);
            btn_start_petroling.setVisibility(View.GONE);
            btn_new_pillar_entry.setVisibility(View.VISIBLE);
        }

        return view;

    }

    public void onActivityCreated(Bundle SavedInstanceState) {
        super.onActivityCreated(SavedInstanceState);

        if (SavedInstanceState == null) {


            String version = DeviceInfoUtils.getAppVersionName();

            if (!version.equalsIgnoreCase(AppController.getInstance().getPrefManger().getAppVersion())) {

                AlertDialogForAnything.showAlertDialogForceUpdateFromDropBox(getActivity(),
                        "App Update", "Press Download To Download The Updated App", "DOWNLOAD",
                        AppConstant.APP_UPDATE_URL);

            }

        } else {

        }
    }

    private void init(View view) {


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(false);
        progressDialog.setMessage("Please Wait...");

        cd = new ConnectionDetector(getActivity());

        btn_start_petroling = (Button) view.findViewById(R.id.btn_startpetroling);
        btn_start_petroling.setOnClickListener(this);
        btn_new_pillar_entry = (Button) view.findViewById(R.id.btn_new_pillar_entry);
        btn_new_pillar_entry.setVisibility(View.GONE);
        btn_new_pillar_entry.setOnClickListener(this);
        btn_stop_patrolling = (Button) view.findViewById(R.id.btn_stoppatrolling);
        btn_stop_patrolling.setOnClickListener(this);
        btn_special_ops = (Button) view.findViewById(R.id.btn_special_ops);
        btn_special_ops.setOnClickListener(this);

        btn_upload_pending_pillar = (Button) view.findViewById(R.id.btn_upload_pending_pillar);
        btn_upload_pending_pillar.setVisibility(View.GONE);
        btn_upload_pending_pillar.setOnClickListener(this);

        btn_map = (FloatingActionButton) view.findViewById(R.id.map);
        btn_map.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {

    }
}
