package com.example.alphaparker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Address;
import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    Marker marker;
    LocationListener locationListener;
    Bundle intentExtra;
    String detectedText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                //get the location name from latitude and longitude
                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    List<Address> addresses =
                            geocoder.getFromLocation(latitude, longitude, 1);
                    String result = addresses.get(0).getLocality()+":";
                    result += addresses.get(0).getCountryName();
                    LatLng latLng = new LatLng(latitude, longitude);
                    if (marker != null){
                        marker.remove();
                        //marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result));
                        //mMap.setMaxZoomPreference(20);
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
                    }
                    else{
                        //marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result));
                        //mMap.setMaxZoomPreference(20);
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 21.0f));
                        String [] lines = MainActivity.detectedText.split("\n");
                        String hours = lines[0];
                        //hours.replace("-","");
                        char c;
                        String startTime = "";
                        String endTime = "";

                        int counterH = 0;

                        int index = 0;
                        int counter = 0;
                        for (int i = 0;i<hours.length();i++) {


                            c = hours.charAt(i);
                            if(c != 'h' && c != '-' && counter == 0){
                                startTime = startTime + c;
                                counterH = 0;
                            }else if (c == 'h'){
                                counter--;
                                counterH = 1;
                            }else if (c == '-'){
                                counter = 1;
                            }else if (c != 'h' && counter > 0 && counterH == 1){
                                endTime = endTime + c;
                            }

                        }
                        int startTimeInt;
                        int endTimeInt;
                        startTimeInt = Integer.parseInt(startTime);
                        endTimeInt = Integer.parseInt(endTime);
                        System.out.println(startTime +" , " +endTime);

                        String day = lines[1];
                        String startDay = "";
                        String endDay = "";
                        int counter2 = 0;
                        char c2;
                        for (int i = 0;i<day.length();i++){
                            c2 = day.charAt(i);
                            if (c2 == 'A'){
                                counter2++;
                            }
                            else if(counter2 == 0 && c2 != ' '){
                                startDay = startDay + c2;
                            }
                            else if(counter2 > 0 && c2 != ' '){
                                endDay = endDay + c2;
                            }

                        }
                        System.out.println(startDay + " , " + endDay);
                        int startDayInt = 0;
                        int endDayInt = 0;
                        if( startDay.equals("LUN")){
                            startDayInt = 1;
                        }

                        if( endDay.equals("VEN")){
                            endDayInt = 5;
                        }
                        addMarkerNoPark(mMap, longitude, latitude,hours + day);
                        System.out.println(day + " ,hoursint " + hours);
                        System.out.println(endTimeInt + " ,timeint " + startTimeInt);
                        System.out.println(startDayInt + " ,int " + endDayInt);
                        System.out.println(latitude + " ,long" + longitude);
                        if (startDayInt >= MainActivity.day && MainActivity.day <= endDayInt && startTimeInt >= MainActivity.hour && endTimeInt <= MainActivity.hour  ){
                            addMarkerNoPark(mMap, latitude, longitude,hours + day);
                        }
                        else {

                            addMarkerPark(mMap, latitude, longitude, hours + day );
                        }

                        addMarkerPark(mMap, 0, 0, "montreal" );

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


    }

    public GoogleMap getMap() {
        return mMap;
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
        double longitude = -73.57;
        double latitude = 45.50;
        System.out.println(MainActivity.detectedText);
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    addMarkerPark(mMap, latitude+0.3, longitude+0.4, "montreal4");
                        addMarkerPark(mMap, latitude+0.2, longitude, "montreal5" );
                        addMarkerPark(mMap,latitude-0.1, longitude+0.8, "montreal1");
                        addMarkerPark(mMap, latitude+0.4, longitude-0.3, "montreal2");
                        addMarkerNoPark(mMap, latitude-0.6, longitude+0.5,"montreal3" );
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }


    public void addMarkerNoPark(GoogleMap map, double x, double y, String title) {
        map.addMarker(new MarkerOptions()
                .position(new LatLng(x, y))
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }


    public void addMarkerPark(GoogleMap map, double x, double y, String title){
        map.addMarker(new MarkerOptions()
                .position(new LatLng(x, y))
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

    }

}
