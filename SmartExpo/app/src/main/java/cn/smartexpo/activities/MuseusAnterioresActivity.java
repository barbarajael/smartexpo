package cn.smartexpo.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import cn.smartexpo.R;
import cn.smartexpo.fragments.MuseusAnterioresFragment;

public class MuseusAnterioresActivity extends ActionBarActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content);

        //prepare fragment
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.frag_place, new MuseusAnterioresFragment());
        ft.commit();
    }
}