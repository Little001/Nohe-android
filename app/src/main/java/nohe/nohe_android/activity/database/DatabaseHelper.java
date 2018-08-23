package nohe.nohe_android.activity.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
import nohe.nohe_android.activity.database.model.Shipment;
import nohe.nohe_android.activity.models.ShipmentModel;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "shipments_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(Shipment.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Shipment.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }
    
    public void insertShipments(List<ShipmentModel> shipments) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` will be inserted automatically.
        // no need to add them
        for (ShipmentModel shipment : shipments) {
            values.put(Shipment.COLUMN_ID, shipment.ID);
            values.put(Shipment.COLUMN_ADDRESS_FROM, shipment.address_from);
            values.put(Shipment.COLUMN_ADDRESS_TO, shipment.address_to);
            values.put(Shipment.COLUMN_LOAD_NOTE, shipment.load_note);
            values.put(Shipment.COLUMN_UNLOAD_NOTE, shipment.unload_note);
            values.put(Shipment.COLUMN_PRICE, shipment.price);
            values.put(Shipment.COLUMN_STATE, shipment.price);
            values.put(Shipment.COLUMN_PHOTOS_BEFORE, shipment.photos_before);
            values.put(Shipment.COLUMN_PHOTOS_AFTER, shipment.photos_after);
            values.put(Shipment.COLUMN_ERROR_CODE, shipment.error_code);

            // insert row
            db.insert(Shipment.TABLE_NAME, null, values);
        }
        db.close();
    }

    public long insertShipment(ShipmentModel shipment) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` will be inserted automatically.
        // no need to add them
        values.put(Shipment.COLUMN_ID, shipment.ID);
        values.put(Shipment.COLUMN_ADDRESS_FROM, shipment.address_from);
        values.put(Shipment.COLUMN_ADDRESS_TO, shipment.address_to);
        values.put(Shipment.COLUMN_LOAD_NOTE, shipment.load_note);
        values.put(Shipment.COLUMN_UNLOAD_NOTE, shipment.unload_note);
        values.put(Shipment.COLUMN_PRICE, shipment.price);
        values.put(Shipment.COLUMN_STATE, shipment.price);
        values.put(Shipment.COLUMN_PHOTOS_BEFORE, shipment.photos_before);
        values.put(Shipment.COLUMN_PHOTOS_AFTER, shipment.photos_after);
        values.put(Shipment.COLUMN_ERROR_CODE, shipment.error_code);

        // insert row
        long id = db.insert(Shipment.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public ShipmentModel getShipment(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Shipment.TABLE_NAME,
                new String[]{Shipment.COLUMN_ID, Shipment.COLUMN_ADDRESS_FROM,
                        Shipment.COLUMN_ADDRESS_TO, Shipment.COLUMN_LOAD_NOTE,
                        Shipment.COLUMN_UNLOAD_NOTE,Shipment.COLUMN_PRICE},
                Shipment.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare shipment object
        ShipmentModel shipment = new ShipmentModel(
                cursor.getInt(cursor.getColumnIndex(Shipment.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Shipment.COLUMN_ADDRESS_FROM)),
                cursor.getString(cursor.getColumnIndex(Shipment.COLUMN_ADDRESS_TO)),
                cursor.getString(cursor.getColumnIndex(Shipment.COLUMN_LOAD_NOTE)),
                cursor.getString(cursor.getColumnIndex(Shipment.COLUMN_UNLOAD_NOTE)),
                cursor.getInt(cursor.getColumnIndex(Shipment.COLUMN_PRICE)),
                ShipmentModel.State.values()[cursor.getInt(cursor.getColumnIndex(Shipment.COLUMN_STATE))],
                cursor.getString(cursor.getColumnIndex(Shipment.COLUMN_PHOTOS_BEFORE)),
                cursor.getString(cursor.getColumnIndex(Shipment.COLUMN_PHOTOS_AFTER)),
                cursor.getInt(cursor.getColumnIndex(Shipment.COLUMN_ERROR_CODE)));

        // close the db connection
        cursor.close();

        return shipment;
    }

    public List<ShipmentModel> getAllShipments() {
        List<ShipmentModel> shipments = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Shipment.TABLE_NAME + " ORDER BY " +
                Shipment.COLUMN_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ShipmentModel shipment = new ShipmentModel(
                        cursor.getInt(cursor.getColumnIndex(Shipment.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(Shipment.COLUMN_ADDRESS_FROM)),
                        cursor.getString(cursor.getColumnIndex(Shipment.COLUMN_ADDRESS_TO)),
                        cursor.getString(cursor.getColumnIndex(Shipment.COLUMN_LOAD_NOTE)),
                        cursor.getString(cursor.getColumnIndex(Shipment.COLUMN_UNLOAD_NOTE)),
                        cursor.getInt(cursor.getColumnIndex(Shipment.COLUMN_PRICE)),
                        ShipmentModel.State.values()[cursor.getInt(cursor.getColumnIndex(Shipment.COLUMN_STATE))],
                        cursor.getString(cursor.getColumnIndex(Shipment.COLUMN_PHOTOS_BEFORE)),
                        cursor.getString(cursor.getColumnIndex(Shipment.COLUMN_PHOTOS_AFTER)),
                        cursor.getInt(cursor.getColumnIndex(Shipment.COLUMN_ERROR_CODE)));
                shipments.add(shipment);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return shipments;
    }

    public List<ShipmentModel> getFinishedShipments() {
        List<ShipmentModel> shipments = new ArrayList<>();


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM " + Shipment.TABLE_NAME + " WHERE state = ?",
                new String[] {String.valueOf(ShipmentModel.State.DONE.getValue())});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ShipmentModel shipment = new ShipmentModel(
                        cursor.getInt(cursor.getColumnIndex(Shipment.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(Shipment.COLUMN_ADDRESS_FROM)),
                        cursor.getString(cursor.getColumnIndex(Shipment.COLUMN_ADDRESS_TO)),
                        cursor.getString(cursor.getColumnIndex(Shipment.COLUMN_LOAD_NOTE)),
                        cursor.getString(cursor.getColumnIndex(Shipment.COLUMN_UNLOAD_NOTE)),
                        cursor.getInt(cursor.getColumnIndex(Shipment.COLUMN_PRICE)),
                        ShipmentModel.State.values()[cursor.getInt(cursor.getColumnIndex(Shipment.COLUMN_STATE))],
                        cursor.getString(cursor.getColumnIndex(Shipment.COLUMN_PHOTOS_BEFORE)),
                        cursor.getString(cursor.getColumnIndex(Shipment.COLUMN_PHOTOS_AFTER)),
                        cursor.getInt(cursor.getColumnIndex(Shipment.COLUMN_ERROR_CODE)));
                shipments.add(shipment);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return shipments;
    }

    public int getShipmentsCount() {
        String countQuery = "SELECT  * FROM " + Shipment.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateShipment(ShipmentModel shipment) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Shipment.COLUMN_STATE, shipment.state.getValue());
        values.put(Shipment.COLUMN_PHOTOS_BEFORE, shipment.photos_before);
        values.put(Shipment.COLUMN_PHOTOS_AFTER, shipment.photos_after);
        values.put(Shipment.COLUMN_ERROR_CODE, shipment.error_code);

        // updating row
        return db.update(Shipment.TABLE_NAME, values, Shipment.COLUMN_ID + " = ?",
                new String[]{String.valueOf(shipment.ID)});
    }

    public void deleteShipment(ShipmentModel shipment) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Shipment.TABLE_NAME, Shipment.COLUMN_ID + " = ?",
                new String[]{String.valueOf(shipment.ID)});
        db.close();
    }

    public void deleteAllShipments() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Shipment.TABLE_NAME, null, null);
        db.close();
    }
}
