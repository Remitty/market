package com.brian.market.modelsList;

import com.brian.market.utills.UrlController;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Auction {
    JSONObject data;
    private String image;
    JSONArray images = new JSONArray();

    public void setData(JSONObject data) {
        this.data = data;
        this.images = data.optJSONArray("images");
    }

    public String getId() {
        return data.optString("id");
    }

    public String getTitle() {
        return data.optString("title");
    }

    public String getImageResourceId(int index) {
        String image =  images.length() > 0 ?images.optJSONObject(index).optString("thumb"): "";
        if(!image.startsWith("http"))
            image = UrlController.IP_ADDRESS + "storage/" + image;
        return image;
    }

    public JSONArray getImages(){return this.images;}

    public String getStartPrice() {
        return data.optString("start_price");
    }

    public String getHighPrice() {
        return data.optString("high_price");
    }

    public boolean isShipping() {
        return data.optInt("isShipping") == 1 ? true: false;
    }

    public String getShippingPrice() {return data.optString("shipping_price");}

    public String getCurrency() {
        JSONObject currency = data.optJSONObject("currency");
        return  currency == null? "$" : currency.optString("currency");
    }

    public String getCategory() {
        JSONObject category = data.optJSONObject("category");
        return  category == null? "No category" : category.optString("cat_name");
    }

    public int getCategoryId() {
        JSONObject category = data.optJSONObject("category");
        return  category == null? 0 : category.optInt("id");
    }

    public String getWinnerID() {
        return data.optString("winner_id");
    }

    public String getStatus() {
        return data.optString("status");
    }

    public String getDuration() {
        return data.optString("day_left");
    }

    public String getDescription() {
        return data.optString("description");
    }

    public String getLocation() {
        return data.optString("location");
    }

    public String getDayAgo() {
        return data.optString("day_ago");
    }

    public String getBidders() {return data.optString("bidder_count");}

    public String getWinner() {
        JSONObject winner = data.optJSONObject("winner");
        if(winner == null)
            return "";
        return winner.optString("first_name") + " " + winner.optString("last_name");
    }
}
