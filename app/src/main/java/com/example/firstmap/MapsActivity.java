package com.example.firstmap;

import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firstmap.Weather.WeatherRespone;
import com.example.firstmap.Weather.WeatherService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

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

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private GoogleMap mMap;


    TextView let;
    TextView left;
    TextView right;
    Geocoder geocoder;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    FusedLocationProviderClient client;
    ImageView imageView;
    ImageView imageView2;
    ImageView imageView3;
    public double x;
    public double y;

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

        imageView = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);

        imageView.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                Toast.makeText(MapsActivity.this, "hi", Toast.LENGTH_LONG).show();
                mylocation();
            }
        });
        mylocation();

        imageView3.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
//                Toast.makeText(MapsActivity.this, "hi", Toast.LENGTH_LONG).show();
                search();
            }
        });

        mylocation();

    }


    private static DecimalFormat df2 = new DecimalFormat("#.##");

    public void mylocation(){
        client = LocationServices.getFusedLocationProviderClient(this);
        requestMultiplePermissions();
        if (checkSelfPermission(ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {

            @Override
            public void onSuccess(Location location) {
                if (location !=null){
                    y= location.getLongitude();
                    x=location.getLatitude();
                    Toast.makeText(MapsActivity.this, location.toString(), Toast.LENGTH_LONG).show();
                    LatLng point = new LatLng(x,y);
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions()
                            .position(point)
                    );
                    putpint(x,y);
                }
            }

        });
    }

    void search(){
    search= (String) let.getText();
        String url = "https://www.google.com/search?q="+search;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    @SuppressLint("WrongConstant")
    private void fetchWeather(){
        progressBar.setVisibility(View.VISIBLE);
//        left.setVisibility(View.GONE);
        let.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
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
                if (!response.isSuccessful()){
                    ResponseBody errorBodey = response.errorBody();
                    try {
                        Log.d("RESPONSEERROR", errorBodey.string());
                    } catch (IOException e) {
                    Log.d("RESPONSEERROR",e.getMessage());
                    }
                    return;
                }else {
                    progressBar.setVisibility(View.GONE);
                    let.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.VISIBLE);
                        System.out.println(response.body().getmMain().getmTemp());
                        left.setText(response.body().getmWeather().get(0).getmDescription());
                        right.setText(df2.format(response.body().getmMain().getmTemp() - 273.15)+"°");
                    Drawable drawable;
                    switch (response.body().getmWeather().get(0).getmDescription()) {
                        case "haze":
                            imageView2.setImageResource(R.drawable.haze);
                            break;
                        case "clear sky":
                             drawable = getDrawable(R.drawable.clearday);
                            imageView2.setImageDrawable(drawable);
                            break;
                        case "light train":
                            drawable = getDrawable(R.drawable.lightrain);
                            imageView2.setImageDrawable(drawable);
                            break;
                        case "overcast clouds":
                            drawable = getDrawable(R.drawable.overcastclouds);
                            imageView2.setImageDrawable(drawable);
                            break;
                        case "scatterd clouds":
                            drawable = getDrawable(R.drawable.scatterdclouds);
                            imageView2.setImageDrawable(drawable);
                            break;
                        case "broken clouds":
                            drawable = getDrawable(R.drawable.scatterdclouds);
                            imageView2.setImageDrawable(drawable);
                            break;
                        case "few clouds":
                            drawable = getDrawable(R.drawable.fewclouds);
                            imageView2.setImageDrawable(drawable);
                            break;
                        case "moderate rain":
                            drawable = getDrawable(R.drawable.moderaterain);
                            imageView2.setImageDrawable(drawable);
                            break;
                        default:
                            drawable = getDrawable(R.drawable.lightrain);
                            imageView2.setImageDrawable(drawable);
                    }
                }
            }
            @Override
            public void onFailure(Call<WeatherRespone> call, Throwable t) {
                //TODO: Display Error;
                Log.d("ad", t.getMessage());
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
    @SuppressLint("WrongViewCast")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(31.9522,35.2332))
                .position(new LatLng(y,x))
                .title("felesten"));
        // Add some markers to the map, and add a data object to each marker.
        // Set a listener for marker click.

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            public void onMapClick(LatLng point){
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(point)
                );
                 x = point.latitude;
                 y = point.longitude;
                putpint(x,y);

            }
        });
    }
    String search;

    void putpint(double x ,double y){
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
            if ( countryName == null) {
                let.setText(city + "," + "Palestine");
                search=city + "," + "Palestine";
            }else if (countryName.equals("Israel")){
                let.setText(city + "," + "Palestine");
                search=city + "," + "Palestine";
            }else let.setText(city + "," + countryName);
        }
        else if (countryName!=null){
            if (countryName.equals("Israel")) {
                let.setText("Palestine");
                search="Palestine";
            }else {
                let.setText(countryName);
                search=countryName;
            }
        }
        else if (feature.equals("Unnamed Road")){
            let.setText("UNKNOWN");
            linearLayout.setVisibility(View.GONE);
            return;
        }else {
            let.setText(feature);
            search=feature;
        }

        mMap.addMarker(new MarkerOptions().position(dot).title(""));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(dot));
        fetchWeather();
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
