package nohe.nohe_android.nohe_cz.interfaces;

import com.android.volley.VolleyError;

import java.util.Map;

public interface VolleyStringResponseListener {
    void onError(VolleyError message);

    void onResponse(String response);

    Map<String, String> getParams();

    Map<String, String> getHeaders();
}
