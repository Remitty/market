package com.brian.market.modelsList;

import com.brian.market.utills.UrlController;

import org.json.JSONException;
import org.json.JSONObject;

public class CoinInfo {
    private JSONObject data;
    private String CoinType;
    private String CoinSymbol;
    private String CoinUsdc;
    private String CoinRate;

    public CoinInfo(JSONObject item) {
        data = item;
    }

    public JSONObject getData() {
        return data;
    }

    public String getCoinType() {
        try {
            return data.getString("coin_name");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCoinId() {
        try {
            return data.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCoinSymbol() {
        try {
            return data.getString("coin_symbol");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCoinRate() {
        try {
            return data.getString("coin_rate");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCoinIcon() {
        String path = "";
        try {
            path = data.getString("coin_icon");
            if(path.equals("") || path.equals("null")) {
                path = data.getString("icon");
            }
            if(!path.startsWith("http"))
                path = UrlController.IP_ADDRESS + path;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return path;
    }

    public JSONObject getCoinWallet() {
        return null;
//        try {
//            return data.getJSONObject("wallet");
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return null;
//        }  catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
    }

    public String getCoinUsdc() {
//        try {
//            JSONObject wallet = data.getJSONObject("wallet");
//            return wallet.getString("coin_amount");
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return null;
//        }

        return null;
    }
}
