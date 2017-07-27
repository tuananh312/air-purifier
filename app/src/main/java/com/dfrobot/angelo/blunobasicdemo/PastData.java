package com.dfrobot.angelo.blunobasicdemo;

/**
 * Created by Anhnonymouz on 17/6/2017.
 */

public class PastData {
    String userID;
    //String timeStamp;
    String temperature;
    String humidity;
    String voc;
    //Double latitude;
    //Double longtitude;
    public PastData(){}

    public PastData(String userID, String temperature, String humidity, String voc) {
        this.userID = userID;
        //this.timeStamp = timeStamp;
        this.temperature = temperature;
        this.humidity = humidity;
        this.voc = voc;
        //this.latitude = latitude;
        //this.longtitude = longtitude;
    }

    public String getUserID() {
        return userID;
    }


    public String getTemperature() {
        return temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getVoc() {
        return voc;
    }

}
