package com.dfrobot.angelo.blunobasicdemo;

import android.app.Application;

/**
 * Created by Anhnonymouz on 21/7/2017.
 */

public class BaseApplication extends Application {
    public BlunoLibrary blunoLibrary;
    @Override
    public void onCreate()
    {
        super.onCreate();
        blunoLibrary = new BlunoLibrary() {
            @Override
            public void onConectionStateChange(connectionStateEnum theconnectionStateEnum) {

            }

            @Override
            public void onSerialReceived(String theString) {

            }
        };
    }

}
