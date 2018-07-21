package nohe.nohe_android.activity.interfaces;

import com.android.volley.VolleyError;

import org.apache.http.entity.mime.MultipartEntity;

import java.util.Map;

/**
 * Created by dkornel on 2/4/17.
 */

public interface VolleyFormResponseListener {
    void onError(VolleyError message);

    void onResponse(String response);

    Map<String, String> getHeaders();

    MultipartEntity getData();
}

