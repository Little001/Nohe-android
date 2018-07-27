package nohe.nohe_android.activity.interfaces;

import com.android.volley.VolleyError;

import java.util.Map;

import nohe.nohe_android.activity.models.ShipmentModel;

public interface GetCurrentShipment {
    void onResponse(ShipmentModel shipment);

    void onError(VolleyError message);

    Map<String, String> getHeaders();
}
