package com.brian.market.signinorup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
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
import com.phonenumberui.PhoneNumberActivity;

import static android.content.Context.MODE_PRIVATE;

public class SignUp_Fragment extends Fragment implements OnClickListener
        , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int RC_SIGN_IN = 0;
    private static FragmentManager fragmentManager;
    Activity activity;
    RestService restService;
    boolean is_verify_on = false;
    String term_page_id;
    RelativeLayout leftSideAttributLayout;
    private boolean mIntentInProgress = true;
    private View view;
    private EditText firstName, lastName, emailId, mobileNumber,
            password;
    private TextView login, titleTV, orTV, terms_conditionsText, tex;
    private Button signUpButton, fbloginButton;
    private SignInButton gmailLoginButton;
    private CheckBox terms_conditions;
    private CallbackManager callbackManager;
    private SettingsMain settingsMain;
    private GoogleApiClient mGoogleApiClient;
    public static int APP_REQUEST_CODE = 99;
    ActionCodeSettings actionCodeSettings;

    public SignUp_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signup_layout, container, false);
        activity = getActivity();
        settingsMain = new SettingsMain(activity);
        restService = UrlController.createService(RestService.class);

        adforest_initViews();
//        adforest_setDataToViews();
//        fbSetup();
        mIntentInProgress = true;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

         actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        .setUrl("https://joiintapp.com/verifysignup")
                        .setDynamicLinkDomain("joiint.page.link")
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                                "com.brian.market",
                                true,
                                "12" )
                        .build();

        setListeners();
        return view;
    }

    // Initialize all views
    @SuppressWarnings("ResourceType")
    private void adforest_initViews() {
        fragmentManager = getActivity().getSupportFragmentManager();

        firstName = view.findViewById(R.id.firstname);
        lastName = view.findViewById(R.id.lastname);
        emailId = view.findViewById(R.id.email);
        mobileNumber = view.findViewById(R.id.mobileNumber);
        password = view.findViewById(R.id.password);
        signUpButton = view.findViewById(R.id.signup);
        login = view.findViewById(R.id.already_user);

        terms_conditions = view.findViewById(R.id.terms_conditions);
        terms_conditionsText = view.findViewById(R.id.terms_conditionsText);

        fbloginButton = view.findViewById(R.id.fbLogin);
        gmailLoginButton = view.findViewById(R.id.gmailLogin);

        titleTV = view.findViewById(R.id.title);
//        orTV = view.findViewById(R.id.or);

//        signUpButton.setTextColor(Color.parseColor(settingsMain.getMainColor()));
//        signUpButton.setBackground(CustomBorderDrawable.customButton(3, 3, 3, 3, settingsMain.getMainColor(), "#00000000", settingsMain.getMainColor(), 2));

        /*LinearLayout linearLayout = view.findViewById(R.id.myLinearLayout);

        for (int i = 0; i < linearLayout.getChildCount(); i++)
            if (linearLayout.getChildAt(i) instanceof EditText) {

                Drawable[] compoundDrawables = ((EditText) linearLayout.getChildAt(i)).getCompoundDrawables();
                Drawable leftCompoundDrawable = compoundDrawables[0];
                ((EditText) linearLayout.getChildAt(i)).setCompoundDrawables(null,null,leftCompoundDrawable,null);

            }*/

        // Setting text selector over textviews
        XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            @SuppressWarnings("deprecation") ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

//            login.setTextColor(csl);
            terms_conditions.setTextColor(csl);
        } catch (Exception ignored) {
        }

//        leftSideAttributLayout = view.findViewById(R.id.btnLL);
//        leftSideAttributLayout.removeAllViews();

    }

    // Set Listeners
    private void setListeners() {
        signUpButton.setOnClickListener(this);
        login.setOnClickListener(this);
        fbloginButton.setOnClickListener(this);
        gmailLoginButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fbLogin:
                adforest_loginToFacebook();
                break;
            case R.id.signup:

                // Call adforest_checkValidation method
//                phoneLogin();
                adforest_checkValidation();
                InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                break;

            case R.id.already_user:

                // Replace login fragment
                new MainActivity().adforest_replaceLoginFragment();
                break;
            case R.id.gmailLogin:

                adforest_signInForSociel();
                Log.e("asdfsd", "display name: ");

                break;
        }

    }

    // Check Validation Method
    private void adforest_checkValidation() {

        // Get all edittext texts
        String getFirstName = firstName.getText().toString();
        String getLastName = lastName.getText().toString();
        String getEmailId = emailId.getText().toString();
        String getMobileNumber = mobileNumber.getText().toString();
        String getPassword = password.getText().toString();

        // Pattern match for email id
        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getEmailId);
        boolean validFlag = true;
        // Check if all strings are null or not
        if (getFirstName.equals("") || getFirstName.length() == 0) {
            firstName.setError("!");
            validFlag = false;
        }
        if (getLastName.equals("") || getLastName.length() == 0) {
            lastName.setError("!");
            validFlag = false;
        }
        if (getEmailId.equals("") || getEmailId.length() == 0) {
            emailId.setError("!");
            validFlag = false;
        }
