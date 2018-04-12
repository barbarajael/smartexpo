package cn.smartexpo.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;

import java.util.Collection;

import cn.smartexpo.R;
import cn.smartexpo.adapters.ListItem;
import cn.smartexpo.classes.GPSTracker;
import cn.smartexpo.classes.LocationName;
import cn.smartexpo.classes.dbHandler;
import cn.smartexpo.fragments.BeaconDetectedFragment;
import cn.smartexpo.fragments.MuseuFragment;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

public class SmartExpoMain extends MaterialNavigationDrawer implements BeaconConsumer, RangeNotifier
{
    /* DECLARATIONS */

    dbHandler dbHandler;

    private GPSTracker gps;
    private LocationName locName;
    private double lat, lng;
    private String localName;

    private BluetoothAdapter bluetooth;
    private BeaconManager mBeaconManager;
    private final int WAIT_TIME = 30000;    // 30 seconds

    private NfcAdapter mNfcAdapter;


    /* ACTIVITY FUNCTIONS */

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void init(Bundle savedInstanceState)
    {
        /* NAVIGATION DRAWER */

        // header of navigation drawer image
        setDrawerHeaderImage(R.mipmap.expo);

        // funtionalities
        setBackPattern(MaterialNavigationDrawer.BACKPATTERN_BACK_TO_FIRST);
        this.disableLearningPattern();
        enableToolbarElevation();

        // create sections
        MaterialSection section1 = newSection(getResources().getString(R.string.museu), getDrawable(R.mipmap.icon_museum), new MuseuFragment());
        MaterialSection section2 = newSection(getResources().getString(R.string.exmuseu), getDrawable(R.mipmap.icon_exmuseums), new Intent(this, MuseusAnterioresActivity.class));
        MaterialSection section3 = newSection(getResources().getString(R.string.news), getDrawable(R.mipmap.icon_news), new Intent(this, NoticiasActivity.class));
        MaterialSection section4 = newSection(getResources().getString(R.string.fav), getDrawable(R.mipmap.icon_favorites), new Intent(this, FavoritosActivity.class));
        MaterialSection section5_1 = this.newSection(getResources().getString(R.string.images), getDrawable(R.mipmap.icon_images), new Intent(this, ImagesActivity.class));

        // add sections
        addSection(section1);
        addSection(section2);
        addSection(section3);
        addSection(section4);

        // saved items - title and sections
        addSubheader("- " + getResources().getString(R.string.saved) + " -");
        addSection(section5_1);
    }


    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        dbHandler = new dbHandler(this);

        gettingLocation();

        if (!isNetworkAvailable())
            connectNet();

        bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (!bluetooth.isEnabled())
            connectBluetooth();
    }


    // FUNCTION get gps location
    public void gettingLocation()
    {
        gps = new GPSTracker(SmartExpoMain.this);
        lat = gps.getLatitude();
        lng = gps.getLongitude();

        if (gps.canGetLocation())
        {
            /* get location name */

            if (isNetworkAvailable())
            {
                locName = new LocationName(lat, lng);

                if (locName != null)
                {
                    // get and execute async task - because it's a network action
                    MyTask mtask = new MyTask();
                    mtask.execute(locName);
                }
            }

            else
            {
                double lat = gps.getLatitude();
                double lng = gps.getLongitude();

                String locationName = "Lat: " + lat + "\nLng: " + lng;

                if (locationName != null)
                    if (lat != 0.0 && lng != 0.0)   // when is still searching, lat=0.0 and lng=0.0
                        Toast.makeText(this, locationName, Toast.LENGTH_LONG).show();
            }
        }

        // open gps settings so the user turns on the location
        else
            connectGPS();
    }


    /* ASYNC TASK */

    private class MyTask extends AsyncTask<LocationName, Void, String>
    {
        private String regex = "[^A-Za-z]+";

        // performs network action
        @Override
        protected String doInBackground(LocationName... params)
        {
            localName = params[0].getLocationName();
            return localName;
        }

        @Override
        protected void onPostExecute(String name)
        {
            if (name != null)
            {
                // if the location's name is not letters, then the location is not a point of interest (it's a house or something)
                if (name.matches(regex))
                    name = getResources().getString(R.string.nao_interesse);

                else
                {
                    ListItem item = new ListItem(name, R.mipmap.icon_favorites);
                    dbHandler.addExmuseusPlace(item);
                }

                // set textView txPlace (present on fragment_museu) with place name
                TextView tvPlace = (TextView) findViewById(R.id.txPlace);
                tvPlace.setText(name);
            }
        }
    }


    /* BUTTONS ON ACTION BAR */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // nfc button
            case R.id.action_nfc:
                mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
                if (!mNfcAdapter.isEnabled())
                    connectNFC();
                return true;

            // bluetooth button
            case R.id.action_bluetooth:
                bluetooth = BluetoothAdapter.getDefaultAdapter();
                if (!bluetooth.isEnabled())
                {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(enableBtIntent);
                }
                else
                    bluetooth.disable();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /* BEACONS */

    @Override
    public void onBeaconServiceConnect()
    {
        org.altbeacon.beacon.Region region = new org.altbeacon.beacon.Region("all-beacons-region", null, null, null);

        try {
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        mBeaconManager.setRangeNotifier(this);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, org.altbeacon.beacon.Region region) {
        for (Beacon beacon: beacons) {
            if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x00)
            {
                runOnUiThread(new Runnable() {
                    public void run() {
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.frame_container, new BeaconDetectedFragment());
                        ft.commit();
                    }
                });

                SystemClock.sleep(WAIT_TIME);
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        // Detect the main Eddystone-UID frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        mBeaconManager.bind(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        mBeaconManager.unbind(this);
    }


    /* VERIFICATIONS */

    // warning - location needs gps enable
    public void connectGPS()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle(getResources().getString(R.string.gps_settings));
        alertDialog.setMessage(getResources().getString(R.string.gps_disabled));
        alertDialog.setPositiveButton(getResources().getString(R.string.action_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton(getResources().getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    // check is network is available
    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // warning - location name needs network enable
    public void connectNet()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle(getResources().getString(R.string.alerta_net));
        alertDialog.setMessage(getResources().getString(R.string.net_disabled));
        alertDialog.setPositiveButton(getResources().getString(R.string.action_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    // warning - beacons need bluetooth enable
    public void connectBluetooth()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle(getResources().getString(R.string.alerta_bluetooth));
        alertDialog.setMessage(getResources().getString(R.string.bluetooth_disabled));
        alertDialog.setPositiveButton(getResources().getString(R.string.action_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    // enable nfc
    public void connectNFC()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle(getResources().getString(R.string.nfc_settings));
        alertDialog.setMessage(getResources().getString(R.string.nfc_get));
        alertDialog.setPositiveButton(getResources().getString(R.string.action_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton(getResources().getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    // check NFC state
    public void lerNFC(View view)
    {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null)
            Toast.makeText(this, getResources().getString(R.string.nfc_unsupported), Toast.LENGTH_LONG).show();

        if (!mNfcAdapter.isEnabled())
        {
            Toast.makeText(this, getResources().getString(R.string.nfc_disabled), Toast.LENGTH_LONG).show();
        }

        else
            Toast.makeText(this, getResources().getString(R.string.nfc_encostar), Toast.LENGTH_LONG).show();
    }
}