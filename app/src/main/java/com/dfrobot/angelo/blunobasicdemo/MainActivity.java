package com.dfrobot.angelo.blunobasicdemo;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;


public class MainActivity  extends BlunoLibrary {
	private Button buttonScan;
	private Button buttonSerialSend;
	private EditText serialSendText;
	private TextView fanSpeedTextView;
	private TextView temperatureTextView;
	private TextView humidityTextView;
	private TextView vocTextView;
	private Button buttonMap;
	private Button buttonControl;
	private DatabaseReference databasePastData;
	private ToggleButton toggleUVC;
	private RadioButton fanspeed1;
	private RadioButton fanspeed2;
	private BlunoLibrary blunoLibrary;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        onCreateProcess();														//onCreate Process by BlunoLibrary


        serialBegin(115200);													//set the Uart Baudrate on BLE chip to 115200

        serialSendText=(EditText) findViewById(R.id.serialSendText);			//initial the EditText of the sending data

        buttonSerialSend = (Button) findViewById(R.id.buttonSerialSend);		//initial the button for sending the data
        buttonSerialSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				serialSend(serialSendText.getText().toString());				//send the data to the BLUNO
			}
		});

        buttonScan = (Button) findViewById(R.id.buttonScan);					//initial the button for scanning the BLE device
        buttonScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				buttonScanOnClickProcess();										//Alert Dialog for selecting the BLE device
			}
		});

		//Navigation to map
		buttonMap = (Button) findViewById(R.id.buttonMap);
		buttonMap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				navigateMapView();
			}
		});

		//Navigation to controls
//		buttonControl = (Button) findViewById(R.id.buttonControl);
//		buttonControl.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				navigateControlView();
//			}
//		});

		//UVC control
		toggleUVC = (ToggleButton) findViewById(R.id.toggleUVC);
		toggleUVC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if (b) {
					//send data number turn on UVC
					serialSend("8");
					Toast.makeText(getApplicationContext(), "Turning on UVC", Toast.LENGTH_SHORT).show();
				} else {
					//send data number turn off UVC
					serialSend("9");
					Toast.makeText(getApplicationContext(), "Turning off UVC", Toast.LENGTH_SHORT).show();
				}
			}
		});


		//Fanspeed control
		fanspeed1 = (RadioButton) findViewById(R.id.radioButton);
		fanspeed1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onRadioButtonClicked(view);
			}
		});

		fanspeed2 = (RadioButton) findViewById(R.id.radioButton3);
		fanspeed2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onRadioButtonClicked(view);
			}
		});



		//Get database reference
		databasePastData = FirebaseDatabase.getInstance().getReference("past_data");

		FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference myRef = database.getReference("message");
		myRef.setValue("Hello World");

		myRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				String value = dataSnapshot.getValue(String.class);
				Log.d(TAG,"Value is: " +value);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				Log.w(TAG,"Failed to read value.", databaseError.toException());

			}
		});


	}

	protected void onResume(){
		super.onResume();
		System.out.println("BlUNOActivity onResume");
		onResumeProcess();														//onResume Process by BlunoLibrary
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		onActivityResultProcess(requestCode, resultCode, data);					//onActivityResult Process by BlunoLibrary
		super.onActivityResult(requestCode, resultCode, data);
	}
	
    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();														//onPause Process by BlunoLibrary
    }
	
	protected void onStop() {
		super.onStop();
		onStopProcess();														//onStop Process by BlunoLibrary
	}
    
	@Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyProcess();														//onDestroy Process by BlunoLibrary
    }

	@Override
	public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
		switch (theConnectionState) {											//Four connection state
		case isConnected:
			buttonScan.setText("Connected");
			break;
		case isConnecting:
			buttonScan.setText("Connecting");
			break;
		case isToScan:
			buttonScan.setText("Scan");
			break;
		case isScanning:
			buttonScan.setText("Scanning");
			break;
		case isDisconnecting:
			buttonScan.setText("isDisconnecting");
			break;
		default:
			break;
		}
	}

	@Override
	public void onSerialReceived(String receivedString) {							//Once connection data received, this function will be called
		try{

			//String[] sensorDataList = receivedString.split(";");
			String[] sensorData = receivedString.split(",");
			Thread.sleep(1000);
			updateTextView(sensorData);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		String id = databasePastData.push().getKey();
//		PastData pastdata = new PastData(id,sensorData[1],sensorData[2],sensorData[3]);
//		databasePastData.child(id).setValue(pastdata);
	}

	public void updateTextView(String[] updates) { //update text view
		fanSpeedTextView = (TextView) findViewById(R.id.textView);
		String fanSpeed = updates[0] + " *C";
		fanSpeedTextView.setText(fanSpeed);
		temperatureTextView = (TextView) findViewById(R.id.textView2);
		String temperature = updates[1] + " %";
		temperatureTextView.setText(temperature);
		humidityTextView = (TextView) findViewById(R.id.textView3);
		humidityTextView.setText(updates[2]);
		vocTextView = (TextView) findViewById(R.id.textView4);
		vocTextView.setText(updates[3]);
	}

	public void onRadioButtonClicked(View view) {
		// Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();

		// Check which radio button was clicked
		switch(view.getId()) {
			case R.id.radioButton:
				if (checked)
					// Fanspeed Slow
					serialSend("10");
					Toast.makeText(getApplicationContext(), "Fanspeed Slow", Toast.LENGTH_SHORT).show();
					break;
			case R.id.radioButton3:
				if (checked)
					// Fanspeed High
					serialSend("11");
					Toast.makeText(getApplicationContext(), "Fanspeed Fast", Toast.LENGTH_SHORT).show();
					break;
		}
	}


	public void navigateMapView(){
		Intent intent = new Intent(this, MapsActivity.class);
		startActivity(intent);
	}
	public void navigateControlView(){
		Intent intent = new Intent(this, MapsActivity.class);
		startActivity(intent);
	}

}