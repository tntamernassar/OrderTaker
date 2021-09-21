package com.example.ordertakerfrontend.BackEnd.Logic.NetworkNotification;

public class HealthCheck extends NetworkNotification {

    public HealthCheck(){
        super("");
    }

    @Override
    public boolean ignore() {
        return true;
    }

    @Override
    public String toString() {
        return "HealthCheck";
    }
}
