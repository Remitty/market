package com.brian.market.models;

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

    public String getTxnId(){
        return data.optString("transaction_id");
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
    public String getPayment(){
        return data.optString("payment_mode");
    }

    public int getShipping(){
        return data.optInt("shipping");
    }

    public String getDate(){
        return data.optString("updated_at");
    }

    public String getShippingContactName(){return data.optString("contact_name");}

    public String getShippingStreet(){return data.optString("address1");}

    public String getShippingApartment(){return data.optString("address2");}

    public String getShippingStateCountry(){return data.optString("city") + ", " + data.optString("state") + ", " + data.optString("country");}

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
