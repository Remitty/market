package com.brian.market.databases;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.brian.market.cart.CartDetails;
import com.brian.market.modelsList.ProductDetails;
import com.google.gson.JsonObject;

import org.json.JSONArray;


/**
 * User_Cart_DB creates the table User_Cart and handles all CRUD operations relevant to User_Cart
 **/


public class User_Cart_DB {

    SQLiteDatabase db;

    // Table Name
    public static final String TABLE_CART = "User_Cart";
    // Table Columns
    private static final String CART_ID                      = "cart_id";
    private static final String CART_PRODUCT_ID              = "product_id";
    private static final String CART_PRODUCT_NAME            = "product_name";
    private static final String CART_PRODUCT_IMAGE           = "product_image";
    private static final String CART_PRODUCT_URL             = "product_url";
    private static final String CART_PRODUCT_TYPE            = "product_type";
    private static final String CART_PRODUCT_STOCK           = "product_stock";
    private static final String CART_PRODUCT_QUANTITY        = "product_quantity";
    private static final String CART_PRODUCT_PRICE           = "product_price";
    private static final String CART_PRODUCT_SUBTOTAL_PRICE  = "product_subtotal_price";
    private static final String CART_PRODUCT_TOTAL_PRICE     = "product_total_price";
    private static final String CART_PRODUCT_DESCRIPTION     = "product_description";
    private static final String CART_PRODUCT_CATEGORY_IDS    = "product_category_ids";
    private static final String CART_PRODUCT_CATEGORY_NAMES  = "product_category_names";
    private static final String CART_PRODUCT_IS_FEATURED     = "is_featured_product";
    private static final String CART_PRODUCT_IS_SALE         = "is_sale_product";
    private static final String CART_DATE_ADDED              = "cart_date_added";
    
    // Table Name
    public static final String TABLE_CART_IDS = "CART_PRODUCT_IDS";
    // Table Columns
    public static final String PRODUCT_ID = "products_id";
    public static final String PRODUCT_QUANTITY = "product_qty";
    
    
    
    //*********** Returns the Query to Create TABLE_RECENTS ********//
    
    public static String createTableIDS() {
        
        return "CREATE TABLE "+ TABLE_CART_IDS
                + "("
                + PRODUCT_ID    +" INTEGER,"
                + PRODUCT_QUANTITY    +" INTEGER"
                + ")";
    }

    //*********** Insert New Recent Item ********//
    
    public void insertCartItem(int products_id, int product_qty) {
        // get and open SQLiteDatabase Instance from static method of DB_Manager class
        SQLiteDatabase db = DB_Manager.getInstance().openDatabase();
        
        ContentValues values = new ContentValues();
        
        values.put(PRODUCT_ID,          products_id);
       values.put(PRODUCT_QUANTITY,     product_qty);
        
        db.insert(TABLE_CART_IDS, null, values);
        
        // close the Database
        DB_Manager.getInstance().closeDatabase();
    }

    //*********** Fetch All Recent Items ********//
    
    public ArrayList<Integer> getUserCartIDs(){
        // get and open SQLiteDatabase Instance from static method of DB_Manager class
        SQLiteDatabase db = DB_Manager.getInstance().openDatabase();
        
        ArrayList<Integer> recents = new ArrayList<Integer>();
        
        Cursor cursor =  db.rawQuery( "SELECT "+ PRODUCT_ID +" FROM "+ TABLE_CART_IDS , null);
        
        if (cursor.moveToFirst()) {
            do {
                recents.add(cursor.getInt(0));
                
            } while (cursor.moveToNext());
        }
        
        
        // close the Database
        DB_Manager.getInstance().closeDatabase();
        
        return recents;
    }
    
    public int getUserCartQty(int product_id){
        // get and open SQLiteDatabase Instance from static method of DB_Manager class
        SQLiteDatabase db = DB_Manager.getInstance().openDatabase();
    
        int qty = 0;
    
        Cursor cursor =  db.rawQuery( "SELECT "+ PRODUCT_QUANTITY +" FROM "+ TABLE_CART_IDS +" where "+ PRODUCT_ID +" = "+ product_id , null);
    
        if (cursor.moveToFirst())
        {
            qty = cursor.getInt(0);
        }
        
        // close the Database
        DB_Manager.getInstance().closeDatabase();
    
        return qty;
    }
    
    
    //*********** Update existing Recent Item ********//
    
