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
import android.widget.RadioGroup;
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
	private TextView temperatureTextView;
	private TextView humidityTextView;
	private TextView dustTextView;
	private TextView vocTextView;
	private Button buttonMap;
	private Button buttonControl;
	private DatabaseReference databasePastData;
	private ToggleButton toggleUVC;
	private ToggleButton toggleDevice;
	private RadioGroup fanSpeedRadioGroup;
	private RadioButton fanspeed1;
	private RadioButton fanspeed2;
	private RadioButton fanspeed3;
	private String batteryRemaining;
	private String filterReplacement;
	private int deviceState = 1;
	private int fanSpeedState = 0;
	private int UVCState = 0;





	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        onCreateProcess();														//onCreate Process by BlunoLibrary


        serialBegin(115200);													//set the Uart Baudrate on BLE chip to 115200

        serialSendText=(EditText) findViewById(R.id.serialSendText);			//initial the EditText of the sending data

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
		buttonControl = (Button) findViewById(R.id.buttonControl);
		buttonControl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				navigateControlView();
			}
		});

		//UVC control
		toggleUVC = (ToggleButton) findViewById(R.id.toggleUVC);
		toggleUVC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

		//Device control
		toggleDevice = (ToggleButton) findViewById(R.id.toggleDevice);
		toggleDevice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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


		//Fanspeed control
		fanspeed1 = (RadioButton) findViewById(R.id.radioButton);
		fanspeed1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onRadioButtonClicked(view);
			}
		});

		fanspeed2 = (RadioButton) findViewById(R.id.radioButton2);
		fanspeed2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onRadioButtonClicked(view);
			}
		});

		fanspeed3 = (RadioButton) findViewById(R.id.radioButton3);
		fanspeed3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onRadioButtonClicked(view);
			}
		});

		fanSpeedRadioGroup = (RadioGroup) findViewById(R.id.fanSpeedRadioGroup);




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
		temperatureTextView = (TextView) findViewById(R.id.textView);
		String temperature = updates[0] + " *C";
		temperatureTextView.setText(temperature);
		humidityTextView = (TextView) findViewById(R.id.textView2);
		String humidity = updates[1] + " %";
		humidityTextView.setText(humidity);
		dustTextView = (TextView) findViewById(R.id.textView3);
		dustTextView.setText(updates[2]);
		vocTextView = (TextView) findViewById(R.id.textView4);
		vocTextView.setText(updates[3]);
		batteryRemaining = updates[4];
		filterReplacement = updates[5];
		deviceState = Integer.parseInt(updates[6]);
		fanSpeedState = Integer.parseInt(updates[7]);
		UVCState = Integer.parseInt(updates[8]);

		if (fanSpeedState == 0) {
			fanspeed1.setChecked(true);
		} else if (fanSpeedState == 1) {
			fanspeed2.setChecked(true);
		} else if (fanSpeedState == 2){
			fanspeed3.setChecked(true);
		}

		if(deviceState == 1) {
			toggleDevice.setChecked(true);
		} else if (deviceState == 0) {
			toggleDevice.setChecked(false);
		}

		if(UVCState == 1) {
			toggleUVC.setChecked(true);
		} else if (UVCState == 0) {
			toggleUVC.setChecked(false);
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


	public void navigateMapView(){
		Intent intent = new Intent(this, MapsActivity.class);
		startActivity(intent);
	}
	public void navigateControlView(){
		Intent intent = new Intent(this, ControlActivity.class);
		startActivity(intent);
	}

}