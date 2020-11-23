package com.brian.market.modelsList;

import org.json.JSONObject;

import java.text.DecimalFormat;

public class WithdrawTransaction {
    private JSONObject data = new JSONObject();

    public void setData(JSONObject data){this.data = data;}

    public String getPrice(){return new DecimalFormat("$#0.00").format(data.optDouble("withdraw_price")).toString();}

    public String getStatus(){return data.optString("status");}

    public String getMethod() {return data.optString("method");}

    public String getDate() {return data.optString("created_at").substring(0, 10);}
}
