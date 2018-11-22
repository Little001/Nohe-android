package nohe.nohe_android.nohe_cz.interfaces;

import com.android.volley.VolleyError;
import java.util.Map;

import nohe.nohe_android.nohe_cz.services.DataFile;

public interface FinishShipment {
    void onResponse();

    void onError(VolleyError message);

    Map<String, String> getParams();

    Map<String, String> getHeaders();

    Map<String, DataFile> getByteData();
}

