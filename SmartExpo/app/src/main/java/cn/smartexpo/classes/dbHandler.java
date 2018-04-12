package cn.smartexpo.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import cn.smartexpo.adapters.ListItem;

public class dbHandler extends SQLiteOpenHelper
{
    // database variables
    private static final String DATABASE_NAME = "smartexpoDB.db";
    private static final int DATABASE_VERSION = 1;

    // exmuseums' table variables
    public static final String TABLE_EXMUSEUS = "exmuseusTable";
    public static final String COLUMN_EXMUSEUS_ID = "_exID";
    public static final String COLUMN_EXMUSEUS_PLACE = "exPlace";
    public static final String COLUMN_EXMUSEUS_ICON = "exIcon";

    // favorites' table variables
    public static final String TABLE_FAV = "favTable";
    public static final String COLUMN_FAV_ID = "_favID";
    public static final String COLUMN_FAV_PLACE = "favPlace";
    public static final String COLUMN_FAV_ICON = "favIcon";

    // images' table variables
    public static final String TABLE_IMG = "imgTable";
    public static final String COLUMN_IMG_ID = "_imgID";
    public static final String COLUMN_IMG = "image";


    // CONSTRUCTOR
    public dbHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // create tables
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // create exmuseus table with the columns
        String exmuseus_query = "CREATE TABLE " + TABLE_EXMUSEUS + " (" +
                COLUMN_EXMUSEUS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EXMUSEUS_PLACE + " TEXT UNIQUE, " +
                COLUMN_EXMUSEUS_ICON + " INTEGER " +
                ")";

        db.execSQL(exmuseus_query);

        // create favorites table with the columns
        String fav_query = "CREATE TABLE " + TABLE_FAV + " (" +
                COLUMN_FAV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FAV_PLACE + " TEXT UNIQUE, " +
                COLUMN_FAV_ICON + " INTEGER " +
                ")";

        // execute the queries created

        db.execSQL(fav_query);

        // create images table with the columns
        String img_query = "CREATE TABLE " + TABLE_IMG + " (" +
                COLUMN_IMG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_IMG + " BLOB " +
                ")";

        // execute the queries created

