package com.dfrobot.angelo.blunobasicdemo;
import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;



public class MainActivity extends BlunoLibrary {
	private ImageButton buttonScan;
	private TextView temperatureTextView;
	private TextView humidityTextView;
	private TextView dustTextView;
	private TextView vocTextView;
	private TextView airQualityTextView;
	private ImageButton buttonMap;
	private ImageButton buttonControl;
	private ImageButton buttonInfo;
	private int airQuality = 0;
//	private DatabaseReference databasePastData;

	public static String[] sensorData = new String[]{"0","0","0","0","0","0","0","0","0"};
	private String temperature = "-";
	private String humidity = "-";
	private String dust = "-";
	private String voc = "-";





	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);



		if ( ContextCompat.checkSelfPermission( this, Manifest.permission.BLUETOOTH ) != PackageManager.PERMISSION_GRANTED ) {

			ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.BLUETOOTH },1);
		}

		if ( ContextCompat.checkSelfPermission( this, Manifest.permission.BLUETOOTH_ADMIN ) != PackageManager.PERMISSION_GRANTED ) {

			ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.BLUETOOTH_ADMIN },1);
		}
		if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

			ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },1);
		}




        onCreateProcess();														//onCreate Process by BlunoLibrary

        serialBegin(115200);													//set the Uart Baudrate on BLE chip to 115200

        buttonScan = (ImageButton) findViewById(R.id.buttonScan);					//initial the button for scanning the BLE device
        buttonScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				buttonScanOnClickProcess();										//Alert Dialog for selecting the BLE device
			}
		});

		//Navigation to map
		buttonMap = (ImageButton) findViewById(R.id.buttonMap);
		buttonMap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				navigateMapView();
			}
		});

		//Navigation to controls
		buttonControl = (ImageButton) findViewById(R.id.buttonControl);
		buttonControl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				navigateControlView();
			}
		});

		//Navigation to device info
		buttonInfo = (ImageButton) findViewById(R.id.button_info);
		buttonInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				navigateInfoView();
			}
		});



//		//Get database reference
//		databasePastData = FirebaseDatabase.getInstance().getReference("past_data");
//
//		FirebaseDatabase database = FirebaseDatabase.getInstance();
//		DatabaseReference myRef = database.getReference("message");
//		myRef.setValue("Hello World");
//
//		myRef.addValueEventListener(new ValueEventListener() {
//			@Override
//			public void onDataChange(DataSnapshot dataSnapshot) {
//				String value = dataSnapshot.getValue(String.class);
//				Log.d(TAG,"Value is: " +value);
//			}
//
//			@Override
//			public void onCancelled(DatabaseError databaseError) {
//				Log.w(TAG,"Failed to read value.", databaseError.toException());
//
//			}
//		});

		temperatureTextView = (TextView) findViewById(R.id.textView);
		temperatureTextView.setText(temperature);
		humidityTextView = (TextView) findViewById(R.id.textView2);
		humidityTextView.setText(humidity);
		dustTextView = (TextView) findViewById(R.id.textView3);
		dustTextView.setText(dust);
		vocTextView = (TextView) findViewById(R.id.textView4);
		vocTextView.setText(voc);
		airQualityTextView = (TextView) findViewById(R.id.textViewAirQuality);
		airQualityTextView.setText("-");


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
			Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
			break;
		case isConnecting:
			Toast.makeText(getApplicationContext(), "Connecting", Toast.LENGTH_SHORT).show();
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

			String[] receivedData = receivedString.split(",");
			if (receivedData[0].equals("A")) {
				for (int i = 1; i <= 4; i++) {
					sensorData[i-1] = receivedData[i];
				}
				updateTextView(sensorData);
			} else if (receivedData[0].equals("B")) {
				for (int i = 1; i <= 5; i++) {
					sensorData[i+3] = receivedData[i];
				}
			}
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

//		String id = databasePastData.push().getKey();
//		PastData pastdata = new PastData(id,sensorData[1],sensorData[2],sensorData[3]);
//		databasePastData.child(id).setValue(pastdata);
	}

	public void updateTextView(String[] updates) { //update text view

		temperature = updates[1];
		temperatureTextView.setText(temperature);
		humidity = updates[2];
		humidityTextView.setText(humidity);
		dust = updates[3];
		dustTextView.setText(dust);
		voc = updates[4];
		vocTextView.setText(voc);

		int dustComponent = calculateDustAirQuality(Integer.parseInt(dust));
		int vocComponent = calculateVOCAirQuality(Integer.parseInt(voc));
		if (dustComponent >= vocComponent){
			airQuality = dustComponent;
		} else {
			airQuality = vocComponent;
		}
		airQualityTextView.setText(Integer.toString(airQuality));


	}




	public void navigateMapView(){
		Intent intent = new Intent(this, MapsActivity.class);
		startActivity(intent);
	}
	public void navigateControlView(){
		Intent intent = new Intent(this, ControlActivity.class);
		startActivity(intent);
	}

	public void navigateInfoView(){
		Intent intent = new Intent(this, InfoActivity.class);
		startActivity(intent);

	}

	public void navigateHomeView(){
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		savedInstanceState.putString("temp", temperature);
		savedInstanceState.putString("humid", humidity);
		savedInstanceState.putString("dust", dust);
		savedInstanceState.putString("voc", voc);
//		savedInstanceState.putString("MyString", "Welcome back to Android");
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.
		temperature = savedInstanceState.getString("temp");
		humidity = savedInstanceState.getString("humid");
		dust = savedInstanceState.getString("dust");
		voc = savedInstanceState.getString("voc");
	}

	public int calculateDustAirQuality(int dust){
		int ij;
		int ij1;
		int xj;
		int xj1;

		int result=0;
		if (0 <= dust && dust <= 12) {
			ij = 0; ij1 = 50; xj = 0; xj1 = 12;
		} else if (dust <= 55) {
			ij = 50; ij1 = 100; xj = 13; xj1 = 55;
		} else if (dust <= 150) {
			ij = 100; ij1 = 200; xj = 56; xj1 = 150;
		} else if (dust <= 250) {
			ij = 200; ij1 = 300; xj = 151; xj1 = 250;
		} else if (dust <= 350) {
			ij = 300; ij1 = 400; xj = 251; xj1 = 350;
		} else if (dust <= 500) {
			ij = 400; ij1 = 500; xj = 351; xj1 = 500;
		} else {
			return 600;
		}

		result = Math.round(((ij1 - ij)*(dust-xj)/(xj1-xj)) + ij);

		return result;

	}

	public int calculateVOCAirQuality(int voc){
		int ij;
		int ij1;
		int xj;
		int xj1;

		int result=0;
		if (0 <= voc && voc <= 200) {
			ij = 0; ij1 = 50; xj = 0; xj1 = 200;
		} else if (voc <= 350) {
			ij = 50; ij1 = 100; xj = 201; xj1 = 350;
		} else if (voc <= 500) {
			ij = 100; ij1 = 250; xj = 351; xj1 = 500;
		} else if (voc <= 757) {
			ij = 250; ij1 = 400; xj = 501; xj1 = 757;
		} else {
			return 500;
		}

		result = Math.round(((ij1 - ij)*(voc-xj)/(xj1-xj)) + ij);

		return result;

	}

}