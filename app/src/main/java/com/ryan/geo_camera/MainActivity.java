package com.ryan.geo_camera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.DIRECTORY_PICTURES;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    Button cameraButton;
    ImageView mImageView;
    String mCurrentPhotoPath;
    Uri photoURI;
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT >= 23)
            requestPermissions(new String[]{Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION}, 2);


        mImageView = (ImageView)findViewById(R.id.imageView);
        cameraButton = (Button)findViewById(R.id.btnCamera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    
    @Override
    protected void onResume(){
        super.onResume();

    }

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            File photoFile = null;
            photoFile = createImageFile();

            if(photoFile != null)
            {
                mCurrentPhotoPath = photoFile.getAbsolutePath();
                photoURI = FileProvider.getUriForFile(this, "com.ryan.geo_camera.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
            else
            {
                Toast.makeText(this, "photoFile = null", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if(requestCode == REQUEST_IMAGE_CAPTURE) {
            gps = new GPSTracker(MainActivity.this);
            if (resultCode == RESULT_OK){
                if(data != null) {
                    previewImage();
                    if(gps.canGetLocation())
                    {
                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();
                        Toast.makeText(this, "Your location is: " + latitude + " " + longitude, Toast.LENGTH_SHORT).show();
                        Log.d("location", "Latitude: " + latitude);
                        Log.d("location", "Longitude: " + longitude);
                        Intent intent = new Intent(this, MapsActivity.class);
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);
                        startActivity(intent);

                    }
                    else
                    {
                        Toast.makeText(this, "couldn't get location", Toast.LENGTH_SHORT).show();
                    }
                    //previewCapturedImage();
                }
            }
        }
        else if (resultCode == RESULT_CANCELED) {
            // user cancelled Image capture
            Toast.makeText(getApplicationContext(),
                    "Cancelled", Toast.LENGTH_SHORT)
                    .show();
        } else {
            // failed to capture image
            Toast.makeText(getApplicationContext(),
                    "Error!", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private File createImageFile(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        gps = new GPSTracker(MainActivity.this);
        double latitude = (double)Math.round(gps.getLatitude() * 10000d) / 10000d;
        double longitude = (double)Math.round(gps.getLongitude() * 10000d) / 10000d;
        String imageFileName = "JPEG_" + timeStamp + "_" + latitude + "_" + longitude + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        }catch (IOException e) {Log.d("mylog", "Exception: " + e.toString());}
        return image;
    }

    private void previewImage()
    {
        Uri uri = photoURI;
        Bitmap bitmap = null;
        try{
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        }catch(IOException e) {Log.d("mylog", "Exception: " + e.toString());}
        Toast.makeText(this, "Image saved to:\n" + uri.toString(), Toast.LENGTH_LONG).show();
        mImageView.setImageBitmap(bitmap);
    }

    private void previewCapturedImage() {
        try {
            mImageView.setVisibility(View.VISIBLE);

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(photoURI.getPath(), options);

           mImageView.setImageBitmap(bitmap);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}
