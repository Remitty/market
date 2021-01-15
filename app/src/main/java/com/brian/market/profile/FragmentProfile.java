package com.brian.market.profile;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brian.market.home.helper.Location_popupModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.brian.market.R;
import com.brian.market.home.HomeActivity;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;


public class FragmentProfile extends Fragment {

    SettingsMain settingsMain;
    TextView verifyBtn, textViewRateNo, textViewUserName, textViewLastLogin;
    TextView editProfBtn, textViewAdsSold, textViewTotalList, textViewExppiry,textViewInactiveAds, textViewTitle;

    TextView textViewNameValue, textViewEmailvalue, textViewPhonevalue,textViewPostalCodevalue,
            textViewAddress1,  textViewAccTypeValue, textViewAccHoldervalue,
            textViewPackgTypevalue, textViewFreeAdsvalue,
            textViewFeatureAdsvalue,  textViewExpiryvalue, textViewVerify,
            textViewAddress2, textViewCountry, textViewState, textViewCity,
            textViewBumpAdsValue, textViewBlockValue, textViewBankName;
    CircleImageView imageViewProfile;
    RatingBar ratingBar;
    RestService restService;
    JSONObject jsonObject;
    LinearLayout bumpAdLayout, blockUserLayout;
    View bumdAdView, blockUserView;
    LinearLayout verifyPhoneLayout;
    JSONObject dialogText, alertDialog;
    Dialog dialog;

    @Override
    public void onStop() {
        super.onStop();
        if (settingsMain != null) {
            settingsMain.setUserVerified(true);
        }
    }

    public FragmentProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        settingsMain = new SettingsMain(getActivity());
        textViewLastLogin = view.findViewById(R.id.loginTime);
        verifyBtn = view.findViewById(R.id.verified);
        textViewRateNo = view.findViewById(R.id.numberOfRate);
        textViewUserName = view.findViewById(R.id.text_viewName);
        textViewVerify = view.findViewById(R.id.textViewVerify);
        verifyPhoneLayout = view.findViewById(R.id.verifyPhoneLayout);

        editProfBtn = view.findViewById(R.id.editProfile);

        textViewAdsSold = view.findViewById(R.id.share);
        textViewTotalList = view.findViewById(R.id.addfav);
        textViewInactiveAds = view.findViewById(R.id.report);
        textViewExppiry = view.findViewById(R.id.expired);
        imageViewProfile = view.findViewById(R.id.image_view);
        ratingBar = view.findViewById(R.id.ratingBar);

//        LayerDrawable stars = (LayerDrawable) this.ratingBar.getProgressDrawable();
//        stars.getDrawable(2).setColorFilter(Color.parseColor("#ffcc00"), PorterDuff.Mode.SRC_ATOP);

        textViewTitle = view.findViewById(R.id.textView);

        textViewNameValue = view.findViewById(R.id.textViewNameValue);
        textViewEmailvalue = view.findViewById(R.id.textViewEmailValue);
        textViewPhonevalue = view.findViewById(R.id.textViewPhoneValue);
        textViewAddress1 = view.findViewById(R.id.textViewAddress1);
        textViewAddress2 = view.findViewById(R.id.textViewAddress2);
        textViewCountry = view.findViewById(R.id.textViewCountry);
        textViewState = view.findViewById(R.id.textViewState);
        textViewCity = view.findViewById(R.id.textViewCity);
        textViewBankName = view.findViewById(R.id.textView_bank_name);
        textViewPostalCodevalue = view.findViewById(R.id.textViewPostalCodeValue);
        textViewAccTypeValue = view.findViewById(R.id.textViewAccount_typeValue);
        textViewAccHoldervalue = view.findViewById(R.id.textViewHolderValue);
        textViewPackgTypevalue = view.findViewById(R.id.textViewPackageValue);
        textViewFreeAdsvalue = view.findViewById(R.id.textViewFreeAdsValue);
        textViewFeatureAdsvalue = view.findViewById(R.id.textViewFeaturAdsValue);
        textViewExpiryvalue = view.findViewById(R.id.textViewExpiryValue);

        textViewBlockValue = view.findViewById(R.id.textViewBlockValue);
        blockUserLayout = view.findViewById(R.id.blockUserLayout);
        blockUserView = view.findViewById(R.id.blockUserView);

        bumpAdLayout = view.findViewById(R.id.bumpAdLayout);
        textViewBumpAdsValue = view.findViewById(R.id.textViewBumpAdsValue);
        bumdAdView = view.findViewById(R.id.bumdAdView);
        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), getActivity());
        dialog = new Dialog(getActivity(), R.style.customDialog);

        adforest_setAllViewsText();
//        ((HomeActivity) getActivity()).changeImage();

        editProfBtn.setOnClickListener(view1 -> replaceFragment(new EditProfile(), "EditProfile"));

//        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.swipe_refresh_layout);
//        swipeRefreshLayout.setEnabled(false);
        return view;
    }

    private void adforest_setAllViewsText() {


        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());
            Call<ResponseBody> myCall = restService.getProfileDetails(UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                            Log.d("info profileGet Details", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info profileGet object", "" + response.getJSONObject("data"));

                                jsonObject = response.getJSONObject("data");
                                getActivity().setTitle("Profile");

                                textViewUserName.setText(jsonObject.getString("first_name") + " " + jsonObject.getString("last_name"));
                                settingsMain.setUserImage(jsonObject.getString("picture"));

                                Picasso.with(getContext()).load(settingsMain.getUserImage())
                                        .error(R.drawable.placeholder)
                                        .placeholder(R.drawable.placeholder)
                                        .into(imageViewProfile);

//                                textViewNameValue.setText(jsonObject.getJSONObject("display_name").getString("value"));
                                textViewPhonevalue.setText(jsonObject.getString("country_code") + jsonObject.getString("mobile"));
                                settingsMain.setUserPhone(jsonObject.getString("country_code") + jsonObject.getString("mobile"));
                                textViewEmailvalue.setText(jsonObject.getString("email"));
                                textViewAddress1.setText(jsonObject.getJSONObject("payment").getString("address"));
                                textViewAddress2.setText(jsonObject.getJSONObject("payment").getString("address2"));
                                textViewCountry.setText(jsonObject.getJSONObject("payment").getString("country"));
                                textViewState.setText(jsonObject.getJSONObject("payment").getString("state"));
                                textViewCity.setText(jsonObject.getJSONObject("payment").getString("city"));
                                textViewPostalCodevalue.setText(jsonObject.getJSONObject("payment").getString("postalcode"));
                                textViewAccTypeValue.setText(jsonObject.getJSONObject("payment").getString("account_number"));
                                textViewBankName.setText(jsonObject.getJSONObject("payment").getString("bank_name"));
                                textViewAccHoldervalue.setText(jsonObject.getJSONObject("payment").getString("account_holder_name"));
                                textViewPackgTypevalue.setText(jsonObject.getJSONObject("payment").getString("account_routing_number"));

                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        SettingsMain.hideDilog();
                    } catch (IOException e) {
                        e.printStackTrace();
                        SettingsMain.hideDilog();
                    }
                    SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info ProfileGet error", String.valueOf(t));
                    Log.d("info ProfileGet error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });

        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
        }
    }


    public void replaceFragment(Fragment someFragment, String tag) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.right_enter, R.anim.left_out, R.anim.left_enter, R.anim.right_out);
        transaction.replace(R.id.profile_frame, someFragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
