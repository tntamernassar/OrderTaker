package com.example.ordertakerfrontend.BackEnd.Services;

import android.content.Context;

import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;

public class Constants {

    /**
     * Application Context, assigned at runtime
     * The context is used for accessing system resources
     * **/
    public static Context CONTEXT = null;

    /**
     * Application Waitress, assigned at runtime
     * The waitress object is used to interact with the backend
     * **/
    public static Waitress WAITRESS = null;


    /**
     * Log file.
     * **/
    public static String LOG_FILE = "log.txt";

    /**
     * Order History File,
     * The Order History File holds all the orders from all the devices
     * **/
    public static String ORDER_HISTORY_FILE = "orders";

    /**
     * Restaurant State File,
     * Hold last Restaurant state
     * **/
    public static String RESTAURANT_STATE_FILE = "restaurant";




}
