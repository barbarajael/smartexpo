package cn.smartexpo.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import cn.smartexpo.R;

public class MuseuFragment extends Fragment
{
    private Button buttonMap;

    public MuseuFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_museu, container, false);

        // unlock orientation on this fragment
        if(getActivity() != null)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        buttonMap = (Button) view.findViewById(R.id.btMapa);

        // go to MuseuMapaFragment when button is clicked
        buttonMap.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.frame_container, new MuseuMapaFragment());
                ft.commit();
           }
        });

        return view;
    }

}
