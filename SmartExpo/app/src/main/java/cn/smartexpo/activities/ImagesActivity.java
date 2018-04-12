package cn.smartexpo.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import cn.smartexpo.R;
import cn.smartexpo.adapters.GridViewAdapter;
import cn.smartexpo.adapters.ImageItem;
import cn.smartexpo.classes.dbHandler;

public class ImagesActivity extends ActionBarActivity
{
    private static final int SELECT_PICTURE = 1;
    private dbHandler dbHandler;

    private ArrayList<ImageItem> imageItems = new ArrayList<>();
    private GridView gridView;
    private GridViewAdapter gridAdapter;

    private ImageItem imageDisplay;
    private ImageView imgV;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        // adapter call
        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, getData());
        gridView.setAdapter(gridAdapter);

        // click on button to add
        findViewById(R.id.addImgbt).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // select a file
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }
        });

        // click on image to display
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                imageDisplay = imageItems.get(position);
                imgV = (ImageView) findViewById(R.id.image_place);

                // set image view visible
                imgV.setVisibility(View.VISIBLE);
                // display image
                imgV.setImageBitmap(imageDisplay.getImage());
            }
        });

        // long click on image to delete
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                imageDisplay = imageItems.get(position);

                // update database
                dbHandler.deleteImage(imageDisplay.getImage());
                // update list of items
                imageItems.remove(position);

                // update adapter
                gridView = (GridView) findViewById(R.id.gridView);
                gridAdapter = new GridViewAdapter(ImagesActivity.this, R.layout.grid_item_layout, imageItems);
                gridView.setAdapter(gridAdapter);

                Toast.makeText(ImagesActivity.this, getResources().getString(R.string.img_removed), Toast.LENGTH_LONG).show();

                return true;
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == SELECT_PICTURE)
            {
                Uri selectedImageUri = data.getData();
                Bitmap bitmap = null;

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    Log.e("MY ERROR", "is -> " + e.toString());
                }

                // image's must be under 200kb
                if (bitmap.getByteCount() < 1638400)
                {
                    // update list of items
                    imageItems.add(new ImageItem(bitmap));

                    // update adapter
                    gridView = (GridView) findViewById(R.id.gridView);
                    gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, imageItems);
                    gridView.setAdapter(gridAdapter);

                    // update database
                    dbHandler.addImage(bitmap);
                }

                else
                    Toast.makeText(this, getResources().getString(R.string.img_too_big), Toast.LENGTH_LONG).show();
            }
        }
    }


    // populate array list of images
    private ArrayList<ImageItem> getData()
    {
        ArrayList<Bitmap> collectionBitmaps;

        // get arraylist from database
        dbHandler = new dbHandler(this);
        collectionBitmaps = dbHandler.getImageResults();

        for (Bitmap bm: collectionBitmaps)
            imageItems.add(new ImageItem(bm));

        return imageItems;
    }


    // press back button
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            imgV = (ImageView)findViewById(R.id.image_place);

            // if an image was being displayed, goes to all images
            if (imgV.getVisibility() == View.VISIBLE)
                imgV.setVisibility(View.GONE);
            // or goes to main activity (SmartExpoMain)
            else
                this.finish();

            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}