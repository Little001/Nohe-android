package nohe.nohe_android.activity.database.model;

import nohe.nohe_android.activity.models.ShipmentModel;

public class Shipment {
    public static final String TABLE_NAME = "shipments";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ADDRESS_FROM = "address_from";
    public static final String COLUMN_ADDRESS_TO = "address_to";
    public static final String COLUMN_LOAD_NOTE = "load_note";
    public static final String COLUMN_UNLOAD_NOTE = "unload_note";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_STATE = "state";
    public static final String COLUMN_PHOTOS_BEFORE = "photos_before";
    public static final String COLUMN_PHOTOS_AFTER = "photos_after";
    public static final String COLUMN_ERROR_CODE = "error_code";
    public static final String COLUMN_LOCAL = "local";

    private int id;
    private String address_from;
    private String address_to;
    private String load_note;
    private String unload_note;
    private int price;
    private ShipmentModel.State state;
    private String photos_before;
    private String photos_after;
    private int error_code;
    private boolean local;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER,"
                    + COLUMN_ADDRESS_FROM + " TEXT,"
                    + COLUMN_ADDRESS_TO + " TEXT,"
                    + COLUMN_LOAD_NOTE + " TEXT,"
                    + COLUMN_UNLOAD_NOTE + " TEXT,"
                    + COLUMN_PRICE + " INTEGER,"
                    + COLUMN_STATE + " INTEGER,"
                    + COLUMN_PHOTOS_BEFORE + " TEXT,"
                    + COLUMN_PHOTOS_AFTER + " TEXT,"
                    + COLUMN_ERROR_CODE + " INTEGER,"
                    + COLUMN_LOCAL + " INTEGER"
                    + ")";

    public Shipment() {
    }

    public Shipment(ShipmentModel shipment) {
        this.id = shipment.ID;
        this.address_from = shipment.address_from;
        this.address_to = shipment.address_to;
        this.load_note = shipment.load_note;
        this.unload_note = shipment.unload_note;
        this.price = shipment.price;
        this.state = shipment.state;
        this.photos_before = shipment.photos_before;
        this.photos_after = shipment.photos_after;
        this.error_code = shipment.error_code;
        this.local = shipment.local;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getAddressFrom() {
        return address_from;
    }
    public void setAddressFrom(String addressFrom) {
        this.address_from = addressFrom;
    }

    public String getAddressTo() {
        return address_to;
    }
    public void setAddressTo(String addressTo) {
        this.address_to = addressTo;
    }

    public String getLoadNote() {
        return load_note;
    }
    public void setLoadNote(String loadNote) {
        this.load_note = loadNote;
    }

    public String getUnloadNote() {
        return unload_note;
    }
    public void setUnloadNote(String unloadNote) {
        this.unload_note = unloadNote;
    }

    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    public ShipmentModel.State getState() {
        return state;
    }
    public void setState(ShipmentModel.State state) {
        this.state = state;
    }

    public String getPhotosBefore() {
        return photos_before;
    }
    public void setPhotosBefore(String photos) {
        this.photos_before = photos;
    }

    public String getPhotosAfter() {
        return photos_after;
    }
    public void setPhotosAfter(String photos) {
        this.photos_after = photos;
    }

    public int getErrorCode() {
        return this.error_code;
    }
    public void setErrorCode(int code) {
        this.error_code = code;
    }

    public boolean isLocal() {
        return this.local;
    }
    public void setLocal(boolean isLocal) {
        this.local = isLocal;
    }
}
