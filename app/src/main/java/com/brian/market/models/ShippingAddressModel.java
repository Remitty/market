package com.brian.market.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class ShippingAddressModel implements Parcelable {
    private String name;
    private String country;
    private String state;
    private String city;
    private String address1;
    private String address2;
    private String phone;
    private String postalCode;

    public ShippingAddressModel(JSONObject data) {
        name = data.optString("contact_name");
        country = data.optString("country");
        state = data.optString("state");
        city = data.optString("city");
        address1 = data.optString("address1");
        address2 = data.optString("address2");
        phone = data.optString("mobile");
        postalCode = data.optString("postal_code");
    }

    protected ShippingAddressModel(Parcel in) {
        name = in.readString();
        country = in.readString();
        state = in.readString();
        city = in.readString();
        address1 = in.readString();
        address2 = in.readString();
        phone = in.readString();
        postalCode = in.readString();
    }

    public String getName() {return this.name;}

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress1() { return address1; }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public JSONObject getDobaObject() {
        JSONObject object = new JSONObject();
        try {
            object.put("addr1", address1);
            object.put("addr2", address2);
            object.put("city", city);
            object.put("countryCode", country);
            object.put("name", name);
            object.put("provinceCode", state);
            object.put("telephone", phone);
            object.put("zip", postalCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public static final Creator<ShippingAddressModel> CREATOR = new Creator<ShippingAddressModel>() {
        @Override
        public ShippingAddressModel createFromParcel(Parcel in) {
            return new ShippingAddressModel(in);
        }

        @Override
        public ShippingAddressModel[] newArray(int size) {
            return new ShippingAddressModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(country);
        dest.writeString(state);
        dest.writeString(city);
        dest.writeString(address1);
        dest.writeString(address2);
        dest.writeString(phone);
        dest.writeString(postalCode);
    }
}
