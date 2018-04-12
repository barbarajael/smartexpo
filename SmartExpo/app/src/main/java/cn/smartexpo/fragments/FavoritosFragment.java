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

public class FavoritosFragment extends ListFragment implements AdapterView.OnItemClickListener
{
    public FavoritosFragment()
    {
        // Required empty public constructor
    }

    dbHandler dbHandler;
    ArrayList<ListItem> favList;
    private ArrayAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    //FUNCTION create menu with text and images
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        dbHandler = new dbHandler(getActivity());
        favList = dbHandler.getFavResults();

        if (favList.isEmpty())
            Toast.makeText(getActivity(), getResources().getString(R.string.fav_not_found), Toast.LENGTH_LONG).show();

        else
        {
            //Create array adapter
            adapter = new ListItemAdapter(getActivity(), favList);
            setListAdapter(adapter);
            getListView().setOnItemClickListener(this);
        }
    }


    //FUNCTION click on items from list
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        String name = favList.get(position).textPlace;

        /* when the item is on favorites, if it's clicked on it just disappears */
        // update database
        dbHandler.deleteFavPlace(name);
        // update list of items
        favList.remove(position);

        // update adapter
        adapter = new ListItemAdapter(getActivity(), favList);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);


        /* also needs to update the item on exmuseus */
        ListItem item = new ListItem(name, R.mipmap.icon_favorites);
        dbHandler.replaceExmuseusPlace(item);
    }
}