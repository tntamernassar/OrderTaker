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
     * UDP BUFFER SIZE
     * **/
    public static final int UDP_BUFFER_SIZE = 10000;

    /**
     * UDP MultiCast Host
     * **/
    public static final String UDP_MULTICAST_HOST = "230.0.0.0";

    /**
     * UDP port
     * **/
    public static final int UDP_PORT = 2000;


    /**
     * UDP repeating time,
     * While udp is not guarantying that the message will be received,
     * we send the message multiple time to lower the chances for loss
     * we assume that the receiver can deal with repeated messages with the version algorithm for example
     * **/
    public static final int UDP_REPEAT = 3;


    /**
     * TCP PORT
     * **/
    public static int TCP_PORT = 2000;



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


    /**
     * Demon Delay Time.
     * **/
    public static final int DEMON_DELAY = 5 * 1000;

    /**
     * The Time Syncing process is on.
     * While syncing, the device only receive syncing responses but don't respond on requests
     * **/
    public static final int SYNCING_TIME = 30 * 1000;

}