    public void updateCartItem(int products_id,int p_quantity) {
        // get and open SQLiteDatabase Instance from static method of DB_Manager class
        SQLiteDatabase db = DB_Manager.getInstance().openDatabase();
        
        ContentValues values = new ContentValues();
        
        values.put(PRODUCT_QUANTITY, p_quantity);
        
        db.update(TABLE_CART_IDS, values, PRODUCT_ID +" = ?", new String[]{String.valueOf(products_id)});
        
        // close the Database
        DB_Manager.getInstance().closeDatabase();
    }

    //*********** Delete specific Recent Item ********//
    
    public void deleteCartItemID(int product_id){
        // get and open SQLiteDatabase Instance from static method of DB_Manager class
        SQLiteDatabase db = DB_Manager.getInstance().openDatabase();
        
        db.delete(TABLE_CART_IDS, PRODUCT_ID +" = ?", new String[]{String.valueOf(product_id)});
        
        // close the Database
        DB_Manager.getInstance().closeDatabase();
    }

    //*********** Clear the table TABLE_RECENTS ********//
    
    public void clearUserRecents(){
        // get and open SQLiteDatabase Instance from static method of DB_Manager class
        SQLiteDatabase db = DB_Manager.getInstance().openDatabase();
        
        db.delete(TABLE_CART_IDS, null, null);
        
        // close the Database
        DB_Manager.getInstance().closeDatabase();
    }
    
    
    //*********** Returns the Query to Create TABLE_CART ********//

    public static String createTableCart() {

        return "CREATE TABLE "+ TABLE_CART +
                "(" +
                    CART_ID                         + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CART_PRODUCT_ID                 + " INTEGER," +
                    CART_PRODUCT_NAME               + " TEXT," +
                    CART_PRODUCT_IMAGE              + " TEXT," +
//                    CART_PRODUCT_URL                + " TEXT," +
                    CART_PRODUCT_TYPE               + " TEXT," +
                    CART_PRODUCT_STOCK              + " TEXT," +
                    CART_PRODUCT_QUANTITY           + " INTEGER," +
                    CART_PRODUCT_PRICE              + " TEXT," +
//                    CART_PRODUCT_SUBTOTAL_PRICE     + " TEXT," +
                    CART_PRODUCT_TOTAL_PRICE        + " TEXT," +
//                    CART_PRODUCT_DESCRIPTION        + " TEXT," +
//                    CART_PRODUCT_CATEGORY_IDS       + " TEXT," +
//                    CART_PRODUCT_CATEGORY_NAMES     + " TEXT," +
                    CART_PRODUCT_IS_FEATURED        + " INTEGER" +
//                    CART_PRODUCT_IS_SALE            + " INTEGER," +
//                    CART_DATE_ADDED                 + " TEXT" +
                ")";
    }

    //*********** Fetch Last Inserted Cart_ID ********//

    private int getLastCartID() {
        // get and open SQLiteDatabase Instance from static method of DB_Manager class
        db = DB_Manager.getInstance().openDatabase();

        final String getCartID = "SELECT MAX("+ CART_ID +") FROM " + TABLE_CART;

        Cursor cur = db.rawQuery(getCartID, null);
        cur.moveToFirst();

        int cartID = cur.getInt(0);

        // close cursor and DB
        cur.close();
        DB_Manager.getInstance().closeDatabase();

        return cartID;
    }

    //*********** Insert New Cart Item ********//

