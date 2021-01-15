package com.brian.market.signinorup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brian.market.App;
import com.brian.market.wxapi.WXPayEntryActivity;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.brian.market.R;
import com.brian.market.home.HomeActivity;
import com.brian.market.utills.CustomBorderDrawable;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import static android.content.Context.MODE_PRIVATE;
import static com.brian.market.R.anim;
import static com.brian.market.R.id;

public class Login_Fragment extends Fragment implements OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int RC_SIGN_IN = 0;
    protected static String user_email;
    private static Animation shakeAnimation;
    private static FragmentManager fragmentManager;
    View view;
    Activity activity;
    EditText emailid, password;
    Button loginButton, startExplore, fbloginButton, btnWechat;
    private SignInButton gmailLoginButton;
    TextView forgotPassword, signUp;
    LinearLayout loginLayout;
    SettingsMain settingsMain;
    LinearLayout linearLayoutLogo;
    ImageView imageViewLogo;
    TextView textViewWelcome, textViewOR, txEn, txCH;
    LinearLayout guestLayout;
    RelativeLayout leftSideAttributLayout;
    boolean is_verify_on = false;
    private boolean mIntentInProgress = true;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    boolean back_pressed = false;

    public Login_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.login_layout, container, false);
        activity = getActivity();
        settingsMain = new SettingsMain(activity);
        adforest_initViews();
        adforest_setDataToViews();
        fbSetup();
        setListeners();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        return view;
    }

    // Initiate Views
    private void adforest_initViews() {
        fragmentManager = getActivity().getSupportFragmentManager();

        emailid = view.findViewById(id.login_emailid);
        emailid.setText(settingsMain.getUserEmail());
        password = view.findViewById(id.login_password);
        loginButton = view.findViewById(id.loginBtn);
        btnWechat = view.findViewById(id.btn_wechat);
        forgotPassword = view.findViewById(id.forgot_password);
        signUp = view.findViewById(id.createAccount);
        fbloginButton = view.findViewById(id.fbLogin);
        gmailLoginButton = view.findViewById(id.gmailLogin);
//        fbloginButton.setVisibility(View.INVISIBLE);
        gmailLoginButton.setVisibility(View.INVISIBLE);
        loginLayout = view.findViewById(id.login_layout);
        startExplore = view.findViewById(id.startExplore);
//        guestLayout = view.findViewById(R.id.guestLayout);

//        textViewOR = view.findViewById(id.or);
        textViewWelcome = view.findViewById(id.welcomeTV);
        imageViewLogo = view.findViewById(id.logoimage);

        txCH = view.findViewById(R.id.tx_ch);
        txEn = view.findViewById(R.id.tx_en);

        imageViewLogo = view.findViewById(id.logoimage);
//        linearLayoutLogo = view.findViewById(id.logo);
//        linearLayoutLogo.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
//        startExplore.setTextColor(Color.parseColor(SettingsMain.getMainColor()));
//        startExplore.setBackground(CustomBorderDrawable.customButton(3, 3, 3, 3, SettingsMain.getMainColor(), "#00000000", SettingsMain.getMainColor(), 2));

//        loginButton.setTextColor(Color.parseColor(SettingsMain.getMainColor()));
//        loginButton.setBackground(CustomBorderDrawable.customButton(3, 3, 3, 3, SettingsMain.getMainColor(), "#00000000", SettingsMain.getMainColor(), 2));

        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
                anim.shake);

// Setting text selector over textviews
        @SuppressWarnings("ResourceType") XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            @SuppressWarnings("deprecation") ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

//            forgotPassword.setTextColor(csl);
//            signUp.setTextColor(csl);
        } catch (Exception ignored) {
        }
