package com.example.ordertakerfrontend.BackEnd.Services;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

public class FileManager {

    private static String basePath = Constants.CONTEXT.getFilesDir().getAbsolutePath();


    public static String getBasePath() {
        return basePath;
    }

    public static void mkdir(String name){
        File file = new File(getBasePath() + "/" + name);
        if (!file.exists()){
            file.mkdir();
        }
    }

    public static String[] listFiles(String path){
        File directory = new File(path);
        File[] files = directory.listFiles();
        String[] names = new String[files.length];
        int i = 0;
        for(File f : files){
            names[i] = f.getName();
            i++;
        }
        return names;
    }

    public synchronized static boolean writeObject(Object object, String filename){

        try {
            File file = new File(basePath + "/" + filename);
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(object);
            os.close();
            fos.close();
            return true;
        } catch (Exception e) {
            Utils.writeToLog("[ERROR] in FileManager.writeObject " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static Object readObject(String filename){
        try {
            File file = new File(basePath+ "/" + filename);
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream is = new ObjectInputStream(fis);
            Object o =  is.readObject();

            is.close();
            fis.close();
            return o;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean appendToFile(String filename, String logEvent){
        try {
            File file = new File(basePath + "/" + filename);
            if(!file.exists()){
                file.createNewFile();
            }

            FileOutputStream fOut = new FileOutputStream(file, true);
            fOut.write(logEvent.getBytes(StandardCharsets.UTF_8));
            fOut.close();
            return true;
        }catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String readFile(String filename){
        try {

            File file = new File(basePath + "/" + filename);
            if (!file.exists()) {
                return "File not exist";
            }
            FileOutputStream os = null;
            StringBuilder text = new StringBuilder();

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null){
                text.append(line);
                text.append('\n');
            }
            br.close();

            return text.toString();
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean writeBytes(String path, byte[] bytes){
        try {
            File file = new File(path);
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.write(bytes);
            os.close();
            fos.close();
            return true;
        }catch (Exception e){
            return false;
        }

    }
}
