package com.ryan.geo_camera;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

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
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        double currentLat = getIntent().getExtras().getDouble("latitude");
        double currentLon = getIntent().getExtras().getDouble("longitude");
        LatLng currentPlace = new LatLng(currentLat, currentLon);
        mMap.addMarker(new MarkerOptions().position(currentPlace));

        for(int i = 0; i < photoInformation.size(); i++)
        {
            //JPEG_20191024_171032_36.089_-94.199_6748836076339466660.jpg
            double lat = photoInformation.get(i).latitude;
            double lon = photoInformation.get(i).longitude;
            LatLng place = new LatLng(lat, lon);
            char[] photoTitle = photoInformation.get(i).photoName.toCharArray();
            String date = photoTitle[9] + "" +photoTitle[10] + "-" + photoTitle[11] + "" + photoTitle[12] + "-" + photoTitle[5] + "" + photoTitle[6] + "" + photoTitle[7] + "" + photoTitle[8];
            Marker myMarker = mMap.addMarker(new MarkerOptions().position(place).title(photoInformation.get(i).photoName).snippet(date));
            myMarker.setTag(photoInformation.get(i));
            markers.add(myMarker);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPlace));
    }

    private ArrayList<String> getAllFiles(String directory)
    {
        ArrayList<String> result = new ArrayList<String>();
        File folder = new File(directory);
        File[] filesInFolder = folder.listFiles();
        for (File file : filesInFolder) {   if (!file.isDirectory())
                result.add(file.getName()); //push the filename as a string
        }

        return result;
    }

    private ArrayList<pair> splitFileName(ArrayList<String> stringToSplit)
    {
        ArrayList<pair> split = new ArrayList<pair>();
        for(int i = 0; i < stringToSplit.size(); i++)        {
            //File name example
            //JPEG_20191024_171032_36.089_-94.199_6748836076339466660.jpg
            String[] temp = stringToSplit.get(i).split("_");
            String name = temp[0]+"_"+temp[1]+"_"+temp[2];
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
        String title = marker.getTitle();
        Toast.makeText(this, test.photoName, Toast.LENGTH_SHORT).show();
        if(marker == null);
        for(int i = 0; i < photoInformation.size(); i++)
        {
            if (test.equals(photoInformation.get(i)))
            {
                finish();
            }
        }
        finish();
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
