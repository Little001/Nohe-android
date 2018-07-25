package nohe.nohe_android.activity.interfaces;

import com.android.volley.VolleyError;

import java.util.Map;

public interface GetCurrentShipment {
    void onResponse();

    void onError(VolleyError message);

    Map<String, String> getHeaders();
}
