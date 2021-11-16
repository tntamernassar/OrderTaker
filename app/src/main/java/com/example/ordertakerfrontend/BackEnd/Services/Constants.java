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
     * Restaurant State File,
     * Hold last Restaurant state
     * **/
    public static String RESTAURANT_STATE_FILE = "restaurant";


    public static final double TCP_CHUNK_LENGTH = 500.0;


    public static String DEFAULT_IMAGE_NAME = "default.png";



    public static int NETWORK_DEMON_INTERVAL = 20 * 1000;

}
