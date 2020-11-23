package com.brian.market.modelsList;

import java.util.ArrayList;

public class homeCatRelatedList {

    private String title;
    private String catId;
    private String viewAllBtnText;
    private ArrayList<ProductDetails> arrayList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getViewAllBtnText() {
        return viewAllBtnText;
    }

    public void setViewAllBtnText(String viewAllBtnText) {
        this.viewAllBtnText = viewAllBtnText;
    }

    public ArrayList<ProductDetails> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<ProductDetails> arrayList) {
        this.arrayList = arrayList;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }
}
