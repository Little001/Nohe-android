package nohe.nohe_android.activity.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import nohe.nohe_android.activity.models.ShipmentModel;

public class CurrentShipmentService {
    private SharedPreferences.Editor editor;
    private Context _context;
    private SharedPreferences pref;

    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "current_shipment_pref";
    private static final String IS_SET = "is_set";
    private static final String ID = "Id";
    private static final String FROM = "From";
    private static final String TO = "To";
    private static final String LOAD_NOTE = "LoadNote";
    private static final String UNLOAD_NOTE = "UnloadNote";
    private static final String PRICE = "Price";


    @SuppressLint("CommitPrefEdits")
    public CurrentShipmentService(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setShipment(ShipmentModel shipment) {
        editor.putBoolean(IS_SET, true);
        editor.putInt(ID, shipment.ID);
        editor.putString(FROM, shipment.from);
        editor.putString(TO, shipment.to);
        editor.putString(LOAD_NOTE, shipment.load_note);
        editor.putString(UNLOAD_NOTE, shipment.unload_note);
        editor.putInt(PRICE, shipment.price);

        editor.commit();
    }

    public void unSetShipment() {
        editor.putBoolean(IS_SET, false);
        editor.putInt(ID, 0);
        editor.putString(FROM, "");
        editor.putString(TO, "");
        editor.putString(LOAD_NOTE, "");
        editor.putString(UNLOAD_NOTE, "");
        editor.putInt(PRICE, 0);

        editor.commit();
    }

    public boolean isSet() {
        return pref.getBoolean(IS_SET, false);
    }

    public Integer getId() {
        return pref.getInt(ID, -1);
    }

    public String getFrom() {
        return pref.getString(FROM, "");
    }

    public String getTo() {
        return pref.getString(TO, "");
    }

    public String getLoadNote() {
        return pref.getString(LOAD_NOTE, "");
    }

    public String getUnloadNote() {
        return pref.getString(UNLOAD_NOTE, "");
    }

    public Integer getPrice() {
        return pref.getInt(PRICE, -1);
    }
}
