package com.example.android.walkmyandroid;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressTask extends AsyncTask<Location, Void, String> {
    private static final String TAG = "FetchAddressTask";
    private Context context;
    private Geocoder geocoder1;
    private Geocoder geocoder2;
    private OnTaskCompleted listener;

    public FetchAddressTask(@NonNull Context context, OnTaskCompleted listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Location... locations) {
        geocoder1 = new Geocoder(context, Locale.getDefault());
        Location singleLocation = locations[0];
        Location secondLocation = locations[1];
        List<Address> addressList1 = null;
        List<Address> addressList2 = null;
        String addressResult = "";

        //float distance = singleLocation.distanceTo(secondLocation);

        try {
            addressList1 = geocoder1.getFromLocation(
                    singleLocation.getLatitude(),
                    singleLocation.getLongitude(),
                    1);
            addressList2 = (geocoder2.getFromLocation(
                    secondLocation.getLatitude(),
                    secondLocation.getLongitude(),
                    1
            ));
        } catch (IOException e) {
            addressResult = context.getString(R.string.service_not_available);
            Log.e(TAG, addressResult + e);
        } catch (IllegalArgumentException e) {
            addressResult = context.getString(R.string.invalid_lat_long_used);
            Log.e(TAG, addressResult + ". "
                    + "Latitude = " + singleLocation.getLatitude()
                    + "Longitude = " + singleLocation.getLongitude(), e);
        }


        if (addressList1 == null || addressList1.size() == 0) {
            if (addressResult.isEmpty()) {
                addressResult = context
                        .getString(R.string.no_address_found);
                Log.e(TAG, addressResult);
            } else {
                Address address = addressList1.get(0);
                ArrayList<String> addressParts = new ArrayList<>();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressParts.add(address.getAddressLine(i));
                }
                addressResult = TextUtils.join("\n", addressParts);
            }
        }
        return addressResult;
    }

    @Override
    protected void onPostExecute(String result) {
        listener.onTaskCompleted(result);
        super.onPostExecute(result);
    }
}

interface OnTaskCompleted {
    void onTaskCompleted(String result);
}