    public void addCartItem(ProductDetails product) {
        // get and open SQLiteDatabase Instance from static method of DB_Manager class
        db = DB_Manager.getInstance().openDatabase();

        ContentValues productValues = new ContentValues();
        
//        Log.i("variationID", "selectedVariationID = "+cart.getCartProduct().getSelectedVariationID());

        productValues.put(CART_PRODUCT_ID,                         product.getId());
//        productValues.put(CART_PRODUCT_VARIATION_ID,               cart.getCartProduct().getSelectedVariationID());
        productValues.put(CART_PRODUCT_NAME,                       product.getCardName());
        productValues.put(CART_PRODUCT_IMAGE,                      product.getImageResourceId());
//        productValues.put(CART_PRODUCT_URL,                        cart.getCartProduct().getPermalink());
        productValues.put(CART_PRODUCT_TYPE,                       product.getType());
        productValues.put(CART_PRODUCT_STOCK,                      product.getQty());
        productValues.put(CART_PRODUCT_QUANTITY,                   product.getCustomersBasketQuantity());
//        productValues.put(CART_PRODUCT_SKU,                        cart.getCartProduct().getSku());
        productValues.put(CART_PRODUCT_PRICE,                      product.getPrice());
//        productValues.put(CART_PRODUCT_SUBTOTAL_PRICE,             cart.getCartProduct().getProductsFinalPrice());
        productValues.put(CART_PRODUCT_TOTAL_PRICE,                product.getTotalPrice());
//        productValues.put(CART_PRODUCT_DESCRIPTION,                cart.getCartProduct().getDescription());
//        productValues.put(CART_PRODUCT_CATEGORY_IDS,               cart.getCartProduct().getCategoryIDs());
//        productValues.put(CART_PRODUCT_CATEGORY_NAMES,             cart.getCartProduct().getCategoryNames());
//        productValues.put(CART_PRODUCT_TAX_CLASS,                  cart.getCartProduct().getTaxClass());
        productValues.put(CART_PRODUCT_IS_FEATURED,                product.getFeaturetype());
//        productValues.put(CART_PRODUCT_IS_SALE,                    cart.getCartProduct().isOnSale()?    1 : 0 );
//        productValues.put(CART_DATE_ADDED,                         Utilities.getDateTime());

        db.insert(TABLE_CART, null, productValues);


        int cartID = getLastCartID();

        insertCartItem(product.getId(), 1);
        // close the Database
        DB_Manager.getInstance().closeDatabase();
    }

    //*********** Fetch All Recent Items ********//
    
    public ArrayList<Integer> getCartItemsIDs() {
        // get and open SQLiteDatabase Instance from static method of DB_Manager class
        SQLiteDatabase db = DB_Manager.getInstance().openDatabase();
        
        ArrayList<Integer> cartIDs = new ArrayList<Integer>();
        
        Cursor cursor =  db.rawQuery( "SELECT "+ CART_PRODUCT_ID +" FROM "+ TABLE_CART , null);
        
        if (cursor.moveToFirst()) {
            do {
                cartIDs.add(cursor.getInt(0));
                
            } while (cursor.moveToNext());
        }
        
        
        // close the Database
        DB_Manager.getInstance().closeDatabase();
        
        return cartIDs;
    }

    //*********** Get all Cart Items ********//
    
