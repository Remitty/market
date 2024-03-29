package com.brian.market.databases;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.brian.market.App;

/**
 * DB_Handler contains Database Version and Name and creates/upgrades all tables in the Database
 **/


public class DB_Handler extends SQLiteOpenHelper {

    // Database Version
    private static final int DB_VERSION = 1;

    // Database Name
    private static final String DB_NAME = "User_DB";


    public DB_Handler() {
        super(App.getContext(), DB_NAME, null, DB_VERSION);
    }


    
    //*********** Creating Database ********//
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Creating Tables
        db.execSQL(User_Recents_DB.createTable());
        db.execSQL(User_Favorites_DB.createTable());
        
        db.execSQL(User_Cart_DB.createTableCart());
        db.execSQL(User_Cart_DB.createTableIDS());
    }


    
    //*********** Upgrading Database ********//
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + User_Recents_DB.TABLE_RECENTS);
        db.execSQL("DROP TABLE IF EXISTS " + User_Favorites_DB.TABLE_FAVORITES);
        
        db.execSQL("DROP TABLE IF EXISTS " + User_Cart_DB.TABLE_CART);
        db.execSQL("drop table if exists " + User_Cart_DB.TABLE_CART_IDS);

        // Create tables again
        onCreate(db);
    }

}
