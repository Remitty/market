package com.brian.market.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.brian.market.utills.UrlController;

import org.json.JSONObject;

public class ProductDetails implements Parcelable {

    private JSONObject data;
    private String totalPrice;
    private int customersBasketQuantity, qty;
    private String isLiked;
    private String id;
    private String title;
    private String categoryName;
    private String image;
    private String type;
    private boolean isFeatured;
    private String path;
    private String unit_price;
    private String date;
    private String location;
    private boolean isFav;

    public ProductDetails(){}

    protected ProductDetails(Parcel in) {
        totalPrice = in.readString();
        customersBasketQuantity = in.readInt();
        qty = in.readInt();
        isLiked = in.readString();
        id = in.readString();
        title = in.readString();
        categoryName = in.readString();
        image = in.readString();
        type = in.readString();
        isFeatured = in.readByte() != 0;
        path = in.readString();
        unit_price = in.readString();
        date = in.readString();
        location = in.readString();
        isFav = in.readByte() != 0;
    }

    public static final Creator<ProductDetails> CREATOR = new Creator<ProductDetails>() {
        @Override
        public ProductDetails createFromParcel(Parcel in) {
            return new ProductDetails(in);
        }

        @Override
        public ProductDetails[] newArray(int size) {
            return new ProductDetails[size];
        }
    };

    public void setData(JSONObject data) {
        this.data = data;
        this.type = data.optString("type");
        this.path = data.optString("path");
        this.unit_price = data.optString("unit_price");
        this.date = data.optString("updated_at").substring(0,10);
        this.location = data.optString("location");
        this.title = data.optString("title");
        this.isFav = data.optBoolean("isFav");
        this.id = data.optString("id");
        this.qty = data.optInt("qty");
        this.image = data.optJSONArray("images").length() > 0 ?data.optJSONArray("images").optJSONObject(0).optString("thumb"): "";
    }

    public String getStatus(){
        return data.optString("status");
    }

    public String getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public int getQty(){return this.qty;};

    public String getDescription(){return data.optString("description");}

    public String getLat(){return data.optString("latitude");}
    public String getLong(){return data.optString("longitude");}

    public String getPrice() {
        return unit_price;
    }

    public String getShipPrice(){
        return data.optString("shipping_price");
    }
    public String getAddress(){
        return data.optString("address");
    }

    public boolean isShipping(){
        return data.optInt("isShipping") == 1 ? true: false;
    }

    public String getDayAgo() { return data.optString("day_ago");}

    public String getCurrency() {
        JSONObject currency = data.optJSONObject("currency");
        return  currency == null? "$" : currency.optString("currency");
    }

    public String getImageResourceId() {
             String image =  this.image;
             if(!image.startsWith("http"))
                 image = UrlController.IP_ADDRESS + "storage/" + image;
             return image;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public boolean getIsfav() {
        return isFav;
    }

    public String getCardName() {
        return title;
    }

    public String getId() {
        return id;
    }

    public boolean getFeaturetype() {
        return data != null ? data.optBoolean("isFeatured") : false;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public int getCustomersBasketQuantity() {
        return customersBasketQuantity;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setQty(int qty){this.qty = qty;}

    public void setCustomersBasketQuantity(int customersBasketQuantity) {
        this.customersBasketQuantity = customersBasketQuantity;
    }

    public String getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(String isLiked) {
        this.isLiked = isLiked;
    }

    public void setFeatured(boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public void setPrice(String price) {
        this.unit_price = price;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setCategoryNames(String categoryNames) {
        this.categoryName = categoryNames;
    }

    public void setCardName(String title) {
        this.title = title;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(totalPrice);
        dest.writeInt(customersBasketQuantity);
        dest.writeInt(qty);
        dest.writeString(isLiked);
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(categoryName);
        dest.writeString(image);
        dest.writeString(type);
        dest.writeByte((byte) (isFeatured ? 1 : 0));
        dest.writeString(path);
        dest.writeString(unit_price);
        dest.writeString(date);
        dest.writeString(location);
        dest.writeByte((byte) (isFav ? 1 : 0));
    }
}