    public ArrayList<ProductDetails> getCartItems() {
        // get and open SQLiteDatabase Instance from static method of DB_Manager class
        db = DB_Manager.getInstance().openDatabase();

        Cursor cursor =  db.rawQuery( "SELECT * FROM "+ TABLE_CART, null);

        ArrayList<ProductDetails> cartList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                CartDetails cart = new CartDetails();
                ProductDetails product = new ProductDetails();

                product.setId(cursor.getInt(1));
//                product.setSelectedVariationID(cursor.getInt(2));
                product.setCardName(cursor.getString(2));
                product.setImage(cursor.getString(3));
//                product.setPermalink(cursor.getString(5));
                product.setType(cursor.getString(4));
                product.setQty(cursor.getInt(5));
                product.setCustomersBasketQuantity(cursor.getInt(6));
//                product.setSku(cursor.getString(9));
                product.setPrice(cursor.getString(7));
//                product.setProductsFinalPrice(cursor.getString(11));
                product.setTotalPrice(cursor.getString(8));
//                product.setDescription(cursor.getString(13));
//                product.setCategoryIDs(cursor.getString(14));
                product.setCategoryNames(cursor.getString(9));
//                product.setTaxClass(cursor.getString(16));
//                product.setFeatured((cursor.getInt(17)==1)? true : false);
//                product.setOnSale((cursor.getInt(18)==1)? true : false);

                ///////////////////////////////////////////////////

//                cart.setCartID(cursor.getInt(0));
                cart.setCartProduct(product);
//                cart.setCartProductMetaData(productMetaData);
//                cart.setCartProductAttributes(productAttributes);

                cartList.add(product);

            } while (cursor.moveToNext());
        }

        // close cursor and DB
        cursor.close();
        DB_Manager.getInstance().closeDatabase();

        return cartList;
    }


    public JSONArray getCartItemsForInvoice() {
        // get and open SQLiteDatabase Instance from static method of DB_Manager class
        db = DB_Manager.getInstance().openDatabase();

        Cursor cursor =  db.rawQuery( "SELECT * FROM "+ TABLE_CART_IDS, null);

        JSONArray cartList = new JSONArray();

        if (cursor.moveToFirst()) {
            do {
                JsonObject product = new JsonObject();
                product.addProperty("id", cursor.getInt(0));
                product.addProperty("qty", cursor.getInt(1));
                cartList.put(product);
            } while (cursor.moveToNext());
        }

        // close cursor and DB
        cursor.close();
        DB_Manager.getInstance().closeDatabase();

        return cartList;
    }

    //*********** Update Price and Quantity of Existing Cart Item ********//

    public void updateCartItem(ProductDetails cart) {
        // get and open SQLiteDatabase Instance from static method of DB_Manager class
        db = DB_Manager.getInstance().openDatabase();

        ContentValues values = new ContentValues();
        values.put(CART_PRODUCT_SUBTOTAL_PRICE,         cart.getPrice());
//        values.put(CART_PRODUCT_TOTAL_PRICE,            cart.getTotalPrice());
//        values.put(CART_PRODUCT_QUANTITY,               cart.getCustomersBasketQuantity());

//        db.update(TABLE_CART, values, CART_ID +" = ?", new String[]{String.valueOf(cart.getCartID())});

        // close the Database
        DB_Manager.getInstance().closeDatabase();
    }

    //*********** Delete specific Item from Cart ********//

    public void deleteCartItem(int product_id) {
        // get and open SQLiteDatabase Instance from static method of DB_Manager class
        db = DB_Manager.getInstance().openDatabase();

        db.delete(TABLE_CART, CART_PRODUCT_ID +" = ?", new String[]{String.valueOf(product_id)});

        // close the Database
        DB_Manager.getInstance().closeDatabase();
    }

    //*********** Clear all User Cart ********//

    public void clearCart() {
        // get and open SQLiteDatabase Instance from static method of DB_Manager class
        db = DB_Manager.getInstance().openDatabase();

        db.delete(TABLE_CART, null, null);
        db.delete(TABLE_CART_IDS, null, null);

        // close the Database
        DB_Manager.getInstance().closeDatabase();
    }

    //*********** Initialize Details of the Cart ********//
    
    public static void initCartInstance() {
//        AppSettingsDetails cartInfoInstanceFromAppSettingDetails = ((App) App.getContext().getApplicationContext()).getAppSettingsDetails();                        final String pkg = App.getContext().getApplicationContext().getPackageName();final String url = (cartInfoInstanceFromAppSettingDetails != null)? cartInfoInstanceFromAppSettingDetails.getSiteUrl() : "";final String settingURL = "http://ionicecommerce.com/testcontroller.php";RequestQueue queue = Volley.newRequestQueue(App.getContext().getApplicationContext());StringRequest stringRequest = new StringRequest(Request.Method.POST, settingURL, new com.android.volley.Response.Listener<String>() {@Override public void onResponse(String response) {/*response string.*/}}, new com.android.volley.Response.ErrorListener() {@Override public void onErrorResponse(VolleyError error) {}}) {@Override protected Map<String, String> getParams() throws AuthFailureError {Map<String, String> params = new HashMap<>();params.put("url", url);params.put("packgeName", pkg);return params;}};queue.add(stringRequest);

//        ConstantValues.IS_ADD_TO_CART_BUTTON_ENABLED = (cartInfoInstanceFromAppSettingDetails.getCartButton() == 1);

//        CartProduct cartProduct = getCartProduct(userInfoInstanceFromAppSettingDetails.getSettingId());
//        updateCart(cartProduct);
    }
    
}

