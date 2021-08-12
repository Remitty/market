package com.brian.market.models;

import org.json.JSONObject;

public class OrderProductDetail {
    private JSONObject data;

    public void setData(JSONObject data){
        this.data = data;
    }

    public String getOrderProductQty(){
        return data.optString("count");
    }

    public ProductDetails getOrderProduct(){
        ProductDetails item = new ProductDetails();
        item.setData(data.optJSONObject("product"));
        return item;
    }
}
