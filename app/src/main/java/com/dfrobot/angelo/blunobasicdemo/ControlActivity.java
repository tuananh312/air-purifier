package com.dfrobot.angelo.blunobasicdemo;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class ControlActivity extends MainActivity {

    private Switch deviceSwitch;
    private Switch uvcSwitch;
    private Button buttonHome;
    private int deviceState;
    private int UVCState;
    public String[] sensorData1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        serialBegin(115200);


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

    public void navigateHomeView(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called

    }

    @Override
    public void onSerialReceived(String receivedString) {							//Once connection data received, this function will be called
//        sensorData = receivedString.split(",");
        Log.d("HIHIHIHI","HIHIHIHI");
        updateTextView1(sensorData);

//		String id = databasePastData.push().getKey();
//		PastData pastdata = new PastData(id,sensorData[1],sensorData[2],sensorData[3]);
//		databasePastData.child(id).setValue(pastdata);
    }

    public void updateTextView1(String[] updates) { //update text view
        System.out.println("LULULULULU");

        deviceState = Integer.parseInt(updates[6]);
        UVCState = Integer.parseInt(updates[8]);

        if(deviceState == 1) {
            deviceSwitch.setChecked(true);
        } else if (deviceState == 0) {
            deviceSwitch.setChecked(false);
        }

        if(UVCState == 1) {
            uvcSwitch.setChecked(true);
        } else if (UVCState == 0) {
            uvcSwitch.setChecked(false);
        }


    }





}
