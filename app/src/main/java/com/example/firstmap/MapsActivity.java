package com.example.firstmap;

import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , GoogleMap.OnMarkerClickListener {
    private GoogleMap mMap;


    TextView let;
    TextView left;
    TextView right;
    Geocoder geocoder;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        let = findViewById(R.id.id1);
        left = findViewById(R.id.text_view_id2);
        right = findViewById(R.id.text_view_id3);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this, Locale.getDefault());


    }
    private static DecimalFormat df2 = new DecimalFormat("#.##");


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("WrongViewCast")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(31.9522,35.2332))
                .title("felesten"));
        // Add some markers to the map, and add a data object to each marker.
        // Set a listener for marker click.

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            public void onMapClick(LatLng point){
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(point)
                );
                JSONObject data = null;

//                let.setText(df2.format(point.latitude)+" || " +df2.format(point.longitude));

                double x = point.latitude;
                double y = point.longitude;

                LatLng sydney = new LatLng(x, y);

                mMap.addMarker(new MarkerOptions().position(sydney).title(""));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(sydney));

//                txtc.setText(Double.toString(x) + "°");
//                txtcy.setText(Double.toString(y) + "°");

                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(x, y, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }

                addresses.get(0).getAdminArea();
                String city = addresses.get(0).getLocality();
                System.out.println(addresses);
                let.setText(city);
                if (city!=null)
                    getJSON(city);

//                txtc1.setText(city);

            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    JSONObject data = null;

    public void getJSON(final String city) {

        new AsyncTask<Void, Void, Void>() {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @SuppressLint("WrongThread")
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q="+city+"&APPID=f8dc5892f83d77ee97338b92b8a93965");

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    StringBuffer json = new StringBuffer(1024);
                    String tmp = "";

                    while((tmp = reader.readLine()) != null)
                        json.append(tmp).append("\n");
                    reader.close();

                    data = new JSONObject(json.toString());

                    if(data.getInt("cod") != 200) {
                        System.out.println("Cancelled");
                        return null;
                    }
                    left.setText(data.getJSONArray("weather").getJSONObject(0).getString("main"));
                    double tp =(data.getJSONObject("main").getDouble("temp"))+-273.15;
                    System.out.println(tp);
                    right.setText(df2.format(tp));
                } catch (Exception e) {

                    System.out.println("Exception "+ e.getMessage());
                    return null;
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void Void) {
                if(data!=null){
                    Log.d("my weather received",data.toString());
                }

            }
        }.execute();

    }


}
