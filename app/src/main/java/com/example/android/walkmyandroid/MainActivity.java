/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.walkmyandroid;

import android.Manifest;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements OnTaskCompleted{
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TAG = "MainActivity";
    private Button button;
    private TextView locationTextview;
    private TextView distanceTextview;
    private Location lastLocation;
    private FusedLocationProviderClient flpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationTextview = findViewById(R.id.textview_location);
        distanceTextview = findViewById(R.id.distance_textView);
        button = findViewById(R.id.button_location);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });

        flpClient = LocationServices.getFusedLocationProviderClient(this);

    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            flpClient.getLastLocation().addOnSuccessListener(
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                new FetchAddressTask(MainActivity.this,
                                        MainActivity.this).execute(location);
                            } else {
                                locationTextview.setText(R.string.no_location);
                            }
                        }
                    });
        }
        locationTextview.setText(getString(R.string.address_text,
                getString(R.string.loading),
                System.currentTimeMillis()));
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(this, R.string.location_permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
                default:
                    break;
        }
    }

    @Override
    public void onTaskCompleted(String result) {
        locationTextview.setText(getString(R.string.address_text, result, System.currentTimeMillis()));
    }
}
