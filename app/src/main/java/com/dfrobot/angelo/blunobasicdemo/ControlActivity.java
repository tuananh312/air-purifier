package com.dfrobot.angelo.blunobasicdemo;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class ControlActivity extends BlunoLibrary {

    private Switch deviceSwitch;
    private Switch uvcSwitch;
    private Button buttonHome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);


        buttonHome = (Button) findViewById(R.id.buttonHome);
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateHomeView();
            }
        });

        deviceSwitch = (Switch) findViewById(R.id.switch1);
        deviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //send data number turn on device
                    serialSend("/");
                    Toast.makeText(getApplicationContext(), "Turning on device", Toast.LENGTH_SHORT).show();
                } else {
                    //send data number turn off device
                    serialSend("0");
                    Toast.makeText(getApplicationContext(), "Turning off device", Toast.LENGTH_SHORT).show();
                }
            }
        });

        uvcSwitch = (Switch) findViewById(R.id.switch2);
        uvcSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //send data number turn on UVC
                    serialSend("4");
                    Toast.makeText(getApplicationContext(), "Turning on UVC", Toast.LENGTH_SHORT).show();
                } else {
                    //send data number turn off UVC
                    serialSend("5");
                    Toast.makeText(getApplicationContext(), "Turning off UVC", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
        switch (theConnectionState) {											//Four connection state
            case isConnected:
                break;
            case isConnecting:
                break;
            case isToScan:
                break;
            case isScanning:
                break;
            case isDisconnecting:
                break;
            default:
                break;
        }
    }

    @Override
    public void onSerialReceived(String receivedString) {							//Once connection data received, this function will be called
        try{

            String[] sensorData = receivedString.split(",");
            Thread.sleep(1000);
            //updateTextView(sensorData);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void navigateHomeView(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }





}