//        leftSideAttributLayout = view.findViewById(id.btnLL);
//        leftSideAttributLayout.removeAllViews();
    }

    void adforest_setDataToViews() {

//        if (SettingsMain.isConnectingToInternet(getActivity())) {
//
////            SettingsMain.showDilog(getActivity());
////            adforest_getLoginViews();
//
//        } else {
//            SettingsMain.hideDilog();
//            Toast.makeText(getActivity(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
//        }

        Log.d("joiint open", settingsMain.getAppOpen()+"");

//        if (!settingsMain.getAppOpen()) {
//            guestLayout.setVisibility(View.VISIBLE);
            startExplore.setVisibility(View.VISIBLE);
//                                settingsMain.setUserName(response.getJSONObject("data").getString("guest_text"));
//        }

    }

    // Set Listeners
    private void setListeners() {
        loginButton.setOnClickListener(this);
        fbloginButton.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        signUp.setOnClickListener(this);
        gmailLoginButton.setOnClickListener(this);
        startExplore.setOnClickListener(this);
        btnWechat.setOnClickListener(this);
        txCH.setOnClickListener(this);
        txEn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case id.fbLogin:

                loginToFacebook();

                break;
            case id.loginBtn:
                adforest_checkValidation();
                InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case id.btn_wechat:
                loginByWechat();
                break;
            case id.forgot_password:

                // Replace forgot password fragment with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(anim.right_enter, anim.left_out)
                        .replace(id.frameContainer,
                                new ForgotPassword_Fragment(),
                                Utils.ForgotPassword_Fragment).commit();
                break;
            case id.tx_ch:
                txCH.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorRedCrayon));
                txEn.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                switchLang("zh_CN");
                break;
            case id.tx_en:
                txEn.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorRedCrayon));
                txCH.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                switchLang("en");
                break;
            case id.createAccount:

                // Replace signup frgament with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(anim.right_enter, anim.left_out)
                        .replace(id.frameContainer, new SignUp_Fragment(),
                                Utils.SignUp_Fragment).commit();
                break;
            case id.gmailLogin:
                signIn();
                break;
            case id.startExplore:
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.adforest", MODE_PRIVATE).edit();
                editor.putString("isSocial", "false");
                editor.apply();
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
                activity.overridePendingTransition(anim.right_enter, anim.left_out);
                activity.finish();
                settingsMain.setUserEmail("");
                settingsMain.setUserImage("");
                break;
        }
    }

    private void switchLang(String lang) {
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        Resources res = getActivity().getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        conf.setLayoutDirection(myLocale);
        res.updateConfiguration(conf, dm);
//        Intent refresh = new Intent(getActivity(), MainActivity.class);
//        getActivity().finish();
//        startActivity(refresh);
    }

    private void loginByWechat() {
        IWXAPI iwxAPI = WXAPIFactory.createWXAPI(getActivity(), getString(R.string.wechat_app_id), true);
        iwxAPI.sendReq(new SendAuth.Req());
        Toast.makeText(activity, "Not approved still", Toast.LENGTH_SHORT).show();
    }


    // Check Validation before adforest_login
    private void adforest_checkValidation() {
        // Get email id and password
        String getEmailId = emailid.getText().toString();
        String getPassword = password.getText().toString();

        // Check patter for email id
        Pattern p = Pattern.compile(Utils.regEx);

        Matcher m = p.matcher(getEmailId);

        // Check for both field is empty or not
        if (getEmailId.equals("") && getEmailId.length() == 0) {
            if (getPassword.equals("") && getPassword.length() == 0) {
                loginLayout.startAnimation(shakeAnimation);
                emailid.setError("!");
                password.setError("!");
            }
        } else if (getEmailId.equals("") && getEmailId.length() == 0) {
            loginLayout.startAnimation(shakeAnimation);
            emailid.requestFocus();
            emailid.setError("!");
        } else if (getPassword.equals("") && getPassword.length() == 0) {
            loginLayout.startAnimation(shakeAnimation);
            password.setError("!");
            password.requestFocus();
        }
        // Check if email id is valid or not
        else if (!m.find()) {
            emailid.requestFocus();
            emailid.setError("!");
        }
        // Else do adforest_login and do your stuff
        else {
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.adforest", MODE_PRIVATE).edit();
            editor.putString("isSocial", "false");
            editor.apply();
            adforest_login(getEmailId, getPassword);/// for test
        }

    }

    private void adforest_login(final String email, final String pswd) {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("email", email);
            params.addProperty("password", pswd);
            params.addProperty("device_token", settingsMain.getDeviceToken());
            Log.d("enteries are", params.toString());

            RestService restService =
                    UrlController.createService(RestService.class);
            Call<ResponseBody> myCall = restService.postLogin(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                            Log.d("info LoginPost responce", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = null;
                            try {
                                response = new JSONObject(responseObj.body().string());
                                if (response.getBoolean("success")) {
                                    if (is_verify_on && !response.getJSONObject("data").getBoolean("is_account_confirm")) {
                                        Log.d("info Login Resp", "" + response.getJSONObject("data"));
                                        Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_LONG).show();
                                        Utils.user_id = response.getJSONObject("data").getString("id");
                                        Handler handler = new Handler();
                                        handler.postDelayed(() -> fragmentManager
                                                .beginTransaction()
                                                .setCustomAnimations(anim.right_enter, anim.left_out)
                                                .replace(id.frameContainer,
                                                        new VerifyAccount_Fragment(),
                                                        Utils.VerifyAccount_Fragment).commit(), 1000);

                                    } else {
                                        Log.d("info Login Post", "" + response.getJSONObject("data"));
//                                    Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();

                                        JSONObject data = response.getJSONObject("data");

                                        settingsMain.setAuthToken(data.getString("access_token"));
                                        settingsMain.setUserLogin(data.getString("id"));
                                        settingsMain.setUserImage(data.getString("picture"));
                                        settingsMain.setUserName(data.getString("first_name") + " " + data.getString("last_name"));
                                        settingsMain.setUserPhone(data.getString("country_code")+data.getString("mobile"));
                                        settingsMain.setUserEmail(email);
                                        settingsMain.setUserPassword(pswd);
                                        settingsMain.setAppOpen(true);

                                        if(data.getJSONObject("payment") == null) {
                                            startActivity(new Intent(getActivity(), ProfileCompleteActivity.class));
                                        }
                                        else {
                                            settingsMain.setProfileComplete(true);
                                            startActivity(new Intent(getActivity(), HomeActivity.class));
                                        }

                                        activity.overridePendingTransition(anim.right_enter, anim.left_out);
                                        activity.finish();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("login error", e.getMessage());
                            }


                        }
                        SettingsMain.hideDilog();
                    } catch (JSONException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    }
//                    catch (IOException e) {
//                        SettingsMain.hideDilog();
//                        e.printStackTrace();
//                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info LoginPost error", String.valueOf(t));
                    Log.d("info LoginPost error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
        }

    }

    private void loginToFacebook() {

        if (SettingsMain.isConnectingToInternet(activity)) {
            LoginManager.getInstance().logInWithReadPermissions(this,
                    Arrays.asList("public_profile", "email"));
        } else {
            Toast.makeText(activity, getString(R.string.internet_error),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void getFBStats(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            Log.i("tag", "Obj " + object.toString());
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.adforest", MODE_PRIVATE).edit();
                            editor.putString("isSocial", "true");
                            editor.apply();


                            adforest_loginSocialMedia(object.getString("email"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields",
                "id,first_name,last_name,email,gender, birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }

    // FB SETUP CALLS
    private void fbSetup() {
        //noinspection deprecation
        FacebookSdk.sdkInitialize(activity.getApplicationContext());
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
        callbackManager = CallbackManager.Factory.create();
        new AccessTokenTracker() {

            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken != null) {
                    Log.i("tag", "In From ONcreate");
                    Log.i("tag", "go to home");
                } else {
                    Log.i("tag", "Else In From ONcreate");
                    Log.i("tag", "Goto splash");
                }
            }
        };

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult result) {
                        // TODO Auto-generated method stub
                        Log.i("tag", "Success ");
                        getFBStats(result.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // TODO Auto-generated method stub
                        Log.i("tag", "On Cancel ");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        // TODO Auto-generated method stub
                        Log.i("tag", "Error " + error);
                    }
                });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.getView().setFocusableInTouchMode(true);
        this.getView().requestFocus();
        this.getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

//This is the filter
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return true;
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                        backPressed();
                        break;
                }
                return true;
            }
        });
    }

    private void backPressed() {
        if (!back_pressed) {
            Toast.makeText(getContext(), getString(R.string.message_press_again_exit), Toast.LENGTH_SHORT).show();
            back_pressed = true;
            android.os.Handler mHandler = new android.os.Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    back_pressed = false;
                }
            }, 2000L);
        } else {

            //Alert dialog for exit form login screen

            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle(settingsMain.getAlertDialogTitle("info"));
            alert.setCancelable(false);
            alert.setMessage(getString(R.string.message_exit));
            alert.setPositiveButton(settingsMain.getAlertOkText(), (dialog, which) -> {
                getActivity().finishAffinity();
                dialog.dismiss();
            });
            alert.setNegativeButton(settingsMain.getAlertCancelText(), (dialogInterface, i) -> dialogInterface.dismiss());
            alert.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.adforest", MODE_PRIVATE).edit();
            editor.putString("isSocial", "true");
            editor.apply();

            adforest_loginSocialMedia(acct != null ? acct.getEmail() : null);

        } else {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    status -> {
                    });
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void signIn() {

        if (mIntentInProgress) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
            mIntentInProgress = false;
        } else {
            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            if (opr.isDone()) {
                Log.d("s", "Got cached sign-in");
                GoogleSignInResult result = opr.get();
                handleSignInResult(result);
            } else {
                // If the user has not previously signed in on this device or the sign-in has expired,
                // this asynchronous branch will attempt to sign in the user silently.  Cross-device
                // single sign-on will occur in this branch.
                opr.setResultCallback(googleSignInResult -> handleSignInResult(googleSignInResult));
            }
        }
    }


    private void adforest_loginSocialMedia(final String email) {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("email", email);
            params.addProperty("type", "social");

            RestService restService =
                    UrlController.createService(RestService.class, email, "1122", getActivity());
            Call<ResponseBody> myCall = restService.postLogin(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info LoginScoial respon", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                Log.d("info LoginScoial", "" + response.getJSONObject("data"));
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();

                                settingsMain.setUserLogin(response.getJSONObject("data").getString("id"));
                                settingsMain.setUserImage(response.getJSONObject("data").getString("profile_img"));
                                settingsMain.setUserName(response.getJSONObject("data").getString("display_name"));
                                settingsMain.setUserPhone(response.getJSONObject("data").getString("phone"));
                                settingsMain.setUserEmail(email);
                                settingsMain.setUserPassword("1122");

                                SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.adforest", MODE_PRIVATE).edit();
                                editor.putString("isSocial", "true");
                                editor.apply();
                                settingsMain.setAppOpen(false);
                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                startActivity(intent);
                                activity.overridePendingTransition(anim.right_enter, anim.left_out);
                                activity.finish();
                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        SettingsMain.hideDilog();
                    } catch (JSONException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info LoginScoial error", String.valueOf(t));
                    Log.d("info LoginScoial error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
        }

    }

    class WechatActivy extends WXPayEntryActivity {

        @Override
        public void wechatLoginSuccess() {

        }

        @Override
        public void wechatPaySuccess() {

        }
    }

}
