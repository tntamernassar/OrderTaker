package com.example.ordertakerfrontend.BackEnd.Services;

import java.util.concurrent.ConcurrentHashMap;

public class ImageCollector {

    private int chunks;
    private ConcurrentHashMap<Integer, String> Base64chunks;

    public ImageCollector(int chunks){
        this.chunks = chunks;
        this.Base64chunks = new ConcurrentHashMap<>();
    }

    public void addChunk(int number, String base64){
        Base64chunks.put(number, base64);
    }


    public String collectImage(){
        StringBuilder stringBuilder = new StringBuilder("");
        for(int i = 1; i < chunks; i++){
            String b = Base64chunks.get(i);
            if(b != null){
                stringBuilder.append(b);
            }
        }
        return stringBuilder.toString();
    }

    public void restart(){
        this.Base64chunks = new ConcurrentHashMap<>();
    }

}
