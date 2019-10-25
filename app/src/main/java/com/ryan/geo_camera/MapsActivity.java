package com.ryan.geo_camera;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.os.Environment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<String> fileNames = new ArrayList<String>();
    ArrayList<pair> photoInformation = new ArrayList<pair>();
    ArrayList<Marker> markers = new ArrayList<Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        fileNames = getAllFiles(path.toString());
        photoInformation = splitFileName(fileNames);




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        double currentLat = getIntent().getExtras().getDouble("latitude");
        double currentLon = getIntent().getExtras().getDouble("longitude");
        LatLng currentPlace = new LatLng(currentLat, currentLon);

        for(int i = 0; i < photoInformation.size(); i++)
        {

            double lat = photoInformation.get(i).latitude;
            double lon = photoInformation.get(i).longitude;
            LatLng place = new LatLng(lat, lon);
            Marker myMarker = mMap.addMarker(new MarkerOptions().position(place).title("Photo Taken"));
            myMarker.setTag(photoInformation.get(i));
            markers.add(myMarker);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPlace));
    }

    private ArrayList<String> getAllFiles(String directory)
    {
        ArrayList<String> result = new ArrayList<String>(); //ArrayList cause you don't know how many files there is
        File folder = new File(directory); //This is just to cast to a File type since you pass it as a String
        File[] filesInFolder = folder.listFiles(); // This returns all the folders and files in your path
        for (File file : filesInFolder) { //For each of the entries do:
            //check that it's not a dir
            if (!file.isDirectory())
                result.add(file.getName()); //push the filename as a string
        }

        return result;
    }

    private ArrayList<pair> splitFileName(ArrayList<String> stringToSplit)
    {
        ArrayList<pair> split = new ArrayList<pair>();
        for(int i = 0; i < stringToSplit.size(); i++)
        {
            //File name example
            //JPEG_20191024_171032_36.089_-94.199_6748836076339466660.jpg
            String[] temp = stringToSplit.get(i).split("_");
            String name = temp[0]+temp[1]+temp[2];
            double lat = Double.valueOf(temp[3]);
            double lon = Double.valueOf(temp[4]);
            pair p = new pair(name, lat, lon);
            split.add(p);
        }
        return split;
    }

    @Override
    public boolean onMarkerClick(final Marker marker)
    {
        pair test = (pair) marker.getTag();
        if(marker == null);
        for(int i = 0; i < photoInformation.size(); i++)
        {
            if (test.equals(photoInformation.get(i)))
            {
                finish();
            }
        }
        return false;
    }
}

class pair
{
    String photoName;
    double latitude;
    double longitude;

    public pair(String photoName, double latitude, double longitude)
    {
        this.photoName = photoName;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
