package cn.smartexpo.classes;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class GPSTracker extends Service implements LocationListener
{
    private final Context context;

    boolean isGPSEnabled = false;
    boolean isNetworlEnabled = false;
    boolean canGetLocation = false;

    Location location;

    double latitude;
    double longitude;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 5 * 60;

    protected LocationManager locationManager;


    public GPSTracker(Context context) {
        this.context = context;
        getLocation();
    }


    public Location getLocation()
    {
        try
        {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworlEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            // not possible to get location by GPS or net
            if (!isGPSEnabled && !isNetworlEnabled)
                this.canGetLocation = false;

            else
            {
                this.canGetLocation = true;

                // get location by net
                if (isNetworlEnabled)
                {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null)
                    {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (location != null)
                        {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                // get location by GPS
                if (isGPSEnabled)
                {
                    if (location == null)
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null)
                    {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (location != null)
                        {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }


    public double getLatitude()
    {
        if (location != null)
            latitude = location.getLatitude();

        return latitude;
    }


    public double getLongitude()
    {
        if (location != null)
            longitude = location.getLongitude();

        return longitude;
    }


    public boolean canGetLocation()
    {
        return this.canGetLocation;
    }


    @Override
    public void onLocationChanged(Location location) 
    {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        getLocation();
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
