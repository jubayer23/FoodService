package com.smartysoft.foodservice.firebase.model;

/**
 * Created by comsol on 01-Jan-18.
 */

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data implements Parcelable
{

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("deadline")
    @Expose
    private String deadline;
    @SerializedName("products")
    @Expose
    private String products;
    @SerializedName("pathId")
    @Expose
    private String pathId;
    @SerializedName("grandTotal")
    @Expose
    private String grandTotal;

    @SerializedName("image")
    @Expose
    private String imageUrl;


    protected Data(Parcel in) {
        title = in.readString();
        message = in.readString();
        name = in.readString();
        mobile = in.readString();
        address = in.readString();
        deadline = in.readString();
        products = in.readString();
        pathId = in.readString();
        grandTotal = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public String getPathId() {
        return pathId;
    }

    public void setPathId(String pathId) {
        this.pathId = pathId;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(message);
        parcel.writeString(name);
        parcel.writeString(mobile);
        parcel.writeString(address);
        parcel.writeString(deadline);
        parcel.writeString(products);
        parcel.writeString(pathId);
        parcel.writeString(grandTotal);
        parcel.writeString(imageUrl);
    }
}