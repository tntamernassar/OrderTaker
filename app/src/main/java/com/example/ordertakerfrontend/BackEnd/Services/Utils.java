package com.example.ordertakerfrontend.BackEnd.Services;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ordertakerfrontend.FrontEnd.Menu;
import com.example.ordertakerfrontend.FrontEnd.MenuProduct;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

public class Utils {

    public static String MapToString(Map<Integer, ?> map) {
        StringBuilder mapAsString = new StringBuilder("{\n");
        for (Integer key : map.keySet()) {
            mapAsString.append("\t"+ key + "=" + map.get(key).toString() + ", \n");
        }
        mapAsString.delete(mapAsString.length()-2, mapAsString.length()).append("\n}");
        return mapAsString.toString();
    }

    public static String dateToString(LocalDateTime date){
        return date.getDayOfMonth() + "-" + date.getMonthValue() + "-" + date.getYear() + " " +
                date.getHour() + ":" + date.getMinute();
    }

    public static LocalDateTime stringToDate(String date){
        String[] comps = date.split(" ");
        String[] day_month_year = comps[0].split("-");
        String[] hour_minute = comps[1].split(":");
        return LocalDateTime.of(
                Integer.parseInt(day_month_year[2]),
                Integer.parseInt(day_month_year[1]),
                Integer.parseInt(day_month_year[0]),
                Integer.parseInt(hour_minute[0]),
                Integer.parseInt(hour_minute[1])
        );
    }


    public synchronized static boolean writeToLog(String logEvent){
        return FileManager.appendToFile(Constants.LOG_FILE, logEvent + " At " + Utils.dateToString(LocalDateTime.now()) + "\n");
    }

    public static String getTimeStamp(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return String.valueOf(timestamp.getTime());
    }

    /**
     * true if t1 > t2
     * **/
    public static boolean isBigger(String t1, String t2){
        Timestamp timestamp1 = new Timestamp(Long.parseLong(t1));
        Timestamp timestamp2 = new Timestamp(Long.parseLong(t1));
        return timestamp1.compareTo(timestamp2) > 0;
    }

    public static void encode_menu_to_json(){
        MenuProduct[] pr = Menu.getInstance().getMenuProductList().toArray(new MenuProduct[0]);
        String res = "";
        for(MenuProduct p : pr){
            res += p.toString() + "},\n";
        }

        res = res.substring(0, res.length()-1);
        res = "{\n\t\"Menu\":[\n" + res + "\n\t]\n}";

        FileManager.appendToFile("tmp", res);
    }

    public static void read_file_From_drive(Context context){
    }
}