//        if (getMobileNumber.equals("") || getMobileNumber.length() == 0) {
//            mobileNumber.setError("!");
//            validFlag = false;
//        }
        if (getPassword.equals("") || getPassword.length() == 0) {
            password.setError("!");
            validFlag = false;
        }

        if(getPassword.length() < 6){
            password.setError("Above 6 characters");
            validFlag = false;
        }
            // Check if email id valid or not
        if (!m.find()) {
            emailId.setError("!");
            validFlag = false;
        }

        if(validFlag) {
            settingsMain.showDilog(getActivity());
//            FirebaseAuth auth = FirebaseAuth.getInstance();
//            auth.sendSignInLinkToEmail(getEmailId, actionCodeSettings)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(getContext(), "Verified email successfully", Toast.LENGTH_SHORT).show();
////                                signinWithFirebaseEmail();
////                                setDynamicLinkForEmailVerify();
//                                adforest_signUp(getFirstName, getLastName, getEmailId, getMobileNumber, getPassword);
//                            }
//                            else{
//                                Toast.makeText(getContext(), "Verified email failed", Toast.LENGTH_SHORT).show();
//                                settingsMain.hideDilog();
//                            }
//                        }
//                    });
            adforest_signUp(getFirstName, getLastName, getEmailId, getMobileNumber, getPassword);

//            final FirebaseUser user = auth.getCurrentUser();
////            user.sendEmailVerification()
////                    .addOnCompleteListener(getActivity(), new OnCompleteListener() {
////                        @Override
////                        public void onComplete(@NonNull Task task) {
////                            // Re-enable button
////                            if (task.isSuccessful()) {
////                                Toast.makeText(getContext(), "Successfully Email verified", Toast.LENGTH_SHORT).show();
////                            } else {
////                                Toast.makeText(getContext(), "Faild Email verified", Toast.LENGTH_SHORT).show();
////                            }
////                        }
////                    });

        }
            // Make sure user should check Terms and Conditions checkbox
//        else if (!terms_conditions.isChecked())
//            terms_conditions.setError("!");
//        else {
//            SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.adforest", MODE_PRIVATE).edit();
//            editor.putString("isSocial", "false");
//            editor.apply();
//            adforest_signUp(getFullName, getEmailId, getMobileNumber, getPassword);
//        }
    }

    private void signinWithFirebaseEmail() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Intent intent = getActivity().getIntent();
        String emailLink = intent.getData().toString();

