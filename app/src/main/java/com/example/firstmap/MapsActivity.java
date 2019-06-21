package com.example.firstmap;

import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firstmap.Weather.WeatherRespone;
import com.example.firstmap.Weather.WeatherService;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , GoogleMap.OnMarkerClickListener {
    private GoogleMap mMap;


    TextView let;
    TextView left;
    TextView right;
    Geocoder geocoder;
    ProgressBar progressBar;
    LinearLayout linearLayout;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        let = findViewById(R.id.id1);
        left = findViewById(R.id.text_view_id2);
        right = findViewById(R.id.text_view_id3);
        progressBar = findViewById(R.id.progressBar);
        linearLayout = findViewById(R.id.linear);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this, Locale.getDefault());


    }
    private static DecimalFormat df2 = new DecimalFormat("#.##");

    @SuppressLint("WrongConstant")
    private void fetchWeather(){
        progressBar.setVisibility(View.VISIBLE);
//        left.setVisibility(View.GONE);
        let.setVisibility(View.GONE);
//        right.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        //status code header
        // 200-300 successful
        //400 bad Request
        //401/403
        //500+ servr Error
        WeatherService service = retrofit.create(WeatherService.class);

        //run the Request
        service.get("a21a79d3c32d92ebc8f8ee542782377f",Double.toString(x),Double.toString(y)).enqueue(new Callback<WeatherRespone>() {
            @Override
            public void onResponse(Call<WeatherRespone> call, Response<WeatherRespone> response) {
                //TODO: updateUI;
//                response.body().getmWeather().get(0).getmMain();
                if (!response.isSuccessful()){
                    ResponseBody errorBodey = response.errorBody();
                    try {
                        Log.d("RESPONSEERROR", errorBodey.string());
                    } catch (IOException e) {
//                        e.printStackTrace();
                    Log.d("RESPONSEERROR",e.getMessage());
                    }
                    return;
                }else {
                    progressBar.setVisibility(View.GONE);
//                    left.setVisibility(View.VISIBLE);
                    let.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.VISIBLE);

//                    right.setVisibility(View.VISIBLE);
                        System.out.println(response.body().getmMain().getmTemp());
                        left.setText(response.body().getmWeather().get(0).getmDescription());
                        right.setText(df2.format(response.body().getmMain().getmTemp() - 273.15)+"°");

                        //                    response.body().getmMain().getmTemp();
                }


//                System.out.println(response.body().getmWeather().get(0).getmMain());
//                System.out.println( response.body().toString());

            }

            @Override
            public void onFailure(Call<WeatherRespone> call, Throwable t) {
                //TODO: Display Error;
                Log.d("ad", t.getMessage());
            }
        });
    }

    public double x;
    public double y;

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
                 x = point.latitude;
                 y = point.longitude;

                LatLng dot = new LatLng(x, y);
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(x, y, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                    let.setText("Please connect to internet");
                    linearLayout.setVisibility(View.GONE);
                    return;
                }

//                addresses.get(0).getAdminArea();
                if (addresses==null || addresses.size()==0){
                    let.setText("UNKNOWN");
                    linearLayout.setVisibility(View.GONE);
                    return;
                }
                String city = addresses.get(0).getLocality();
                String countryName = addresses.get(0).getCountryName();
                String feature = addresses.get(0).getFeatureName();
                System.out.println(addresses);
                if (city!=null) {
                    if ( countryName == null)
                        let.setText(city + "," + "Palestine");
                    else if (countryName.equals("Israel")){
                        let.setText(city + "," + "Palestine");
                    }else let.setText(city + "," + countryName);
                }
//                else if (city==null & countryName==null & feature!=null){
//                    let.setText(feature);
//                }
                else if (countryName!=null){
                    if (countryName.equals("Israel"))
                        let.setText("Palestine");
                    else {
                    let.setText(countryName);
                    }
                }
                else {
                    let.setText("UNKNOWN");
                    linearLayout.setVisibility(View.GONE);
                    return;
                }
                mMap.addMarker(new MarkerOptions().position(dot).title(""));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(dot));
//                if (city!=null)
//                    getJSON(city);

//                txtc1.setText(city);
                fetchWeather();
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
