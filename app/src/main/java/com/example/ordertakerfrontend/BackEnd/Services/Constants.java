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
    public static final String LOG_FILE = "log.txt";


    /**
     * Restaurant State File,
     * Hold last Restaurant state
     * **/
    public static final String RESTAURANT_STATE_FILE = "restaurant";


    /**
     * TCP Chunk length used while chunking a large message (e.g: image)
     * each chunk size will be as stated in this variable
     * **/
    public static final double TCP_CHUNK_LENGTH = 500.0;


    /**
     * Name of the default image that is used for products
     * **/
    public static final String DEFAULT_IMAGE_NAME = "default.png";


    /**
     * The interval that the network demon is checking the health of the connection
     * **/
    public static final int NETWORK_DEMON_INTERVAL = 20 * 1000;

}