// Confirm the link is a sign-in with email link.
        if (auth.isSignInWithEmailLink(emailLink)) {
            // Retrieve this from wherever you stored it
            String email = emailId.getText().toString();

            // The client SDK will parse the code from the link for you.
            auth.signInWithEmailLink(email, emailLink)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("firebase email", "Successfully signed in with email link!");
                                AuthResult result = task.getResult();
                                // You can access the new user via result.getUser()
                                // Additional user info profile *not* available via:
                                // result.getAdditionalUserInfo().getProfile() == null
                                // You can check if the user is new or existing:
                                // result.getAdditionalUserInfo().isNewUser()
                            } else {
                                Log.e("firebase email", "Error signing in with email link", task.getException());
                            }
                        }
                    });
        }
    }


    void adforest_signUp(String firstName, final String lastName, final String email, final String phone, final String pswd) {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            JsonObject params = new JsonObject();
            params.addProperty("first_name", firstName);
            params.addProperty("last_name", lastName);
            params.addProperty("email", email);
//            params.addProperty("phone", phone);
            params.addProperty("password", pswd);
//            params.addProperty("password_confirmation", pswd);
            params.addProperty("device_token", settingsMain.getDeviceToken());

            Log.d("info Register", "" + params.toString());
            Call<ResponseBody> myCall = restService.postRegister(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                            Log.d("info SignUp Responce", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                if (is_verify_on) {
                                    Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_LONG).show();
                                    Utils.user_id = response.getJSONObject("data").getString("id");
                                    Log.d("info SignUp Data", "" + response.getJSONObject("data"));
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            fragmentManager
                                                    .beginTransaction()
                                                    .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                                                    .replace(R.id.frameContainer,
                                                            new VerifyAccount_Fragment(),
                                                            Utils.VerifyAccount_Fragment).commit();
                                        }
                                    }, 1000);

                                } else {
                                    Log.d("info SignUp Data", "" + response.getJSONObject("data"));
                                    Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                    settingsMain.setUserLogin(response.getJSONObject("data").getString("id"));
                                    settingsMain.setAuthToken(response.getJSONObject("data").getString("access_token"));
                                    settingsMain.setUserEmail(email);
                                    settingsMain.setUserPassword(pswd);
                                    settingsMain.setUserName(firstName + " " + lastName);
                                    settingsMain.setAppOpen(true);
                                    settingsMain.setReinstall(false);
                                    Intent intent = new Intent(getActivity(), ProfileCompleteActivity.class);
                                    startActivity(intent);
                                    activity.overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                                    activity.finish();

                                }
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
                    Log.d("info SignUp error", String.valueOf(t));
                    Log.d("info SignUp error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void adforest_loginToFacebook() {

        if (SettingsMain.isConnectingToInternet(activity)) {
            LoginManager.getInstance().logInWithReadPermissions(this,
                    Arrays.asList("public_profile", "email"));
        } else {
            Toast.makeText(activity, getString(R.string.internet_error),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void getFBStats(AccessToken accessToken) {
        // Application code
        Log.i("tag_Here", "getFb");

        GraphRequest request = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object,
                                            GraphResponse response) {
                        // Application code
                        try {
                            Log.i("tag_Here", response.toString());
                            Log.i("tag", "Obj " + object.toString());

                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.adforest", MODE_PRIVATE).edit();
                            editor.putString("isSocial", "true");
                            editor.apply();

                            adforest_loginSocialMedia(object.getString("email"));

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            if (data != null && data.hasExtra("PHONE_NUMBER") && data.getStringExtra("PHONE_NUMBER") != null) {
                String phoneNumber = data.getStringExtra("PHONE_NUMBER");
                settingsMain.setUserPhone(phoneNumber);
                activity.overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                activity.finish();
//                    mobileNumber = phoneNumber;
//                SharedHelper.putKey(RegisterActivity.this, "mobile", phoneNumber);
//                registerAPI();
            } else {
                // If mobile number is not verified successfully You can hendle according to your requirement.
                Toast.makeText(getActivity(),getString(R.string.message_verify_failed),Toast.LENGTH_SHORT).show();
//                    Intent goToLogin = new Intent(RegisterActivity.this, WelcomeScreenActivity.class);
//                    goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(goToLogin);
//                    finish();
            }
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
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                        }
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

    private void adforest_signInForSociel() {

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
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                        handleSignInResult(googleSignInResult);
                    }
                });
            }
        }
    }

    private void adforest_loginSocialMedia(final String email) {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("email", email);
            params.addProperty("type", "social");
            RestService restService = UrlController.createService(RestService.class, email, "1122", getContext());
            Call<ResponseBody> myCall = restService.postLogin(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info LoginScoial respon", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                Log.d("info", "" + response.getJSONObject("data"));
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();

                                settingsMain.setUserLogin(response.getJSONObject("data").getString("id"));
                                settingsMain.setUserImage(response.getJSONObject("data").getString("profile_img"));
                                settingsMain.setUserName(response.getJSONObject("data").getString("display_name"));
                                settingsMain.setUserPhone(response.getJSONObject("data").getString("phone"));
                                settingsMain.setUserEmail(email);
                                settingsMain.setUserPassword("1122");
                                settingsMain.setAppOpen(false);
                                SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.adforest", MODE_PRIVATE).edit();
                                editor.putString("isSocial", "true");
                                editor.apply();

                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                startActivity(intent);
                                activity.overridePendingTransition(R.anim.right_enter, R.anim.left_out);
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

    public void phoneLogin() {
        Intent intent = new Intent(getActivity(), PhoneNumberActivity.class);
        //Optionally you can add toolbar title
        intent.putExtra("TITLE", getResources().getString(R.string.app_name));
        //Optionally you can pass phone number to populate automatically.
        intent.putExtra("PHONE_NUMBER", "");
        startActivityForResult(intent, APP_REQUEST_CODE);
    }


}
