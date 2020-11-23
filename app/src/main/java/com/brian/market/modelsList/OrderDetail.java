package com.brian.market.modelsList;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderDetail {
    private JSONObject data;

    public void setData(JSONObject data) {
        this.data = data;
    }

    public JSONObject getData(){return this.data;}

    public String getOrderId(){
        return data.optString("id");
    }

    public int getProductCounts(){
        return data.optInt("no_products");
    }

    public int getProductKinds(){
        return data.optInt("kind");
    }

    public double getPrice(){
        return (data.optDouble("taxes") + data.optDouble("subtotal_price")+data.optDouble("shipping_price"));
    }

    public double getSubtotalPrice(){
        return data.optDouble("subtotal_price");
    }

    public String getStatus(){
        return data.optString("status");
    }

    public int getShipping(){
        return data.optInt("shipping");
    }

    public String getDate(){
        return data.optString("updated_at");
    }

    public String getShippingContactName(){return data.optString("contact_name");}

    public String getShippingStreet(){return data.optString("street");}

    public String getShippingApartment(){return data.optString("apartment");}

    public String getShippingStateCountry(){return data.optString("state_country");}

    public String getShippingPostalCode(){return data.optString("postal_code");}

    public String getShippingMobile(){return data.optString("mobile");}

    public ArrayList<OrderProductDetail> getOrderProducts(){
        JSONArray productordrs = data.optJSONArray("productorders");
        ArrayList<OrderProductDetail> orderProductDetailArrayList = new ArrayList<>();
        OrderProductDetail orderProduct = new OrderProductDetail();
        for (int i = 0; i < productordrs.length(); i ++){
            JSONObject productorder = productordrs.optJSONObject(i);
            orderProduct.setData(productorder);
            orderProductDetailArrayList.add(orderProduct);
        }

        return orderProductDetailArrayList;
    }
}
