package com.msarangal.travelkhaanademo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;

import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener {

    private TextView stationName, arrival, heading;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private RequestQueue requestQueue;
    private List<TrainData> trainData = new LinkedList();
    private List<LatLng> points = new LinkedList();
    private ImageView profilePic, logout;
    private TextView name;
    private Button place_order;
    private static final String TAG_TRAIN_INFO = "train_info";
    private static final String TAG_STATION_NAME = "station_name";
    private static final String TAG_IS_STOP = "is_stop";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGITUDE = "longitude";
    private static final String TAG_ARRIVAL_TIME = "arrival_time";
    private static final String TAG_HALT = "halt_duration";
    private SharedPreferences sharedPreferences;
    private String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // profilePic = (ImageView) findViewById(R.id.profPic);
        name = (TextView) findViewById(R.id.name);
        logout = (ImageView) findViewById(R.id.imgLogout);
        requestQueue = VolleySingleton.getInstance().getRequestQueue();
        stationName = (TextView) findViewById(R.id.nameOfStation);
        arrival = (TextView) findViewById(R.id.Arrival_halt);
        heading = (TextView) findViewById(R.id.heading);
        place_order = (Button) findViewById(R.id.place_order);

        Intent i = getIntent();
        final String username = i.getStringExtra("name");
        email = i.getStringExtra("email");

        place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MapsActivity.this, OrderConfirmation.class);

                i.putExtra("email", email);
                i.putExtra("stationName", stationName.getText().toString());
                i.putExtra("arr_halt", arrival.getText().toString());

                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//sign out
                SignOut();

                sharedPreferences = MapsActivity.this.getSharedPreferences("tkhaana", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
//change the shared prefs
                editor.putBoolean("isConnected", false);
                editor.commit();


            }
        });


//        if (i.hasExtra("byteArray")) {
//            Bitmap b = BitmapFactory.decodeByteArray(
//                    getIntent().getByteArrayExtra("byteArray"), 0, getIntent().getByteArrayExtra("byteArray").length);
//            profilePic.setImageBitmap(b);
//        }

        sharedPreferences = MapsActivity.this.getSharedPreferences("tkhaana", MODE_PRIVATE);
        name.setText(sharedPreferences.getString("UserName", "username"));


        UseVolley();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //  setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        long temp_lat, temp_long = 0;
        String station_name = "", arrival_time = "", halt = "";
        LatLng temLatLong = null;

        //  mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mMap.clear();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMarkerClickListener(this);

        PolylineOptions polylineOptions = new PolylineOptions().addAll(points).width(5).color(Color.RED).geodesic(true).visible(true);

        mMap.addPolyline(polylineOptions);

        LatLng latLng = new LatLng(points.get(1).latitude, points.get(1).longitude);

//        MarkerOptions options = new MarkerOptions().title("start").position(latLng).flat(true).visible(true);
//
//        mMap.addMarker(options);

        for (int i = 0; i < trainData.size(); i++) {

            TrainData current = trainData.get(i);

            if (current.isStop == 1) {
                temp_lat = current.lat;
                temp_long = current.longi;
                temLatLong = new LatLng(temp_lat, temp_long);
                station_name = current.StationName;
                arrival_time = current.arrival_time;
                halt = current.halt;

            }

            MarkerOptions opt = new MarkerOptions().title(station_name).position(temLatLong).flat(false).visible(true).snippet("arrival: " + arrival_time + "\nhalt: " + halt).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

            mMap.addMarker(opt);
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to Mountain View
                .zoom(6)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    public void SignOut() {

        MainActivity.signOutFromGplus();

        finish();

    }

    public void UseVolley() {


        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, "http://184.106.215.147/travelkhana/services/TrainSchedule?trainno=11015&login=testapi:testapi", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObj = new JSONObject(response);
                    JSONArray trainInfoArray = jsonObj.getJSONArray(TAG_TRAIN_INFO);

                    for (int i = 0; i < trainInfoArray.length(); i++) {

                        JSONObject info = trainInfoArray.getJSONObject(i);

                        TrainData data = new TrainData();

                        data.StationName = info.getString(TAG_STATION_NAME);
                        data.isStop = info.getInt(TAG_IS_STOP);
                        data.lat = info.getLong(TAG_LATITUDE);
                        data.longi = info.getLong(TAG_LONGITUDE);
                        data.arrival_time = info.getString(TAG_ARRIVAL_TIME);
                        data.halt = info.getString(TAG_HALT);

                        LatLng point = new LatLng(info.getLong(TAG_LATITUDE), info.getLong(TAG_LONGITUDE));

                        points.add(point);

                        trainData.add(data);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setUpMapIfNeeded();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {


                    Toast.makeText(getApplicationContext(), "Request timed out, please try again...", Toast.LENGTH_LONG).show();

                } else if (error instanceof NoConnectionError) {

                    Toast.makeText(getApplicationContext(), "no internet connection, please turn the WiFi or Data pack ON", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {

                    Toast.makeText(getApplicationContext(), "Auth failure please try again...", Toast.LENGTH_LONG).show();

                } else if (error instanceof ServerError) {

                    Toast.makeText(getApplicationContext(), "Server Error, please try again...", Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {

                    Toast.makeText(getApplicationContext(), "Network error, please try again...", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {

                    Toast.makeText(getApplicationContext(), "Parse error, please try again...", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return null;
            }
        };


        int socketTimeout = 5000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        requestQueue.add(jsonObjectRequest);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        heading.setVisibility(View.GONE);
        stationName.setVisibility(View.VISIBLE);
        arrival.setVisibility(View.VISIBLE);
        place_order.setVisibility(View.VISIBLE);

        stationName.setText(marker.getTitle().toString());
        arrival.setText(marker.getSnippet().toString());
        return false;
    }

}
