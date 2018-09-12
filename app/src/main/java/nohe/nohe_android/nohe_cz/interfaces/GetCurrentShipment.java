package nohe.nohe_android.nohe_cz.interfaces;

import com.android.volley.VolleyError;
import org.json.JSONException;
import java.util.List;
import java.util.Map;
import nohe.nohe_android.nohe_cz.models.ShipmentModel;

public interface GetCurrentShipment {
    void onResponse(List<ShipmentModel> shipments) throws JSONException;

    void onError(VolleyError message);

    Map<String, String> getHeaders();
}
