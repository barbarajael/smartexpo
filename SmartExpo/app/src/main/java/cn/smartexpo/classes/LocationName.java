package cn.smartexpo.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class LocationName extends BroadcastReceiver
{
    private double latitude;
    private double longitude;
    String localName;

    public LocationName(double latitude, double longitude)
    {
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public String getLocationName()
    {
        String address = "http://maps.google.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&sensor=true";
        URL url = null;
        BufferedReader in = null;
        String inputLine = null;

        try
        {
            url = new URL(address);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        try
        {
            in = new BufferedReader(new InputStreamReader(url.openStream()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        try
        {
            // read all the content on the site
            while ((inputLine = in.readLine()) != null)
                // get only the line needed
                if (inputLine.contains("long_name"))
                    break;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // get only the name of the place from the string
        String[] parts = inputLine.split("\"long_name\" : ");
        localName = parts[1];
        localName = localName.substring(1, localName.length() - 2);

        return localName;
    }


    public String getLastKnownLocationName(String resource)
    {
        if (localName != null)
            return localName;

        else
            return resource;
    }


    @Override
    public void onReceive(Context context, Intent intent)
    {

    }
}
