package nohe.nohe_android.nohe_cz.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
import nohe.nohe_android.nohe_cz.database.model.Shipment;
import nohe.nohe_android.nohe_cz.models.ShipmentModel;
import nohe.nohe_android.nohe_cz.services.LoginService;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 10;

    // Database Name
    private static final String DATABASE_NAME = "shipments_db";

    private LoginService loginService;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        loginService = new LoginService(context.getApplicationContext());
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
    
    public void insertOrUpdateShipments(List<ShipmentModel> shipments) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        // `id` will be inserted automatically.
        // no need to add them
        for (ShipmentModel shipment : shipments) {
            if (updateLocalShipmentToNormal(shipment) == 0) {
                db.insert(Shipment.TABLE_NAME, null, setShipmentValues(shipment));
            }
        }
        db.close();
    }

    public boolean insertShipment(ShipmentModel shipment) {
        ShipmentModel existingShipment = getShipmentByIdShipment(shipment.ID);

        if (existingShipment != null) {
            return false;
        }

        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        // insert row
        db.insert(Shipment.TABLE_NAME, null, setShipmentValues(shipment));

        // close db connection
        db.close();

        // return newly inserted row id
        return true;
    }

    public ShipmentModel getShipmentById(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Shipment.TABLE_NAME,
                new String[]{Shipment.COLUMN_ID, Shipment.COLUMN_ID_SHIPMENT,
                        Shipment.COLUMN_ADDRESS_FROM,
                        Shipment.COLUMN_ADDRESS_TO, Shipment.COLUMN_LOAD_NOTE,
                        Shipment.COLUMN_UNLOAD_NOTE, Shipment.COLUMN_PRICE,
                        Shipment.COLUMN_STATE, Shipment.COLUMN_PHOTOS_BEFORE,
                        Shipment.COLUMN_PHOTOS_AFTER, Shipment.COLUMN_ERROR_CODE,
                        Shipment.COLUMN_LOCAL},
                Shipment.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare shipment object

        ShipmentModel shipment = null;
        if (cursor.moveToFirst()) {
            shipment = getShipmentModelFromDB(cursor);
        }

        // close the db connection
        cursor.close();

        return shipment;
    }

    public ShipmentModel getShipmentByIdShipment(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Shipment.TABLE_NAME,
                new String[]{Shipment.COLUMN_ID, Shipment.COLUMN_ID_SHIPMENT, Shipment.COLUMN_ADDRESS_FROM,
                        Shipment.COLUMN_ADDRESS_TO, Shipment.COLUMN_LOAD_NOTE,
                        Shipment.COLUMN_UNLOAD_NOTE, Shipment.COLUMN_PRICE,
                        Shipment.COLUMN_STATE, Shipment.COLUMN_PHOTOS_BEFORE,
                        Shipment.COLUMN_PHOTOS_AFTER, Shipment.COLUMN_ERROR_CODE,
                        Shipment.COLUMN_LOCAL},
                Shipment.COLUMN_ID_SHIPMENT + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare shipment object

        ShipmentModel shipment = null;
        if (cursor.moveToFirst()) {
            shipment = getShipmentModelFromDB(cursor);
        }

        // close the db connection
        cursor.close();

        return shipment;
    }

    public List<ShipmentModel> getAllShipments() {
        List<ShipmentModel> shipments = new ArrayList<>();
        Integer driver = loginService.getUserId();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Shipment.TABLE_NAME + " WHERE driver = ? ORDER BY " +
                Shipment.COLUMN_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {driver.toString()});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                shipments.add(getShipmentModelFromDB(cursor));
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return shipments;
    }

    public List<ShipmentModel> getFinishedShipments() {
        List<ShipmentModel> shipments = new ArrayList<>();
        Integer driver = loginService.getUserId();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM " + Shipment.TABLE_NAME + " WHERE state = ? and driver = ?",
                new String[] {String.valueOf(ShipmentModel.State.DONE.getValue()), driver.toString()});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                shipments.add(getShipmentModelFromDB(cursor));
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
        return db.update(Shipment.TABLE_NAME, values, Shipment.COLUMN_ID_SHIPMENT + " = ?",
                new String[]{String.valueOf(shipment.ID)});
    }

    public int updateLocalShipment(int ID, ShipmentModel shipment) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Shipment.COLUMN_ID_SHIPMENT, shipment.ID);
        values.put(Shipment.COLUMN_ADDRESS_FROM, shipment.address_from);
        values.put(Shipment.COLUMN_ADDRESS_TO, shipment.address_to);

        // updating row
        return db.update(Shipment.TABLE_NAME, values, Shipment.COLUMN_ID_SHIPMENT + " = ?",
                new String[]{String.valueOf(ID)});
    }

    public int updateLocalShipmentToNormal(ShipmentModel shipment) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Shipment.COLUMN_ADDRESS_FROM, shipment.address_from);
        values.put(Shipment.COLUMN_ADDRESS_TO, shipment.address_to);
        values.put(Shipment.COLUMN_PRICE, shipment.price);
        values.put(Shipment.COLUMN_UNLOAD_NOTE, shipment.unload_note);
        values.put(Shipment.COLUMN_LOAD_NOTE, shipment.load_note);
        values.put(Shipment.COLUMN_LOCAL, 0);

        // updating row
        return db.update(Shipment.TABLE_NAME, values, Shipment.COLUMN_ID_SHIPMENT + " = ?",
                new String[]{String.valueOf(shipment.ID)});
    }

    public void deleteShipment(ShipmentModel shipment) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Shipment.TABLE_NAME, Shipment.COLUMN_ID_SHIPMENT + " = ?",
                new String[]{String.valueOf(shipment.ID)});
        db.close();
    }

    public void deleteUselessShipments(List<ShipmentModel> shipments) {
        Integer driver = loginService.getUserId();
        List<ShipmentModel> localShipments = new ArrayList<>();
        List<ShipmentModel> allShipments = getAllShipments();

        for (ShipmentModel shipment: allShipments) {
            if (shipment.local && shipment.state != ShipmentModel.State.NEW) {
                continue;
            }
            localShipments.add(shipment);
        }

        if (shipments == null) {
            shipments = new ArrayList<>();
        }
        localShipments.removeAll(shipments);
        SQLiteDatabase db = this.getWritableDatabase();

        for (ShipmentModel shipment: localShipments) {
            db.delete(Shipment.TABLE_NAME, Shipment.COLUMN_ID_SHIPMENT + " = ? and driver = ?",
                    new String[]{String.valueOf(shipment.ID), driver.toString()});
        }
        db.close();
    }

    private ShipmentModel getShipmentModelFromDB(Cursor cursor) {
        ShipmentModel shipment =  new ShipmentModel(
                cursor.getInt(cursor.getColumnIndex(Shipment.COLUMN_ID_SHIPMENT)),
                cursor.getString(cursor.getColumnIndex(Shipment.COLUMN_ADDRESS_FROM)),
                cursor.getString(cursor.getColumnIndex(Shipment.COLUMN_ADDRESS_TO)),
                cursor.getString(cursor.getColumnIndex(Shipment.COLUMN_LOAD_NOTE)),
                cursor.getString(cursor.getColumnIndex(Shipment.COLUMN_UNLOAD_NOTE)),
                cursor.getInt(cursor.getColumnIndex(Shipment.COLUMN_PRICE)),
                ShipmentModel.State.fromValue(cursor.getInt(cursor.getColumnIndex(Shipment.COLUMN_STATE))),
                cursor.getString(cursor.getColumnIndex(Shipment.COLUMN_PHOTOS_BEFORE)),
                cursor.getString(cursor.getColumnIndex(Shipment.COLUMN_PHOTOS_AFTER)),
                cursor.getInt(cursor.getColumnIndex(Shipment.COLUMN_ERROR_CODE)),
                cursor.getInt(cursor.getColumnIndex(Shipment.COLUMN_LOCAL)) == 1);
        shipment.setDbId(cursor.getInt(cursor.getColumnIndex(Shipment.COLUMN_ID)));

        return shipment;
    }

    private ContentValues setShipmentValues(ShipmentModel shipment) {
        ContentValues values = new ContentValues();
        Integer driver = loginService.getUserId();

        if (shipment.state == null) {
            shipment.state = ShipmentModel.State.NEW;
        }

        values.put(Shipment.COLUMN_ID_SHIPMENT, shipment.ID);
        values.put(Shipment.COLUMN_ADDRESS_FROM, shipment.address_from);
        values.put(Shipment.COLUMN_ADDRESS_TO, shipment.address_to);
        values.put(Shipment.COLUMN_LOAD_NOTE, shipment.load_note);
        values.put(Shipment.COLUMN_UNLOAD_NOTE, shipment.unload_note);
        values.put(Shipment.COLUMN_PRICE, shipment.price);
        values.put(Shipment.COLUMN_STATE, shipment.state.getValue());
        values.put(Shipment.COLUMN_PHOTOS_BEFORE, shipment.photos_before);
        values.put(Shipment.COLUMN_PHOTOS_AFTER, shipment.photos_after);
        values.put(Shipment.COLUMN_ERROR_CODE, shipment.error_code);
        values.put(Shipment.COLUMN_LOCAL, shipment.local ? 1 : 0);
        values.put(Shipment.COLUMN_DRIVER, driver.toString());

        return values;
    }
}
