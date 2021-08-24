package com.brian.market.doba.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DobaProduct {
    JSONObject data;
    public DobaProduct(JSONObject object) {
        data = object;
    }

    public String getSpuId() {
        try {
            return data.getString("spuId");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getItemNo() {
        try {
            JSONObject child = getChildren().getJSONObject(0);
            JSONArray stocks = child.getJSONArray("stocks");
            String itemno = stocks.getJSONObject(0).getString("itemNo");
            return itemno;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getQty() {
        try {
            JSONObject child = getChildren().getJSONObject(0);
            JSONArray stocks = child.getJSONArray("stocks");
            String qty = stocks.getJSONObject(0).getString("availableNum");
            return qty;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getTitle() {
        try {
            return data.getString("title");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getDescription() {
        try {
            return data.getString("goodsDesc");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public JSONArray getPics() {
        try {
            JSONObject child = getChildren().getJSONObject(0);
            JSONArray picsArray = child.getJSONArray("skuPicList");
            JSONArray pics = new JSONArray();
            for (int i = 0; i < picsArray.length(); i ++) {
                JSONObject object = new JSONObject();
                object.put("thumb", picsArray.getString(i));
                pics.put(object);
            }
            return pics;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCurrency() {
        try {
            JSONObject child = getChildren().getJSONObject(0);
            return child.getString("currencyId");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getMarketPrice() {
        try {
            JSONObject child = getChildren().getJSONObject(0);
            return child.getString("marketPrice");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getShipLimit() {
        try {
            String ship = data.getString("shipLimit");
            ship = ship.replace("[", "");
            ship = ship.replace("\"", "");
            ship = ship.replace("]", "");
            return ship;
//            String shipTo="";
//            String ship = data.getString("shipLimit");
//            JSONArray limits = new JSONArray(ship);
//            for (int i = 0; i < limits.length(); i ++) {
//                shipTo += limits.getString(i);
//                if(i != limits.length()-1)
//                    shipTo += ", ";
//            }
//            return shipTo;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

//    public String getShipMethod() {
//        try {
//            JSONArray methods = data.getJSONArray("shipMethods");
//            String shipMethods = "";
//            for(int i = 0; i < methods.length(); i ++) {
//                shipMethods += methods.getJSONObject(i).getString("shipName");
//                if(i != methods.length()-1) shipMethods += ",";
//            }
//            return shipMethods;
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return "";
//        }
//    }

    public String getShipMethod() {
        try {
            JSONArray methods = data.getJSONArray("shipMethods");
            String shipMethods = methods.getJSONObject(0).getString("shipName");

            return shipMethods;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getShipId() {
        try {
            JSONArray methods = data.getJSONArray("shipMethods");
            String shipMethods = methods.getJSONObject(0).getString("shipId");
            return shipMethods;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private JSONArray getChildren() {
        try {
            return data.getJSONArray("children");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