        db.execSQL(img_query);
    }

    // update database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // in order to update, first needs to deleted (drop)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXMUSEUS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAV);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMG);

        // the tables were deleted, so need to create again
        onCreate(db);
    }


    /* EXMUSEUS */

    // add new row to the exmuseus table
    public void addExmuseusPlace (ListItem item)
    {
        // get reference to database
        SQLiteDatabase db = getWritableDatabase();

        ContentValues exmuseus_values = new ContentValues();
        // adding
        exmuseus_values.put(COLUMN_EXMUSEUS_PLACE, item.textPlace);
        exmuseus_values.put(COLUMN_EXMUSEUS_ICON, item.imgPlace_id);

        // inserts a new value into the database
        db.insert(TABLE_EXMUSEUS, null, exmuseus_values);
        db.close();
    }

    // convert exmuseus table to an arraylist
    public ArrayList<ListItem> getExmuseusResults()
    {
        // get reference to database
        SQLiteDatabase db = getReadableDatabase();

        ArrayList<ListItem> resultList = new ArrayList<>();
        String exmuseus_query = "SELECT * FROM " + TABLE_EXMUSEUS + " WHERE 1";
        Cursor c = db.rawQuery(exmuseus_query, null);

        // get it from db
        while (c.moveToNext())
        {
            String dbString = c.getString(1);
            int dbIcon = c.getInt(c.getColumnIndex(COLUMN_EXMUSEUS_ICON));

            try
            {
                ListItem item = new ListItem(dbString, dbIcon);
                resultList.add(item);
            } catch (Exception e) {
                Log.e("MY ERROR", "is -> " + e.toString());
            }
        }

        c.close();
        db.close();
        return resultList;
    }

    // replace a row in the exmuseus table
    public void replaceExmuseusPlace (ListItem item)
    {
        // get reference to database
        SQLiteDatabase db = getWritableDatabase();

        ContentValues row = new ContentValues();
        // adding
        row.put(COLUMN_EXMUSEUS_PLACE, item.textPlace);
        row.put(COLUMN_EXMUSEUS_ICON, item.imgPlace_id);

        // replace
        db.replace(TABLE_EXMUSEUS, null, row);

        db.close();
    }


    /* FAVORITES */

    // add new row to the favorites table
    public void addFavPlace (ListItem item)
    {
        // get reference to database
        SQLiteDatabase db = getWritableDatabase();

        ContentValues fav_values = new ContentValues();
        // adding
        fav_values.put(COLUMN_FAV_PLACE, item.textPlace);
        fav_values.put(COLUMN_FAV_ICON, item.imgPlace_id);

        // inserts a new value into the database
        db.insert(TABLE_FAV, null, fav_values);

        db.close();
    }

    // convert favorites table to an arraylist
    public ArrayList<ListItem> getFavResults()
    {
        // get reference to database
        SQLiteDatabase db = getReadableDatabase();

        ArrayList<ListItem> resultList = new ArrayList<>();
        String fav_query = "SELECT * FROM " + TABLE_FAV + " WHERE 1";
        Cursor c = db.rawQuery(fav_query, null);

        // get it from db
        while (c.moveToNext())
        {
            String dbString = c.getString(1);
            int dbIcon = c.getInt(c.getColumnIndex(COLUMN_FAV_ICON));

            try
            {
                ListItem item = new ListItem(dbString, dbIcon);
                resultList.add(item);
            } catch (Exception e) {
                Log.e("MY ERROR", "is -> " + e.toString());
            }
        }

        c.close();
        db.close();
        return resultList;
    }

    // delete place from favorites table
    public void deleteFavPlace (String placeName)
    {
        // get reference to database
        SQLiteDatabase db = getWritableDatabase();

        // delete a single row
        db.execSQL("DELETE FROM " + TABLE_FAV + " WHERE " +
                COLUMN_FAV_PLACE + "=\"" + placeName + "\";");

        db.close();
    }


    /* IMAGES */

    // add new row to the favorites table
    public void addImage (Bitmap image)
    {
        // get byte array
        byte[] data = getBitmapAsByteArray(image);

        // get reference to database
        SQLiteDatabase db = getWritableDatabase();

        ContentValues img_values = new ContentValues();
        // adding
        img_values.put(COLUMN_IMG, data);

        // inserts a new value into the database
        db.insert(TABLE_IMG, null, img_values);

        db.close();
    }

    // convert image to byte array
    public byte[] getBitmapAsByteArray(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }


    // convert images table to an arraylist
    public ArrayList<Bitmap> getImageResults()
    {
        // get reference to database
        SQLiteDatabase db = getReadableDatabase();

        ArrayList<Bitmap> resultList = new ArrayList<>();
        String img_query = "SELECT * FROM " + TABLE_IMG + " WHERE 1";
        Cursor c = db.rawQuery(img_query, null);

        // get it from db
        while (c.moveToNext()) {
            byte[] dbByte = c.getBlob(c.getColumnIndex(COLUMN_IMG));
            Bitmap image = getImage(dbByte);

            try
            {
                resultList.add(image);
            }
            catch (Exception e)
            {
                Log.e("MY ERROR", "is -> " + e.toString());
            }
        }

        c.close();
        db.close();
        return resultList;
    }

    // convert image
    public Bitmap getImage(byte[] image)
    {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    // delete image from images table
    public void deleteImage (Bitmap image)
    {
        // get reference to database
        SQLiteDatabase db = getWritableDatabase();

        // get byte array
        byte[] data = getBitmapAsByteArray(image);

        // delete a single row
        db.execSQL("DELETE FROM " + TABLE_IMG + " WHERE " +
                COLUMN_IMG + "=\"" + data + "\";");

        db.close();
    }
}
