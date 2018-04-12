package cn.smartexpo.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;

import cn.smartexpo.R;
import cn.smartexpo.adapters.ListItem;
import cn.smartexpo.adapters.ListItemAdapter;
import cn.smartexpo.classes.dbHandler;

public class MuseusAnterioresFragment extends ListFragment implements AdapterView.OnItemClickListener
{
    public MuseusAnterioresFragment()
    {
        // Required empty public constructor
    }

    private dbHandler dbHandler;
    private ArrayList<ListItem> exmuseusList;
    private ArrayAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    //FUNCTION create menu with text and images
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        // get arraylist from database
        dbHandler = new dbHandler(getActivity());
        exmuseusList = dbHandler.getExmuseusResults();

        if (exmuseusList.isEmpty())
            Toast.makeText(getActivity(), getResources().getString(R.string.exmuseus_not_found), Toast.LENGTH_LONG).show();

        else
        {
            //Create array adapter
            adapter = new ListItemAdapter(getActivity(), exmuseusList);
            setListAdapter(adapter);
            getListView().setOnItemClickListener(this);
        }
    }


    //FUNCTION click on items from list
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        ListItem item;
        String name = exmuseusList.get(position).textPlace;
        int icon = exmuseusList.get(position).imgPlace_id;
        int icon_fav = R.mipmap.icon_favorites;
        int icon_notfav = R.mipmap.icon_nonfav;
        
        // if place's icon is a full heart (non fav yet), becomes a fav
        if (icon == icon_fav)
        {
            // change the icon
            icon = icon_notfav;
            item = new ListItem(name, icon);

            // add item to favorites table
            ListItem item_fav = new ListItem(name, icon_fav);
            dbHandler.addFavPlace(item_fav);
        }

        // if place's icon is a empty heart (already fav), becomes not fav
        else
        {
            // change the icon
            icon = icon_fav;

            item = new ListItem(name, icon);    // not needed here, but a few lines below
            // remove item to favorites table
            dbHandler.deleteFavPlace(name);
        }

        // update list of items
        exmuseusList.set(position, item);

        // update adapter
        adapter = new ListItemAdapter(getActivity(), exmuseusList);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);

        // update database
        dbHandler.replaceExmuseusPlace(item);
    }
}
