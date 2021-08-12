package com.brian.market.doba.models;

import org.json.JSONException;
import org.json.JSONObject;

public class DobaCategory {
    private JSONObject data;

    public DobaCategory(JSONObject object) {
        data = object;
    }

    public String getCatId() {
        try {
            return data.getString("catId");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getCatName() {
        try {
            return data.getString("catName");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public Integer getLevel() {
        try {
            return data.getInt("level");
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
