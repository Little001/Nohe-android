package nohe.nohe_android.nohe_cz.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import nohe.nohe_android.nohe_cz.models.ShipmentModel;

public class CurrentShipmentService {
    private SharedPreferences.Editor editor;
    private Context _context;
    private SharedPreferences pref;

    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "current_shipment_pref";
    private static final String IS_SET = "is_set";
    private static final String SHIPMENTS = "shipments";


    @SuppressLint("CommitPrefEdits")
    public CurrentShipmentService(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setShipments(List<ShipmentModel> shipments) {
        Gson gson = new Gson();
        String json = gson.toJson(shipments);

        editor.putBoolean(IS_SET, true);
        editor.putString(SHIPMENTS, json);

        editor.commit();
    }

    public void unSetShipments() {
        editor.putBoolean(IS_SET, false);
        editor.putString(SHIPMENTS, "");

        editor.commit();
    }

    public boolean isSet() {
        return pref.getBoolean(IS_SET, false);
    }

    public List<ShipmentModel> getShipments() {
        Gson gson = new Gson();
        String json = pref.getString(SHIPMENTS, "");
        Type type = new TypeToken<List<ShipmentModel>>(){}.getType();

        return gson.fromJson(json, type);
    }
}
