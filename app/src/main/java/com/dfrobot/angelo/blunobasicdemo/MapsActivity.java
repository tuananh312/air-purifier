package com.dfrobot.angelo.blunobasicdemo;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dfrobot.angelo.blunobasicdemo.UniformCostSearch.MAX_VALUE;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GPSTracker gps;
    private double latitude;
    private double longitude;
    private Map<String,Integer> map;
    private Button drawRoute;
    private Button resetRoute;
    private ArrayList<String> results = new ArrayList<>();     // list of coordinates sorted by route
    private Polyline polyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gps = new GPSTracker(this);
        if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

        } else {
            gps.showSettingsAlert();
        }

        //sample data
        String currentPos = "" + latitude + ", " + longitude;


        //TODO: add destination node into map as fetch location indicated by user. air pollution in destination is assumed to be 0
        map = new HashMap<String,Integer>();
        map.put(currentPos,155);
        map.put("1.339977, 103.847665",100);
        map.put("1.337231, 103.965721",50);
        map.put("1.343993, 103.957653",200);

        List<String> coordinateList = new ArrayList<String>();
        for (String key: map.keySet()){
            coordinateList.add(key);
        }

        // source
        int source = 0;
        source = coordinateList.indexOf(""+latitude+", "+longitude);

        int destination = 0;
        destination = coordinateList.indexOf("1.339977, 103.847665");
        int distance = 0;


        int adjacencyMatrix[][];
        int number_of_vertices;

        number_of_vertices = coordinateList.size();
        adjacencyMatrix = new int[number_of_vertices+1][number_of_vertices+1];
        for (int i = 1; i <= number_of_vertices; i++)
        {
            for (int j = 1; j <= number_of_vertices; j++)
            {
                String s = coordinateList.get(i-1);
                String d = coordinateList.get(j-1);
                Log.i("Distance weight: ", "distance is " + Math.round(getDistance(s,d)));
                adjacencyMatrix[i][j] = (int) Math.round(getDistance(s,d)/10*0.6 + map.get(d)*0.4);
                if (i == j)
                {
                    adjacencyMatrix[i][j] = 0;
                    continue;
                }
                if (adjacencyMatrix[i][j] == 0)
                {
                    adjacencyMatrix[i][j] = MAX_VALUE;
                }
            }
        }

        UniformCostSearch uniformCostSearch = new UniformCostSearch(number_of_vertices);
        distance = uniformCostSearch.uniformCostSearch(adjacencyMatrix,source, destination);
        String route = "";
        route = uniformCostSearch.getPath();
        Log.d("Path check: ", "The optimized path is " + route);
        System.out.println(route);

        String[] resultset = route.split(" ");


        // get coordinates from the search result
        for (int i = 0; i < resultset.length; i++) {
            int index;
            index = Integer.parseInt(resultset[i]);
            results.add(coordinateList.get(index));
        }







        drawRoute = (Button) findViewById(R.id.btn_draw);
        drawRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickShowRoute();
            }
        });

        resetRoute = (Button) findViewById(R.id.button_reset);
        resetRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                polyline.remove();
            }
        });

    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        LatLng current = new LatLng(latitude, longitude);


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current,15));

        for (String key: map.keySet()) {
            String[] coordinates = key.split(",");
            Double lat = Double.parseDouble(coordinates[0]);
            Double lng = Double.parseDouble(coordinates[1]);
            Integer airquality = map.get(key);
            mMap.addMarker(new MarkerOptions()
            .position(new LatLng(lat,lng))
            .title(airquality.toString())
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }

        mMap.addMarker(new MarkerOptions().position(current).title("Marker in Singapore"));

    }

    public void onClickShowRoute() {
        // draw paths
        for (int j = 0; j < results.size() - 1; j++) {

            drawPath(results.get(j),results.get(j+1));
        }
    }

    public float getDistance(String source, String destination){
        String[] coordinatesSource = source.split(",");
        String[] coordinatesDestination = destination.split(",");
        LatLng l1 = new LatLng(Double.parseDouble(coordinatesSource[0]),Double.parseDouble(coordinatesSource[1]));
        LatLng l2 = new LatLng(Double.parseDouble(coordinatesDestination[0]),Double.parseDouble(coordinatesDestination[1]));
        float[] results = new float[1];
        Location.distanceBetween(l1.latitude, l1.longitude,
                l2.latitude, l2.longitude,
                results);
        return results[0];
    }

    public void drawPath(String source, String destination) {
        String[] coordinatesSource = source.split(",");
        String[] coordinatesDestination = destination.split(",");
        LatLng l1 = new LatLng(Double.parseDouble(coordinatesSource[0]),Double.parseDouble(coordinatesSource[1]));
        LatLng l2 = new LatLng(Double.parseDouble(coordinatesDestination[0]),Double.parseDouble(coordinatesDestination[1]));
        String url = getMapsApiDirectionsUrl(l1, l2);
        ReadTask downloadTask = new ReadTask();
        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    private String  getMapsApiDirectionsUrl(LatLng origin,LatLng dest) {
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;


        return url;

    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            // TODO Auto-generated method stub
            String data = "";
            try {
                MapHttpConnection http = new MapHttpConnection();
                data = http.readUr(url[0]);


            } catch (Exception e) {
                // TODO: handle exception
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String,Integer, List<List<HashMap<String , String >>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {
            // TODO Auto-generated method stub
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(4);
                polyLineOptions.color(Color.BLUE);
            }

            polyline = mMap.addPolyline(polyLineOptions);

        }
    }
}


