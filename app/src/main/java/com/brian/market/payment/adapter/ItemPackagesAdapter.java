package com.brian.market.payment.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.daasuu.bl.ArrowDirection;
import com.daasuu.bl.BubbleLayout;
import com.daasuu.bl.BubblePopupHelper;
import com.tooltip.Tooltip;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.xm.weidongjian.popuphelper.PopupWindowHelper;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import com.brian.market.R;
import com.brian.market.adapters.SpinnerAndListAdapter;
import com.brian.market.helper.OnItemClickListenerPackages;
import com.brian.market.modelsList.PackagesModel;
import com.brian.market.modelsList.subcatDiloglist;
import com.brian.market.utills.SettingsMain;

public class ItemPackagesAdapter extends RecyclerView.Adapter<ItemPackagesAdapter.CustomViewHolder> {

    SettingsMain settingsMain;
    private List<PackagesModel> feedItemList;
    private OnItemClickListenerPackages onItemClickListener;
    private Context mContext;
    private PopupWindowHelper popupWindowHelper;
    private View popView;
    SimpleTooltip simpleTooltip;

    public ItemPackagesAdapter(Context context1, List<PackagesModel> feedItemList) {
        this.feedItemList = feedItemList;
        settingsMain = new SettingsMain(context1);
        this.mContext = context1;

    }

