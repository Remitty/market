package com.brian.market.cart;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brian.market.databases.User_Cart_DB;
import com.brian.market.modelsList.ProductDetails;
import com.brian.market.payment.ShippingActivity;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.brian.market.R;
import com.brian.market.home.adapter.ItemMainAllLocationAds;
import com.brian.market.modelsList.homeCatListModel;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {

    RecyclerView recyclerView;
    ItemMainAllLocationAds itemMainAllLocationAds;
    ArrayList<homeCatListModel> cartList = new ArrayList<>();
    SettingsMain settingsMain;
    RestService restService;

    private Context context;
    private JSONObject responseData;

    CartItemAdapter cartItemsAdapter;
    static List<ProductDetails> cartItemsList;
    ArrayList<Integer> recents;

    User_Cart_DB user_cart_db = new User_Cart_DB();
    private LinearLayout cart_view;
    private LinearLayout cart_view_empty;
    private RecyclerView cart_items_recycler;
    private Button btnCheckout;
    private TextView cart_total_price;

    private double cartSubTotal;
    private double cartTotalPrice;

    public CartFragment() {
        // Required empty public constructor
    }

    public static CartFragment newInstance() {
        CartFragment fragment = new CartFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();
        settingsMain = new SettingsMain(context);

        restService = UrlController.createService(RestService.class);

        cart_total_price = view.findViewById(R.id.cart_total_price);

        cart_view = view.findViewById(R.id.cart_view);
        cart_view_empty = view.findViewById(R.id.cart_view_empty);

        cart_items_recycler = view.findViewById(R.id.cart_list);

        cartItemsList = new ArrayList<>();
        int size = user_cart_db.getUserCartIDs().size();
        recents = user_cart_db.getUserCartIDs();

        if (recents.size() < 1) {
            cart_view.setVisibility(View.GONE);
            cart_view_empty.setVisibility(View.VISIBLE);
        }
        else {
            cart_view.setVisibility(View.VISIBLE);
            cart_view_empty.setVisibility(View.GONE);

            cartItemsList = user_cart_db.getCartItems();

        }

        // Initialize the AddressListAdapter for RecyclerView
        cartItemsAdapter = new CartItemAdapter(getContext(), cartItemsList, CartFragment.this);

        // Set the Adapter and LayoutManager to the RecyclerView
        cart_items_recycler.setAdapter(cartItemsAdapter);
        cart_items_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        cartItemsAdapter.notifyDataSetChanged();

        btnCheckout = view.findViewById(R.id.btn_checkout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(settingsMain.getAppOpen()) {
                    if (cartItemsList.size() != 0) {
                        proceedCheckout();
                    }
                    else{
                        Toast.makeText(context, "You didn't add any product into cart.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(context, "You can't order. Please login.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateCart();

    }

    private void getCartData() {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

                SettingsMain.showDilog(getActivity());
                JsonObject object = new JsonObject();
                object.addProperty("cartlist", settingsMain.getCart().toString());
            Call<ResponseBody> myCall = restService.mycart(object, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        Log.d("info cart Responce", "" + responseObj.toString());

                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                responseData = response.getJSONObject("data");
                                Log.d("home data", responseData.toString());

//                                addCartProducts(response.body());
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
                    Log.d("info HomeGet error", String.valueOf(t));
//                    Timber.d(String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });

        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
        }
    }

    //*********** Adds Products returned from the Server to the RecentViewedList ********//
    public void addCartProducts(ProductDetails product) {

        cartItemsList.add(product);
        cartItemsAdapter.notifyDataSetChanged();
    }


    public static int GetItemQTY(int product_id){
        User_Cart_DB user_cart_db = new User_Cart_DB();
        return user_cart_db.getUserCartQty(product_id);

    }

    public static void UpdateCartItemQty(int id, int qty){

        User_Cart_DB user_cart_db = new User_Cart_DB();
        user_cart_db.updateCartItem(id,qty);

    }

    //*********** Static method to Add the given Item to User's Cart ********//

    public static void AddCartItem(CartDetails cartDetails) {
        User_Cart_DB user_cart_db = new User_Cart_DB();
//        user_cart_db.addCartItem(cartDetails);
    }


    //*********** Static method to Add the given Item to User's Cart ********//

    public static void AddCartItemID(int productID,int product_qty) {
        User_Cart_DB user_cart_db = new User_Cart_DB();
        user_cart_db.insertCartItem(productID,product_qty);
    }



    //*********** Static method to Update the given Item in User's Cart ********//

    public static void UpdateCartItem(ProductDetails cartDetails) {
        User_Cart_DB user_cart_db = new User_Cart_DB();
        user_cart_db.updateCartItem(cartDetails);
    }


    //*********** Static method to Delete the given Item from User's Cart ********//

    public static void DeleteCartItem(int cart_item_id) {
        User_Cart_DB user_cart_db = new User_Cart_DB();
        user_cart_db.deleteCartItem(cart_item_id);
    }

    //*********** Static method to Delete the given Item from User's Cart ********//

    public static void DeleteCartItemID(int product_id) {
        User_Cart_DB user_cart_db = new User_Cart_DB();
        user_cart_db.deleteCartItemID(product_id);
    }

    //*********** Static method to Clear User's Cart ********//

    public static void ClearCart() {
        User_Cart_DB user_cart_db = new User_Cart_DB();
        user_cart_db.clearCart();

        for (int i=0; i<getCartSize(); i++){
            DeleteCartItemID(cartItemsList.get(i).getId());
        }
        cartItemsList.clear();
    }


    //*********** Static method to get total number of Items in User's Cart ********//

    public static int getCartSize() {

        User_Cart_DB user_cart_db = new User_Cart_DB();
        int cartSize = user_cart_db.getUserCartIDs().size();
        return user_cart_db.getUserCartIDs().size();
    }

    //*********** Static method to check if the given Product is already in User's Cart ********//

    public static boolean checkCartHasProduct(int cart_item_id) {
        User_Cart_DB user_cart_db = new User_Cart_DB();
        return user_cart_db.getCartItemsIDs().contains(cart_item_id);
//        return false;
    }

    public void updateCart() {

        double total = 0;
        double subtotal = 0;
        double totalDiscount = 0;

        // Calculate Cart's total Price
        for (int i=0;  i<cartItemsList.size();  i++) {
            subtotal += Double.parseDouble(cartItemsList.get(i).getPrice())*GetItemQTY(cartItemsList.get(i).getId());
        }

        for (int x=0;  x<cartItemsList.size();  x++) {
            double setTotalPrice = Double.parseDouble(cartItemsList.get(x).getPrice())*GetItemQTY(cartItemsList.get(x).getId());
            cartItemsList.get(x).setTotalPrice(""+setTotalPrice);
        }

        cartItemsAdapter.notifyDataSetChanged();

        total = subtotal - totalDiscount;

        cartTotalPrice  = total;
        cartSubTotal  = subtotal;

        setCartTotal();

    }

    public void setCartTotal() {
        cart_total_price.setText(new DecimalFormat("$#0.00").format(cartTotalPrice));
    }

    //*********** Change the Layout View of My_Cart Fragment based on Cart Items ********//

    public void updateCartView(int cartListSize) {

        // Check if Cart has some Items
        if (cartListSize != 0) {
            cart_view.setVisibility(View.VISIBLE);
            cart_view_empty.setVisibility(View.GONE);
        } else {
            cart_view.setVisibility(View.GONE);
            cart_view_empty.setVisibility(View.VISIBLE);
        }
    }

    //*********** Set Order Details and Proceed to Checkout ********//

    private void proceedCheckout() {

       startActivity(new Intent(getActivity(), ShippingActivity.class));

    }


}
