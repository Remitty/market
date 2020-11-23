package com.brian.market.modelsList;

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
        try {
            return data.getString("icon");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
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