    @Override
    public ItemPackagesAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_packages, null);
        return new ItemPackagesAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemPackagesAdapter.CustomViewHolder customViewHolder, int i) {

        final PackagesModel feedItem = feedItemList.get(i);

        if (settingsMain.getAppOpen()) {
            customViewHolder.spinner.setVisibility(View.GONE);
        }
        customViewHolder.name.setText(feedItem.getPlanType());
        customViewHolder.price.setText(feedItem.getPrice());
        customViewHolder.validaty.setText(feedItem.getValidaty());
        customViewHolder.ads.setText(feedItem.getFreeAds());
        customViewHolder.featureads.setText(feedItem.getFeatureAds());
        customViewHolder.bumpAds.setText(feedItem.getBumupAds());
        customViewHolder.biddingAds.setText(feedItem.getAllowBidding());
        customViewHolder.noOfImages.setText(feedItem.getNumOfImages());
        customViewHolder.videoUrl.setText(feedItem.getVideoUrl());
        customViewHolder.allowTags.setText(feedItem.getAllowTags());
        if (feedItem.getAllowCats() == null) {
            customViewHolder.allowCats.setVisibility(View.GONE);
            customViewHolder.allowCatsValue.setVisibility(View.VISIBLE);
        } else
            customViewHolder.allowCats.setText(feedItem.getAllowCats());

        customViewHolder.titlePackages.setText(feedItem.getListTitleText());
        customViewHolder.allowCatsValue.setText(feedItem.getReadMoreText());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, feedItem.getSpinnerData());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        customViewHolder.spinner.setAdapter(adapter);
        customViewHolder.spinner.setTag(feedItem.getBtnTag());


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(feedItem);
            }
        };

        customViewHolder.allowCatsValue.setOnClickListener(new View.OnClickListener() {


            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
//                Toast.makeText(mContext, "clicked on all", Toast.LENGTH_SHORT).show();


//                PopupWindow popUpWindow;
//                popView = LayoutInflater.from(mContext).inflate(R.layout.item_packages_dialog, null);
//                popUpWindow = new PopupWindow(popView);
//                popUpWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//                popUpWindow.showAtLocation( view, Gravity.RIGHT, 0, 10);

                BubbleLayout bubbleLayout = (BubbleLayout) LayoutInflater.from(mContext).inflate(R.layout.bubblelayout, null);
                PopupWindow popupWindow = BubblePopupHelper.create(mContext, bubbleLayout);
//                Drawable d = new ColorDrawable(Color.GREEN);
//                d.setAlpha(222);
//                popupWindow.setBackgroundDrawable(d);

                final Random random = new Random();
                bubbleLayout.setArrowDirection(ArrowDirection.BOTTOM_CENTER);
//                popupWindow.showAtLocation( view, Gravity.BOTTOM, 0, 10);
// Show anchored to button
                popupWindow.showAtLocation(view, Gravity.BOTTOM, 0,
                        view.getBottom() - 250);

                popupWindow.showAsDropDown(view);
//                popUpWindow.showAtLocation(view, Gravity.TOP | Gravity.LEFT, rect.left, rect.bottom);

//                popUpWindow.setElevation(20);
//                popUpWindow.setAnimationStyle(R.anim.popupanim);

//                popView.setPadding(20,30,0,0);
                //                popUpWindow.setHeight(90);
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                params.topMargin = 10;
//                params.bottomMargin = 10;
//                popView.setLayoutParams(params);
//                popupWindowHelper = new PopupWindowHelper(popView);
//                popView.setForegroundGravity(View.TEXT_ALIGNME///NT_CENTER);
//                popView.setBackgroundColor(Color.BLUE);
//                popView.setPadding(0,0,23,55);
//                popupWindowHelper.setCancelable(true);
//                popupWindowHelper.showAsPopUp(view,19,19);
//                popupWindowHelper.showAsPopUp(view);
//                popupWindowHelper.showAtLocation(view,ViewGroup.TEXT_ALIGNMENT_CENTER,0,0);
//                popupWindowHelper.showAsPopUp(view);


//                final PopupWindow popupWindow = new PopupWindow(view.getContext());
//                LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//                view = inflater.inflate(R.layout.item_packages_dialog, null);
//                popupWindow.setFocusable(true);
//                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                popupWindow.setOutsideTouchable(true);
//                popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
//                popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
//                popupWindow.setContentView(view);
//Tooltip tooltip= new Tooltip.Builder(view)
//        .setText("hello")
//        .setArrowEnabled(true)
//        .show();
//                View view13 = LayoutInflater.from(mContext).inflate(R.layout.bubblelayout, null);
//                View view12 = LayoutInflater.from(mContext).inflate(R.layout.item_packages_dialog, null);

//                Tooltip tooltip = new Tooltip.Builder(mContext)
//                        .anchor(view12, Tooltip.BOTTOM)
//                        .content(view13)
//                        .into((ViewGroup) view12)
////                        .withTip(new Tooltip.Tip(tipWidth, tipHeight, tipColor))
//                        .show();
////                BubbleLayout bubbleLayout = (BubbleLayout) LayoutInflater.from(mContext).inflate(R.layout.item_packages_dialog, null);
//                PopupWindow popupWindow = BubblePopupHelper.create(mContext, bubbleLayout);
//             popupWindow=view.findViewById(R.id.bubblela)
//                final Random random = new Random();
//                bubbleLayout.setArrowDirection(ArrowDirection.TOP_CENTER);

//                popupWindowHelper.showFromTop(view);
//                popupWindowHelper.showFromBottom(view);


//                AlertDialog dialog;
//                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                View view12 = LayoutInflater.from(mContext).inflate(R.layout.item_packages_dialog, null);
//                builder.setView(view12);
//                dialog = builder.create();
//                dialog.show();
                TextView textView = bubbleLayout.findViewById(R.id.titlePackages);
                ListView listView = bubbleLayout.findViewById(R.id.listView);
                textView.setText(feedItem.getListTitleText());
                textView.getText().toString();
                final ArrayList<subcatDiloglist> listitems12 = new ArrayList<>();


                for (int j = 0; j < feedItem.getAllowCatsValue().length(); j++) {
                    try {
                        JSONObject jsonObject = feedItem.getAllowCatsValue().getJSONObject(j);
                        subcatDiloglist subDiloglist = new subcatDiloglist();
                        subDiloglist.setName(jsonObject.getString("cat_name_ch"));
                        listitems12.add(subDiloglist);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                final SpinnerAndListAdapter spinnerAndListAdapter1 = new SpinnerAndListAdapter((Activity) mContext, listitems12, true);
//                Toast.makeText(mContext, "ehwejkrf", Toast.LENGTH_SHORT).show();
                listView.setAdapter(spinnerAndListAdapter1);
            }
        });
        View.OnTouchListener listener1 = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onItemClickListener.onItemTouch();
                return false;
            }
        };
        customViewHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onItemClickListener.onItemSelected(feedItem, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        customViewHolder.spinner.setOnTouchListener(listener1);


    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public void setOnItemClickListener(OnItemClickListenerPackages onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView name, validaty, ads, price, featureads, bumpAds, biddingAds, noOfImages, videoUrl, allowTags, allowCats, allowCatsValue, titlePackages;
        Spinner spinner;
        RelativeLayout selectPackageLayout;
        Button ok;
        Tooltip tooltip;
        boolean spinnerTouched = false;

        CustomViewHolder(View view) {
            super(view);
//            this.ok = view.findViewById(R.id.dialog_ok_btn);
            this.biddingAds = view.findViewById(R.id.textView28);
            this.noOfImages = view.findViewById(R.id.textView29);
            this.videoUrl = view.findViewById(R.id.textView30);
            this.allowTags = view.findViewById(R.id.textView31);
            this.allowCats = view.findViewById(R.id.textView32);
            this.allowCatsValue = view.findViewById(R.id.textView33);
            this.name = view.findViewById(R.id.textView22);
            this.price = view.findViewById(R.id.textView26);
            this.validaty = view.findViewById(R.id.textView23);
            this.ads = view.findViewById(R.id.textView24);
            this.featureads = view.findViewById(R.id.textView25);
            this.bumpAds = view.findViewById(R.id.textView27);
            this.titlePackages = view.findViewById(R.id.textViewtitle);
            spinner = view.findViewById(R.id.selectPlan);
            selectPackageLayout = view.findViewById(R.id.selectPackageLayout);
            allowCatsValue.setTextColor(Color.parseColor(settingsMain.getMainColor()));
            price.setTextColor(Color.parseColor(settingsMain.getMainColor()));
//            allowCatsValue.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    tooltip= new Tooltip.Builder(allowCatsValue)
//                            .setText((CharSequence) feedItemList.get(getAdapterPosition()).getAllowCatsValue())
//                            .setArrowEnabled(true)
//                            .show();
//                }
//            });

        }
    }

}
