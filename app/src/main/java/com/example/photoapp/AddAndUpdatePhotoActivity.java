package com.example.photoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddAndUpdatePhotoActivity extends AppCompatActivity implements LocationListener {

    //views
    private ImageView imageView;
    private EditText title_input, description_input, keyWords_input;
    private FloatingActionButton savePhoto_button;

    //actionBar
    private ActionBar actionBar;

    //permission costants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;

    //image pick constants
    private static final int IMAGE_PICK_CAMERA_CODE = 102;
    private static final int IMAGE_PICK_GALLERY_CODE = 103;

    //location
    private LocationManager locationManager;
    private TextView locationTextView;
    private static final int LOCATION_REQUEST_CODE = 104;


    //arrays of permissions
    private String[] cameraPermissions; //camera and storage
    private String[] storagePermissions; //only storage
    private String[] locationPermissions;

    //variables (will contain data to save)
    private Uri imageUri;
    private String id, title, description, keyWords, addressOfImage, addedTime, updatedTime;
    private boolean isEditMode = false;

    //db helper
    MyDatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_and_update_photo);

        //init
        actionBar = getSupportActionBar();
        //title
        actionBar.setTitle("Dodaj zdjęcie");
        //back button
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //init views
        imageView = findViewById(R.id.imageView);
        title_input = findViewById(R.id.title_input);
        description_input = findViewById(R.id.description_input);
        keyWords_input = findViewById(R.id.keyWords_input);
        savePhoto_button = findViewById(R.id.savePhoto_button);

        //get data from intent
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("isEditMode", false);

        //set data to views
        if (isEditMode) {
            //update data
            actionBar.setTitle("Aktualizuj szczegóły zdjęcia");

            id = intent.getStringExtra("ID");
            title = intent.getStringExtra("TITLE");
            imageUri = Uri.parse(intent.getStringExtra("IMAGE"));
            description = intent.getStringExtra("DESCRIPTION");
            keyWords = intent.getStringExtra("KEY_WORDS");
            addedTime = intent.getStringExtra("ADDED_TIME");
            updatedTime = intent.getStringExtra("UPDATED_TIME");
            addressOfImage = intent.getStringExtra("ADDRESS");

            //set data to views
            title_input.setText(title);
            description_input.setText(description);
            keyWords_input.setText(keyWords);

            //if no image was selected
            if (imageUri.toString().equals("null")) {
                //noo image, set default
                imageView.setImageResource(R.drawable.ic_photo);
            } else {
                //have image
                imageView.setImageURI(imageUri);
            }

        }

        //init db helper
        myDB = new MyDatabaseHelper(this);


        //init permission arrays
        cameraPermissions = new String[]{android.Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        //location
        locationTextView = findViewById(R.id.locationTextView);

        //click image view to show image pick dialog
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show image pick dialog
                imagePickDialog();
            }
        });

        //click save button to save record
        savePhoto_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });
    }

    private void inputData() {
        //get data
        title = "" + title_input.getText().toString().trim();
        description = "" + description_input.getText().toString().trim();
        keyWords = "" + keyWords_input.getText().toString().trim();

        if (isEditMode) {
            //update data
            String timestamp = "" + System.currentTimeMillis();
            myDB.updateRecord(
                    "" + id,
                    "" + title,
                    "" + imageUri,
                    "" + description,
                    "" + keyWords,
                    "" + addedTime,
                    "" + timestamp,
                    "" + addressOfImage
            );
            Toast.makeText(this, "Zaaktualizowano pomyślnie...", Toast.LENGTH_SHORT).show();
        } else {
            //new data
            if (title.isEmpty() || description.isEmpty() || keyWords.isEmpty()) {
                Toast.makeText(this, "Uzupełnij wszystkie pola", Toast.LENGTH_SHORT).show();
                return;
            }
            //save to db
            String timestamp = "" + System.currentTimeMillis();
            long id = myDB.insertRecord(
                    "" + title,
                    "" + imageUri,
                    "" + description,
                    "" + keyWords,
                    "" + timestamp,
                    "" + timestamp,
                    "" + addressOfImage
            );
            Toast.makeText(this, "Dodano rekord o id: " + id, Toast.LENGTH_SHORT).show();
        }
    }

    private void imagePickDialog() {
        //options to display in dialog
        String[] options = {"Aparat", "Galeria"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //title
        builder.setTitle("Dodaj zdjęcie:");
        //set items/options
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle clicks
                if (which == 0) {
                    //camera clicked
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        //permission already granted
                        pickFromCamera();
                    }
                }
                //gallery clicked
                else if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        //permission already granted
                        pickFromGallery();
                    }
                }
            }
        });
        //create/show dialog
        builder.create().show();
    }

    private void pickFromCamera() {
        //intent to pick image from camera, the image will be returned in onActivityResult method
        getLocation(); // !!!!!!!!!!!!!

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Tytuł zdjęcia");
        values.put(MediaStore.Images.Media.TITLE, "Opis zdjęcia");

        //put image uri
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //intent to open camera for image
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        //intent to pick image from gallery, the image will be returned in onActivityResult method
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*"); //only images
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission() {
        //check if storage permission is enabled
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        //request the storage permission
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        //check if camera permission is enabled or not
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);

        return result && result1 && result2;
    }

    private void requestCameraPermission() {
        //request the camera permission
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }


    //location
    private void getLocation() {
        if(checkLocationPermission()) {
            try {
                locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 5F, (LocationListener) AddAndUpdatePhotoActivity.this);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            requestLocationPermission();
        }
    }

    private boolean checkLocationPermission() {
        //check if camera permission is enabled or not
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestLocationPermission() {
        //ActivityCompat.requestPermissions(this,locationPermissions,LOCATION_REQUEST_CODE);
        ActivityCompat.requestPermissions(this,locationPermissions,LOCATION_REQUEST_CODE);
    }

    //end of location

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // go back by clicking back button of acionbar
        return super.onSupportNavigateUp();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //result of permission allowed/denied
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    //if allowed returns true otherwise false
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted){
                        //both permissions allowed
                        pickFromCamera();
                    }else{
                        Toast.makeText(this, "Uprawnienia do aparatu i pamięci i lokalizacji są wymagane",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    //if allowed returns true otherwise false
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if(storageAccepted) {
                        //storage permission allowed
                        pickFromGallery();
                    }else{
                        Toast.makeText(this, "Uprawnienie do pamięci jest wymagane...",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length>0){
                    //if allowed returns true otherwise false
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if(locationAccepted) {
                        //location permission allowed
                        getLocation();
                    }else{
                        Toast.makeText(this, "Uprawnienie do lokalizacji jest wymagane...",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //image picked from camera or gallery will received here
        if(resultCode == RESULT_OK){
            //image is picked
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                //picked from gallery
                //crop image
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);

            } else if(requestCode == IMAGE_PICK_CAMERA_CODE){
                //picked from camera
                //crop image
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);
            } else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                //croped image received
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if(resultCode == RESULT_OK){
                    Uri resultUri = result.getUri();
                    imageUri = resultUri;
                    //set image
                    imageView.setImageURI(resultUri);
                    addressOfImage = "" + locationTextView.getText().toString().trim();
                }
                else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    Exception error = result.getError();
                    Toast.makeText(this,""+error,Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //location
    @Override
    public void onLocationChanged(@NonNull Location location) {
        try{
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);
            locationTextView.setText(address);
            addressOfImage = address;

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
}