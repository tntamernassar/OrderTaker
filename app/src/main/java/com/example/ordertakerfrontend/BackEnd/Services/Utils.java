package com.example.ordertakerfrontend.BackEnd.Services;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.InputType;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.ordertakerfrontend.FrontEnd.Menus.Menu;
import com.example.ordertakerfrontend.FrontEnd.Menus.MenuProduct;
import com.example.ordertakerfrontend.FrontEnd.Popups.TextInputCallback;
import com.example.ordertakerfrontend.FrontEnd.Popups.YesNoCallbacks;
import com.example.ordertakerfrontend.MainActivity;
import com.example.ordertakerfrontend.R;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

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

    public static Bitmap drawMultilineTextToBitmap(String gText) {
        Resources resources = Constants.CONTEXT.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = Bitmap.createBitmap(700, 300, Bitmap.Config.ARGB_8888);

        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);

        // new antialiased Paint
        TextPaint paint=new TextPaint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.rgb(61, 61, 61));
        // text size in pixels
        paint.setTextSize((int) (14 * scale));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // set text width to canvas width minus 16dp padding
        int textWidth = canvas.getWidth() - (int) (16 * scale);

        // init StaticLayout for text
        StaticLayout textLayout = new StaticLayout(
                gText, paint, textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

        // get height of multiline text
        int textHeight = textLayout.getHeight();

        // get position of text's top left corner
        float x = (bitmap.getWidth() - textWidth)/2;
        float y = (bitmap.getHeight() - textHeight)/2;

        // draw text to the Canvas center
        canvas.save();
        canvas.translate(x, y);
        textLayout.draw(canvas);
        canvas.restore();

        return bitmap;
    }

    public static Bitmap textAsBitmap(String text, float textSize) {
        /**
         *  see
         *  https://snipplr.com/view/73714/android-draw-text-to-dynamically-sized-bitmap
         *  for creating height dynamic bitmap
         * **/
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        float baseline = -paint.ascent();
        int width = (int) 500;
        int height = (int) 70;
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawColor(Color.WHITE);
        canvas.drawText(text, 370, baseline, paint);
        return image;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String generateString(int targetStringLength) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }


    public static void YesNoDialog(Context context, String title, YesNoCallbacks callbacks){
        // Build an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set a title for alert dialog
        builder.setTitle(title);


        // Set the alert dialog yes button click listener
        builder.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callbacks.yes();
            }
        });

        // Set the alert dialog no button click listener
        builder.setNegativeButton("لا", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callbacks.no();
            }
        });

        AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();
    }

    public static void AcquireInputDialog(Context context, String title, TextInputCallback callback){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("تم", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String text = input.getText().toString();
                callback.onInput(text);
            }
        });
        builder.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public static void ShowSuccessAlert(final Context context, String heading) {
        SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
        dialog.setTitleText(heading);
        dialog.show();
        Button btn = (Button) dialog.findViewById(R.id.confirm_button);
        btn.setText("اوكي");
    }

    public static void ShowErrorAlert(final Context context, String heading) {
        SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
        dialog.setTitleText(heading);
        dialog.show();
        Button btn = (Button) dialog.findViewById(R.id.confirm_button);
        btn.setText("اوكي");
    }

    public static void ShowWarningAlert(final Context context, String heading) {
        SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        dialog.setTitleText(heading);
        dialog.show();
        Button btn = (Button) dialog.findViewById(R.id.confirm_button);
        btn.setText("اوكي");
    }

}
