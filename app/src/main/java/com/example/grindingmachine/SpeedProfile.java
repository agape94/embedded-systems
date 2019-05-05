package com.example.grindingmachine;

import java.io.Serializable;

public class SpeedProfile implements Serializable {
    private String mTitle;
    private int mSpeed;

    SpeedProfile (){
        mTitle = "";
        mSpeed = Constants.MINIMUM_RPM_VALUE;
    }

    SpeedProfile(String title, int speed){
        mTitle = title;
        if(speed >= Constants.MINIMUM_RPM_VALUE && speed <= Constants.MAXIMUM_RPM_VALUE){
            mSpeed = speed;
        }else {
            mSpeed = Constants.MINIMUM_RPM_VALUE;
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public int getSpeed() {
        return mSpeed;
    }

    public void setSpeed(int speed) {
        if(speed >= Constants.MINIMUM_RPM_VALUE && speed <= Constants.MAXIMUM_RPM_VALUE){
            mSpeed = speed;
        }else {
            mSpeed = Constants.MINIMUM_RPM_VALUE;
        }
    }
}
