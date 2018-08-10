package nohe.nohe_android.activity.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GpsListService {
    private SharedPreferences.Editor editor;
    private Context _context;
    private SharedPreferences pref;

    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "gps_list_pref";
    private static final String GPS_LIST = "gps_list";

    @SuppressLint("CommitPrefEdits")
    public GpsListService(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void clear() {
        Gson gson = new Gson();
        List<String> gps_list = getList();

        gps_list.clear();
        String newJson = gson.toJson(gps_list);

        editor.putString(GPS_LIST, newJson);
        editor.commit();
    }


    public void add(String gps) {
        Gson gson = new Gson();
        List<String> gps_list = getList();

        gps_list.add(gps);
        String newJson = gson.toJson(gps_list);

        editor.putString(GPS_LIST, newJson);
        editor.commit();
    }

    public List<String> getList() {
        Gson gson = new Gson();
        String json = pref.getString(GPS_LIST, "");
        Type type = new TypeToken<List<String>>(){}.getType();
        List<String> gps_list = gson.fromJson(json, type);

        if (gps_list == null) {
            return new ArrayList<String>();
        }
        return gson.fromJson(json, type);
    }
}

