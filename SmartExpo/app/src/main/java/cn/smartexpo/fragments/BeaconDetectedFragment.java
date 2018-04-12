package cn.smartexpo.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import cn.smartexpo.R;

public class BeaconDetectedFragment extends Fragment
{
    private Button buttonQrc;

    public BeaconDetectedFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view =  inflater.inflate(R.layout.fragment_beacon_detected, container, false);

        buttonQrc = (Button) view.findViewById(R.id.btQRCode);

        // get the information by reading QRCode when button is clicked
        buttonQrc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    Intent intent = new Intent("la.droid.qr.scan");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
                    startActivityForResult(intent, 0);
                }

                // if not installed, asks to install the app to read the qr code
                catch (Exception e)
                {
                    Uri marketUri = Uri.parse("http://market.android.com/details?id=la.droid.qr");
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                    startActivity(marketIntent);
                }
            }
        });

        return view;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }


    // helper of the QR Code function
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if( 0 == requestCode && data != null && data.getExtras() != null )
        {
            String result = data.getExtras().getString("la.droid.qr.result");

            // open link
            Uri marketUri = Uri.parse(result);
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            startActivity(marketIntent);
        }
    }


    //FUNCTION back button goes back on Fragments
    @Override
    public void onResume()
    {
        super.onResume();

        // lock orientation on this fragment
        if(getActivity() != null)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // go back to right fragment
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.frame_container, new MuseuFragment());
                    ft.commit();

                    return true;
                }

                return false;
            }
        });
    }
}
