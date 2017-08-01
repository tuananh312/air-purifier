package com.dfrobot.angelo.blunobasicdemo;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class ControlActivity extends MainActivity {

    private Switch deviceSwitch;
    private Switch uvcSwitch;
    private Button buttonHomeControl;
    private int deviceState = 1;
    private int UVCState = 0;
    private int fanSpeedState = 0;
    private RadioButton fanspeed1;
    private RadioButton fanspeed2;
    private RadioButton fanspeed3;
    private RadioGroup fanSpeedRadioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        serialBegin(115200);




        buttonHomeControl = (Button) findViewById(R.id.buttonHomeControl);
        buttonHomeControl.setOnClickListener(new View.OnClickListener() {
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

        fanspeed1 = (RadioButton) findViewById(R.id.radioButton);
        fanspeed1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonClicked(view);
            }
        });

        fanspeed2 = (RadioButton) findViewById(R.id.radioButton2);
        fanspeed2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonClicked(view);
            }
        });

        fanspeed3 = (RadioButton) findViewById(R.id.radioButton3);
        fanspeed3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonClicked(view);
            }
        });

        fanSpeedRadioGroup = (RadioGroup) findViewById(R.id.fanSpeedRadioGroup);

        updateTextView1(sensorData);

    }



    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called

    }

    @Override
    public void onSerialReceived(String receivedString) {							//Once connection data received, this function will be called
//        sensorData = receivedString.split(",");
        updateTextView1(sensorData);

//		String id = databasePastData.push().getKey();
//		PastData pastdata = new PastData(id,sensorData[1],sensorData[2],sensorData[3]);
//		databasePastData.child(id).setValue(pastdata);
    }

    public void updateTextView1(String[] updates) { //update text view

        deviceState = Integer.parseInt(updates[6]);
        fanSpeedState = Integer.parseInt(updates[7]);
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

        if (fanSpeedState == 0) {
            fanspeed1.setChecked(true);
        } else if (fanSpeedState == 1) {
            fanspeed2.setChecked(true);
        } else if (fanSpeedState == 2){
            fanspeed3.setChecked(true);
        }


    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton:
                if (checked)
                    // Fanspeed Auto
                    serialSend("1");
                Toast.makeText(getApplicationContext(), "Fanspeed Auto", Toast.LENGTH_SHORT).show();
                break;

            case R.id.radioButton2:
                if (checked)
                    // Fanspeed Slow
                    serialSend("2");
                Toast.makeText(getApplicationContext(), "Fanspeed Slow", Toast.LENGTH_SHORT).show();
                break;

            case R.id.radioButton3:
                if (checked)
                    // Fanspeed High
                    serialSend("3");
                Toast.makeText(getApplicationContext(), "Fanspeed Fast", Toast.LENGTH_SHORT).show();
                break;
        }
    }





}
