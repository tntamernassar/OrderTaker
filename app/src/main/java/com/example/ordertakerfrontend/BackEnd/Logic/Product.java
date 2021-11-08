package com.example.ordertakerfrontend.BackEnd.Logic;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Objects;

public interface Product extends Serializable {

    default JSONObject toJSON(){
        return null;
    }

}
