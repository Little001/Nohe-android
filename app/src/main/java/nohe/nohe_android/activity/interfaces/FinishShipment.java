package nohe.nohe_android.activity.interfaces;

import com.android.volley.VolleyError;
import java.util.Map;

public interface FinishShipment {
    void onResponse();

    void onError(VolleyError message);

    Map<String, String> getParams();

    Map<String, String> getHeaders();
}

