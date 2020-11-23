package com.brian.market.modelsList;

import com.brian.market.utills.UrlController;

import org.json.JSONObject;

public class homeCatListModel {
    private String title;
    private String thumbnail;
    private String id;
    private boolean has_children;
    private String adsCount;
    private JSONObject data;

    public void setData(JSONObject data){this.data = data;}

    public boolean isHas_children() {
        return has_children;
    }

    public void setHas_children(boolean has_children) {
        this.has_children = has_children;
    }

    public String getAdsCount() {
        return adsCount;
    }

    public void setAdsCount(String adsCount) {
        this.adsCount = adsCount;
    }

    public String getTitle() {
        return title;
    }

    public String getBGColor(){return data.optString("bg_color");}

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        if(!thumbnail.startsWith("http"))
            thumbnail = UrlController.IP_ADDRESS + "storage/" + thumbnail;
        this.thumbnail = thumbnail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
