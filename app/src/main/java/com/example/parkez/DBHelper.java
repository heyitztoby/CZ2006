package com.example.parkez;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    // init database

    //DB Name
    private static final String DATABASE_NAME = "hdb-carpark-information.db";

    //DB Table Name
    private static final String TABLE_ITEMS = "hdbcarparks";

    //Carpark Table Column Name
    private static final String CARPARK_NO = "car_park_no";
    private static final String ADDRESS = "address";
    private static final String XCOORD = "roadName";
    private static final String YCOORD = "description";
    private static final String CARPARK_TYPE = "car";
    private static final String PARKING_SYSTEM_TYPE = "type_of_parking_system";
    private static final String SHORT_TERM_PARKING = "short_term_parking";
    private static final String FREE_PARKING = "free_packing";
    private static final String NIGHT_PARKING = "night_parking";
    private static final String CARPARK_DECKS = "car_park_decks";
    private static final String GANTRY_HEIGHT = "gantry_height";
    private static final String CARPARK_BASEMENT = "car_park_basement";


    public final static String DATABASE_PATH = "/data/data/com.example.parkez/databases/";
    public static final int DATABASE_VERSION = 1;
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;

    }

    //Create a empty database on the system
    public void createDatabase() throws IOException
    {

        boolean dbExist = checkDataBase();

        if(dbExist)
        {
            Log.v("DB Exists", "db exists");
            // By calling this method here onUpgrade will be called on a
            // writeable database, but only if the version number has been
            // bumped
            //onUpgrade(myDataBase, DATABASE_VERSION_old, DATABASE_VERSION);
        }

        boolean dbExist1 = checkDataBase();
        if(!dbExist1)
        {
            this.getReadableDatabase();
            try
            {
                this.close();
                copyDataBase();
            }
            catch (IOException e)
            {
                throw new Error("Error copying database");
            }
        }

    }
    //Check database already exist or not
    private boolean checkDataBase()
    {
        boolean checkDB = false;
        try
        {
            String myPath = DATABASE_PATH + DATABASE_NAME;
            File dbfile = new File(myPath);
            checkDB = dbfile.exists();
        }
        catch(SQLiteException e)
        {
        }
        return checkDB;
    }
    //Copies your database from your local assets-folder to the just created empty database in the system folder
    public void copyDataBase() throws IOException
    {

        InputStream mInput = myContext.getAssets().open(DATABASE_NAME);
        String outFileName = DATABASE_PATH + DATABASE_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[2024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }
    //delete database
    public void db_delete()
    {
        File file = new File(DATABASE_PATH + DATABASE_NAME);
        if(file.exists())
        {
            file.delete();
            System.out.println("delete database file.");
        }
    }
    //Open database
    public void openDatabase() throws SQLException
    {
        String myPath = DATABASE_PATH + DATABASE_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void closeDataBase()throws SQLException
    {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
        {
            Log.v("Database Upgrade", "Database version higher than old.");
            db_delete();
        }

    }

    // Functions to get each data starts here - SQL queries

    private Carpark getCarparkObject(Cursor cursor) {
        Carpark cp = new Carpark();
        cp.setCarparkNo(cursor.getString(0));
        cp.setAddress(cursor.getString(1));
        cp.setXCoord(cursor.getLong(2));
        cp.setYCoord(cursor.getLong(3));
        cp.setCarparkType(cursor.getString(4));
        cp.setParkingSystemType(cursor.getString(5));
        cp.setShortTermParking(cursor.getString(6));
        cp.setFreeParking(cursor.getString(7));
        cp.setNightParking(cursor.getString(8));
        cp.setCarparkDecks(cursor.getInt(9));
        cp.setGantryHeight(cursor.getDouble(10));
        cp.setCarparkBasement(cursor.getString(11));
        return cp;
    }


    /**
     * Gets all Bus Stops in the database
     * @return An ArrayList of all bus stops in the DB
     */
    public ArrayList<Carpark> getAllCarparks(){
        String query = "SELECT * FROM " + TABLE_ITEMS;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Carpark> results = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            do {
                results.add(getCarparkObject(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return results;
    }

    public Carpark getCarparkByCarparkCode(String carparkNo){
        carparkNo = DatabaseUtils.sqlEscapeString(carparkNo);
        String query = "SELECT * FROM " + TABLE_ITEMS + " WHERE " + CARPARK_NO + "=" + carparkNo + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        Carpark cp = new Carpark();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()){
            do {
                cp = getCarparkObject(cursor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return cp;
    }

    public int getSize(){
        String query = "SELECT * FROM " + TABLE_ITEMS + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

}
