package com.dfrobot.angelo.blunobasicdemo;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class InfoActivity extends MainActivity {

    private Button buttonHomeInfo;
    private Button buttonControlInfo;

    private String batteryRemaining = "-";
    private String filterReplacement = "-";

    private TextView batteryTextView;
    private TextView filterTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        serialBegin(115200);



        buttonHomeInfo = (Button) findViewById(R.id.buttonHomeInfo);
        buttonHomeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateHomeView();
            }
        });

        buttonControlInfo = (Button) findViewById(R.id.buttonControlInfo);
        buttonControlInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateControlView();
            }
        });

        batteryTextView = (TextView) findViewById(R.id.textViewBattery);
        batteryTextView.setText(batteryRemaining);

        filterTextView = (TextView) findViewById(R.id.textViewFilter);
        filterTextView.setText(filterReplacement);

        updateTextView2(sensorData);


    }

    @Override
    public void onSerialReceived(String receivedString) {							//Once connection data received, this function will be called
//        sensorData = receivedString.split(",");
        updateTextView2(sensorData);

//		String id = databasePastData.push().getKey();
//		PastData pastdata = new PastData(id,sensorData[1],sensorData[2],sensorData[3]);
//		databasePastData.child(id).setValue(pastdata);
    }

    public void updateTextView2(String[] updates) { //update text view

        batteryRemaining = updates[4];
        batteryTextView.setText(batteryRemaining);
        filterReplacement = updates[5];
        filterTextView.setText(filterReplacement);

    }

}
