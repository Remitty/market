package com.brian.market.modelsList;

import org.json.JSONException;
import org.json.JSONObject;

public class CreditCard {

    private JSONObject data;

    public void setData(JSONObject data) {
        this.data = data;
    }

    public JSONObject getData() {
        return data;
    }

    public String getId() {
        try {
            return data.getString("id");
        } catch (JSONException e) {
            return "0";
        }
    }

    public String getCardId() {
        try {
            return data.getString("card_id");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getBrand() {
        try {
            return data.getString("brand");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getLastFour() {
        try {
            return data.getString("last_four");
        } catch (JSONException e) {
            return "";
        }
    }
}
