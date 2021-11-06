package com.example.ordertakerfrontend.BackEnd.Services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import com.example.ordertakerfrontend.Network.NetworkManager.NetworkAdapter;
import com.example.ordertakerfrontend.Network.NetworkMessages.Out.TabletImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class ImagesManager {

    private static ConcurrentHashMap<String, ImageCollector> imageCollectorHashMap = new ConcurrentHashMap<>();

    public static void sendImageInChucks(String name, String base64){
        int chunkSize = (int)Constants.TCP_CHUNK_LENGTH;
        int chunksNeeded =  (int)Math.ceil(base64.length() / chunkSize);

        for(int i = 0; i < chunksNeeded; i++){
            int chunkNumber = i + 1;
            String chunk = base64.substring(i * chunkSize, Math.min(i*chunkSize + chunkSize, base64.length()));
            NetworkAdapter.getInstance().send(new TabletImage(name, chunk, chunksNeeded, chunkNumber));
        }

    }

    public static void addChunk(String fileName, String base64, int chunks, int chunkNumber){
        if (!imageCollectorHashMap.containsKey(fileName)){
            imageCollectorHashMap.put(fileName, new ImageCollector(chunks));
        }

        imageCollectorHashMap.get(fileName).addChunk(chunkNumber, base64);
    }

    public static String collectImage(String fileName){
        String s = imageCollectorHashMap.get(fileName).collectImage();
        imageCollectorHashMap.get(fileName).restart();
        return s;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String makeFileName(){
        String[] files = listImages();

        String randomStr = Utils.generateString(10);
        while (Arrays.asList(files).contains(randomStr + ".png")){
            randomStr = Utils.generateString(10);
        }

        return randomStr + ".png";
    }

    public static String[] listImages(){
        String path = FileManager.getBasePath() + "/images/";
        return FileManager.listFiles(path);
    }

    public static String imageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);

        return encoded;
    }

    public static Bitmap Base64ToImage(String name){
        String path = FileManager.getBasePath() + "/images/" + name;
        File imgFile = new File(path);
        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        return bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean saveImage(String name, String base64) {
        if (base64 != null) {
            try {
                String path = FileManager.getBasePath() + "/images/" + name;
                byte[] decodedString = java.util.Base64.getDecoder().decode(base64);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                File file = new File(path);
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }else {
            return false;
        }
    }

    public static void deleteImage(String name){
        if(!name.equals(Constants.DEFAULT_IMAGE_NAME)) {
            String path = FileManager.getBasePath() + "/images/" + name;
            File file = new File(path);
            file.delete();
        }
    }

}
