package com.smartysoft.foodservice.firebase.model;

/**
 * Created by comsol on 01-Jan-18.
 */

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationData implements Parcelable
{

    @SerializedName("data")
    @Expose
    private Data data;
    public final static Parcelable.Creator<NotificationData> CREATOR = new Creator<NotificationData>() {


        @SuppressWarnings({
                "unchecked"
        })
        public NotificationData createFromParcel(Parcel in) {
            return new NotificationData(in);
        }

        public NotificationData[] newArray(int size) {
            return (new NotificationData[size]);
        }

    }
            ;

    protected NotificationData(Parcel in) {
        this.data = ((Data) in.readValue((Data.class.getClassLoader())));
    }

    public NotificationData() {
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(data);
    }

    public int describeContents() {
        return 0;
    }

}